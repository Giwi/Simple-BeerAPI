FROM openjdk:8-jre-alpine
MAINTAINER Giwi Soft <giwi@free.fr>
RUN mkdir -p /opt/beerApi
COPY application/Simple-BeerAPI-fat.jar /opt/beerApi/.
EXPOSE 4567
CMD ["java",  "-jar", "/opt/beerApi/Simple-BeerAPI-fat.jar"]