$(document).ready(function () 
{
    $('#sumChart').highcharts(
    	{
	        chart: {zoomType: 'x', spacingRight: 20},
	        title: {text: 'Sumarry graph for run ${run.label}[${run.id}]'},
	        subtitle: {
	            text: document.ontouchstart === undefined ?
	                'Click and drag in the plot area to zoom in' :
	                'Drag your finger over the plot to zoom in'
	        },
	        xAxis: {type: 'datetime', title: {text: null}},
	        yAxis: {title:{text: 'Response Time (ms)'}},
	        tooltip: {shared: true},
	        legend: {enabled: false},
	        plotOptions: {
	            area: {
	                fillColor: {
	                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
	                    stops: [
	                        [0, Highcharts.getOptions().colors[0]],
	                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
	                    ]
	                },
	                lineWidth: 1,
	                marker: {enabled: false},
	                shadow: false,
	                states: {hover: {lineWidth: 1}},
	                threshold: null
	            }
        	},
        	series: [
        	{
	            type: 'area',
	            name: 'Response time (ms)',
	            pointInterval: 1000,
	            pointStart: ${start?c},
	            data: [<#list run.samples as sample>${sample.elapsed?c},</#list>]
        	}]
    	});
});
