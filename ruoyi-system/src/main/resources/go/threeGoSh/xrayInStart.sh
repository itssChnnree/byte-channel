#!/bin/bash
# ============================================================
# Xray Reality+VLESS 一键脚本 (多配置文件版)
# 用法： bash xray_vless.sh [-p PORT] [-i IP] [-d DEST] [-s NAMES] [-close]
# 特点：reality+vless，confdir模式，端口管理，防火墙后置，二维码
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
API_URL="https://api.ganguo168.com/serverResourcesThree/insert"
QUERY_BASE_URL="https://www.ganguo168.com/#/query-config"

# 固定路径
XRAY_DIR="/usr/local/etc/xray"
CONF_FILE="${XRAY_DIR}/vlessConfig.json"
PORT_RECORD_FILE="/usr/local/etc/firewalld.json"

# 保存旧端口（用于防火墙清理）
OLD_PORTS=""

# 用户参数
PORT=""
SERVER_IP=""
DEST="lacity.gov:443"
SERVER_NAMES="lacity.gov,www.lacity.gov"
CLIENT_ID=""
PUBLIC_KEY=""
PRIVATE_KEY=""
SHORT_ID=""
PASSWORD=""
SERVER_NAMES_JSON=""

# 二维码开关
QR_ENABLED=true

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
        ufw)
            ufw delete allow "$port"/tcp 2>/dev/null || true
            ufw delete allow "$port"/udp 2>/dev/null || true
            ufw reload 2>/dev/null || true
            ;;
        firewalld)
            firewall-cmd --permanent --remove-port="${port}/tcp" 2>/dev/null || true
            firewall-cmd --permanent --remove-port="${port}/udp" 2>/dev/null || true
            firewall-cmd --reload 2>/dev/null || true
            ;;
        iptables)
            iptables -D INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null || true
            iptables -D INPUT -p udp --dport "$port" -j ACCEPT 2>/dev/null || true
            ;;
    esac
    sleep 1
}

open_port() {
    local port="$1"
    echo -e "${BLUE}[INFO]${NC} 开放端口 $port ..."
    case "$FW_TYPE" in
        ufw)
            ufw allow "$port"/tcp 2>/dev/null || true
            ufw allow "$port"/udp 2>/dev/null || true
            ufw reload 2>/dev/null || true
            ;;
        firewalld)
            firewall-cmd --permanent --add-port="${port}/tcp" 2>/dev/null || true
            firewall-cmd --permanent --add-port="${port}/udp" 2>/dev/null || true
            firewall-cmd --reload 2>/dev/null || true
            ;;
        iptables)
            iptables -I INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null || true
            iptables -I INPUT -p udp --dport "$port" -j ACCEPT 2>/dev/null || true
            ;;
        none)
            echo -e "${YELLOW}[WARN]${NC} 未检测到防火墙，请手动开放端口 $port"
            return 0
            ;;
    esac
    sleep 1
    local ok=false
    if [ "$FW_TYPE" = "ufw" ]; then
        ufw status 2>/dev/null | grep -qw "$port" && ok=true
    elif [ "$FW_TYPE" = "firewalld" ]; then
        firewall-cmd --list-ports 2>/dev/null | grep -qw "${port}/tcp" && ok=true
    elif [ "$FW_TYPE" = "iptables" ]; then
        iptables -C INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null && ok=true
    fi
    [ "$ok" = true ] && echo -e "${GREEN}[OK]${NC} 端口 $port 已开放" || echo -e "${YELLOW}[WARN]${NC} 端口 $port 开放验证失败"
}

# 提前收集旧端口（在写入新配置前调用）
save_old_ports() {
    OLD_PORTS=""
    local key="vlessConfig.json"
    # 1. 从 firewalld.json 获取历史端口
    if [ -f "$PORT_RECORD_FILE" ] && command -v python3 &>/dev/null; then
        local recorded=$(python3 -c "
import json
try:
    with open('$PORT_RECORD_FILE') as f:
        data = json.load(f)
    if isinstance(data, dict) and '$key' in data:
        print(' '.join(map(str, data['$key'])))
except:
    pass
" 2>/dev/null)
        if [ -n "$recorded" ]; then
            OLD_PORTS="$recorded"
        fi
    fi
    # 2. 从当前 vlessConfig.json 提取端口
    if [ -f "$CONF_FILE" ]; then
        local cur_port=$(grep -oP '"port"\s*:\s*\K\d+' "$CONF_FILE" 2>/dev/null | head -1)
        if [ -n "$cur_port" ]; then
            OLD_PORTS="$OLD_PORTS $cur_port"
        fi
    fi
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
            if [ "$port" != "$PORT" ]; then
                close_old_port "$port"
            fi
        done
    fi
    # 2. 放行新端口
    open_port "$PORT"
    echo -e "${GREEN}[OK]${NC} 防火墙规则已更新"
}

# ===================== 端口冲突检查 =====================
check_port_conflict() {
    local new_port="$1"
    local key="vlessConfig.json"

    # 方法1：优先使用 fuser（不会卡死）
    local listening_info=""
    if command -v fuser &>/dev/null; then
        listening_info=$(fuser -n tcp "$new_port" 2>&1 || true)
    fi

    # 方法2：fuser不可用时，使用 timeout + ss，并过滤掉标题行
    if [ -z "$listening_info" ] && command -v timeout &>/dev/null; then
        listening_info=$(timeout 3 ss -tlnp "sport = :${new_port}" 2>/dev/null | tail -n +2)
        if [ -z "$listening_info" ]; then
            listening_info=$(timeout 3 ss -tlnp 2>/dev/null | grep -E "LISTEN[^:]*:${new_port}\s" || true)
        fi
    fi

    if [ -z "$listening_info" ]; then
        echo -e "${GREEN}[INFO]${NC} 端口 $new_port 未被占用，可以继续"
        return 0
    fi

    echo -e "${YELLOW}[WARN]${NC} 端口 $new_port 已被占用，详细信息:"
    echo "$listening_info" | head -3

    # 检查 firewalld.json 中是否记录了该端口（属于本脚本的 vlessConfig 历史）
    if [ -f "$PORT_RECORD_FILE" ] && command -v python3 &>/dev/null; then
        local in_record=$(python3 -c "
import json
try:
    with open('$PORT_RECORD_FILE') as f:
        data = json.load(f)
    if isinstance(data, dict) and '$key' in data:
        ports = data['$key']
        if $new_port in ports:
            print('yes')
except:
    pass
" 2>/dev/null)
        if [ "$in_record" = "yes" ]; then
            echo -e "${GREEN}[INFO]${NC} 端口 $new_port 属于本脚本历史记录，允许覆盖"
            return 0
        fi
    else
        echo -e "${RED}[ERR]${NC} 端口被占用，且无法验证是否为脚本历史端口（python3 或记录文件缺失）"
        echo -e "${YELLOW}       请手动检查或更换端口后重试。${NC}"
        exit 1
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

# ===================== 密钥生成 =====================
generate_keys() {
    echo -e "${CYAN}[Key]${NC} 生成 x25519 公私钥..."
    local xray_bin="xray"
    if [ -x "/usr/local/bin/xray" ]; then
        xray_bin="/usr/local/bin/xray"
    fi

    local key_output
    key_output=$("$xray_bin" x25519 2>&1) || {
        echo -e "${RED}[ERR]${NC} x25519 密钥生成失败"
        exit 1
    }

    PRIVATE_KEY=$(echo "$key_output" | sed -n 's/.*PrivateKey: *//p' | head -1)
    PUBLIC_KEY=$(echo "$key_output"  | sed -n 's/.*(PublicKey): *//p' | head -1)

    if [ -z "$PRIVATE_KEY" ] || [ -z "$PUBLIC_KEY" ]; then
        echo -e "${RED}[ERR]${NC} 解析公私钥失败"
        echo "Raw output:"
        echo "$key_output"
        exit 1
    fi

    echo -e "${GREEN}[OK]${NC} Private key: $PRIVATE_KEY"
    echo -e "${GREEN}[OK]${NC} Public key:  $PUBLIC_KEY"
}

generate_clientid() {
    CLIENT_ID=""
    for i in $(seq 1 10); do
        CLIENT_ID="${CLIENT_ID}$(( RANDOM % 10 ))"
    done
    echo -e "${BLUE}[INFO]${NC} 生成 clients.id: $CLIENT_ID"
}

generate_shortid() {
    SHORT_ID=$(cat /dev/urandom 2>/dev/null | tr -dc 'a-f0-9' | head -c 12 || \
               openssl rand -hex 6 2>/dev/null | head -c 12 || \
               { for i in $(seq 1 12); do printf '%x' $(( RANDOM % 16 )); done; })
    echo -e "${BLUE}[INFO]${NC} 生成 shortId: $SHORT_ID"
}

build_server_names_json() {
    IFS=',' read -ra NAMES <<< "$SERVER_NAMES"
    local lines=()
    for name in "${NAMES[@]}"; do
        name=$(echo "$name" | xargs)
        [ -n "$name" ] && lines+=("            \"$name\"")
    done
    local last_idx=$((${#lines[@]} - 1))
    for i in "${!lines[@]}"; do
        if [ "$i" -lt "$last_idx" ]; then
            echo "${lines[$i]},"
        else
            echo "${lines[$i]}"
        fi
    done
}

# ===================== 生成配置 =====================
write_config() {
    echo -e "${BLUE}[INFO]${NC} 写入配置文件..."
    mkdir -p "$XRAY_DIR"

    # 端口冲突检查
    check_port_conflict "$PORT"

    # 生成 serverNames JSON 行
    SERVER_NAMES_JSON=$(build_server_names_json)

    cat > "$CONF_FILE" << EOF
{
  "log": {
    "loglevel": "warning"
  },
  "inbounds": [
    {
      "port": ${PORT},
      "protocol": "vless",
      "settings": {
        "clients": [
          {
            "id": "${CLIENT_ID}",
            "flow": "xtls-rprx-vision"
          }
        ],
        "decryption": "none"
      },
      "streamSettings": {
        "network": "tcp",
        "security": "reality",
        "realitySettings": {
          "show": false,
          "dest": "${DEST}",
          "xver": 0,
          "serverNames": [
${SERVER_NAMES_JSON}
          ],
          "privateKey": "${PRIVATE_KEY}",
          "shortIds": [
            "${SHORT_ID}"
          ],
          "fingerprint": "chrome"
        }
      },
      "sniffing": {
        "enabled": true,
        "destOverride": [
          "http",
          "tls"
        ]
      },
      "tag": "inbound-${PORT}"
    }
  ],
  "outbounds": [
    {
      "protocol": "freedom",
      "tag": "direct"
    },
    {
      "protocol": "blackhole",
      "tag": "block"
    }
  ],
  "routing": {
    "domainStrategy": "IPIfNonMatch",
    "rules": []
  }
}
EOF
    echo -e "${GREEN}[OK]${NC} 配置文件 $CONF_FILE 已写入"
}

# ===================== 更新 firewalld.json =====================
update_port_record() {
    local key="vlessConfig.json"
    local port=$PORT

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
  "$key": [$PORT]
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

    # 优先使用 systemd 服务管理
    local service_file=$(systemctl show -p FragmentPath xray 2>/dev/null | cut -d= -f2)
    if [ -n "$service_file" ] && [ -f "$service_file" ]; then
        echo -e "${BLUE}[INFO]${NC} 检测到 systemd 服务，修改为 confdir 启动..."
        cp "$service_file" "${service_file}.bak.$(date +%s)"
        sed -i "s|^ExecStart=.*|ExecStart=${xray_bin} -confdir ${XRAY_DIR}|" "$service_file"
        systemctl daemon-reload
        systemctl restart xray
        systemctl enable xray 2>/dev/null || true
        echo -e "${GREEN}[OK]${NC} systemd 服务已更新并重启"
    else
        echo -e "${YELLOW}[WARN]${NC} 未找到 systemd 服务，手动启动..."
        pkill -f "xray.*-confdir.*${XRAY_DIR}" 2>/dev/null || true
        sleep 1
        nohup "$xray_bin" -confdir "$XRAY_DIR" > /var/log/xray.log 2>&1 &
        sleep 2
        if pgrep -f "xray.*-confdir.*${XRAY_DIR}" > /dev/null; then
            echo -e "${GREEN}[OK]${NC} Xray 进程已启动（后台）"
        else
            echo -e "${RED}[ERR]${NC} Xray 启动失败，请检查配置"
            exit 1
        fi
    fi
    echo -e "${GREEN}[OK]${NC} Xray 已成功运行"
}

# ===================== 上报 =====================
upload_config() {
    local ip=$SERVER_IP
    [ -z "$ip" ] && ip=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || echo "0.0.0.0")
    echo -e "${BLUE}[INFO]${NC} 上报配置到 API..."
    local request_body=$(cat << EOF
{
  "resourcesIp": "${ip}",
  "publicBrokerKey": "${PUBLIC_KEY}",
  "sni": "${SERVER_NAMES}",
  "shortId": "${SHORT_ID}",
  "userId": "${CLIENT_ID}",
  "nodePort": "${PORT}"
}
EOF
)
    local response
    response=$(curl -s --connect-timeout 10 --max-time 30 -X POST -H "Content-Type: application/json" -d "$request_body" "$API_URL" 2>&1) || {
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

# ===================== 输出 =====================
status_icon() {
    [ "$1" = "true" ] && echo -e "${GREEN}✓${NC}" || echo -e "${RED}✗${NC}"
}

print_qrcode() {
    local url="$1"
    if command -v qrencode &> /dev/null; then
        echo -e "  ${CYAN}请用微信扫码后在浏览器中打开查询链接信息${NC}"
        echo -e "  ${CYAN}因微信浏览器内核版本过低，会出现查询失败情况${NC}"
        qrencode -t ANSIUTF8 -m 1 -s 2 "$url" 2>/dev/null | while IFS= read -r line; do
            echo "  $line"
        done
    else
        echo -e "${YELLOW}[WARN]${NC} qrencode 未安装，无法显示二维码"
    fi
}

print_result() {
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║        VLESS+Reality 一键部署完成           ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "  服务器 IP : ${CYAN}${SERVER_IP}${NC}"
    echo -e "  端口      : ${CYAN}${PORT}${NC}"
    echo -e "  协议      : vless + reality + tcp"
    echo -e "  回落目标  : ${CYAN}${DEST}${NC}"
    echo -e "  服务域名  : ${CYAN}${SERVER_NAMES}${NC}"
    echo -e "  Client ID : ${CYAN}${CLIENT_ID}${NC}"
    echo -e "  公钥      : ${CYAN}${PUBLIC_KEY}${NC}"
    echo -e "  短ID      : ${CYAN}${SHORT_ID}${NC}"
    echo -e "  防火墙    : ${FW_TYPE}"
    if [ -n "$PASSWORD" ]; then
        local full_url="${QUERY_BASE_URL}/${PASSWORD}"
        echo -e "  查询链接  : ${CYAN}${full_url}${NC}"
        if [ "$QR_ENABLED" = "true" ]; then
            echo ""
            print_qrcode "$full_url"
        fi
    else
        echo -e "  ${RED}上报失败，未获取查询链接${NC}"
    fi
    echo ""
    echo -e "${GREEN}════════════════════════════════════════════${NC}"
}

# ===================== 主流程 =====================
usage() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -p, --port PORT           监听端口 (必填, 1-65535)"
    echo "  -i, --ip IP               服务器公网IP (不指定则自动检测)"
    echo "  -d, --dest DEST           回落目标 (默认: lacity.gov:443)"
    echo "  -s, --server-names NAMES  可用域名, 逗号分隔 (默认: lacity.gov,www.lacity.gov)"
    echo "  -close                    关闭二维码显示（默认显示二维码）"
    echo "  -h, --help                显示帮助"
    echo ""
    echo "示例:"
    echo "  $0 -p 45673"
    echo "  $0 -p 45673 -i 137.175.93.245"
    echo "  $0 -p 45673 -close"
    exit 0
}

main() {
    PORT=""
    SERVER_IP=""
    while [[ $# -gt 0 ]]; do
        case "$1" in
            -p|--port)           PORT="$2"; shift 2 ;;
            -i|--ip)             SERVER_IP="$2"; shift 2 ;;
            -d|--dest)           DEST="$2"; shift 2 ;;
            -s|--server-names)   SERVER_NAMES="$2"; shift 2 ;;
            -close)              QR_ENABLED=false; shift ;;
            -h|--help)           usage ;;
            *) echo -e "${RED}[ERR]${NC} 未知参数: $1"; usage ;;
        esac
    done

    [ -z "$PORT" ] && { echo -e "${RED}[ERR]${NC} 必须指定端口号 (-p/--port)"; usage; }
    [[ "$PORT" =~ ^[0-9]+$ ]] && [ "$PORT" -ge 1 ] && [ "$PORT" -le 65535 ] || {
        echo -e "${RED}[ERR]${NC} 端口号必须是 1-65535 之间的整数"; exit 1;
    }

    echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║      Xray Reality+VLESS 一键脚本           ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
    echo ""

    detect_pkg_manager
    install_if_missing "curl" "curl"
    install_if_missing "ss" "iproute2"
    install_if_missing "python3" "python3"
    install_if_missing "fuser" "psmisc"
    if [ "$QR_ENABLED" = "true" ]; then
        install_if_missing "qrencode" "qrencode" || true
    fi

    # 自动检测IP
    if [ -z "$SERVER_IP" ]; then
        echo -e "${BLUE}[INFO]${NC} 未指定IP，自动检测公网IP..."
        SERVER_IP=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || \
                    curl -s --connect-timeout 5 ip.sb 2>/dev/null || \
                    curl -s --connect-timeout 5 icanhazip.com 2>/dev/null || \
                    curl -s --connect-timeout 5 api.ipify.org 2>/dev/null || echo "0.0.0.0")
        [ "$SERVER_IP" = "0.0.0.0" ] && { echo -e "${RED}[ERR]${NC} 自动检测IP失败，请用 -i 指定"; exit 1; }
    fi
    echo -e "${BLUE}[INFO]${NC} 服务器IP: $SERVER_IP"

    enable_bbr
    install_xray
    generate_keys
    generate_clientid
    generate_shortid
    save_old_ports         # ❶ 收集旧端口
    write_config           # ❷ 写入配置（含端口冲突检查）
    update_port_record     # ❸ 更新记录为新端口
    restart_xray           # ❹ 启动 Xray
    finalize_firewall      # ❺ 启动成功后配置防火墙
    upload_config || true
    print_result
}

main "$@"
