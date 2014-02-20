<script type='text/javascript'>

var rsChart;

$(document).ready(function () {
        rsChart = new Highcharts.Chart({
            chart: {renderTo: 'RequestSize',type: 'column', zoomType: 'x', spacingRight: 20},
            title: {text: 'Request size (octet) for run ${run.label}[${run.id}]'},
            xAxis: {
                categories: ['Undefined'],
                labels: { rotation: -45,align: 'right',style: {fontSize: '13px',fontFamily: 'Verdana, sans-serif'},
                formatter: function() {return this.value.length > 35 ? this.value.slice(0, 35)+'...': this.value;}
                }
            },
            yAxis: {min: 0,title: {text: 'Request size (octet)'}},
            legend: {enabled: false},
            tooltip: {pointFormat: 'Request size: <b>{point.y:.1f} octets</b>',},
            series: [{
                name: 'Request Size',
                data: [0],
                dataLabels: {enabled: true,rotation: -90,color: '#FFFFFF',align: 'right',x: 4, y: 10,style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif',textShadow: '0 0 3px black'}}
            }]
        });
    });
</script>
<div id="RequestSize"  class="tab-pane active"></div>