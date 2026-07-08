#!/bin/bash
# ============================================================
# Xray SOCKS5 全量一键脚本 (基于原 Dante 脚本改造)
# 用法： bash startSocket5.sh [-p PORT] [-u USER] [-pw PASSWORD] [-h]
# 特点：内置账户认证，兼容 Ubuntu/Debian/CentOS，自动防火墙
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
API_URL="https://api.ganguo168.com/socks5Resources/insert"
QUERY_BASE_URL="https://www.ganguo168.com/#/query-config/socks5"
PASSWORD=""

# ===================== 系统识别 =====================
detect_pkg_manager() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        case "$ID" in
            ubuntu|debian) PKG_MANAGER="apt" ;;
            centos|rhel|fedora|rocky|almalinux) PKG_MANAGER="yum" ;;
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
    # 尝试从旧 Xray 配置中提取端口（如果存在）
    if [ -f /usr/local/etc/xray/config.json ]; then
        grep -oP '"port"\s*:\s*\K\d+' /usr/local/etc/xray/config.json 2>/dev/null | head -1
    fi
}

configure_firewall() {
    detect_firewall
    local old_port=$(get_old_port)
    [ -n "$old_port" ] && [ "$old_port" != "$SOCKS_PORT" ] && close_old_port "$old_port"
    open_port "$SOCKS_PORT"
}

# ===================== Xray 安装 =====================
install_xray() {
    echo -e "${CYAN}[Xray]${NC} 安装/检查 Xray ..."
    if command -v xray &> /dev/null; then
        echo -e "${GREEN}[OK]${NC} 检测到 xray"
        return
    fi

    echo -e "${YELLOW}[WARN]${NC} 未找到 xray，通过官方脚本安装..."
    # 使用官方安装脚本（自动识别系统架构）
    if ! bash -c "$(curl -L https://github.com/XTLS/Xray-install/raw/main/install-release.sh)" @ install; then
        echo -e "${RED}[ERR]${NC} Xray 安装失败，请检查网络后重试"
        exit 1
    fi

    if ! command -v xray &> /dev/null; then
        echo -e "${RED}[ERR]${NC} 安装后仍找不到 xray 命令"
        exit 1
    fi
    echo -e "${GREEN}[OK]${NC} Xray 安装完成"
}

# ===================== 生成 Xray 配置 =====================
write_config() {
    mkdir -p /usr/local/etc/xray

    # 用户名保护：禁止使用 root
    if [ "$SOCKS_USER" = "root" ]; then
        echo -e "${RED}[ERR]${NC} 严禁使用 root 作为 SOCKS 用户名，会破坏系统认证！已自动更改为随机用户。"
        SOCKS_USER=""
    fi

    [ -z "$SOCKS_USER" ] && SOCKS_USER="user$(tr -dc 'a-z0-9' < /dev/urandom | head -c 6)"
    [ -z "$SOCKS_PASS" ] && SOCKS_PASS="$(tr -dc 'A-Za-z0-9' < /dev/urandom | head -c 16)"

    # 生成 Xray 配置文件（带认证的 SOCKS5 入站）
    cat > /usr/local/etc/xray/config.json << EOF
{
  "inbounds": [
    {
      "port": ${SOCKS_PORT},
      "protocol": "socks",
      "settings": {
        "auth": "password",
        "accounts": [
          {
            "user": "${SOCKS_USER}",
            "pass": "${SOCKS_PASS}"
          }
        ],
        "udp": true
      }
    }
  ],
  "outbounds": [
    {
      "protocol": "freedom",
      "settings": {}
    }
  ]
}
EOF
    echo -e "${GREEN}[OK]${NC} Xray 配置文件已写入"
}

restart_xray() {
    echo -e "${CYAN}[Xray]${NC} 启动/重启 Xray 服务..."
    systemctl restart xray || {
        echo -e "${YELLOW}[WARN]${NC} systemd 重启失败，尝试手动启动 xray..."
        pkill xray 2>/dev/null || true
        nohup /usr/local/bin/xray run -config /usr/local/etc/xray/config.json > /var/log/xray.log 2>&1 &
        sleep 2
        if pgrep -f "xray" > /dev/null; then
            echo -e "${GREEN}[OK]${NC} Xray 进程已启动"
        else
            echo -e "${RED}[ERR]${NC} Xray 启动失败，请检查配置"
            exit 1
        fi
    }
    systemctl enable xray 2>/dev/null || true
    echo -e "${GREEN}[OK]${NC} Xray 已启动并设为开机自启"
}

# ===================== 输出和上报 =====================
print_result() {
    local ip=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || \
               curl -s --connect-timeout 5 ip.sb 2>/dev/null || \
               curl -s --connect-timeout 5 icanhazip.com 2>/dev/null || \
               curl -s --connect-timeout 5 api.ipify.org 2>/dev/null || echo "未知")
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║       Xray SOCKS5 全量部署完成             ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "  服务器 IP : ${CYAN}${ip}${NC}"
    echo -e "  端口      : ${CYAN}${SOCKS_PORT}${NC}"
    echo -e "  用户名    : ${CYAN}${SOCKS_USER}${NC}"
    echo -e "  密码      : ${CYAN}${SOCKS_PASS}${NC}"
    echo -e "  认证方式  : 用户名/密码 (内置)"
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
    echo -e "${BLUE}║        Xray SOCKS5 全量一键脚本            ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
    echo ""

    detect_pkg_manager
    install_if_missing "curl" "curl"
    install_if_missing "ss" "iproute2"

    enable_bbr
    configure_firewall
    install_xray
    write_config
    restart_xray
    upload_config || true
    print_result
}

main "$@"
