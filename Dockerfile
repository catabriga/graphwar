FROM java:openjdk-7-jdk-alpine

RUN mkdir /compile

WORKDIR /compile

CMD [ "./compile.sh" ]
