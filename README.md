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
- Build CentralPerf image
```
cd scripts
./build_jmeter_image.sh
```
- Launch Central Perf containers
```
./launch.sh
```

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
