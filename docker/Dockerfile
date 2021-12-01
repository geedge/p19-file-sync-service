ARG JDK_IMAGE
ARG GO_IMAGE
#编译环境，生成二进制文件
#FROM 192.168.40.153:9080/common/golang:1.15.6 as builder
FROM ${GO_IMAGE} as builder

WORKDIR /build
COPY xjar.go /build/
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o xjar .

FROM ${JDK_IMAGE}
MAINTAINER Galaxy
VOLUME /tmp
WORKDIR /home/tsg/galaxy/p19-file-sync-service
COPY --from=builder /build .
COPY config config
ARG JAR_FILE
COPY ${JAR_FILE} p19-file-sync-service.xjar
#dockercompose set JAVA_OPTS
ENV JAVA_OPTS=" -Xmx1g -Xms1g -Xmn128m -Xss256k -XX:MetaspaceSize=128m -XX:MaxPermSize=128m -XX:SurvivorRatio=2 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:MaxTenuringThreshold=15 -XX:+UseCMSCompactAtFullCollection -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8
#ENV TZ=Asia/Almaty
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT [ "sh", "-c", "./xjar java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar p19-file-sync-service.xjar" ]