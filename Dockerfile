FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
ENV MAVEN_OPTS="-Xmx256m"
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java","-jar","app.jar"]
