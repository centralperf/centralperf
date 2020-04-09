FROM debian:stretch
LABEL maintainer="charles@amoae.com"

ARG JMETER_VERSION
ARG JVM_ARGS

ENV JMETER_HOME /opt/apache-jmeter-${JMETER_VERSION}
ENV JMETER_BIN  ${JMETER_HOME}/bin
ENV MIRROR_HOST http://apache.belnet.be
ENV JMETER_DOWNLOAD_URL ${MIRROR_HOST}/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz
ENV JMETER_PLUGINS_DOWNLOAD_URL http://repo1.maven.org/maven2/kg/apc
ENV JMETER_PLUGINS_FOLDER ${JMETER_HOME}/lib/ext/

ENV JMETER_PLUGINS_MANAGER_VERSION 1.3
ENV CMDRUNNER_VERSION 2.2
ENV JSON_LIB_VERSION 2.4
ENV JSON_LIB_FULL_VERSION ${JSON_LIB_VERSION}-jdk15
# Inspired by https://hub.docker.com/r/egaillardon/jmeter-plugins/ for plugins

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

RUN cd /tmp/ \
 && curl --location --silent --show-error --output ${JMETER_HOME}/lib/ext/jmeter-plugins-manager-${JMETER_PLUGINS_MANAGER_VERSION}.jar http://search.maven.org/remotecontent?filepath=kg/apc/jmeter-plugins-manager/${JMETER_PLUGINS_MANAGER_VERSION}/jmeter-plugins-manager-${JMETER_PLUGINS_MANAGER_VERSION}.jar \
 && curl --location --silent --show-error --output ${JMETER_HOME}/lib/cmdrunner-${CMDRUNNER_VERSION}.jar http://search.maven.org/remotecontent?filepath=kg/apc/cmdrunner/${CMDRUNNER_VERSION}/cmdrunner-${CMDRUNNER_VERSION}.jar \
 && curl --location --silent --show-error --output ${JMETER_HOME}/lib/json-lib-${JSON_LIB_FULL_VERSION}.jar https://search.maven.org/remotecontent?filepath=net/sf/json-lib/json-lib/${JSON_LIB_VERSION}/json-lib-${JSON_LIB_FULL_VERSION}.jar \
 && java -cp ${JMETER_HOME}/lib/ext/jmeter-plugins-manager-${JMETER_PLUGINS_MANAGER_VERSION}.jar org.jmeterplugins.repository.PluginManagerCMDInstaller \
 && ${JMETER_HOME}/bin/PluginsManagerCMD.sh install \
blazemeter-debugger=0.6,\
bzm-hls=3.0,\
bzm-http2=1.5,\
bzm-jmeter-citrix-plugin=0.5.5,\
bzm-parallel=0.9,\
bzm-random-csv=0.6,\
bzm-rte=2.3,\
bzm-siebel=0.1.0-beta,\
custom-soap=1.3.3,\
jmeter.backendlistener.azure=0.2.0,\
jmeter.backendlistener.elasticsearch=2.6.10,\
jmeter.backendlistener.kafka=1.0.0,\
jmeter.pack-listener=1.7,\
jpgc-autostop=0.1,\
jpgc-casutg=2.9,\
jpgc-cmd=2.2,\
jpgc-csl=0.1,\
jpgc-csvars=0.1,\
jpgc-dbmon=0.1,\
jpgc-directory-listing=0.3,\
jpgc-dummy=0.4,\
jpgc-ffw=2.0,\
jpgc-fifo=0.2,\
jpgc-filterresults=2.2,\
jpgc-functions=2.1,\
jpgc-ggl=2.0,\
jpgc-graphs-additional=2.0,\
jpgc-graphs-basic=2.0,\
jpgc-graphs-composite=2.0,\
jpgc-graphs-dist=2.0,\
jpgc-graphs-vs=2.0,\
jpgc-hadoop=2.0,\
jpgc-httpraw=0.1,\
jpgc-jms=0.2,\
jpgc-jmxmon=0.2,\
jpgc-json=2.7,\
jpgc-lockfile=0.1,\
jpgc-mergeresults=2.1,\
jpgc-pde=0.1,\
jpgc-perfmon=2.1,\
jpgc-plancheck=2.4,\
jpgc-prmctl=0.4,\
jpgc-redis=0.3,\
jpgc-rotating-listener=0.2,\
jpgc-sense=3.5,\
jpgc-standard=2.0,\
jpgc-sts=2.4,\
jpgc-synthesis=2.2,\
jpgc-tst=2.5,\
jpgc-udp=0.4,\
jpgc-webdriver=3.1,\
jpgc-wsc=0.7,\
jpgc-xml=0.1,\
jpgc-xmpp=1.5.1,\
kafkameter=0.2.0,\
mqtt-sampler=0.0.1-SNAPSHOT,\
netflix-cassandra=0.2-SNAPSHOT,\
ssh-sampler=1.1.1-SNAPSHOT,\
tilln-iso8583=1.0,\
tilln-sshmon=1.2,\
tilln-wssecurity=1.7,\
websocket-sampler=1.0.2-SNAPSHOT,\
websocket-samplers=1.2.2 \
 && ${JMETER_HOME}/bin/jmeter --version \
 && ${JMETER_HOME}/bin/PluginsManagerCMD.sh status \
 && chmod +x ${JMETER_HOME}/bin/*.sh \
 && rm -fr /tmp/*

ENV PATH $PATH:$JMETER_BIN

COPY entrypoint.sh /

WORKDIR ${JMETER_HOME}

ENTRYPOINT ["/entrypoint.sh"]