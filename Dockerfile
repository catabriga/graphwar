FROM java:openjdk-7-jdk

RUN mkdir /compile

WORKDIR /compile

CMD [ "./compile.sh" ]
