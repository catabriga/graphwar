FROM amazoncorretto:8

RUN mkdir /compile

WORKDIR /compile

CMD [ "./compile.sh" ]