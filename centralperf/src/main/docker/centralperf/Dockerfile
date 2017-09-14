FROM tomcat:8.5
MAINTAINER Charles Le Gallic

RUN apt-get update \
	&& apt-get install -y wget unzip \
	&& apt-get clean \
	&& rm -rf /var/lib/apt/lists/*

ENV JMETER_VERSION 3.2

RUN mkdir -p /opt/centralperf/ \
	&& wget --progress=dot:mega  https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-$JMETER_VERSION.zip \
	&& unzip apache-jmeter-$JMETER_VERSION.zip -d /opt/centralperf \
	&& ln -s /opt/centralperf/apache-jmeter-$JMETER_VERSION /opt/centralperf/jmeter \
	&& rm -f /opt/centralperf/apache-jmeter-$JMETER_VERSION.zip

ENV CENTRALPERF_VERSION 1.1-20170908.142126-1

RUN wget --progress=dot:mega -O $CATALINA_HOME/webapps/centralperf.war https://pic.amoae.com/nexus/repository/maven-snapshots/org/centralperf/centralperf-webapp/1.1-SNAPSHOT/centralperf-webapp-$CENTRALPERF_VERSION.war

# Update shared loaders to load CP external properties file
RUN mkdir -p /opt/centralperf/config \
	&& sed -i -- 's/shared\.loader=.*/shared.loader=\/opt\/centralperf\/config\//' $CATALINA_HOME/conf/catalina.properties

COPY ./config/centralperf-docker-config.properties /opt/centralperf/config/
