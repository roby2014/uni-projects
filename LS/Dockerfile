FROM openjdk:17
WORKDIR /usr/app
COPY .env .env
COPY ./static-content ./static-content
COPY ./build/libs ./libs
ENV ENV_FILE=".env"
CMD ["java", "-jar", "./libs/2223-2-LEIC42D-G14.jar"]