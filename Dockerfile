FROM primetoninc/jdk:1.7

ENV MAVEN_VERSION 3.5.3

RUN yum install -y wget 

RUN wget http://archive.apache.org/dist/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz 

RUN tar xzf apache-maven-3.5.3-bin.tar.gz -C /usr/share 

RUN mv /usr/share/apache-maven-3.5.3 /usr/share/maven 

RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

RUN export PATH=/usr/share/maven/bin:$PATH

ENV MAVEN_HOME /usr/share/maven

RUN echo $JAVA_HOME

COPY  .  /usr/share/jpetstore-6-vaadin-spring-boot

WORKDIR /usr/share/jpetstore-6-vaadin-spring-boot



