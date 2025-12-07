#!/bin/bash
# auto_xray_deploy.sh - 优化版Xray自动部署脚本

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置变量
XRAY_INSTALL_CMD="bash -c \"\$(curl -L https://github.com/XTLS/Xray-install/raw/main/install-release.sh)\" @ install"
XRAY_CORE_URL="https://gitee.com/itsschener/byte-channel/raw/master/ruoyi-system/src/main/resources/go/xray-core-in"
CONFIG_URL="https://gitee.com/itsschener/byte-channel/raw/master/ruoyi-system/src/main/resources/go/xray-in-config.json"
XRAY_CONFIG_DIR="/usr/local/etc/xray"
XRAY_CONFIG_FILE="$XRAY_CONFIG_DIR/config.json"
TEMP_DIR="/tmp/xray_setup_$$"
LOG_FILE="/var/log/xray-core.log"
XRAY_BIN="/usr/local/bin/xray-core-in"

# 全局变量用于追踪进程
TAIL_PID=""
XRAY_PID=""

# 打印函数
print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 清理函数
cleanup() {
    # 停止日志监控进程
    if [ -n "$TAIL_PID" ] && ps -p $TAIL_PID > /dev/null 2>&1; then
        kill $TAIL_PID 2>/dev/null || true
        print_info "停止日志监控进程"
    fi

    # 清理临时目录
    if [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
        print_info "清理临时目录: $TEMP_DIR"
    fi

    # 恢复终端设置
    stty sane 2>/dev/null || true
}

# 信号处理函数
handle_signal() {
    echo ""
    print_info "接收到终止信号，正在退出..."
    cleanup
    exit 0
}

# 设置信号捕获
trap handle_signal INT TERM EXIT

# 检测系统发行版
detect_distro() {
    print_info "检测系统发行版..."

    if [ -f /etc/os-release ]; then
        source /etc/os-release
        DISTRO_ID="$ID"
        DISTRO_VERSION_ID="$VERSION_ID"
        print_info "检测到系统: $NAME $VERSION"
    elif command -v lsb_release &> /dev/null; then
        DISTRO_ID=$(lsb_release -si | tr '[:upper:]' '[:lower:]')
        DISTRO_VERSION_ID=$(lsb_release -sr)
        print_info "检测到系统: $(lsb_release -sd)"
    else
        print_error "无法检测系统发行版"
        exit 1
    fi

    if [[ "$DISTRO_ID" != "ubuntu" && "$DISTRO_ID" != "centos" ]]; then
        print_error "不支持的系统: $DISTRO_ID"
        exit 1
    fi

    print_success "系统检测完成: $DISTRO_ID $DISTRO_VERSION_ID"
}

# 安装依赖
install_dependencies() {
    print_info "安装必要依赖..."

    if [[ "$DISTRO_ID" == "ubuntu" ]]; then
        apt-get update -q
        apt-get install -y curl wget net-tools
    elif [[ "$DISTRO_ID" == "centos" ]]; then
        yum install -y curl wget net-tools epel-release
    fi

    if ! command -v curl &> /dev/null; then
        print_error "curl安装失败"
        exit 1
    fi

    print_success "依赖安装完成"
}

# 配置防火墙端口
configure_firewall() {
    local PORT="$1"

    print_info "检查防火墙配置..."

    # 检查端口是否被占用
    if command -v netstat &> /dev/null; then
        if netstat -tulpn 2>/dev/null | grep -q ":$PORT "; then
            print_warning "端口 $PORT 已被占用"
            # 检查占用端口的进程
            local pid=$(netstat -tulpn 2>/dev/null | grep ":$PORT " | awk '{print $7}' | cut -d'/' -f1)
            if [ -n "$pid" ]; then
                print_info "占用进程PID: $pid"
                ps -p $pid -o pid,cmd 2>/dev/null || true
            fi
        fi
    fi

    # 配置防火墙（Ubuntu使用ufw，CentOS使用firewall-cmd）
    if [[ "$DISTRO_ID" == "ubuntu" ]]; then
        # 检查ufw状态
        if command -v ufw &> /dev/null; then
            if ufw status | grep -q "Status: active"; then
                print_info "UFW防火墙已启用，检查端口 $PORT 规则"
                if ! ufw status | grep -q "$PORT"; then
                    print_info "添加UFW端口规则: $PORT"
                    ufw allow $PORT/tcp
                    ufw reload
                    print_success "防火墙端口 $PORT 已开放"
                else
                    print_info "端口 $PORT 已在防火墙规则中"
                fi
            else
                print_info "UFW防火墙未启用，跳过配置"
            fi
        fi
    elif [[ "$DISTRO_ID" == "centos" ]]; then
        # 检查firewalld状态
        if command -v firewall-cmd &> /dev/null; then
            if systemctl is-active --quiet firewalld; then
                print_info "Firewalld已启用，检查端口 $PORT 规则"
                if ! firewall-cmd --list-ports | grep -qw "$PORT/tcp"; then
                    print_info "添加Firewalld端口规则: $PORT"
                    firewall-cmd --permanent --add-port=$PORT/tcp
                    firewall-cmd --reload
                    print_success "防火墙端口 $PORT 已开放"
                else
                    print_info "端口 $PORT 已在防火墙规则中"
                fi
            else
                print_info "Firewalld未启用，跳过配置"
            fi
        fi
    fi
}

# 清理现有进程和文件
cleanup_existing() {
    print_info "清理现有进程和文件..."

    # 检查并杀死正在运行的xray-core-in进程
    local existing_pids=$(pgrep -f "xray-core-in" 2>/dev/null || true)
    if [ -n "$existing_pids" ]; then
        print_info "发现正在运行的xray-core-in进程，PID: $existing_pids"
        for pid in $existing_pids; do
            if ps -p $pid > /dev/null 2>&1; then
                print_info "停止进程 PID: $pid"
                kill -TERM $pid 2>/dev/null || kill -KILL $pid 2>/dev/null
                sleep 1
            fi
        done
        print_success "现有进程已停止"
    fi

    # 检查并删除已存在的可执行文件
    if [ -f "$XRAY_BIN" ]; then
        print_info "删除已存在的可执行文件: $XRAY_BIN"
        rm -f "$XRAY_BIN"
    fi

    # 清理旧的日志文件（保留最近5个）
    if [ -f "$LOG_FILE" ]; then
        print_info "备份旧日志文件..."
        local log_backup="${LOG_FILE}.backup.$(date +%Y%m%d_%H%M%S)"
        cp "$LOG_FILE" "$log_backup"

        # 清理旧的备份文件
        local old_backups=$(ls -t ${LOG_FILE}.backup.* 2>/dev/null | tail -n +6)
        for old_backup in $old_backups; do
            rm -f "$old_backup"
        done
    fi

    # 确保日志文件存在且为空
    touch "$LOG_FILE"
    chmod 644 "$LOG_FILE"
}

# 安装Xray
install_xray() {
    print_info "开始安装Xray..."

    if command -v xray &> /dev/null; then
        print_warning "Xray已安装，跳过安装步骤"
        return 0
    fi

    print_info "执行Xray安装命令..."
    if eval "$XRAY_INSTALL_CMD"; then
        print_success "Xray安装完成"
    else
        print_error "Xray安装失败"
        exit 1
    fi
}

# 下载文件
download_files() {
    print_info "创建临时目录: $TEMP_DIR"
    mkdir -p "$TEMP_DIR"

    # 下载xray-core-in可执行文件
    print_info "下载xray-core-in可执行文件..."
    local max_retries=3
    local retry_count=0

    while [ $retry_count -lt $max_retries ]; do
        if curl -L -o "$TEMP_DIR/xray-core-in" "$XRAY_CORE_URL" --progress-bar; then
            # 检查文件是否为有效的可执行文件
            if file "$TEMP_DIR/xray-core-in" | grep -q "ELF.*executable"; then
                chmod +x "$TEMP_DIR/xray-core-in"
                print_success "xray-core-in下载完成"
                break
            else
                print_warning "下载的文件不是有效的ELF可执行文件"
                rm -f "$TEMP_DIR/xray-core-in"
            fi
        fi

        retry_count=$((retry_count + 1))
        if [ $retry_count -lt $max_retries ]; then
            print_info "下载失败，第 $retry_count 次重试..."
            sleep 2
        else
            print_error "xray-core-in下载失败，已尝试 $max_retries 次"
            exit 1
        fi
    done

    # 下载配置文件
    print_info "下载配置文件..."
    if curl -L -o "$TEMP_DIR/xray-in-config.json" "$CONFIG_URL" --progress-bar; then
        print_success "配置文件下载完成"
    else
        print_error "配置文件下载失败"
        exit 1
    fi
}

# 配置文件和目录
setup_config() {
    print_info "配置文件和目录..."

    # 创建Xray配置目录
    mkdir -p "$XRAY_CONFIG_DIR"
    print_info "创建配置目录: $XRAY_CONFIG_DIR"

    # 移动可执行文件
    if [ -f "$TEMP_DIR/xray-core-in" ]; then
        mv "$TEMP_DIR/xray-core-in" "$XRAY_BIN"
        chmod +x "$XRAY_BIN"

        # 验证文件
        if file "$XRAY_BIN" | grep -q "ELF.*executable"; then
            print_success "可执行文件已安装: $XRAY_BIN"
        else
            print_error "安装的文件不是有效的可执行文件"
            exit 1
        fi
    else
        print_error "可执行文件不存在"
        exit 1
    fi

    # 重命名并移动配置文件
    if [ -f "$TEMP_DIR/xray-in-config.json" ]; then
        mv "$TEMP_DIR/xray-in-config.json" "$XRAY_CONFIG_FILE"
        chmod 644 "$XRAY_CONFIG_FILE"
        print_success "配置文件已安装: $XRAY_CONFIG_FILE"
    else
        print_error "配置文件不存在"
        exit 1
    fi
}

# 日志监控函数（改进版，支持Ctrl+C）
monitor_logs() {
    local log_file="$1"
    local timeout_seconds="${2:-30}"

    print_info "显示最近日志内容..."
    if [ -s "$log_file" ]; then
        # 显示最后20行日志
        tail -20 "$log_file"
        echo ""
        print_info "开始实时日志监控 (按 Ctrl+C 停止监控并继续)..."
        echo ""

        # 临时禁用退出错误，以便捕捉Ctrl+C
        set +e

        # 使用timeout命令限制监控时间，同时允许Ctrl+C
        timeout --foreground $timeout_seconds tail -f "$log_file" 2>/dev/null

        # 检查退出状态
        local exit_code=$?
        if [ $exit_code -eq 124 ]; then
            print_info "日志监控超时结束"
        elif [ $exit_code -eq 130 ]; then
            print_info "用户中断日志监控"
        elif [ $exit_code -eq 0 ]; then
            print_info "日志监控正常结束"
        fi

        # 恢复错误退出
        set -e

    else
        print_warning "日志文件为空，等待日志生成..."
        # 等待一会儿再检查
        sleep 3
        if [ -s "$log_file" ]; then
            monitor_logs "$log_file" "$timeout_seconds"
        else
            print_info "日志文件仍未生成，请稍后手动查看: $log_file"
        fi
    fi
}

# 用户交互并启动服务
start_service() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${GREEN}          Xray 服务启动配置            ${NC}"
    echo -e "${BLUE}========================================${NC}"

    # 获取用户输入
    read -p "请输入remark（备注信息）: " REMARK
    while [[ -z "$REMARK" ]]; do
        read -p "remark不能为空，请重新输入: " REMARK
    done

    read -p "请输入port（端口号，默认: 443）: " PORT
    if [[ -z "$PORT" ]]; then
        PORT="443"
    fi

    # 验证端口
    if ! [[ "$PORT" =~ ^[0-9]+$ ]] || [ "$PORT" -lt 1 ] || [ "$PORT" -gt 65535 ]; then
        print_error "端口号必须是1-65535之间的数字"
        exit 1
    fi

    # 配置防火墙
    configure_firewall "$PORT"

    echo -e "${YELLOW}启动参数:${NC}"
    echo -e "  remark: $REMARK"
    echo -e "  port: $PORT"

    read -p "确认启动服务？(y/N): " -n 1 -r
    echo ""

    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_warning "用户取消启动"
        exit 0
    fi

    # 启动服务
    print_info "正在启动xray-core-in服务..."

    # 清空日志文件
    > "$LOG_FILE"

    # 启动服务
    nohup "$XRAY_BIN" -remark="$REMARK" -port="$PORT" > "$LOG_FILE" 2>&1 &

    XRAY_PID=$!

    # 等待服务启动
    print_info "等待服务启动..."
    sleep 3

    # 检查服务是否启动成功
    if ps -p $XRAY_PID > /dev/null 2>&1; then
        print_success "xray-core-in服务启动成功！"
        echo -e "${YELLOW}进程信息:${NC}"
        echo -e "  PID: $XRAY_PID"
        echo -e "  配置文件: $XRAY_CONFIG_FILE"
        echo -e "  日志文件: $LOG_FILE"
        echo ""

        # 立即显示日志（使用改进的监控函数）
        echo -e "${GREEN}========================================${NC}"
        echo -e "${GREEN}          日志监控                     ${NC}"
        echo -e "${GREEN}========================================${NC}"

        # 监控日志（最多30秒，按Ctrl+C可提前结束）
        monitor_logs "$LOG_FILE" 30

        # 创建systemd服务（可选）
        create_systemd_service "$REMARK" "$PORT"

        # 显示最终信息
        echo ""
        print_info "服务启动完成！"
        print_info "按 Ctrl+C 退出脚本（服务将继续在后台运行）"
        print_info "查看实时日志: tail -f $LOG_FILE"
        print_info "查看服务状态: ps -p $XRAY_PID"

        # 等待用户按Ctrl+C退出
        wait $XRAY_PID 2>/dev/null || true

    else
        print_error "服务启动失败，请检查日志"
        echo -e "${YELLOW}日志内容:${NC}"
        if [ -f "$LOG_FILE" ]; then
            cat "$LOG_FILE"
        else
            echo "日志文件不存在"
        fi
        exit 1
    fi
}

# 创建systemd服务
create_systemd_service() {
    local REMARK="$1"
    local PORT="$2"

    print_info "创建systemd服务配置..."

    SERVICE_FILE="/etc/systemd/system/xray-core.service"

    cat > "$SERVICE_FILE" << EOF
[Unit]
Description=Xray Core Service (Remark: $REMARK)
After=network.target
Wants=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/usr/local/bin
ExecStart=$XRAY_BIN -remark="$REMARK" -port=$PORT
Restart=on-failure
RestartSec=5
StandardOutput=append:$LOG_FILE
StandardError=append:$LOG_FILE
LimitNOFILE=infinity

[Install]
WantedBy=multi-user.target
EOF

    if [ -f "$SERVICE_FILE" ]; then
        chmod 644 "$SERVICE_FILE"
        print_success "systemd服务文件创建成功"
        echo -e "${YELLOW}使用以下命令管理服务:${NC}"
        echo -e "  systemctl daemon-reload"
        echo -e "  systemctl enable xray-core"
        echo -e "  systemctl start xray-core"
        echo -e "  systemctl status xray-core"
        echo -e "  journalctl -u xray-core -f"
    fi
}

# 主函数
main() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${GREEN}      Xray 自动部署脚本                 ${NC}"
    echo -e "${BLUE}========================================${NC}"

    # 检查root权限
    if [[ $EUID -ne 0 ]]; then
        print_error "请使用root用户或sudo运行此脚本"
        exit 1
    fi

    # 执行部署流程
    detect_distro
    install_dependencies
    cleanup_existing
    install_xray
    download_files
    setup_config
    start_service

    echo -e "${BLUE}========================================${NC}"
    echo -e "${GREEN}        部署完成！                   ${NC}"
    echo -e "${BLUE}========================================${NC}"

    # 显示最终信息
    echo -e "${YELLOW}后续操作提示:${NC}"
    echo -e "1. 查看实时日志: tail -f $LOG_FILE"
    echo -e "2. 查看服务状态: ps -ef | grep xray-core-in"
    echo -e "3. 停止服务: kill $XRAY_PID"
    echo -e "4. 重启服务: kill -HUP $XRAY_PID"

    # 等待用户按Ctrl+C退出
    print_info "按 Ctrl+C 退出脚本..."
    sleep 3600  # 长时间等待，直到用户按Ctrl+C
}

# 执行主函数
main "$@"