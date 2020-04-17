<#--
  Copyright (C) 2020  The Central Perf authors
 
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.
 
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.
 
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
<#macro prettyDate dateToFormat>
	<span title="${dateToFormat?datetime?iso_utc}"><script>document.write(moment("${dateToFormat?datetime?iso_utc}").fromNow())</script></span>
</#macro>

<#macro truncate textToTruncate >
	<span title="${textToTruncate}">${textToTruncate?truncate_w(20)}</span>
</#macro>