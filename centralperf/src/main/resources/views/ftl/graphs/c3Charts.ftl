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

<script langage="javascript">

	function refreshStats() {
		$.getJSON("${rc.contextPath}/api/getRunStatsJSON/${run.id?c}",'', function(res){
			if(res!=null){
				$("#runProcessOuput").html(res.runOutput);
				$("#summaryCurrentUsers").html(res.currentUsers);
				$("#summaryNumberOfSamples").html(res.numberOfSample);
				numberOfSample=res.numberOfSample;
				$("#summaryMaxUsers").html(res.maxUsers);
				$("#summaryCurrentBandwith").html(res.currentBandwithWithUnit);
				$("#summaryTotalBandwith").html(res.totalBandwithWithUnit);             
				$("#summaryAverageResponseTime").html(res.averageResponseTime);
				$("#summaryAverageLatency").html(res.averageLatency);
				$("#summaryRequestPerSeconds").html(Math.round(res.numberOfSample / res.duration * 100000)/100);
				$("#summaryErrorRate").html(res.errorRate + "%");
				$("#summaryLastSampleDate").html(res.lastSampleDate);
				$("#summaryLaunchedDate").html("${run.startDate?date?string.short}");
                $("#summaryLaunchedTime").html("${run.startDate?time}");
                <#if !run.running>
                	$("#summaryDuration").text(prettyDuration(res.duration));
                <#else>
					if(res.running == false){location.reload();}
                </#if>				
			}
		});
	}

	function generateGraph(chartId)
	{
		var chart;
		switch(chartId){
				case "SUM":
					chart=c3.generate({
						bindto: '#summaryChart',
		    			data: {
		    				x: 'x',
					    	x_format:'%d-%m-%Y %X',
					        url: '${rc.contextPath}/api/getSumChartCSV/${run.id?c}',
					        axes: {
					            RT: 'y',
					            CR: 'y2'
					        },
					        types: {
					            RT: 'area',
					            CR: 'spline',
					        },
					        names: {
					            RT: 'Response time (ms)',
					            CR: 'Concurrent request'
					        }
		    			},
					    axis: {
					 		x: {
					 			type : 'timeseries',
					 			localtime : true,
					            label: 'elapsed time',
					            tick: {
					            	count: 30,
					            	format: '%X',
					            	rotate: 45
					            },
					            height: 50
					        },
					        y: {
					            label: 'Response time (ms)'
					        },
					        y2: {
					            show: true,
					            label: 'concurrent requests'
					        }
					    },
					    grid: {
					        y: {
					            show: true
					        }
					    },
					    subchart: {
					        show: true
					    },
						transition: {
        					duration: 0
    					}					    
					});
					break; 
				case "RT":
        			chart=c3.generate({
						bindto: '#responseTimeChart',
					    data: {
					    	x : 'x',
					        url: '${rc.contextPath}/api/getRTChartCSV/${run.id?c}',
					        type: 'bar',
					        labels: true,
					        groups: [
					            ['wait', 'download']
					        ]
					    },
					    axis: {
							 x: {
							 	type: 'categorized', // this needed to load string x value
							 	tick: {rotate: 45},
            					height: 130
							 }
					    },
						transition: {
        					duration: 0
    					}							    
					});
            		break; 
				case "RS":
        			chart=c3.generate({
						bindto: '#responseSizeChart',
					    data: {
					    	x : 'x',
					        url: '${rc.contextPath}/api/getRSChartCSV/${run.id?c}',
					        type: 'bar',
					        labels: {
					        	format: {
					        		y: function(v,id){ return humanFileSize(v,true); }
					        	}
					        }
					    },
					    axis: {
							 x: {
							 	type: 'categorized', // this needed to load string x value
							 	tick: {rotate: 45},
            					height: 130
							 },
							 y: {
								tick: {
									format: function (d) { return humanFileSize(d,true); }
								}
							}							 
					    },
						transition: {
        					duration: 0
    					}							    
					});
            		break; 
            	case "ER":
        			chart=c3.generate({
						bindto: '#errorRateChart',
					    data: {
					    	x : 'x',
					        url: '${rc.contextPath}/api/getERChartCSV/${run.id?c}',
					        type: 'bar',
					        labels: true,
					        groups: [['ok', 'errors']]
					    },
        				color: {pattern: ['#19A319','#C13030']},
					    axis: {
							 x: {
							 	type: 'categorized', // this needed to load string x value
							 	tick: {rotate: 45},
            					height: 130
							 }
					    },
						transition: {
        					duration: 0
    					}		
					});
            		break; 
        }
        return chart;
	}
	
	
	function updateChart(chart, chartId)
	{
		switch(chartId){
				case "SUM":
					chart.load({x: 'x', url: '${rc.contextPath}/api/getSumChartCSV/${run.id?c}'});
            		break; 
            	case "RT":		
            		chart.load({url: '${rc.contextPath}/api/getRTChartCSV/${run.id?c}'});		
					break; 
            	case "RS":		
            		chart.load({url: '${rc.contextPath}/api/getRSChartCSV/${run.id?c}'});		
					break; 
            	case "ER":		
            		chart.load({url: '${rc.contextPath}/api/getERChartCSV/${run.id?c}'});		
					break; 
        }
    }
	
</script>
