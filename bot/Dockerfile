FROM openjdk:21
WORKDIR app
COPY /target/bot.jar /app/bot.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080