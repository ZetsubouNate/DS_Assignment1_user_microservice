FROM openjdk:22-slim

COPY target/user_microservice-0.0.1-SNAPSHOT.jar ./app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
