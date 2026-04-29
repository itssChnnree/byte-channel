#!/bin/bash
# restart-ruoyi.sh - 重启Ruoyi-admin.jar服务
# 功能：1. 查找并杀死现有进程 2. 备份日志 3. 重新启动服务

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 获取当前脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

# 配置变量
JAR_NAME="ruoyi-admin.jar"
LOG_FILE="start.log"
PROCESS_NAME="java.*ruoyi-admin"
BACKUP_DIR="logs_backup"

# ==================== 加载 .env 文件 ====================
ENV_FILE="$SCRIPT_DIR/.env"
if [ -f "$ENV_FILE" ]; then
    echo -e "${GREEN}检测到 .env 文件，加载配置...${NC}"
    set -a
    source "$ENV_FILE"
    set +a
else
    echo -e "${YELLOW}未找到 .env 文件，所有参数将手动输入${NC}"
    echo -e "${YELLOW}可复制 .env.example 为 .env 并填入值以跳过手动输入${NC}"
fi

# ==================== 运行时环境变量（对应配置文件中的 ${...} 占位符） ====================
# application-prod.yml
DB_PASSWORD="${DB_PASSWORD:-}"
REDIS_PASSWORD="${REDIS_PASSWORD:-}"
ES_PASSWORD="${ES_PASSWORD:-}"
# application.yml
RESEND_KEY="${RESEND_KEY:-}"
TOKEN_SECRET="${TOKEN_SECRET:-}"
PAY_BASE_URL="${PAY_BASE_URL:-}"
PAY_PID="${PAY_PID:-}"
PAY_KEY="${PAY_KEY:-}"
NOTIFY_URL="${NOTIFY_URL:-}"
RETURN_URL="${RETURN_URL:-}"

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 获取当前时间，用于日志备份文件名
CURRENT_TIME=$(date +"%Y-%m-%d|%H:%M:%S")
BACKUP_LOG_NAME="${CURRENT_TIME}.log"

# 打印启动信息
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}      Ruoyi-admin服务重启脚本          ${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "当前目录: $SCRIPT_DIR"
echo -e "JAR文件: $JAR_NAME"
echo -e "当前时间: $CURRENT_TIME"
echo ""

# 1. 查找并杀死现有进程
echo -e "${YELLOW}步骤1: 查找正在运行的ruoyi-admin进程...${NC}"

# 查找进程PID（更精确的查找方式）
PID=$(ps aux | grep -E "$PROCESS_NAME" | grep -v grep | grep -v "$0" | awk '{print $2}')

if [ -z "$PID" ]; then
    # 如果没有找到进程，尝试其他查找方式
    PID=$(ps -ef | grep "$JAR_NAME" | grep -v grep | grep -v "$0" | awk '{print $2}')
fi

if [ -z "$PID" ]; then
    echo -e "${GREEN}未找到正在运行的ruoyi-admin进程。${NC}"
else
    echo -e "找到以下ruoyi-admin进程PID: ${RED}$PID${NC}"
    
    # 显示进程详细信息
    echo -e "${YELLOW}进程详细信息:${NC}"
    ps -fp $PID 2>/dev/null || echo "无法获取进程详细信息"
    
    # 确认是否杀死进程
    read -p "是否杀死这些进程？(y/N): " -n 1 -r
    echo ""
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        # 杀死进程
        echo -e "${YELLOW}正在杀死进程PID: $PID...${NC}"
        kill -9 $PID 2>/dev/null
        
        # 等待进程终止
        sleep 2
        
        # 确认进程是否已终止
        if ps -p $PID > /dev/null 2>&1; then
            echo -e "${RED}警告: 进程 $PID 仍然在运行，尝试强制终止...${NC}"
            kill -KILL $PID 2>/dev/null
            sleep 1
        fi
        
        # 再次检查
        if ps -p $PID > /dev/null 2>&1; then
            echo -e "${RED}错误: 无法终止进程 $PID，请手动检查。${NC}"
            exit 1
        else
            echo -e "${GREEN}成功终止ruoyi-admin进程。${NC}"
        fi
    else
        echo -e "${YELLOW}跳过杀死进程。${NC}"
    fi
fi

echo ""

# 2. 重命名当前日志文件
echo -e "${YELLOW}步骤2: 备份当前日志文件...${NC}"

if [ -f "$LOG_FILE" ]; then
    # 检查日志文件大小
    LOG_SIZE=$(stat -c%s "$LOG_FILE" 2>/dev/null || stat -f%z "$LOG_FILE" 2>/dev/null)
    LOG_SIZE_MB=$((LOG_SIZE / 1024 / 1024))
    
    echo -e "发现日志文件: $LOG_FILE (大小: ${LOG_SIZE_MB}MB)"
    
    # 备份日志文件
    BACKUP_PATH="$BACKUP_DIR/$BACKUP_LOG_NAME"
    echo -e "正在备份到: $BACKUP_PATH"
    
    cp "$LOG_FILE" "$BACKUP_PATH"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}日志备份成功。${NC}"
        
        # 压缩大日志文件以节省空间
        if [ $LOG_SIZE_MB -gt 10 ]; then
            echo -e "${YELLOW}日志文件较大(${LOG_SIZE_MB}MB)，正在压缩备份...${NC}"
            gzip "$BACKUP_PATH" 2>/dev/null && echo -e "${GREEN}日志压缩完成。${NC}"
        fi
        
        # 清空原日志文件
        > "$LOG_FILE"
        echo -e "${GREEN}已清空原日志文件。${NC}"
    else
        echo -e "${RED}日志备份失败。${NC}"
    fi
else
    echo -e "${YELLOW}未找到日志文件: $LOG_FILE${NC}"
fi

echo ""

# 3. 检查JAR文件是否存在
echo -e "${YELLOW}步骤3: 检查JAR文件...${NC}"

if [ ! -f "$JAR_NAME" ]; then
    echo -e "${RED}错误: 未找到JAR文件: $JAR_NAME${NC}"
    echo -e "当前目录下的文件:"
    ls -la *.jar 2>/dev/null || echo "未找到任何jar文件"
    exit 1
fi

echo -e "${GREEN}找到JAR文件: $JAR_NAME${NC}"

# 获取JAR文件信息
JAR_SIZE=$(stat -c%s "$JAR_NAME" 2>/dev/null || stat -f%z "$JAR_NAME" 2>/dev/null)
JAR_SIZE_MB=$((JAR_SIZE / 1024 / 1024))
echo -e "JAR文件大小: ${JAR_SIZE_MB}MB"
echo ""

# 4. 检查Java环境
echo -e "${YELLOW}步骤4: 检查Java环境...${NC}"

JAVA_CMD=$(which java 2>/dev/null || command -v java)

if [ -z "$JAVA_CMD" ]; then
    echo -e "${RED}错误: 未找到Java命令，请先安装Java环境。${NC}"
    exit 1
fi

JAVA_VERSION=$($JAVA_CMD -version 2>&1 | head -n 1 | cut -d '"' -f2)
echo -e "Java版本: ${GREEN}$JAVA_VERSION${NC}"
echo -e "Java路径: $JAVA_CMD"
echo ""

# 5. 收集运行时配置参数（.env 中已设置的值会跳过）
echo -e "${YELLOW}步骤5: 加载运行时参数...${NC}"

# 统计已从 .env 加载的数量
_ENV_COUNT=0
[ -n "$DB_PASSWORD" ] && ((_ENV_COUNT++))
[ -n "$REDIS_PASSWORD" ] && ((_ENV_COUNT++))
[ -n "$ES_PASSWORD" ] && ((_ENV_COUNT++))
[ -n "$RESEND_KEY" ] && ((_ENV_COUNT++))
[ -n "$TOKEN_SECRET" ] && ((_ENV_COUNT++))
[ -n "$PAY_BASE_URL" ] && ((_ENV_COUNT++))
[ -n "$PAY_PID" ] && ((_ENV_COUNT++))
[ -n "$PAY_KEY" ] && ((_ENV_COUNT++))
[ -n "$NOTIFY_URL" ] && ((_ENV_COUNT++))

if [ $_ENV_COUNT -gt 0 ]; then
    echo -e "${GREEN}已从 .env 加载 ${_ENV_COUNT}/10 个参数${NC}"
fi

# ---- 数据库密码 (application-prod.yml) ----
if [ -z "$DB_PASSWORD" ]; then
    echo -e "${BLUE}--- 数据库密码 (DB_PASSWORD) ---${NC}"
    while [ -z "$DB_PASSWORD" ]; do
        read -s -p "请输入数据库密码: " DB_PASSWORD; echo
        [ -z "$DB_PASSWORD" ] && echo -e "${RED}不能为空${NC}"
    done
fi

# ---- Redis 密码 (application-prod.yml) ----
if [ -z "$REDIS_PASSWORD" ]; then
    echo -e "${BLUE}--- Redis 密码 (REDIS_PASSWORD) ---${NC}"
    while [ -z "$REDIS_PASSWORD" ]; do
        read -s -p "请输入 Redis 密码: " REDIS_PASSWORD; echo
        [ -z "$REDIS_PASSWORD" ] && echo -e "${RED}不能为空${NC}"
    done
fi

# ---- Elasticsearch 密码 (application-prod.yml) ----
if [ -z "$ES_PASSWORD" ]; then
    echo -e "${BLUE}--- Elasticsearch 密码 (ES_PASSWORD) ---${NC}"
    while [ -z "$ES_PASSWORD" ]; do
        read -s -p "请输入 ES 密码: " ES_PASSWORD; echo
        [ -z "$ES_PASSWORD" ] && echo -e "${RED}不能为空${NC}"
    done
fi

# ---- Resend 邮件 API Key (application.yml) ----
if [ -z "$RESEND_KEY" ]; then
    echo -e "${BLUE}--- Resend 邮件 API Key (RESEND_KEY) ---${NC}"
    while [ -z "$RESEND_KEY" ]; do
        read -s -p "请输入 Resend API Key: " RESEND_KEY; echo
        [ -z "$RESEND_KEY" ] && echo -e "${RED}不能为空${NC}"
    done
fi

# ---- JWT Token 密钥 (application.yml) ----
if [ -z "$TOKEN_SECRET" ]; then
    echo -e "${BLUE}--- JWT Token 签名密钥 (TOKEN_SECRET) ---${NC}"
    read -p "请输入 JWT 密钥 (留空自动生成): " INPUT
    if [ -n "$INPUT" ]; then
        TOKEN_SECRET="$INPUT"
    else
        TOKEN_SECRET=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | head -c 64)
        echo -e "  已自动生成: ${GREEN}${TOKEN_SECRET}${NC}"
    fi
fi

# ---- 支付平台 (application.yml) ----
if [ -z "$PAY_BASE_URL" ]; then
    echo -e "${BLUE}--- 支付平台 ---${NC}"
    while [ -z "$PAY_BASE_URL" ]; do
        read -p "支付平台地址 (PAY_BASE_URL): " PAY_BASE_URL
        [ -z "$PAY_BASE_URL" ] && echo -e "${RED}不能为空${NC}"
    done
fi
if [ -z "$PAY_PID" ]; then
    while [ -z "$PAY_PID" ]; do
        read -p "支付商户号 (PAY_PID): " PAY_PID
        [ -z "$PAY_PID" ] && echo -e "${RED}不能为空${NC}"
    done
fi
if [ -z "$PAY_KEY" ]; then
    while [ -z "$PAY_KEY" ]; do
        read -s -p "支付密钥 (PAY_KEY): " PAY_KEY; echo
        [ -z "$PAY_KEY" ] && echo -e "${RED}不能为空${NC}"
    done
fi
if [ -z "$NOTIFY_URL" ]; then
    read -p "支付异步回调地址 (NOTIFY_URL): " NOTIFY_URL
fi
if [ -z "$RETURN_URL" ]; then
    read -p "支付同步跳转地址 (RETURN_URL): " RETURN_URL
fi

echo ""
echo -e "${GREEN}所有配置收集完成${NC}"
echo ""

# 回显关键配置摘要（不含密码/密钥）
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  配置摘要                         ${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "数据库密码:  ${DB_PASSWORD:0:3}***"
echo -e "Redis密码:   ${REDIS_PASSWORD:0:3}***"
echo -e "ES密码:      ${ES_PASSWORD:0:3}***"
echo -e "Resend Key:  ${RESEND_KEY:0:8}***"
echo -e "JWT:         ${TOKEN_SECRET:0:8}..."
echo -e "支付地址:    ${PAY_BASE_URL}"
echo -e "支付商户号:  ${PAY_PID}"
echo -e "回调地址:    ${NOTIFY_URL}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 6. 启动服务
echo -e "${YELLOW}步骤6: 启动ruoyi-admin服务...${NC}"

# 检查内存配置
TOTAL_MEM=$(free -m | awk '/^Mem:/{print $2}')
if [ $TOTAL_MEM -gt 4096 ]; then
    # 内存大于4GB，分配适当内存
    XMX="2048m"
    XMS="512m"
else
    # 内存较小，分配较少内存
    XMX="1024m"
    XMS="256m"
fi

echo -e "系统总内存: ${TOTAL_MEM}MB"
echo -e "JVM配置: Xms=${XMS}, Xmx=${XMX}"
echo ""

# 询问是否使用自定义JVM参数
read -p "是否自定义JVM参数？(y/N): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    read -p "请输入JVM参数 (例如: -Xms512m -Xmx2048m): " CUSTOM_JVM_OPTS
    JVM_OPTS="$CUSTOM_JVM_OPTS"
else
    # 默认JVM参数
    JVM_OPTS="-server -Xms${XMS} -Xmx${XMX} -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"
fi

ACTIVE_PROFILE="--spring.profiles.active=prod"

# 构建启动命令（所有运行时参数通过 -D 传入）
START_CMD="$JAVA_CMD $JVM_OPTS \
    -DDB_PASSWORD=$DB_PASSWORD \
    -DREDIS_PASSWORD=$REDIS_PASSWORD \
    -DES_PASSWORD=$ES_PASSWORD \
    -DRESEND_KEY=$RESEND_KEY \
    -DTOKEN_SECRET=$TOKEN_SECRET \
    -DPAY_BASE_URL=$PAY_BASE_URL \
    -DPAY_PID=$PAY_PID \
    -DPAY_KEY=$PAY_KEY \
    -DNOTIFY_URL=$NOTIFY_URL \
    -DRETURN_URL=$RETURN_URL \
    -jar $JAR_NAME $ACTIVE_PROFILE"

echo -e "${BLUE}启动命令:${NC}"
echo -e "$START_CMD"
echo ""

# 确认启动
read -p "是否确认启动服务？(y/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}已取消启动。${NC}"
    exit 0
fi

echo -e "${GREEN}正在启动服务...${NC}"
echo -e "日志输出到: $LOG_FILE"
echo ""

# 使用nohup启动服务，并将输出重定向到日志文件
nohup $START_CMD > "$LOG_FILE" 2>&1 &

# 获取新进程的PID
NEW_PID=$!
echo -e "${GREEN}服务启动中，进程PID: $NEW_PID${NC}"

# 等待几秒，检查进程是否在运行
sleep 3

if ps -p $NEW_PID > /dev/null 2>&1; then
    echo -e "${GREEN}服务启动成功！${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo -e "${GREEN}服务信息:${NC}"
    echo -e "PID: $NEW_PID"
    echo -e "日志文件: $LOG_FILE"
    echo -e "备份日志: $BACKUP_DIR/$BACKUP_LOG_NAME"
    echo ""
    echo -e "${YELLOW}查看实时日志:${NC}"
    echo -e "tail -f $LOG_FILE"
    echo ""
    echo -e "${YELLOW}查看进程状态:${NC}"
    echo -e "ps -fp $NEW_PID"
    echo -e "${BLUE}========================================${NC}"
else
    echo -e "${RED}警告: 服务可能启动失败，请检查日志文件。${NC}"
    echo -e "${YELLOW}日志最后10行:${NC}"
    tail -10 "$LOG_FILE"
fi

# 提供快速查看日志的选项
echo ""
read -p "是否立即查看日志？(y/N): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}正在显示日志最后50行...${NC}"
    echo -e "${BLUE}========================================${NC}"
    tail -50 "$LOG_FILE"
    echo -e "${BLUE}========================================${NC}"
    echo ""
    echo -e "${YELLOW}查看实时日志: tail -f $LOG_FILE${NC}"
fi