#!/bin/bash
# xrayRelay.sh - Xray 中转节点 reality+vless 配置生成与上报脚本
# 兼容 CentOS 7/8/9, Ubuntu 18/20/22/24, Debian 10/11/12 等主流 Linux 发行版
# 功能：BBR优化 → 防火墙开放端口 → x25519生成密钥 → 随机UUID/shortId → 写入config.json(出站指向上游) → 重启xray → 上报API
# 模式：
#   full        全量覆盖配置文件（仅保留当前 inbound/outbound/rules）
#   incremental 增量：若上游已存在，则删除其整套配置（出站+关联入站+路由规则）后再新增；否则直接新增

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

XRAY_BIN="/usr/local/bin/xray"
XRAY_CONFIG_DIR="/usr/local/etc/xray"
XRAY_CONFIG_FILE="$XRAY_CONFIG_DIR/config.json"
API_URL="https://api.ganguo168.com/serverResourcesThree/insert"
QUERY_BASE_URL="https://www.ganguo168.com/#/query-config"

PKG_MANAGER=""
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

UPSTREAM_ADDRESS=""
UPSTREAM_PORT=""
UPSTREAM_ID=""
UPSTREAM_PBK=""
UPSTREAM_SID=""
UPSTREAM_SNI=""

FW_TYPE="none"
FW_PORT_OK=false
BBR_ENABLED=false
XRAY_RUNNING=false

# 操作模式：full 或 incremental
MODE="full"

# 所有输出函数重定向到 stderr，避免干扰 $( ) 捕获返回值
print_info()    { echo -e "${BLUE}[INFO]${NC} $1" >&2; }
print_success() { echo -e "${GREEN}[OK]${NC}  $1" >&2; }
print_warning() { echo -e "${YELLOW}[WARN]${NC} $1" >&2; }
print_error()   { echo -e "${RED}[ERR]${NC} $1" >&2; }

usage() {
    echo "用法: $0 [选项]"
    echo ""
    echo "入站参数 (和入口节点一样):"
    echo "  -p, --port PORT           监听端口 (必填, 1-65535)"
    echo "  -i, --ip IP               服务器公网IP (不指定则自动检测)"
    echo "  -d, --dest DEST           回落目标 (默认: lacity.gov:443)"
    echo "  -s, --server-names NAMES  可用域名, 逗号分隔 (默认: lacity.gov,www.lacity.gov)"
    echo ""
    echo "出站参数 (指向上游入口节点):"
    echo "  -u, --upstream-address IP 上游入口节点IP (必填)"
    echo "  -r, --upstream-port PORT  上游入口节点端口 (必填)"
    echo "  -n, --upstream-id UUID    上游入口节点UUID/ClientID (必填)"
    echo "  -k, --upstream-pbk KEY    上游入口节点publicKey (必填)"
    echo "  -t, --upstream-sid ID     上游入口节点shortId (必填)"
    echo "  -w, --upstream-sni SNI    上游TLS SNI (默认: 本节点server-names第一个域名)"
    echo ""
    echo "模式参数:"
    echo "  -m, --mode {full|incremental}  配置模式: full=全量覆盖(默认), incremental=增量添加/更新"
    echo ""
    echo "  -h, --help                显示帮助"
    echo ""
    echo "支持系统: CentOS 7/8/9, Ubuntu 18/20/22/24, Debian 10/11/12"
    echo ""
    echo "示例:"
    echo "  # 全量配置（覆盖已有）"
    echo "  $0 -m full -p 45673 -u 137.175.93.245 -r 56790 -n 'd6f7a3c3-...' -k '2SAAzbmdk...' -t '5689902540'"
    echo "  # 增量添加/删除旧配置后新增"
    echo "  $0 -m incremental -p 45673 -u 137.175.93.245 -r 56790 -n 'd6f7a3c3-...' -k '2SAAzbmdk...' -t '5689902540'"
    exit 0
}

detect_pkg_manager() {
    if command -v apt-get &> /dev/null; then
        PKG_MANAGER="apt"
    elif command -v dnf &> /dev/null; then
        PKG_MANAGER="dnf"
    elif command -v yum &> /dev/null; then
        PKG_MANAGER="yum"
    else
        print_error "无法识别包管理器 (需要 apt-get / dnf / yum)"
        exit 1
    fi
    print_info "包管理器: $PKG_MANAGER"
}

install_if_missing() {
    local cmd="$1"
    local pkg="$2"

    if command -v "$cmd" &> /dev/null; then
        return 0
    fi

    print_info "$cmd 未安装，尝试安装 $pkg ..."

    case "$PKG_MANAGER" in
        apt)
            apt-get update -qq
            DEBIAN_FRONTEND=noninteractive apt-get install -y -qq "$pkg"
            ;;
        dnf|yum)
            $PKG_MANAGER install -y -q "$pkg" 2>/dev/null || true
            ;;
    esac

    if ! command -v "$cmd" &> /dev/null; then
        print_warning "$cmd 安装失败，请手动安装"
        return 1
    fi

    print_success "$cmd 安装完成"
    return 0
}

ensure_deps() {
    detect_pkg_manager
    install_if_missing "curl" "curl"
    if [ "$MODE" = "incremental" ]; then
        install_if_missing "jq" "jq"
    fi
}

detect_public_ip() {
    local ip
    ip=$(curl -s --connect-timeout 5 --max-time 10 ifconfig.me 2>/dev/null) || \
    ip=$(curl -s --connect-timeout 5 --max-time 10 ipinfo.io/ip 2>/dev/null) || \
    ip=$(curl -s --connect-timeout 5 --max-time 10 icanhazip.com 2>/dev/null) || \
    ip=$(curl -s --connect-timeout 5 --max-time 10 api.ipify.org 2>/dev/null)
    echo "$ip"
}

enable_bbr() {
    print_info "检查 BBR 拥塞控制..."

    local kernel_major
    kernel_major=$(uname -r | cut -d. -f1)
    local kernel_minor
    kernel_minor=$(uname -r | cut -d. -f2)

    if [ "$kernel_major" -lt 4 ] || { [ "$kernel_major" -eq 4 ] && [ "$kernel_minor" -lt 9 ]; }; then
        print_warning "内核版本 $(uname -r) < 4.9，不支持 BBR"
        BBR_ENABLED=false
        return 0
    fi

    local cc
    cc=$(sysctl -n net.ipv4.tcp_congestion_control 2>/dev/null || echo "")
    if [ "$cc" = "bbr" ]; then
        print_success "BBR 已启用"
        BBR_ENABLED=true
        return 0
    fi

    print_info "尝试开启 BBR..."
    modprobe tcp_bbr 2>/dev/null || true

    local sysctl_file
    if [ -d /etc/sysctl.d ]; then
        sysctl_file="/etc/sysctl.d/99-bbr.conf"
    else
        sysctl_file="/etc/sysctl.conf"
    fi

    if ! grep -q "tcp_congestion_control.*=.*bbr" "$sysctl_file" 2>/dev/null; then
        cat >> "$sysctl_file" << 'SYSCTL_EOF'

net.core.default_qdisc = fq
net.ipv4.tcp_congestion_control = bbr
SYSCTL_EOF
    fi

    sysctl -p "$sysctl_file" > /dev/null 2>&1 || sysctl -p > /dev/null 2>&1 || true

    cc=$(sysctl -n net.ipv4.tcp_congestion_control 2>/dev/null || echo "")
    if [ "$cc" = "bbr" ]; then
        print_success "BBR 已开启 (qdisc=fq, cc=bbr)"
        BBR_ENABLED=true
    else
        print_warning "BBR 开启失败，当前拥塞控制: $cc"
        BBR_ENABLED=false
    fi
}

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
    print_info "防火墙类型: $FW_TYPE"
}

# 获取配置文件中的所有入站端口（空格分隔）
get_all_inbound_ports() {
    if [ ! -f "$XRAY_CONFIG_FILE" ]; then
        echo ""
        return
    fi
    jq -r '.inbounds[]?.port | select(. != null)' "$XRAY_CONFIG_FILE" 2>/dev/null | tr '\n' ' '
}

close_port() {
    local port="$1"
    print_info "关闭端口 $port ..."

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
            # 循环删除所有匹配该端口的 ACCEPT 规则
            while iptables -D INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null; do :; done
            ;;
        none)
            return 0
            ;;
    esac

    sleep 1
    if check_port_open "$port" "$FW_TYPE"; then
        print_warning "端口 $port 关闭失败，请手动检查"
    else
        print_success "端口 $port 已关闭"
    fi
}

check_port_open() {
    local port="$1"
    local method="$2"

    case "$method" in
        ufw)
            ufw status 2>/dev/null | grep -qw "$port" && return 0 || return 1
            ;;
        firewalld)
            firewall-cmd --list-ports 2>/dev/null | grep -qw "${port}/tcp" && return 0 || return 1
            ;;
        iptables)
            iptables -C INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null && return 0 || return 1
            ;;
    esac
    return 1
}

open_port() {
    local port="$1"
    print_info "开放端口 $port ..."

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
            # 先删除所有现有规则，再插入一条（避免重复）
            while iptables -D INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null; do :; done
            iptables -I INPUT -p tcp --dport "$port" -j ACCEPT
            ;;
        none)
            print_warning "未检测到防火墙，请手动开放端口 $port"
            FW_PORT_OK=true
            return 0
            ;;
    esac

    sleep 1
    if check_port_open "$port" "$FW_TYPE"; then
        print_success "端口 $port 已开放"
        FW_PORT_OK=true
    else
        print_warning "端口 $port 开放验证失败，请手动检查"
        FW_PORT_OK=false
    fi
}

XRAY_INSTALL_CMD="bash -c \"\$(curl -L https://github.com/XTLS/Xray-install/raw/main/install-release.sh)\" @ install"

check_xray() {
    if [ -f "$XRAY_BIN" ]; then
        local ver
        ver=$("$XRAY_BIN" version 2>&1 | head -1 || echo "未知")
        print_info "检测到 xray: $ver"
        return 0
    fi

    print_info "未找到 xray，正在自动安装..."
    install_if_missing "curl" "curl"

    if eval "$XRAY_INSTALL_CMD"; then
        print_success "Xray 安装完成"
        if [ -f "$XRAY_BIN" ]; then
            local ver
            ver=$("$XRAY_BIN" version 2>&1 | head -1 || echo "未知")
            print_info "xray 版本: $ver"
            return 0
        fi
    fi

    print_error "Xray 安装失败"
    exit 1
}

generate_keys() {
    print_info "生成 x25519 公私钥..."

    local key_output
    key_output=$("$XRAY_BIN" x25519 2>&1) || {
        print_error "x25519 密钥生成失败"
        exit 1
    }

    PRIVATE_KEY=$(printf '%s' "$key_output" | sed -n 's/.*PrivateKey: *//p' | head -1)
    PUBLIC_KEY=$(printf '%s' "$key_output"  | sed -n 's/.*(PublicKey): *//p' | head -1)

    if [ -z "$PRIVATE_KEY" ] || [ -z "$PUBLIC_KEY" ]; then
        print_error "解析公私钥失败"
        echo "Raw output:"
        printf '%s\n' "$key_output"
        exit 1
    fi

    print_success "Private key: $PRIVATE_KEY"
    print_success "Public key:  $PUBLIC_KEY"
}

random_hex() {
    local len="$1"
    cat /dev/urandom 2>/dev/null | tr -dc 'a-f0-9' | head -c "$len" 2>/dev/null || \
    openssl rand -hex $(( len / 2 )) 2>/dev/null | head -c "$len" 2>/dev/null || \
    { for i in $(seq 1 "$len"); do printf '%x' $(( RANDOM % 16 )); done; }
}

random_digits() {
    local len="$1"
    local result=""
    for i in $(seq 1 "$len"); do
        result="${result}$(( RANDOM % 10 ))"
    done
    echo "$result"
}

generate_clientid() {
    CLIENT_ID=$(random_digits 10)
    print_info "生成 clients.id: $CLIENT_ID"
}

generate_shortid() {
    SHORT_ID=$(random_hex 12)
    print_info "生成 shortId:    $SHORT_ID"
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

# 全量模式：返回旧的所有入站端口列表（空格分隔）
write_config_full() {
    print_info "全量模式：覆盖写入配置文件..."

    # 获取旧的所有入站端口
    local old_ports
    old_ports=$(get_all_inbound_ports)
    
    mkdir -p "$XRAY_CONFIG_DIR"

    cat > "$XRAY_CONFIG_FILE" << XRAYEOF
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
      "tag": "proxy",
      "protocol": "vless",
      "settings": {
        "vnext": [
          {
            "address": "${UPSTREAM_ADDRESS}",
            "port": ${UPSTREAM_PORT},
            "users": [
              {
                "id": "${UPSTREAM_ID}",
                "flow": "xtls-rprx-vision",
                "encryption": "none"
              }
            ]
          }
        ]
      },
      "streamSettings": {
        "network": "tcp",
        "security": "reality",
        "realitySettings": {
          "serverName": "${UPSTREAM_SNI}",
          "fingerprint": "chrome",
          "show": false,
          "publicKey": "${UPSTREAM_PBK}",
          "shortId": "${UPSTREAM_SID}",
          "spiderX": "/"
        }
      }
    }
  ],
  "routing": {
    "domainStrategy": "IPIfNonMatch",
    "rules": [
      {
        "type": "field",
        "inboundTag": [
          "inbound-${PORT}"
        ],
        "outboundTag": "proxy"
      }
    ]
  }
}
XRAYEOF

    chmod 644 "$XRAY_CONFIG_FILE"
    print_success "配置文件已写入: $XRAY_CONFIG_FILE"
    
    # 返回旧端口列表
    echo "$old_ports"
}

# 增量模式：返回被删除的入站端口列表（空格分隔）
write_config_incremental() {
    print_info "增量模式：更新/添加配置到现有文件..."
    mkdir -p "$XRAY_CONFIG_DIR"

    # 记录将被删除的端口
    local deleted_ports=""

    # 如果配置文件不存在或不是合法JSON，创建空骨架
    if [ ! -f "$XRAY_CONFIG_FILE" ] || ! jq empty "$XRAY_CONFIG_FILE" 2>/dev/null; then
        print_info "配置文件不存在或无效，初始化空配置..."
        cat > "$XRAY_CONFIG_FILE" << EOF
{
  "log": {"loglevel": "warning"},
  "inbounds": [],
  "outbounds": [],
  "routing": {
    "domainStrategy": "IPIfNonMatch",
    "rules": []
  }
}
EOF
    fi

    # 定义新入站 tag 和新出站 tag
    local inbound_tag="inbound-${PORT}"
    local outbound_tag="out-${UPSTREAM_ADDRESS}-${UPSTREAM_PORT}"

    # 生成 serverNames 数组（纯JSON数组）
    local server_names_array
    server_names_array=$(echo "$SERVER_NAMES" | tr ',' '\n' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//' | jq -R . | jq -s .)

    # 构建新 inbound 对象（JSON）
    local new_inbound
    new_inbound=$(jq -n \
        --arg port "$PORT" \
        --arg client_id "$CLIENT_ID" \
        --arg dest "$DEST" \
        --arg private_key "$PRIVATE_KEY" \
        --arg short_id "$SHORT_ID" \
        --arg inbound_tag "$inbound_tag" \
        --argjson server_names "$server_names_array" \
        '{
            port: ($port | tonumber),
            protocol: "vless",
            settings: {
                clients: [{ id: $client_id, flow: "xtls-rprx-vision" }],
                decryption: "none"
            },
            streamSettings: {
                network: "tcp",
                security: "reality",
                realitySettings: {
                    show: false,
                    dest: $dest,
                    xver: 0,
                    serverNames: $server_names,
                    privateKey: $private_key,
                    shortIds: [$short_id],
                    fingerprint: "chrome"
                }
            },
            sniffing: {
                enabled: true,
                destOverride: ["http", "tls"]
            },
            tag: $inbound_tag
        }')

    # 构建新 outbound 对象（JSON）
    local new_outbound
    new_outbound=$(jq -n \
        --arg address "$UPSTREAM_ADDRESS" \
        --arg port "$UPSTREAM_PORT" \
        --arg upstream_id "$UPSTREAM_ID" \
        --arg upstream_sni "$UPSTREAM_SNI" \
        --arg upstream_pbk "$UPSTREAM_PBK" \
        --arg upstream_sid "$UPSTREAM_SID" \
        --arg outbound_tag "$outbound_tag" \
        '{
            tag: $outbound_tag,
            protocol: "vless",
            settings: {
                vnext: [{
                    address: $address,
                    port: ($port | tonumber),
                    users: [{
                        id: $upstream_id,
                        flow: "xtls-rprx-vision",
                        encryption: "none"
                    }]
                }]
            },
            streamSettings: {
                network: "tcp",
                security: "reality",
                realitySettings: {
                    serverName: $upstream_sni,
                    fingerprint: "chrome",
                    show: false,
                    publicKey: $upstream_pbk,
                    shortId: $upstream_sid,
                    spiderX: "/"
                }
            }
        }')

    # 1. 检查是否已存在相同上游（address+port）的出站
    local outbound_idx
    outbound_idx=$(jq --arg addr "$UPSTREAM_ADDRESS" --argjson port "$UPSTREAM_PORT" \
        '.outbounds | map(.settings.vnext[0].address == $addr and .settings.vnext[0].port == $port) | index(true)' \
        "$XRAY_CONFIG_FILE")

    if [ "$outbound_idx" != "null" ] && [ -n "$outbound_idx" ]; then
        # 上游已存在，需要删除整套旧配置
        print_info "检测到已存在上游 $UPSTREAM_ADDRESS:$UPSTREAM_PORT，将删除其整套配置（出站+关联入站+路由规则）后重新添加。"

        # 获取该出站的 tag
        local old_outbound_tag
        old_outbound_tag=$(jq -r --argjson idx "$outbound_idx" '.outbounds[$idx].tag' "$XRAY_CONFIG_FILE")
        if [ -z "$old_outbound_tag" ] || [ "$old_outbound_tag" = "null" ]; then
            old_outbound_tag="$outbound_tag"
        fi
        print_info "旧出站 tag: $old_outbound_tag"

        # 查找所有 outboundTag == old_outbound_tag 的规则，提取其 inboundTag 数组（需要删除的入站标签）
        local inbound_tags_to_delete
        inbound_tags_to_delete=$(jq -c --arg tag "$old_outbound_tag" \
            '[.routing.rules[] | select(.outboundTag == $tag) | .inboundTag[]] | unique' \
            "$XRAY_CONFIG_FILE")
        print_info "需要删除的入站标签列表: $inbound_tags_to_delete"

        # 记录这些入站对应的端口，以便稍后关闭防火墙
        if [ "$inbound_tags_to_delete" != "[]" ] && [ -n "$inbound_tags_to_delete" ]; then
            for tag in $(echo "$inbound_tags_to_delete" | jq -r '.[]'); do
                local port
                port=$(jq -r --arg t "$tag" '.inbounds[] | select(.tag == $t) | .port' "$XRAY_CONFIG_FILE" 2>/dev/null)
                if [ -n "$port" ] && [ "$port" != "null" ]; then
                    deleted_ports="$deleted_ports $port"
                fi
                print_info "删除入站 tag=$tag"
                jq --arg t "$tag" '.inbounds = [.inbounds[] | select(.tag != $t)]' "$XRAY_CONFIG_FILE" > "$XRAY_CONFIG_FILE.tmp"
                mv "$XRAY_CONFIG_FILE.tmp" "$XRAY_CONFIG_FILE"
            done
        fi

        # 删除该出站
        print_info "删除出站 tag=$old_outbound_tag"
        jq --arg t "$old_outbound_tag" '.outbounds = [.outbounds[] | select(.tag != $t)]' "$XRAY_CONFIG_FILE" > "$XRAY_CONFIG_FILE.tmp"
        mv "$XRAY_CONFIG_FILE.tmp" "$XRAY_CONFIG_FILE"

        # 删除所有 outboundTag == old_outbound_tag 的路由规则
        print_info "删除所有 outboundTag=$old_outbound_tag 的路由规则"
        jq --arg t "$old_outbound_tag" '.routing.rules = [.routing.rules[] | select(.outboundTag != $t)]' "$XRAY_CONFIG_FILE" > "$XRAY_CONFIG_FILE.tmp"
        mv "$XRAY_CONFIG_FILE.tmp" "$XRAY_CONFIG_FILE"

        print_success "旧配置已删除，现在添加新配置..."
    else
        print_info "未找到上游 $UPSTREAM_ADDRESS:$UPSTREAM_PORT，将直接新增配置。"
    fi

    # 2. 添加新的入站、出站和路由规则
    # 先检查入站端口是否已被其他配置占用
    local inbound_exists
    inbound_exists=$(jq --argjson port "$PORT" '.inbounds | map(.port == $port) | any' "$XRAY_CONFIG_FILE")
    if [ "$inbound_exists" = "true" ]; then
        print_warning "端口 $PORT 已被其他入站使用（不属于当前上游），将覆盖更新该入站。"
        local inbound_idx
        inbound_idx=$(jq --argjson port "$PORT" '.inbounds | map(.port == $port) | index(true)' "$XRAY_CONFIG_FILE")
        jq --argjson idx "$inbound_idx" --argjson new_in "$new_inbound" \
            '.inbounds[$idx] = $new_in' "$XRAY_CONFIG_FILE" > "$XRAY_CONFIG_FILE.tmp"
        mv "$XRAY_CONFIG_FILE.tmp" "$XRAY_CONFIG_FILE"
    else
        jq --argjson new_in "$new_inbound" '.inbounds += [$new_in]' "$XRAY_CONFIG_FILE" > "$XRAY_CONFIG_FILE.tmp"
        mv "$XRAY_CONFIG_FILE.tmp" "$XRAY_CONFIG_FILE"
    fi

    # 添加新出站
    jq --argjson new_out "$new_outbound" '.outbounds += [$new_out]' "$XRAY_CONFIG_FILE" > "$XRAY_CONFIG_FILE.tmp"
    mv "$XRAY_CONFIG_FILE.tmp" "$XRAY_CONFIG_FILE"

    # 添加新路由规则（确保不重复）
    jq --arg out_tag "$outbound_tag" \
        '.routing.rules = [.routing.rules[] | select(.outboundTag != $out_tag)]' \
        "$XRAY_CONFIG_FILE" > "$XRAY_CONFIG_FILE.tmp"
    mv "$XRAY_CONFIG_FILE.tmp" "$XRAY_CONFIG_FILE"

    local new_rule
    new_rule=$(jq -n --arg out_tag "$outbound_tag" --arg in_tag "$inbound_tag" \
        '{type: "field", inboundTag: [$in_tag], outboundTag: $out_tag}')
    jq --argjson new_rule "$new_rule" '.routing.rules += [$new_rule]' "$XRAY_CONFIG_FILE" > "$XRAY_CONFIG_FILE.tmp"
    mv "$XRAY_CONFIG_FILE.tmp" "$XRAY_CONFIG_FILE"

    print_success "增量配置更新完成: $XRAY_CONFIG_FILE"
    
    # 返回被删除的端口列表（去除首尾空格）
    echo "$deleted_ports" | xargs
}

# 配置防火墙：关闭指定端口列表，开放新端口
configure_firewall() {
    local close_ports="$1"   # 空格分隔的端口列表
    local new_port="$2"
    
    detect_firewall
    
    # 关闭旧端口
    if [ -n "$close_ports" ]; then
        for port in $close_ports; do
            if [ -n "$port" ] && [ "$port" != "$new_port" ]; then
                close_port "$port"
            fi
        done
    fi
    
    # 开放新端口
    open_port "$new_port"
}

restart_xray() {
    print_info "重启 xray 服务..."
    XRAY_RUNNING=false

    if command -v systemctl &> /dev/null && systemctl is-active --quiet xray 2>/dev/null; then
        systemctl restart xray
        sleep 3
        if systemctl is-active --quiet xray 2>/dev/null; then
            print_success "xray 服务重启成功 (systemd)"
            XRAY_RUNNING=true
            return 0
        fi
    fi

    if command -v systemctl &> /dev/null; then
        print_info "尝试 systemctl start..."
        systemctl start xray 2>/dev/null || true
        sleep 3
        if systemctl is-active --quiet xray 2>/dev/null; then
            print_success "xray 服务启动成功 (systemd)"
            XRAY_RUNNING=true
            return 0
        fi
    fi

    print_info "直接启动 xray 进程..."
    pkill -f "xray run" 2>/dev/null || true
    sleep 1
    nohup "$XRAY_BIN" run -config "$XRAY_CONFIG_FILE" > /var/log/xray.log 2>&1 &
    sleep 3

    if pgrep -f "xray run" > /dev/null 2>&1; then
        print_success "xray 进程已启动 (PID: $(pgrep -f 'xray run' | head -1))"
        XRAY_RUNNING=true
    else
        print_error "xray 启动失败: cat /var/log/xray.log"
        XRAY_RUNNING=false
        return 1
    fi
}

upload_config() {
    print_info "上报配置到 API..."

    local request_body
    request_body=$(cat << EOF
{
  "resourcesIp": "${SERVER_IP}",
  "publicBrokerKey": "${PUBLIC_KEY}",
  "sni": "${SERVER_NAMES}",
  "shortId": "${SHORT_ID}",
  "userId": "${CLIENT_ID}",
  "nodePort": "${PORT}"
}
EOF
)
    local response
    response=$(curl -s --connect-timeout 10 --max-time 30 \
        -X POST \
        -H "Content-Type: application/json" \
        -d "$request_body" \
        "$API_URL" 2>&1) || {
        print_error "API 请求失败: $response"
        return 1
    }

    local code
    code=$(echo "$response" | grep -o '"code"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*$')
    if [ "$code" != "200" ]; then
        local err_msg
        err_msg=$(echo "$response" | grep -o '"msg"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"msg"[[:space:]]*:[[:space:]]*"//;s/"$//')
        print_error "API 返回错误 (code=$code): $err_msg"
        return 1
    fi

    PASSWORD=$(echo "$response" | grep -o '"message"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"message"[[:space:]]*:[[:space:]]*"//;s/"$//')
    if [ -z "$PASSWORD" ]; then
        print_error "解析查询密码失败"
        return 1
    fi

    print_success "上报成功"
    return 0
}

status_icon() {
    if [ "$1" = "true" ] || [ "$1" = "1" ]; then
        echo -e "${GREEN}✓${NC}"
    else
        echo -e "${RED}✗${NC}"
    fi
}

print_result() {
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║         中转节点 系统检测 & 配置报告        ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════╝${NC}"
    echo ""

    echo -e "  ${CYAN}系统信息${NC}"
    if command -v lsb_release &> /dev/null; then
        echo -e "  发行版:  $(lsb_release -ds 2>/dev/null || echo '-')"
    elif [ -f /etc/os-release ]; then
        echo -e "  发行版:  $(. /etc/os-release && echo "$NAME $VERSION")"
    fi
    echo -e "  内核:    $(uname -r)"
    echo -e "  CPU:     $(nproc 2>/dev/null || echo 1) 核"
    echo ""

    local cc_display
    cc_display=$(sysctl -n net.ipv4.tcp_congestion_control 2>/dev/null || echo "unknown")
    local qdisc_display
    qdisc_display=$(sysctl -n net.core.default_qdisc 2>/dev/null || echo "unknown")
    echo -e "  ${CYAN}BBR 加速${NC}"
    printf "  状态:    %b  %s (cc=%s, qdisc=%s)\n" \
        "$(status_icon "$BBR_ENABLED")" \
        "$([ "$BBR_ENABLED" = true ] && echo "已启用" || echo "未启用")" \
        "$cc_display" "$qdisc_display"
    echo ""

    echo -e "  ${CYAN}防火墙${NC}"
    echo -e "  类型:    $FW_TYPE"
    printf "  端口 %s: %b  %s\n" \
        "$PORT" \
        "$(status_icon "$FW_PORT_OK")" \
        "$([ "$FW_PORT_OK" = true ] && echo "已开放" || echo "未开放")"
    echo ""

    echo -e "  ${CYAN}Xray 服务${NC}"
    printf "  xray:    %b  %s\n" \
        "$(status_icon "$XRAY_RUNNING")" \
        "$([ "$XRAY_RUNNING" = true ] && echo "运行中" || echo "未运行")"
    echo -e "  端口:    $PORT"
    echo -e "  协议:    vless + reality + tcp"
    echo -e "  回落:    $DEST"
    echo -e "  域名:    $SERVER_NAMES"
    echo -e "  Client ID: $CLIENT_ID"
    echo -e "  公钥:    $PUBLIC_KEY"
    echo -e "  短ID:    $SHORT_ID"
    echo ""

    echo -e "  ${CYAN}上游入口节点${NC}"
    echo -e "  地址:    $UPSTREAM_ADDRESS:$UPSTREAM_PORT"
    echo -e "  UUID:    $UPSTREAM_ID"
    echo -e "  公钥:    $UPSTREAM_PBK"
    echo -e "  短ID:    $UPSTREAM_SID"
    echo -e "  SNI:     $UPSTREAM_SNI"
    echo ""

    echo -e "  ${CYAN}配置查询链接${NC}"
    if [ -n "$PASSWORD" ]; then
        local full_url="${QUERY_BASE_URL}/${PASSWORD}"
        echo -e "  ${CYAN}${full_url}${NC}"
        echo ""
    else
        echo -e "  ${RED}上报失败，未获取查询链接${NC}"
    fi
    echo ""
    echo -e "${GREEN}════════════════════════════════════════════${NC}"
}

install_qrencode() {
    # 二维码功能已移除，此函数为空，仅保留以兼容调用
    :
}

main() {
    PORT=""
    SERVER_IP=""

    while [[ $# -gt 0 ]]; do
        case "$1" in
            -p|--port)               PORT="$2"; shift 2 ;;
            -i|--ip)                 SERVER_IP="$2"; shift 2 ;;
            -d|--dest)               DEST="$2"; shift 2 ;;
            -s|--server-names)       SERVER_NAMES="$2"; shift 2 ;;
            -u|--upstream-address)   UPSTREAM_ADDRESS="$2"; shift 2 ;;
            -r|--upstream-port)      UPSTREAM_PORT="$2"; shift 2 ;;
            -n|--upstream-id)        UPSTREAM_ID="$2"; shift 2 ;;
            -k|--upstream-pbk)       UPSTREAM_PBK="$2"; shift 2 ;;
            -t|--upstream-sid)       UPSTREAM_SID="$2"; shift 2 ;;
            -w|--upstream-sni)       UPSTREAM_SNI="$2"; shift 2 ;;
            -m|--mode)               MODE="$2"; shift 2 ;;
            -h|--help)               usage ;;
            *) print_error "未知参数: $1"; usage ;;
        esac
    done

    [ -z "$PORT" ] && { print_error "必须指定端口号 (-p/--port)"; usage; }
    [[ "$PORT" =~ ^[0-9]+$ ]] && [ "$PORT" -ge 1 ] && [ "$PORT" -le 65535 ] || {
        print_error "端口号必须是 1-65535 之间的整数"; exit 1;
    }

    [ -z "$UPSTREAM_ADDRESS" ] && { print_error "必须指定上游入口节点IP (-u/--upstream-address)"; usage; }
    [ -z "$UPSTREAM_PORT" ] && { print_error "必须指定上游入口节点端口 (-r/--upstream-port)"; usage; }
    [ -z "$UPSTREAM_ID" ] && { print_error "必须指定上游入口节点UUID (-n/--upstream-id)"; usage; }
    [ -z "$UPSTREAM_PBK" ] && { print_error "必须指定上游入口节点publicKey (-k/--upstream-pbk)"; usage; }
    [ -z "$UPSTREAM_SID" ] && { print_error "必须指定上游入口节点shortId (-t/--upstream-sid)"; usage; }

    if [ "$MODE" != "full" ] && [ "$MODE" != "incremental" ]; then
        print_error "模式必须是 full 或 incremental"
        usage
    fi

    if [ -z "$UPSTREAM_SNI" ]; then
        UPSTREAM_SNI="${SERVER_NAMES%%,*}"
        print_info "上游SNI未指定，使用本节点server-names第一个域名: $UPSTREAM_SNI"
    fi

    echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║     Xray 中转节点 一键部署脚本             ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
    echo ""

    echo -e "${CYAN}[Step 1/7]${NC} 安装依赖"
    ensure_deps

    if [ -z "$SERVER_IP" ]; then
        print_info "未指定IP，自动检测公网IP..."
        SERVER_IP=$(detect_public_ip)
        [ -z "$SERVER_IP" ] && { print_error "自动检测公网IP失败，请使用 -i/--ip 手动指定"; usage; }
    fi
    print_info "服务器IP: $SERVER_IP"

    echo ""
    echo -e "${CYAN}[Step 2/7]${NC} 开启 BBR 加速"
    enable_bbr

    echo ""
    echo -e "${CYAN}[Step 3/7]${NC} 生成密钥 & 配置"
    check_xray
    generate_keys
    generate_clientid
    generate_shortid
    SERVER_NAMES_JSON=$(build_server_names_json)

    # 根据模式生成配置，并获取需要关闭的端口列表
    local close_ports=""
    if [ "$MODE" = "full" ]; then
        close_ports=$(write_config_full)
    else
        close_ports=$(write_config_incremental)
    fi

    echo ""
    echo -e "${CYAN}[Step 4/7]${NC} 配置防火墙"
    configure_firewall "$close_ports" "$PORT"

    echo ""
    echo -e "${CYAN}[Step 5/7]${NC} 重启 Xray"
    restart_xray

    echo ""
    echo -e "${CYAN}[Step 6/7]${NC} 上报配置"
    upload_config || true

    echo ""
    echo -e "${CYAN}[Step 7/7]${NC} 安装二维码依赖（已禁用二维码显示）"
    install_qrencode

    echo ""
    print_result
}

main "$@"
