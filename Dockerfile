FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/shop.jar shop.jar
EXPOSE 8080
CMD ["java", "-jar", "shop.jar"]