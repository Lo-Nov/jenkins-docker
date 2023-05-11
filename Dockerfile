
#Docker Build Stage
FROM maven:3-jdk-11-alpine AS build

#Build stage
WORKDIR /opt/app

COPY ./ /opt/app
RUN mvn clean install -DskipTests

#Docker Build Stage
FROM openjdk:11-jdk-alpine

COPY --from=build /opt/app/target/*.jar app.jar

ENV PORT 8081
EXPOSE $PORT

ENTRYPOINT ["java","-jar","-Xmx1024M","-Dserver-port=${PORT}","app.jar"]



