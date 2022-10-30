FROM openjdk:17-jdk-slim

COPY . .

RUN ./gradlew build -x test

COPY ./build/libs/*-SNAPSHOT.jar /app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "/app.jar"]