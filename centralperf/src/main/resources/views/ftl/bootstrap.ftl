<#--
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
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->

<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Initialize" menu="bootstrap">
    <div class="page-header">
        <h1>Initialize Central Perf</h1>
    </div>
    <div class="jumbotron">
        It seem's that this is the first time you launched Central Perf<br/>
        Do you wish to initialize it with sample data ?<br>
        <a href="${rc.contextPath}/bootstrap/initialize?importSamples=true" class="btn btn-primary">Yes, import samples</a> 
        <a href="${rc.contextPath}/bootstrap/initialize?importSamples=false" class="btn btn-default">Skip...</a>
    </div>
</@layout.main>