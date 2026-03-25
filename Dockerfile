# 1. Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Run Stage
FROM eclipse-temurin:21-jre-alpine

# 🛠️ INSTALL CHROME & DRIVER FOR SCRAPING
RUN apk update && apk add --no-cache chromium chromium-chromedriver

# Tell Selenium where Chrome is
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROME_DRIVER=/usr/bin/chromedriver

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
