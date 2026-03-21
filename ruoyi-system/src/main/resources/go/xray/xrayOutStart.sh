#!/usr/bin/env bash
# shellcheck shell=bash

set -euo pipefail

# ========== 配置区域 ==========
# 本地 Xray 压缩包路径（请根据实际修改）
LOCAL_XRAY_ZIP="/xray/Xray-linux-64.zip"
# ==============================

pkg_manager() {
    local OP="$1" PM=yum
    shift
    if command -v dnf &>/dev/null; then
        PM=dnf
    fi
    case "$OP" in
    add)
        OP='install -y'
        ;;
    del)
        OP='remove'
        ;;
    esac
    if [ $# -eq 0 ]; then
        echo "$PM $OP"
    else
        $PM "$OP" "$@"
    fi
}

check_distro() {
    if [ ! -f /etc/redhat-release ] && [ ! -f /etc/centos-release ] && [ ! -f /etc/fedora-release ]; then
        echo "warning: This script is designed for RHEL/CentOS/Fedora systems."
    fi
    if [ -z "$(command -v systemctl)" ]; then
        echo "error: No systemd detected. This script requires systemd."
        return 1
    fi
    return 0
}

check_if_running_as_root() {
    if [ "$(id -u)" -eq 0 ]; then
        return 0
    else
        echo "error: You must run this script as root!"
        return 1
    fi
}

identify_architecture() {
    if [ "$(uname)" != 'Linux' ]; then
        echo "error: This operating system is not supported."
        return 1
    fi
    case "$(uname -m)" in
    'i386' | 'i686')
        MACHINE='32'
        ;;
    'amd64' | 'x86_64')
        MACHINE='64'
        ;;
    'armv5tel')
        MACHINE='arm32-v5'
        ;;
    'armv6l')
        MACHINE='arm32-v6'
        grep Features /proc/cpuinfo | grep -qw 'vfp' || MACHINE='arm32-v5'
        ;;
    'armv7' | 'armv7l')
        MACHINE='arm32-v7a'
        grep Features /proc/cpuinfo | grep -qw 'vfp' || MACHINE='arm32-v5'
        ;;
    'armv8' | 'aarch64')
        MACHINE='arm64-v8a'
        ;;
    'mips')
        MACHINE='mips32'
        ;;
    'mipsle')
        MACHINE='mips32le'
        ;;
    'mips64')
        MACHINE='mips64'
        lscpu | grep -q "Little Endian" && MACHINE='mips64le'
        ;;
    'mips64le')
        MACHINE='mips64le'
        ;;
    'ppc64')
        MACHINE='ppc64'
        ;;
    'ppc64le')
        MACHINE='ppc64le'
        ;;
    'riscv64')
        MACHINE='riscv64'
        ;;
    's390x')
        MACHINE='s390x'
        ;;
    *)
        echo "error: The architecture is not supported."
        return 1
        ;;
    esac
    if [ ! -f '/etc/os-release' ]; then
        echo "error: Don't use outdated Linux distributions."
        return 1
    fi
}

install_dependencies() {
    local deps_needed=""

    if ! command -v unzip &>/dev/null; then
        deps_needed="$deps_needed unzip"
    fi
    if ! command -v curl &>/dev/null && ! command -v wget &>/dev/null; then
        deps_needed="$deps_needed curl"
    fi

    if [ -n "$deps_needed" ]; then
        echo "Installing required dependencies:$deps_needed"
        pkg_manager add $deps_needed
    fi
}

prepare_xray_files() {
    echo "Using local Xray files..."

    if [ ! -f "$LOCAL_XRAY_ZIP" ]; then
        echo "error: Local file not found: $LOCAL_XRAY_ZIP"
        exit 1
    fi

    cp "$LOCAL_XRAY_ZIP" "$ZIP_FILE"
    echo "Copied: $LOCAL_XRAY_ZIP -> $ZIP_FILE"

    if [ -f "${LOCAL_XRAY_ZIP}.dgst" ]; then
        cp "${LOCAL_XRAY_ZIP}.dgst" "$ZIP_FILE.dgst"
        echo "Found and copied checksum file."
    else
        echo "warning: No .dgst file found, skipping SHA256 verification."
    fi
}

verification_xray() {
    if [ ! -f "$ZIP_FILE.dgst" ]; then
        echo "info: Skipping SHA256 verification (no .dgst file)"
        return 0
    fi

    local CHECKSUM LOCALSUM
    CHECKSUM=$(awk -F '= ' '/256=/ {print $2}' "$ZIP_FILE.dgst")
    LOCALSUM=$(sha256sum "$ZIP_FILE" | awk '{printf $1}')
    if [ "$CHECKSUM" != "$LOCALSUM" ]; then
        echo 'error: SHA256 check failed! The local file may be corrupted.'
        return 1
    fi
    echo "SHA256 verification passed."
}

decompression() {
    unzip -q "$ZIP_FILE" -d "$TMP_DIRECTORY"
}

is_it_running() {
    XRAY_RUNNING='0'
    if systemctl is-active --quiet xray 2>/dev/null || pgrep xray &>/dev/null; then
        echo "Stopping existing Xray service..."
        systemctl stop xray 2>/dev/null || true
        XRAY_RUNNING='1'
    fi
}

install_xray() {
    install -m 755 "${TMP_DIRECTORY}xray" "/usr/local/bin/xray"
    install -d /usr/local/share/xray/
    install -m 644 "${TMP_DIRECTORY}geoip.dat" "/usr/local/share/xray/geoip.dat"
    install -m 644 "${TMP_DIRECTORY}geosite.dat" "/usr/local/share/xray/geosite.dat"
}

install_confdir() {
    CONFDIR='0'
    if [ ! -d '/usr/local/etc/xray/' ]; then
        install -d /usr/local/etc/xray/
        for BASE in 00_log 01_api 02_dns 03_routing 04_policy 05_inbounds 06_outbounds 07_transport 08_stats 09_reverse; do
            echo '{}' >"/usr/local/etc/xray/$BASE.json"
        done
        CONFDIR='1'
    fi
}

install_log() {
    LOG='0'
    if [ ! -d '/var/log/xray/' ]; then
        install -d -m 755 /var/log/xray/
        # CentOS 使用 root 用户运行，或者你可以创建 xray 用户
        install -m 644 /dev/null /var/log/xray/access.log
        install -m 644 /dev/null /var/log/xray/error.log
        LOG='1'
    else
        chmod 755 /var/log/xray/
        chmod 644 /var/log/xray/*.log 2>/dev/null || true
    fi
}

# CentOS 使用 systemd 服务文件
install_startup_service_file() {
    SYSTEMD='0'
    if [ ! -f '/etc/systemd/system/xray.service' ]; then
        echo "Creating systemd service file..."

        cat > /etc/systemd/system/xray.service << 'EOF'
[Unit]
Description=Xray Service
Documentation=https://github.com/xtls
After=network.target nss-lookup.target

[Service]
User=root
Group=root
ExecStart=/usr/local/bin/xray run -config /usr/local/etc/xray/config.json
Restart=on-failure
RestartPreventExitStatus=23
LimitNPROC=10000
LimitNOFILE=1000000

[Install]
WantedBy=multi-user.target
EOF

        chmod 644 /etc/systemd/system/xray.service
        systemctl daemon-reload
        SYSTEMD='1'
        echo "Created: /etc/systemd/system/xray.service"
    fi
}

information() {
    echo 'installed: /usr/local/bin/xray'
    echo 'installed: /usr/local/share/xray/geoip.dat'
    echo 'installed: /usr/local/share/xray/geosite.dat'
    if [ "$CONFDIR" -eq '1' ]; then
        echo 'installed: /usr/local/etc/xray/00_log.json'
        echo 'installed: /usr/local/etc/xray/01_api.json'
        echo 'installed: /usr/local/etc/xray/02_dns.json'
        echo 'installed: /usr/local/etc/xray/03_routing.json'
        echo 'installed: /usr/local/etc/xray/04_policy.json'
        echo 'installed: /usr/local/etc/xray/05_inbounds.json'
        echo 'installed: /usr/local/etc/xray/06_outbounds.json'
        echo 'installed: /usr/local/etc/xray/07_transport.json'
        echo 'installed: /usr/local/etc/xray/08_stats.json'
        echo 'installed: /usr/local/etc/xray/09_reverse.json'
    fi
    if [ "$LOG" -eq '1' ]; then
        echo 'installed: /var/log/xray/'
    fi
    if [ "$SYSTEMD" -eq '1' ]; then
        echo 'installed: /etc/systemd/system/xray.service'
    fi
    rm -r "$TMP_DIRECTORY"
    echo "removed: $TMP_DIRECTORY"
    echo "You may need to execute a command to remove dependent software: $(pkg_manager del) unzip curl"
    if [ "$XRAY_RUNNING" -eq '1' ]; then
        systemctl start xray
    else
        echo 'Please execute the command: systemctl enable xray; systemctl start xray'
    fi
    echo "info: Xray is installed."
}

main() {
    check_distro || return 1
    check_if_running_as_root || return 1
    identify_architecture || return 1
    install_dependencies

    TMP_DIRECTORY="$(mktemp -d)/"
    ZIP_FILE="${TMP_DIRECTORY}Xray-linux-${MACHINE}.zip"

    prepare_xray_files
    verification_xray
    decompression
    is_it_running
    install_xray
    install_confdir
    install_log
    install_startup_service_file || return 1
    information
}

main