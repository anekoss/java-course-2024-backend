FROM openjdk:21
WORKDIR app
COPY /target/scrapper.jar /app/scrapper.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080