
<script type='text/javascript'>

$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
	$('#ResponseTime').highcharts().reflow();
	$('#RequestSize').highcharts().reflow();
});

var rtChart;
$(document).ready(function () {
        rtChart = new Highcharts.Chart({
            chart: {renderTo: 'ResponseTime',type: 'column', zoomType: 'x', spacingRight: 20},
            title: {text: 'Response Time (ms) for run ${run.label}[${run.id}]'},
            xAxis: {
                categories: ['Undefined'],
                labels: { rotation: -45,align: 'right',style: {fontSize: '13px',fontFamily: 'Verdana, sans-serif'}}
            },
            yAxis: {min: 0,title: {text: 'Response Time (ms)'},stackLabels: {enabled: true,style: {fontWeight: 'bold', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'}}},
            legend: {align: 'right',x: -70,verticalAlign: 'top',y: 20,floating: true,backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColorSolid) || 'white',borderColor: '#CCC',borderWidth: 1,shadow: false},
            tooltip: {formatter: function() {return '<b>'+ this.x +'</b><br/>'+this.series.name +': '+ this.y +' ms<br/><b>Total: '+ this.point.stackTotal+' ms</b>';}},
            plotOptions: {column: {stacking: 'normal',dataLabels: {enabled: true,color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'}}},
            series: [{
                name: 'download',
                data: [0],
                dataLabels: {enabled: true,rotation: -90,color: '#FFFFFF',align: 'middle',x: 4, y: 10,style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif',textShadow: '0 0 3px black'}}
            },{
                name: 'latency',
                data: [0],
                dataLabels: {enabled: true,rotation: -90,color: '#FFFFFF',align: 'middle',x: 4, y: 10,style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif',textShadow: '0 0 3px black'}}
            }]
        });
    });
</script>
<div id="ResponseTime"  class="tab-pane active"></div>
