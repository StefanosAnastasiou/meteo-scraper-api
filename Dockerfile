FROM alpine:latest

RUN apk update && \
    apk upgrade && \
    apk add --no-cache bash maven openjdk17

RUN mkdir /app

WORKDIR /app

COPY /target/Meteo-Scraper-API.jar /app

EXPOSE 8445

CMD ["java", "-Duser.timezone=Europe/Athens", "-jar", "Meteo-Scraper-API.jar"]