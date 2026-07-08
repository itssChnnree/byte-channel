#!/bin/bash
# ============================================================
# Dante SOCKS5 全量一键脚本 (修复版)
# 用法： bash startSocket5.sh [-p PORT] [-u USER] [-pw PASSWORD] [-h]
# 特点：系统认证（无 userlist），兼容 Ubuntu/Debian/CentOS，防锁防火墙
# 修复：CentOS9 自动启用EPEL；强制创建PAM文件防CPU爆满
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
            ubuntu|debian) PKG_MANAGER="apt" ;;
            centos|rhel|fedora) PKG_MANAGER="yum" ;;
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
            # 添加超时，防止网络问题永久挂起
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
        echo -e "${YELLOW}[WARN]${NC} 内核 $(uname -r) < 4.9，不支持 BBR"
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
    if [ ! -d /etc/sysctl.d ]; then sysctl_file="/etc/sysctl.conf"; fi

    if ! grep -q "tcp_congestion_control.*=.*bbr" "$sysctl_file" 2>/dev/null; then
        cat >> "$sysctl_file" << 'EOF'

net.core.default_qdisc = fq
net.ipv4.tcp_congestion_control = bbr
EOF
    fi
    sysctl -p "$sysctl_file" > /dev/null 2>&1 || sysctl -p > /dev/null 2>&1 || true

    cc=$(sysctl -n net.ipv4.tcp_congestion_control 2>/dev/null || echo "")
    if [ "$cc" = "bbr" ]; then
        echo -e "${GREEN}[OK]${NC} BBR 已成功开启"
        BBR_ENABLED=true
    else
        echo -e "${YELLOW}[WARN]${NC} BBR 开启失败，当前: $cc"
        BBR_ENABLED=false
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
        ufw)
            ufw delete allow "$port"/tcp 2>/dev/null || true
            ufw reload 2>/dev/null || true
            ;;
        firewalld)
            firewall-cmd --permanent --remove-port="${port}/tcp" 2>/dev/null || true
            firewall-cmd --reload 2>/dev/null || true
            ;;
        iptables)
            iptables -D INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null || true
            ;;
    esac
    sleep 1
}

open_port() {
    local port="$1"
    echo -e "${BLUE}[INFO]${NC} 开放端口 $port ..."
    case "$FW_TYPE" in
        ufw)
            ufw allow "$port"/tcp 2>/dev/null
            ufw reload 2>/dev/null || true
            ;;
        firewalld)
            firewall-cmd --permanent --add-port="${port}/tcp" 2>/dev/null
            firewall-cmd --reload 2>/dev/null || true
            ;;
        iptables)
            iptables -I INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null || true
            ;;
        none)
            echo -e "${YELLOW}[WARN]${NC} 未检测到防火墙，请手动开放端口 $port"
            FW_PORT_OK=true
            return 0
            ;;
    esac
    sleep 1
    if [ "$FW_TYPE" = "ufw" ]; then
        ufw status 2>/dev/null | grep -qw "$port" && FW_PORT_OK=true || FW_PORT_OK=false
    elif [ "$FW_TYPE" = "firewalld" ]; then
        firewall-cmd --list-ports 2>/dev/null | grep -qw "${port}/tcp" && FW_PORT_OK=true || FW_PORT_OK=false
    elif [ "$FW_TYPE" = "iptables" ]; then
        iptables -C INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null && FW_PORT_OK=true || FW_PORT_OK=false
    fi
    if [ "$FW_PORT_OK" = true ]; then
        echo -e "${GREEN}[OK]${NC} 端口 $port 已开放"
    else
        echo -e "${YELLOW}[WARN]${NC} 端口 $port 开放验证失败，请手动检查"
    fi
}

get_old_port() {
    if [ -f /etc/danted.conf ]; then
        grep -oP 'internal:\s+[\d.]+ port = \K\d+' /etc/danted.conf 2>/dev/null | head -1
    fi
}

configure_firewall() {
    detect_firewall
    local old_port=$(get_old_port)
    if [ -n "$old_port" ] && [ "$old_port" != "$SOCKS_PORT" ]; then
        close_old_port "$old_port"
    fi
    open_port "$SOCKS_PORT"
}

# ===================== Dante 安装 =====================
install_dante() {
    echo -e "${CYAN}[Dante]${NC} 安装/检查 Dante ..."

    # 优先查找 sockd / danted
    if command -v sockd &> /dev/null; then
        DANTE_BIN="sockd"
        echo -e "${GREEN}[OK]${NC} 检测到 sockd"
        return
    fi
    if command -v danted &> /dev/null; then
        DANTE_BIN="danted"
        echo -e "${GREEN}[OK]${NC} 检测到 danted"
        return
    fi

    for path in /usr/sbin/sockd /usr/bin/sockd /usr/local/sbin/sockd /usr/local/bin/sockd \
                /usr/sbin/danted /usr/bin/danted /usr/local/sbin/danted /usr/local/bin/danted; do
        if [ -f "$path" ]; then
            DANTE_BIN="$(basename "$path")"
            export PATH="$PATH:$(dirname "$path")"
            echo -e "${GREEN}[OK]${NC} 找到二进制: $path"
            return
        fi
    done

    local found=$(find /usr -type f \( -name sockd -o -name danted \) 2>/dev/null | head -1)
    if [ -n "$found" ]; then
        DANTE_BIN="$(basename "$found")"
        export PATH="$PATH:$(dirname "$found")"
        echo -e "${GREEN}[OK]${NC} 全局搜索找到: $found"
        return
    fi

    echo -e "${YELLOW}[WARN]${NC} 未找到 sockd/danted，尝试安装 dante-server..."

    # ---------- 修复：自动启用 EPEL (CentOS/RHEL 8+) ----------
    case "$PKG_MANAGER" in
        dnf|yum)
            # 检查并安装 EPEL（CentOS 9 默认没有 dante-server）
            if ! rpm -q epel-release &>/dev/null; then
                echo -e "${BLUE}[INFO]${NC} 正在安装 EPEL 仓库..."
                timeout 60 $PKG_MANAGER install -y epel-release || {
                    echo -e "${RED}[ERR]${NC} EPEL 安装失败，请手动安装后重试"
                    exit 1
                }
            fi
            ;;
    esac
    # ---------------------------------------------------------

    case "$PKG_MANAGER" in
        apt)
            apt-get update -qq
            DEBIAN_FRONTEND=noninteractive apt-get install --reinstall -y dante-server || true
            ;;
        dnf|yum)
            # 增加超时，避免永久卡死
            timeout 120 $PKG_MANAGER install -y dante-server || {
                echo -e "${RED}[ERR]${NC} dante-server 安装失败，请检查网络或手动安装"
                exit 1
            }
            ;;
    esac

    if command -v sockd &> /dev/null; then DANTE_BIN="sockd"; echo -e "${GREEN}[OK]${NC} 安装后找到 sockd"; return; fi
    if command -v danted &> /dev/null; then DANTE_BIN="danted"; echo -e "${GREEN}[OK]${NC} 安装后找到 danted"; return; fi
    found=$(find /usr -type f \( -name sockd -o -name danted \) 2>/dev/null | head -1)
    if [ -n "$found" ]; then
        DANTE_BIN="$(basename "$found")"
        export PATH="$PATH:$(dirname "$found")"
        echo -e "${GREEN}[OK]${NC} 安装后全局搜索找到: $found"
        return
    fi

    echo -e "${RED}[ERR]${NC} 无法安装或找到 sockd/danted"
    echo "  请手动执行: apt-get install --reinstall dante-server"
    exit 1
}

# ===================== PAM 认证配置强制创建 =====================
ensure_pam_config() {
    echo -e "${BLUE}[INFO]${NC} 检查并创建 PAM 认证配置..."
    # 这两个文件缺失会导致 Dante 认证疯狂重试，CPU 100%
    if [ ! -f /etc/pam.d/sockd ]; then
        cat > /etc/pam.d/sockd << 'EOF'
auth    required    pam_unix.so
account required    pam_unix.so
EOF
        echo -e "${GREEN}[OK]${NC} 已创建 /etc/pam.d/sockd"
    else
        echo -e "${GREEN}[OK]${NC} /etc/pam.d/sockd 已存在"
    fi

    if [ ! -f /etc/pam.d/danted ]; then
        cat > /etc/pam.d/danted << 'EOF'
auth    required    pam_unix.so
account required    pam_unix.so
EOF
        echo -e "${GREEN}[OK]${NC} 已创建 /etc/pam.d/danted"
    else
        echo -e "${GREEN}[OK]${NC} /etc/pam.d/danted 已存在"
    fi
}

# ===================== 配置 (使用系统用户认证) =====================
write_config() {
    mkdir -p /etc/danted
    if [ -z "$SOCKS_USER" ]; then
        SOCKS_USER="user$(tr -dc 'a-z0-9' < /dev/urandom | head -c 6)"
    fi
    if [ -z "$SOCKS_PASS" ]; then
        SOCKS_PASS="$(tr -dc 'A-Za-z0-9' < /dev/urandom | head -c 16)"
    fi

    # 创建系统用户（隐藏，不可登录）
    useradd -r -s /bin/false "$SOCKS_USER" 2>/dev/null || true
    echo "${SOCKS_USER}:${SOCKS_PASS}" | chpasswd

    EXTERNAL_IP=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || \
                  curl -s --connect-timeout 5 ip.sb 2>/dev/null || \
                  curl -s --connect-timeout 5 icanhazip.com 2>/dev/null || \
                  curl -s --connect-timeout 5 api.ipify.org 2>/dev/null || echo "0.0.0.0")

    # 配置无 userlist，直接使用 PAM 系统认证
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
    echo -e "${GREEN}[OK]${NC} 配置文件已写入 (系统用户认证)"

    # 确保 PAM 配置存在，防止 CPU 爆满
    ensure_pam_config
}

restart_dante() {
    echo -e "${CYAN}[Dante]${NC} 启动/重启 Dante ($DANTE_BIN)..."
    if systemctl is-active --quiet danted 2>/dev/null; then
        systemctl restart danted
    elif systemctl is-active --quiet sockd 2>/dev/null; then
        systemctl restart sockd
    else
        systemctl start danted 2>/dev/null || systemctl start sockd 2>/dev/null || {
            echo -e "${YELLOW}[WARN]${NC} systemd 启动失败，尝试直接运行 $DANTE_BIN ..."
            pkill "$DANTE_BIN" 2>/dev/null || true
            nohup "$DANTE_BIN" -f /etc/danted.conf > /var/log/danted.log 2>&1 &
            sleep 2
            if pgrep -f "$DANTE_BIN" > /dev/null; then
                echo -e "${GREEN}[OK]${NC} Dante 进程已启动 ($DANTE_BIN)"
                return
            fi
            echo -e "${RED}[ERR]${NC} Dante 启动失败，查看 /var/log/danted.log"
            exit 1
        }
    fi
    systemctl enable danted 2>/dev/null || systemctl enable sockd 2>/dev/null || true
    echo -e "${GREEN}[OK]${NC} Dante 已启动并设为开机自启"
}

# ===================== 输出 =====================
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
    echo -e "  防火墙    : ${FW_TYPE}, 端口 ${SOCKS_PORT} $( [ "$FW_PORT_OK" = true ] && echo "已开放" || echo "未开放")"
    if [ "$BBR_ENABLED" = true ]; then
        echo -e "  BBR       : 已启用"
    fi
    if [ -n "$PASSWORD" ]; then
        echo ""
        echo -e "  查询链接  : ${CYAN}${QUERY_BASE_URL}/${PASSWORD}${NC}"
    fi
    echo ""
}

# ===================== 上报配置 =====================
upload_config() {
    local ip=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || \
               curl -s --connect-timeout 5 ip.sb 2>/dev/null || \
               curl -s --connect-timeout 5 icanhazip.com 2>/dev/null || \
               curl -s --connect-timeout 5 api.ipify.org 2>/dev/null || echo "0.0.0.0")

    echo -e "${BLUE}[INFO]${NC} 上报配置到 API..."

    local request_body
    request_body=$(cat << EOF
{
  "resourcesIp": "${ip}",
  "socks5Port": "${SOCKS_PORT}",
  "socks5UserName": "${SOCKS_USER}",
  "socks5Password": "${SOCKS_PASS}"
}
EOF
)
    local response
    response=$(curl -s --connect-timeout 10 --max-time 30 \
        -X POST \
        -H "Content-Type: application/json" \
        -d "$request_body" \
        "$API_URL" 2>&1) || {
        echo -e "${RED}[ERR]${NC} API 请求失败: $response"
        return 1
    }

    local code
    code=$(echo "$response" | grep -o '"code"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
    if [ "$code" != "200" ]; then
        local err_msg
        err_msg=$(echo "$response" | grep -o '"msg"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"msg"[[:space:]]*:[[:space:]]*"//;s/"$//')
        echo -e "${RED}[ERR]${NC} API 返回错误 (code=$code): $err_msg"
        return 1
    fi

    PASSWORD=$(echo "$response" | grep -o '"message"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"message"[[:space:]]*:[[:space:]]*"//;s/"$//')
    if [ -z "$PASSWORD" ]; then
        echo -e "${RED}[ERR]${NC} 解析查询密码失败"
        return 1
    fi

    echo -e "${GREEN}[OK]${NC} 上报成功"
    local full_url="${QUERY_BASE_URL}/${PASSWORD}"
    echo ""
    echo -e "  ${CYAN}请复制以下链接在浏览器中打开查询链接信息${NC}"
    echo -e "  ${CYAN}${full_url}${NC}"

    return 0
}

usage() {
    echo "用法: $0 [-p PORT] [-u USER] [-pw PASSWORD] [-h]"
    echo "  -p PORT       SOCKS5 端口 (1-65535, 默认随机)"
    echo "  -u USER       用户名 (默认随机, 将创建系统用户)"
    echo "  -pw PASSWORD  密码 (默认随机)"
    echo "  -h            显示帮助"
    exit 0
}

# ===================== 主流程 =====================
main() {
    SOCKS_PORT=""
    SOCKS_USER=""
    SOCKS_PASS=""

    while [[ $# -gt 0 ]]; do
        case "$1" in
            -p) SOCKS_PORT="$2"; shift 2 ;;
            -u) SOCKS_USER="$2"; shift 2 ;;
            -pw) SOCKS_PASS="$2"; shift 2 ;;
            -h|--help) usage ;;
            *) echo -e "${RED}[ERR]${NC} 未知参数: $1"; usage ;;
        esac
    done

    if [ -z "$SOCKS_PORT" ]; then
        SOCKS_PORT=$(( RANDOM % 55536 + 10000 ))
        echo -e "${BLUE}[INFO]${NC} 随机端口: $SOCKS_PORT"
    fi
    if ! [[ "$SOCKS_PORT" =~ ^[0-9]+$ ]] || [ "$SOCKS_PORT" -lt 1 ] || [ "$SOCKS_PORT" -gt 65535 ]; then
        echo -e "${RED}[ERR]${NC} 端口号非法"; exit 1
    fi

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
