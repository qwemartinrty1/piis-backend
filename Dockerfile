# Этап сборки
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Финальный этап
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/supabase-java-crud-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]