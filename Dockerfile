FROM openjdk:17-jdk-slim

COPY . .

RUN ./gradlew build -x test

RUN ls ./build/libs/

COPY /build/libs/*-SNAPSHOT.jar /app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "/app.jar"]
