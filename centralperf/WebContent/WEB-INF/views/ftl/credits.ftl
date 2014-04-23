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

<@layout.main title="Credits" menu="credits">

	<script src="${rc.contextPath}/resources/js/jsxml.js"></script>
	<script type="text/javascript">
		// Load licenses.xml file and transform with XSLT
		$(document).ready(function(){
			jsxml.trans('${rc.contextPath}/resources/licenses/licenses.xml', '${rc.contextPath}/resources/licenses/licenses.xsl',function(resultString){
			    $('#server-side-licenses').html(resultString);
			});
		}
		);
	</script>



    <legend style="line-height: 40px">Authors</legend>
    <div class="row">
			<div class="media col-md-6">
				<a class="pull-left" href="http://www.sqli.com/eng/">
				    <img class="media-object pull-left" src="${rc.contextPath}/resources/img/credits/dclairac.jpeg" alt="Dominique Clairac" style="width: 80px;">
				</a>
			  <div class="media-body">
			    <h4 class="media-heading">Dominique CLAIRAC</h4>
			    Dominique is a french IT Senior Consultant at Alcyonix (SQLI Group) with more than 11 years of experience in IT operations as well as in software development.
He has now specialized in auditing IT production sites with a special focus on performance and robustness. He is also <a href="https://www.redhat.com/wapps/training/certification/verify.html?certNumber=705010633800073">JBoss Certified (JCAA)</a> and specialist on Red Hat solutions.
			  </div>
			</div>
			<div class="media col-md-6" style="margin-top:0px">
			  <a class="pull-left" href="http://www.amoae.com">
			    <img class="media-object" src="${rc.contextPath}/resources/img/credits/clegallic.png" alt="Charles Le Gallic">
			  </a>
			  <div class="media-body">
			    <h4 class="media-heading">Charles LE GALLIC</h4>
			    Founder of <a href="http://www.amoae.com">AMOAE</a> in february 2012, a french IT consulting company, Charles is an IT consultant since 2002, but coding is still one of it's favorite hobby.
			  </div>
			</div> 
		</div>
		<div class="row" style="margin-top:50px;margin-bottom:50px;">
			<div class="media col-md-6">
			  <a class="pull-left" href="http://www.centralperf.org">
			    <img class="media-object" src="${rc.contextPath}/resources/img/credits/unknown-user-80x80.png" alt="You ?">
			  </a>
			  <div class="media-body">
			    <h4 class="media-heading">You ?</h4>
			    As Central Perf is a Free Software, Open Source project, you can join to help us improve it, make suggestions, build plugins for injectors....
			  </div>
			</div>	
			<div class="media col-md-6">
			</div>
				
		</div>
	
		<legend style="line-height: 40px">Third party licenses</legend>
        <h4>Client side</h4>
		<div id="client-side-licenses">
			<ul>
				<li>
					Bootstrap <a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a>
				</li>
				<li>
					Jquery <a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a>
				</li>
				<li>
					X-Editable<a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a>
				</li>
				<li>
					Bootstrap Select<a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a>
				</li>
				<li>
					jQuery Simple Modal <a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a>
				</li>
				<li>
					jsTree <a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a>
				</li>			
				<li>
					moment.js <a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a>
				</li>
				<li>
					D3.js <a href="http://opensource.org/licenses/BSD-3-Clause">BSD license</a>
				</li>
				<li>
					C3.js <a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a>
				</li>
			</ul>
		</div>
		<h4>Server side</h4>
        <div id="server-side-licenses">
        	Loading...
        </div>
        <h4>Others</h4>
        <div id="others-licenses">
			<ul>
				<li>
					jMeter <a href="http://www.apache.org/licenses/">MIT</a>
				</li>
				<li>
					Gatling <a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache v2.0</a>
				</li>
				<li>
					Tomcat <a href="http://www.apache.org/licenses/">Apache</a>
				</li>
			</ul>        
        </div>

      <!-- FOOTER -->
      <footer>
        <p class="pull-right"><a href="#">Back to top</a></p>
      </footer>
    
</@layout.main>
