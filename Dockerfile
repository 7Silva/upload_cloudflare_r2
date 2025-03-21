FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/cdn-r2-0.0.2.jar app.jar

EXPOSE 3006

ENTRYPOINT ["java","-jar","app.jar"]