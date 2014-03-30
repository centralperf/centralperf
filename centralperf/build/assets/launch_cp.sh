#!/bin/sh

#  Copyright (C) 2014  The Central Perf authors
# 
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Affero General Public License as
#  published by the Free Software Foundation, either version 3 of the
#  License, or (at your option) any later version.
# 
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>

# Replace jmeter.bat by jmeter in property file if necessary
sed -i 's/jmeter\.bat/jmeter/g' ../config/*.properties

# Launch CP
echo "Launching Central Perf"
../tomcat/bin/startup.sh

