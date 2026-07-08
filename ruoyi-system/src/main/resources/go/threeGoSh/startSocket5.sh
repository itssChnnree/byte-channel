#!/bin/bash
# ============================================================
# Dante SOCKS5 全量一键脚本 (CentOS 禁用版)
# 用法： bash startSocket5.sh [-p PORT] [-u USER] [-pw PASSWORD] [-h]
# 特点：系统认证（无 userlist），兼容 Ubuntu/Debian/CentOS，防锁防火墙
# 修改：CentOS 系统直接拒绝执行
# ============================================================
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

PKG_MANAGER=""
FW_TYPE="none"
FW_PORT_OK=false
BBR_ENABLED=false
DANTE_BIN=""
API_URL="https://api.ganguo168.com/socks5Resources/insert"
QUERY_BASE_URL="https://www.ganguo168.com/#/query-config/socks5"
PASSWORD=""

# ===================== 系统识别 =====================
detect_pkg_manager() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        case "$ID" in
            centos)
                echo -e "${RED}[ERR]${NC} 当前系统为 CentOS，本脚本不支持 CentOS 系统，请使用其他操作系统。"
                exit 1
                ;;
            ubuntu|debian) PKG_MANAGER="apt" ;;
            rhel|fedora|rocky|almalinux) PKG_MANAGER="yum" ;;
            *) 
                if command -v apt-get &> /dev/null; then PKG_MANAGER="apt"
                elif command -v dnf &> /dev/null; then PKG_MANAGER="dnf"
                elif command -v yum &> /dev/null; then PKG_MANAGER="yum"
                else
                    echo -e "${RED}[ERR]${NC} 无法识别包管理器"
                    exit 1
                fi
                ;;
        esac
    else
        if command -v apt-get &> /dev/null; then PKG_MANAGER="apt"
        elif command -v yum &> /dev/null; then PKG_MANAGER="yum"
        else
            echo -e "${RED}[ERR]${NC} 不支持的操作系统"
            exit 1
        fi
    fi
    echo -e "${BLUE}[INFO]${NC} 包管理器: $PKG_MANAGER"
}

install_if_missing() {
    local cmd="$1"
    local pkg="$2"
    if command -v "$cmd" &> /dev/null; then return 0; fi
    echo -e "${BLUE}[INFO]${NC} 安装 $pkg ..."
    case "$PKG_MANAGER" in
        apt)
            apt-get update -qq
            DEBIAN_FRONTEND=noninteractive apt-get install -y -qq "$pkg"
            ;;
        dnf|yum)
            timeout 60 $PKG_MANAGER install -y -q "$pkg" 2>/dev/null || true
            ;;
    esac
}

# ===================== BBR =====================
enable_bbr() {
    echo -e "${CYAN}[BBR]${NC} 检查并尝试开启 BBR ..."
    local kernel_major=$(uname -r | cut -d. -f1)
    local kernel_minor=$(uname -r | cut -d. -f2)
    if [ "$kernel_major" -lt 4 ] || { [ "$kernel_major" -eq 4 ] && [ "$kernel_minor" -lt 9 ]; }; then
        echo -e "${YELLOW}[WARN]${NC} 内核 < 4.9，不支持 BBR"
        BBR_ENABLED=false
        return
    fi

    local cc=$(sysctl -n net.ipv4.tcp_congestion_control 2>/dev/null || echo "")
    if [ "$cc" = "bbr" ]; then
        echo -e "${GREEN}[OK]${NC} BBR 已启用"
        BBR_ENABLED=true
        return
    fi

    modprobe tcp_bbr 2>/dev/null || true
    local sysctl_file="/etc/sysctl.d/99-bbr.conf"
    [ ! -d /etc/sysctl.d ] && sysctl_file="/etc/sysctl.conf"
    if ! grep -q "tcp_congestion_control.*=.*bbr" "$sysctl_file" 2>/dev/null; then
        cat >> "$sysctl_file" << 'EOF'

net.core.default_qdisc = fq
net.ipv4.tcp_congestion_control = bbr
EOF
    fi
    sysctl -p "$sysctl_file" > /dev/null 2>&1 || sysctl -p > /dev/null 2>&1 || true
    cc=$(sysctl -n net.ipv4.tcp_congestion_control 2>/dev/null || echo "")
    if [ "$cc" = "bbr" ]; then
        echo -e "${GREEN}[OK]${NC} BBR 已开启"
        BBR_ENABLED=true
    else
        echo -e "${YELLOW}[WARN]${NC} BBR 开启失败，当前: $cc"
    fi
}

# ===================== 防火墙 =====================
detect_firewall() {
    if command -v ufw &> /dev/null && ufw status 2>/dev/null | grep -q "Status: active"; then
        FW_TYPE="ufw"
    elif command -v firewalld &> /dev/null && systemctl is-active --quiet firewalld 2>/dev/null; then
        FW_TYPE="firewalld"
    elif command -v firewall-cmd &> /dev/null && firewall-cmd --state 2>/dev/null | grep -q "running"; then
        FW_TYPE="firewalld"
    elif command -v iptables &> /dev/null; then
        FW_TYPE="iptables"
    else
        FW_TYPE="none"
    fi
    echo -e "${BLUE}[INFO]${NC} 防火墙类型: $FW_TYPE"
}

close_old_port() {
    local port="$1"
    echo -e "${BLUE}[INFO]${NC} 关闭旧端口 $port ..."
    case "$FW_TYPE" in
        ufw) ufw delete allow "$port"/tcp 2>/dev/null; ufw reload 2>/dev/null || true ;;
        firewalld) firewall-cmd --permanent --remove-port="${port}/tcp" 2>/dev/null; firewall-cmd --reload 2>/dev/null || true ;;
        iptables) iptables -D INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null || true ;;
    esac
    sleep 1
}

open_port() {
    local port="$1"
    echo -e "${BLUE}[INFO]${NC} 开放端口 $port ..."
    case "$FW_TYPE" in
        ufw) ufw allow "$port"/tcp 2>/dev/null; ufw reload 2>/dev/null || true ;;
        firewalld) firewall-cmd --permanent --add-port="${port}/tcp" 2>/dev/null; firewall-cmd --reload 2>/dev/null || true ;;
        iptables) iptables -I INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null || true ;;
        none) echo -e "${YELLOW}[WARN]${NC} 未检测到防火墙，请手动开放端口 $port"; FW_PORT_OK=true; return 0 ;;
    esac
    sleep 1
    if [ "$FW_TYPE" = "ufw" ]; then
        ufw status 2>/dev/null | grep -qw "$port" && FW_PORT_OK=true || FW_PORT_OK=false
    elif [ "$FW_TYPE" = "firewalld" ]; then
        firewall-cmd --list-ports 2>/dev/null | grep -qw "${port}/tcp" && FW_PORT_OK=true || FW_PORT_OK=false
    elif [ "$FW_TYPE" = "iptables" ]; then
        iptables -C INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null && FW_PORT_OK=true || FW_PORT_OK=false
    fi
    [ "$FW_PORT_OK" = true ] && echo -e "${GREEN}[OK]${NC} 端口 $port 已开放" || echo -e "${YELLOW}[WARN]${NC} 端口 $port 开放验证失败"
}

get_old_port() {
    [ -f /etc/danted.conf ] && grep -oP 'internal:\s+[\d.]+ port = \K\d+' /etc/danted.conf 2>/dev/null | head -1
}

configure_firewall() {
    detect_firewall
    local old_port=$(get_old_port)
    [ -n "$old_port" ] && [ "$old_port" != "$SOCKS_PORT" ] && close_old_port "$old_port"
    open_port "$SOCKS_PORT"
}

# ===================== Dante 安装 (CentOS 网络降级) =====================
install_dante() {
    echo -e "${CYAN}[Dante]${NC} 安装/检查 Dante ..."

    # 查找已有二进制
    for bin in sockd danted; do
        if command -v $bin &> /dev/null; then
            DANTE_BIN="$bin"
            echo -e "${GREEN}[OK]${NC} 检测到 $bin"
            return
        fi
    done
    for path in /usr/sbin/sockd /usr/bin/sockd /usr/local/sbin/sockd /usr/local/bin/sockd \
                /usr/sbin/danted /usr/bin/danted /usr/local/sbin/danted /usr/local/bin/danted; do
        [ -f "$path" ] && { DANTE_BIN=$(basename "$path"); export PATH="$PATH:$(dirname "$path")"; echo -e "${GREEN}[OK]${NC} 找到 $path"; return; }
    done
    local found=$(find /usr -type f \( -name sockd -o -name danted \) 2>/dev/null | head -1)
    [ -n "$found" ] && { DANTE_BIN=$(basename "$found"); export PATH="$PATH:$(dirname "$found")"; echo -e "${GREEN}[OK]${NC} 找到 $found"; return; }

    echo -e "${YELLOW}[WARN]${NC} 未找到 sockd/danted，开始安装..."

    case "$PKG_MANAGER" in
        apt)
            apt-get update -qq
            DEBIAN_FRONTEND=noninteractive apt-get install --reinstall -y dante-server || true
            ;;
        dnf|yum)
            # 尝试通过包管理器安装（设置超时）
            echo -e "${BLUE}[INFO]${NC} 尝试通过 $PKG_MANAGER 安装 dante-server..."
            if ! timeout 30 $PKG_MANAGER install -y dante-server 2>/dev/null; then
                echo -e "${YELLOW}[WARN]${NC} 包管理器安装失败，切换至 RPM 包直装..."

                # 自动下载 EPEL 和 dante-server + miniupnpc
                install_rpm_pkg() {
                    local url="$1"
                    local name="$2"
                    local tmp="/tmp/${name}.rpm"
                    curl -fsSL --connect-timeout 10 --max-time 30 "$url" -o "$tmp" || return 1
                    rpm -ivh --nodeps "$tmp" || return 1
                    rm -f "$tmp"
                }

                # 根据 CentOS 版本选择 EPEL URL（9/8）
                local epel_url=""
                if grep -q 'release 9' /etc/redhat-release 2>/dev/null; then
                    epel_url="https://dl.fedoraproject.org/pub/epel/9/Everything/x86_64/Packages"
                else
                    epel_url="https://dl.fedoraproject.org/pub/epel/8/Everything/x86_64/Packages"
                fi

                # 安装 EPEL 标识（非必须，但可提供仓库元数据）
                install_rpm_pkg "$epel_url/e/epel-release-9-2.el9.noarch.rpm" "epel-release" 2>/dev/null || true

                # 下载并安装 dante-server (自动抓取最新版本)
                local dante_rpm_url=$(curl -s "$epel_url/d/" | grep -oP 'href="dante-server-[^"]+\.x86_64\.rpm"' | tail -1 | sed 's/href="//;s/"//')
                [ -z "$dante_rpm_url" ] && { echo -e "${RED}[ERR]${NC} 获取 dante-server RPM 失败"; exit 1; }
                echo -e "${BLUE}[INFO]${NC} 下载 dante-server: $dante_rpm_url"
                install_rpm_pkg "$epel_url/d/$dante_rpm_url" "dante-server" || { echo -e "${RED}[ERR]${NC} dante-server RPM 安装失败"; exit 1; }

                # 下载并安装 miniupnpc（防止运行缺少 .so 文件）
                local miniupnpc_rpm_url=$(curl -s "$epel_url/m/" | grep -oP 'href="miniupnpc-[0-9][^"]+\.x86_64\.rpm"' | grep -v devel | tail -1 | sed 's/href="//;s/"//')
                if [ -n "$miniupnpc_rpm_url" ]; then
                    echo -e "${BLUE}[INFO]${NC} 下载 miniupnpc: $miniupnpc_rpm_url"
                    install_rpm_pkg "$epel_url/m/$miniupnpc_rpm_url" "miniupnpc" || true
                fi

                ldconfig
            fi
            ;;
    esac

    # 再次查找二进制
    for bin in sockd danted; do command -v $bin &> /dev/null && { DANTE_BIN="$bin"; echo -e "${GREEN}[OK]${NC} 安装后找到 $bin"; return; }; done
    found=$(find /usr -type f \( -name sockd -o -name danted \) 2>/dev/null | head -1)
    [ -n "$found" ] && { DANTE_BIN=$(basename "$found"); export PATH="$PATH:$(dirname "$found")"; echo -e "${GREEN}[OK]${NC} 找到 $found"; return; }

    echo -e "${RED}[ERR]${NC} 安装失败，无法找到 sockd/danted"
    exit 1
}

# ===================== PAM 认证配置 =====================
ensure_pam_config() {
    echo -e "${BLUE}[INFO]${NC} 检查并创建 PAM 认证配置..."
    for pamfile in sockd danted; do
        if [ ! -f /etc/pam.d/$pamfile ]; then
            cat > /etc/pam.d/$pamfile << 'EOF'
auth    required    pam_unix.so
account required    pam_unix.so
EOF
            echo -e "${GREEN}[OK]${NC} 已创建 /etc/pam.d/$pamfile"
        else
            echo -e "${GREEN}[OK]${NC} /etc/pam.d/$pamfile 已存在"
        fi
    done
}

# ===================== 配置 (系统用户认证) =====================
write_config() {
    mkdir -p /etc/danted

    # 用户名保护：禁止使用 root
    if [ "$SOCKS_USER" = "root" ]; then
        echo -e "${RED}[ERR]${NC} 严禁使用 root 作为 SOCKS 用户名，会破坏系统认证！已自动更改为随机用户。"
        SOCKS_USER=""
    fi

    [ -z "$SOCKS_USER" ] && SOCKS_USER="user$(tr -dc 'a-z0-9' < /dev/urandom | head -c 6)"
    [ -z "$SOCKS_PASS" ] && SOCKS_PASS="$(tr -dc 'A-Za-z0-9' < /dev/urandom | head -c 16)"

    # 创建系统用户
    useradd -r -s /bin/false "$SOCKS_USER" 2>/dev/null || true
    echo "${SOCKS_USER}:${SOCKS_PASS}" | chpasswd

    # 获取外部 IP，失败则改用默认路由接口名
    EXTERNAL_IP=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || \
                  curl -s --connect-timeout 5 ip.sb 2>/dev/null || \
                  curl -s --connect-timeout 5 icanhazip.com 2>/dev/null || \
                  curl -s --connect-timeout 5 api.ipify.org 2>/dev/null || echo "")
    if [ -z "$EXTERNAL_IP" ] || [ "$EXTERNAL_IP" = "0.0.0.0" ]; then
        # 获取默认网卡接口名
        EXTERNAL_IP=$(ip route get 8.8.8.8 | awk '{print $5; exit}')
        [ -z "$EXTERNAL_IP" ] && EXTERNAL_IP="eth0"
        echo -e "${YELLOW}[WARN]${NC} 无法获取外网 IP，使用接口名: $EXTERNAL_IP"
    fi

    cat > /etc/danted.conf << EOF
logoutput: syslog
internal: 0.0.0.0 port = ${SOCKS_PORT}
external: ${EXTERNAL_IP}
socksmethod: username
user.privileged: root
user.unprivileged: nobody
user.libwrap: nobody
client pass {
    from: 0.0.0.0/0 to: 0.0.0.0/0
    log: connect disconnect error
}
socks pass {
    from: 0.0.0.0/0 to: 0.0.0.0/0
    log: connect disconnect error
    socksmethod: username
}
EOF
    echo -e "${GREEN}[OK]${NC} 配置文件已写入"

    ensure_pam_config
}

restart_dante() {
    echo -e "${CYAN}[Dante]${NC} 启动/重启 Dante ($DANTE_BIN)..."
    # 尝试 systemd
    if systemctl is-active --quiet danted 2>/dev/null; then
        systemctl restart danted
    elif systemctl is-active --quiet sockd 2>/dev/null; then
        systemctl restart sockd
    else
        systemctl start danted 2>/dev/null || systemctl start sockd 2>/dev/null || {
            echo -e "${YELLOW}[WARN]${NC} systemd 启动失败，直接运行 $DANTE_BIN"
            pkill "$DANTE_BIN" 2>/dev/null || true
            # 前台验证启动（快速失败）
            $DANTE_BIN -f /etc/danted.conf -d 1 &
            sleep 2
            if ! pgrep -f "$DANTE_BIN" > /dev/null; then
                # 查看最后一次错误
                journalctl -xe --no-pager | grep -i "$DANTE_BIN" | tail -5 || true
                echo -e "${RED}[ERR]${NC} Dante 启动失败，请检查外网 IP 配置（/etc/danted.conf）或手动运行 sockd -d 1 排查"
                exit 1
            fi
            kill %1 2>/dev/null || true
            # 后台正式启动
            nohup "$DANTE_BIN" -f /etc/danted.conf > /var/log/danted.log 2>&1 &
            sleep 1
            pgrep -f "$DANTE_BIN" > /dev/null && echo -e "${GREEN}[OK]${NC} Dante 进程已启动" || { echo -e "${RED}[ERR]${NC} 守护进程启动失败"; exit 1; }
        }
    fi
    systemctl enable danted 2>/dev/null || systemctl enable sockd 2>/dev/null || true
    echo -e "${GREEN}[OK]${NC} Dante 已启动并设为开机自启"
}

# ===================== 输出和上报 =====================
print_result() {
    local ip=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || \
               curl -s --connect-timeout 5 ip.sb 2>/dev/null || \
               curl -s --connect-timeout 5 icanhazip.com 2>/dev/null || \
               curl -s --connect-timeout 5 api.ipify.org 2>/dev/null || echo "未知")
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║       Dante SOCKS5 全量部署完成            ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "  服务器 IP : ${CYAN}${ip}${NC}"
    echo -e "  端口      : ${CYAN}${SOCKS_PORT}${NC}"
    echo -e "  用户名    : ${CYAN}${SOCKS_USER}${NC}"
    echo -e "  密码      : ${CYAN}${SOCKS_PASS}${NC}"
    echo -e "  认证方式  : 用户名/密码 (系统用户)"
    echo -e "  防火墙    : ${FW_TYPE}"
    [ -n "$PASSWORD" ] && echo -e "  查询链接  : ${CYAN}${QUERY_BASE_URL}/${PASSWORD}${NC}"
    echo ""
}

upload_config() {
    local ip=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || \
               curl -s --connect-timeout 5 ip.sb 2>/dev/null || \
               curl -s --connect-timeout 5 icanhazip.com 2>/dev/null || \
               curl -s --connect-timeout 5 api.ipify.org 2>/dev/null || echo "0.0.0.0")
    echo -e "${BLUE}[INFO]${NC} 上报配置到 API..."
    local request_body="{\"resourcesIp\":\"${ip}\",\"socks5Port\":\"${SOCKS_PORT}\",\"socks5UserName\":\"${SOCKS_USER}\",\"socks5Password\":\"${SOCKS_PASS}\"}"
    local response=$(curl -s --connect-timeout 10 --max-time 30 -X POST -H "Content-Type: application/json" -d "$request_body" "$API_URL" 2>&1) || {
        echo -e "${RED}[ERR]${NC} API 请求失败"; return 1
    }
    local code=$(echo "$response" | grep -o '"code"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
    if [ "$code" != "200" ]; then
        local err_msg=$(echo "$response" | grep -o '"msg"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"msg"[[:space:]]*:[[:space:]]*"//;s/"$//')
        echo -e "${RED}[ERR]${NC} API 错误 (code=$code): $err_msg"; return 1
    fi
    PASSWORD=$(echo "$response" | grep -o '"message"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"message"[[:space:]]*:[[:space:]]*"//;s/"$//')
    [ -z "$PASSWORD" ] && { echo -e "${RED}[ERR]${NC} 解析密码失败"; return 1; }
    echo -e "${GREEN}[OK]${NC} 上报成功: ${QUERY_BASE_URL}/${PASSWORD}"
    return 0
}

usage() {
    echo "用法: $0 [-p PORT] [-u USER] [-pw PASSWORD] [-h]"
    exit 0
}

main() {
    SOCKS_PORT=""; SOCKS_USER=""; SOCKS_PASS=""
    while [[ $# -gt 0 ]]; do
        case "$1" in
            -p) SOCKS_PORT="$2"; shift 2 ;;
            -u) SOCKS_USER="$2"; shift 2 ;;
            -pw) SOCKS_PASS="$2"; shift 2 ;;
            -h|--help) usage ;;
            *) echo -e "${RED}[ERR]${NC} 未知参数: $1"; usage ;;
        esac
    done

    [ -z "$SOCKS_PORT" ] && SOCKS_PORT=$(( RANDOM % 55536 + 10000 ))
    [[ "$SOCKS_PORT" =~ ^[0-9]+$ ]] && [ "$SOCKS_PORT" -ge 1 ] && [ "$SOCKS_PORT" -le 65535 ] || { echo -e "${RED}[ERR]${NC} 端口号非法"; exit 1; }

    echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║        Dante SOCKS5 全量一键脚本           ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
    echo ""

    detect_pkg_manager
    install_if_missing "curl" "curl"
    install_if_missing "ss" "iproute2"

    enable_bbr
    configure_firewall
    install_dante
    write_config
    restart_dante
    upload_config || true
    print_result
}

main "$@"
