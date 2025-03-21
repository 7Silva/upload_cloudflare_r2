FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

COPY target/cdn-r2-0.0.1.jar app.jar

EXPOSE 3006

ENTRYPOINT ["java","-jar","app.jar"]