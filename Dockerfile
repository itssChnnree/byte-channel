# 使用官方 OpenJDK 11 轻量级镜像作为基础
FROM openjdk:11-jdk-slim

# 设置工作目录（容器内的应用运行目录）
WORKDIR /app

# 将宿主机 jar 包复制到容器工作目录
# 注意：Dockerfile 需放在 /root/ruoyi_admin 同级目录，或修改路径
COPY /root/ruoyi_admin/ruoyi-admin.jar /app/ruoyi-admin.jar

# 暴露应用端口（根据实际修改，若依默认8080）
EXPOSE 3452

# 设置JVM参数（按需调整）
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dspring.profiles.active=prod"

# 启动应用（使用 exec 形式确保信号传递）
CMD exec java $JAVA_OPTS -jar ruoyi-admin.jar