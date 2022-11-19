FROM arm64v8/openjdk:17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:17-oraclelinux8
COPY --from=build home/app/target/ms-ccom-0.0.1-SNAPSHOT.jar /home/app/app.jar
EXPOSE 9009
ENTRYPOINT ["java", "-jar", "/home/app/app.jar"]
