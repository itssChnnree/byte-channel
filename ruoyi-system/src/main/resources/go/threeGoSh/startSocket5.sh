#!/bin/bash
# ============================================================
# Xray SOCKS5 全量一键脚本 (多配置文件版 - 最终)
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

# 固定路径
XRAY_DIR="/usr/local/etc/xray"
CONF_FILE="${XRAY_DIR}/socksConfig.json"
PORT_RECORD_FILE="/usr/local/etc/firewalld.json"

# 保存旧端口（用于防火墙清理）
OLD_PORTS=""

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
        none) echo -e "${YELLOW}[WARN]${NC} 未检测到防火墙，请手动开放端口 $port"; return 0 ;;
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

# 提前收集旧端口（在写入新配置前调用）
save_old_ports() {
    OLD_PORTS=""
    # 1. 从 firewalld.json 获取 socksConfig.json 的历史端口
    if [ -f "$PORT_RECORD_FILE" ] && command -v python3 &>/dev/null; then
        local recorded=$(python3 -c "
import json
try:
    with open('$PORT_RECORD_FILE') as f:
        data = json.load(f)
    if isinstance(data, dict) and 'socksConfig.json' in data:
        print(' '.join(map(str, data['socksConfig.json'])))
except:
    pass
" 2>/dev/null)
        if [ -n "$recorded" ]; then
            OLD_PORTS="$recorded"
        fi
    fi
    # 2. 从当前 socksConfig.json 提取端口
    if [ -f "$CONF_FILE" ]; then
        local cur_port=$(grep -oP '"port"\s*:\s*\K\d+' "$CONF_FILE" 2>/dev/null | head -1)
        if [ -n "$cur_port" ]; then
            OLD_PORTS="$OLD_PORTS $cur_port"
        fi
    fi
    # 去重
    OLD_PORTS=$(echo "$OLD_PORTS" | tr ' ' '\n' | sort -u | tr '\n' ' ')
    if [ -n "$OLD_PORTS" ]; then
        echo -e "${BLUE}[INFO]${NC} 已记录旧端口: $OLD_PORTS"
    else
        echo -e "${BLUE}[INFO]${NC} 未发现旧端口记录"
    fi
}

# 最后执行防火墙配置（Xray 启动成功后）
finalize_firewall() {
    detect_firewall
    echo -e "${CYAN}[Firewall]${NC} 开始配置防火墙..."

    # 1. 关闭旧端口（排除当前新端口）
    if [ -n "$OLD_PORTS" ]; then
        for port in $OLD_PORTS; do
            if [ "$port" != "$SOCKS_PORT" ]; then
                close_old_port "$port"
            fi
        done
    fi

    # 2. 放行新端口
    open_port "$SOCKS_PORT"

    echo -e "${GREEN}[OK]${NC} 防火墙规则已更新"
}

# ===================== 端口冲突检查 =====================
check_port_conflict() {
    local new_port="$1"
    
    # 精确匹配监听端口，避免 80 误匹配 8080 等
    local listening_info=""
    if ss -tlnp "sport = :${new_port}" 2>/dev/null | grep -q LISTEN; then
        listening_info=$(ss -tlnp "sport = :${new_port}" 2>/dev/null)
    else
        listening_info=$(ss -tlnp 2>/dev/null | grep -E "LISTEN[^:]*:${new_port}\s")
    fi

    if [ -z "$listening_info" ]; then
        echo -e "${GREEN}[INFO]${NC} 端口 $new_port 未被占用，可以继续"
        return 0
    fi

    local proc_info=$(echo "$listening_info" | awk '{print $NF}' | sed 's/users:(("//;s/".*//')
    echo -e "${YELLOW}[WARN]${NC} 端口 $new_port 已被进程占用: $proc_info"

    # 检查 firewalld.json 中是否记录了该端口（属于本脚本的 socksConfig 历史）
    if [ -f "$PORT_RECORD_FILE" ] && command -v python3 &>/dev/null; then
        local in_record=$(python3 -c "
import json, sys
try:
    with open('$PORT_RECORD_FILE') as f:
        data = json.load(f)
    if isinstance(data, dict) and 'socksConfig.json' in data:
        ports = data['socksConfig.json']
        if $new_port in ports:
            print('yes')
except:
    pass
" 2>/dev/null)
        if [ "$in_record" = "yes" ]; then
            echo -e "${GREEN}[INFO]${NC} 端口 $new_port 属于本脚本历史记录，允许覆盖"
            return 0
        fi
    fi

    echo -e "${RED}[ERR]${NC} 端口 $new_port 被其他服务占用，无法继续。"
    echo -e "${YELLOW}       请更换端口后重试，或先停止占用该端口的服务。${NC}"
    exit 1
}

# ===================== Xray 安装 =====================
install_xray() {
    echo -e "${CYAN}[Xray]${NC} 安装/检查 Xray ..."
    if command -v xray &> /dev/null; then
        echo -e "${GREEN}[OK]${NC} 检测到 xray"
        return
    fi

    echo -e "${YELLOW}[WARN]${NC} 未找到 xray，通过官方脚本安装..."
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
    mkdir -p "$XRAY_DIR"

    if [ "$SOCKS_USER" = "root" ]; then
        echo -e "${RED}[ERR]${NC} 严禁使用 root 作为 SOCKS 用户名，会破坏系统认证！已自动更改为随机用户。"
        SOCKS_USER=""
    fi

    [ -z "$SOCKS_USER" ] && SOCKS_USER="user$(tr -dc 'a-z0-9' < /dev/urandom | head -c 6)"
    [ -z "$SOCKS_PASS" ] && SOCKS_PASS="$(tr -dc 'A-Za-z0-9' < /dev/urandom | head -c 16)"

    # 端口冲突检查
    check_port_conflict "$SOCKS_PORT"

    # 处理旧的 config.json（避免合并冲突）
    if [ -f "${XRAY_DIR}/config.json" ]; then
        local bak="${XRAY_DIR}/config.json.bak.$(date +%s)"
        mv "${XRAY_DIR}/config.json" "$bak"
        echo -e "${YELLOW}[WARN]${NC} 旧的 config.json 已备份为 $(basename "$bak")，防止冲突"
    fi

    cat > "$CONF_FILE" << EOF
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
    echo -e "${GREEN}[OK]${NC} 配置文件 $CONF_FILE 已写入"
}

# ===================== 更新 firewalld.json =====================
update_port_record() {
    local key="socksConfig.json"
    local port=$SOCKS_PORT

    if command -v python3 &>/dev/null; then
        python3 -c "
import json, sys, os
record_file = '$PORT_RECORD_FILE'
key = '$key'
new_port = $port

data = {}
if os.path.exists(record_file):
    try:
        with open(record_file) as f:
            data = json.load(f)
    except:
        data = {}

if not isinstance(data, dict):
    data = {}

data[key] = [new_port]

with open(record_file, 'w') as f:
    json.dump(data, f, indent=2)
    f.write('\n')
"
        echo -e "${GREEN}[OK]${NC} 端口记录已更新至 $PORT_RECORD_FILE"
    else
        cat > "$PORT_RECORD_FILE" << EOF
{
  "$key": [$SOCKS_PORT]
}
EOF
        echo -e "${YELLOW}[WARN]${NC} 未找到 python3，firewalld.json 已直接覆盖（历史记录丢失）"
    fi
}

# ===================== 启动 Xray（confdir 模式） =====================
restart_xray() {
    echo -e "${CYAN}[Xray]${NC} 启动/重启 Xray 服务（confdir 模式）..."

    local xray_bin="xray"
    if [ -x "/usr/local/bin/xray" ]; then
        xray_bin="/usr/local/bin/xray"
    fi

    local service_file="/etc/systemd/system/xray.service"
    if [ -f "$service_file" ]; then
        echo -e "${BLUE}[INFO]${NC} 检测到 systemd 服务，修改为 confdir 启动..."
        cp "$service_file" "${service_file}.bak.$(date +%s)"
        sed -i "s|^ExecStart=.*|ExecStart=${xray_bin} -confdir ${XRAY_DIR}|" "$service_file"
        systemctl daemon-reload
        systemctl restart xray
        systemctl enable xray 2>/dev/null || true
        echo -e "${GREEN}[OK]${NC} systemd 服务已更新并重启"
    else
        echo -e "${YELLOW}[WARN]${NC} 未找到 systemd 服务，手动启动..."
        pkill xray 2>/dev/null || true
        nohup "$xray_bin" -confdir "$XRAY_DIR" > /var/log/xray.log 2>&1 &
        sleep 2
        if pgrep -f "xray" > /dev/null; then
            echo -e "${GREEN}[OK]${NC} Xray 进程已启动（后台）"
        else
            echo -e "${RED}[ERR]${NC} Xray 启动失败，请检查配置"
            exit 1
        fi
    fi
    echo -e "${GREEN}[OK]${NC} Xray 已成功运行"
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

# ===================== 主流程 =====================
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
    install_if_missing "python3" "python3"

    enable_bbr
    install_xray
    save_old_ports         # ❶ 先收集旧端口（此时 firewalld.json 和 socksConfig.json 尚未更新）
    write_config           # ❷ 写配置（含端口冲突检查）
    update_port_record     # ❸ 更新记录为新端口
    restart_xray           # ❹ 启动 Xray（失败则退出，避免空放行）
    finalize_firewall      # ❺ 启动成功后配置防火墙（关闭旧端口、放行新端口）
    upload_config || true
    print_result
}

main "$@"
