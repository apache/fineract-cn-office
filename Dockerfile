FROM openjdk:8-jdk-alpine

ARG office_port=2023

ENV server.max-http-header-size=16384 \
    cassandra.clusterName="Test Cluster" \
    server.port=$office_port

WORKDIR /tmp
COPY office-service-boot-0.1.0-BUILD-SNAPSHOT.jar .

CMD ["java", "-jar", "office-service-boot-0.1.0-BUILD-SNAPSHOT.jar"]
