# Central Perf

Central Perf allows you to manage all your jMeter performance tests in a single place

## Installation

### With Docker

- [Install Docker](https://docs.docker.com/install/)
- [Install Docker Compose](https://docs.docker.com/compose/install/)
- Clone this repository
```
git clone https://github.com/centralperf/centralperf.git
cd centralperf
```
- Create your configuration file in the root of the repo (if necessary). [Default configuration](src/main/docker/.env) will be used instead
```
vi centralperf.config
```

#### From Github
- Build CentralPerf image
```
cd scripts
./build_jmeter_image.sh
```
- Launch Central Perf containers
```
./launch.sh
```

#### From source
- Add following to [repo_root]/centralperf.config
```
DEPLOY_CENTRALPERF_FROM=local
```
- Build project with Maven
```
mvn clean package
```
- Launch Central Perf containers
```
cd scripts
./launch.sh
```

## Release History

### 1.3.0

- Schedule runs with start delay or cron expression
- Database migrations with FlyWay
- Allow to choose jMeter Docker Image
- jMeter Plugins in jMeter container (based on https://hub.docker.com/r/egaillardon/jmeter-plugins/)
- Bug fixes 

### 1.2.0

- Full Docker based deployment
- Docker based jMeter runner option
- Utility scripts to setup platform, reset, launch...

## Architecture

Central Perf is based on several pluggable components
- **Core** : the core of the application, coded with Java / Spring Boot
- **Backend** : pluggable solutions to store tests results (samples data)
    - Elastic Search (default)
    - PosgreSQL is an alternative
- **Visualization** : pluggable solution to display test results (graphs, metrics)
    - Kibana (default)
    - Embeded JS visualization (based on D3.js)
- **Runner** : pluggable solution to run stress tests
    - jMeter as Docker containers (default)
    - Local jMeter installation
    - Gatling
    
 Central Perf is build to be deployed as Docker containers by default, but it's not mandatory. 
 It can be deployed as a regular Java Webapp in a servlet container.
