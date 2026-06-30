FROM java:openjdk-7-jdk

WORKDIR /compile

ADD compile.sh /compile/
ADD rsc/ /compile/rsc
ADD src/ /compile/src

RUN [ "./compile.sh"]
