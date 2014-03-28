================================================================================
  Copyright (C) 2014  The Central Perf authors
 
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.
 
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.
 
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>
================================================================================

		=======================
		Central Perf Quickstart
		=======================

Pre-requisites
==============
- JRE 1.7
- JAVA_HOME env. variable set

Launch Central Perf
=================== 
- Launch launch_cp.sh or launch_cp.bat
- Open your browser and go to : http://localhost:8080/centralperf/
- Import samples (recommanded)
- Enjoy

Configuration
=============
You can change configuration by :
- Editing defaut configuration file : config/centralperf-quickstart-config.properties
- Editing some configuration parameters directly through the UI : http://localhost:8080/centralperf/configuration

Notes
=====
Central Perf "Quickstart" is a ready to use bundle to start with Central Perf.
The database is an embedded HSQLDB database, not suitable for production.
If you wish to deploy Central Perf into production, use a more robust database, like PostgreSQL. 