FROM openjdk:17
LABEL authors="modocentral"
WORKDIR /app
COPY target/MessagingService-0.0.1-SNAPSHOT.jar MessagingService.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "MessagingService.jar"]