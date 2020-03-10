# Central Perf

Central Perf allows you to manage all your jMeter performance tests in a single place

## Installation

### With Docker

- [Install Docker](https://docs.docker.com/install/)
- [Install Docker Compose](https://docs.docker.com/compose/install/)
- Clone this repository and launch the setup script
```
git clone https://github.com/centralperf/centralperf.git
cd centralperf/scripts
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
