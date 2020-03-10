FROM debian:stretch
LABEL maintainer="charles@amoae.com"

ARG JMETER_VERSION

ENV JMETER_HOME /opt/apache-jmeter-${JMETER_VERSION}
ENV JMETER_BIN  ${JMETER_HOME}/bin
ENV MIRROR_HOST http://apache.belnet.be
ENV JMETER_DOWNLOAD_URL ${MIRROR_HOST}/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz
ENV JMETER_PLUGINS_DOWNLOAD_URL http://repo1.maven.org/maven2/kg/apc
ENV JMETER_PLUGINS_FOLDER ${JMETER_HOME}/lib/ext/

RUN apt-get update \
	&& apt-get install -y ca-certificates \
	&& update-ca-certificates \
            && apt-get install -y openjdk-8-jre tzdata curl unzip bash \
            && cp /usr/share/zoneinfo/Europe/Paris /etc/localtime \
            && echo "Europe/Paris" >  /etc/timezone \
	&& rm -rf /var/lib/apt/lists/*

RUN echo "Using jMeter ${JMETER_VERSION}"

RUN mkdir -p /tmp/dependencies  \
	&& curl -L --silent ${JMETER_DOWNLOAD_URL} >  /tmp/dependencies/apache-jmeter-${JMETER_VERSION}.tgz  \
	&& mkdir -p /opt  \
	&& tar -xzf /tmp/dependencies/apache-jmeter-${JMETER_VERSION}.tgz -C /opt  \
	&& rm -rf /tmp/dependencies

# Some plugins
RUN curl -L --silent ${JMETER_PLUGINS_DOWNLOAD_URL}/jmeter-plugins-dummy/0.2/jmeter-plugins-dummy-0.2.jar -o ${JMETER_PLUGINS_FOLDER}/jmeter-plugins-dummy-0.2.jar
RUN curl -L --silent ${JMETER_PLUGINS_DOWNLOAD_URL}/jmeter-plugins-cmn-jmeter/0.5/jmeter-plugins-cmn-jmeter-0.5.jar -o ${JMETER_PLUGINS_FOLDER}/jmeter-plugins-cmn-jmeter-0.5.jar

ENV PATH $PATH:$JMETER_BIN

COPY entrypoint.sh /

WORKDIR ${JMETER_HOME}

ENTRYPOINT ["/entrypoint.sh"]