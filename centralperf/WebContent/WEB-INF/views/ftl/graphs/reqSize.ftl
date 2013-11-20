<#if runRTGraph?exists && (run.samples?size gt 0)>
<script type='text/javascript'>
$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        $('#RequestSize').highcharts({
            chart: {type: 'column', zoomType: 'x', spacingRight: 20},
            title: {text: 'Request size (octet) for run ${run.label}[${run.id}]'},
            xAxis: {
                categories: ${runRTGraph.label},
                labels: { rotation: -45,align: 'right',style: {fontSize: '13px',fontFamily: 'Verdana, sans-serif'}}
            },
            yAxis: {min: 0,title: {text: 'Request size (octet)'}},
            legend: {enabled: false},
            tooltip: {pointFormat: 'Request size: <b>{point.y:.1f} octets</b>',},
            series: [{
                name: 'Request Size',
                data: ${runRTGraph.size},
                dataLabels: {enabled: true,rotation: -90,color: '#FFFFFF',align: 'right',x: 4, y: 10,style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif',textShadow: '0 0 3px black'}}
            }]
        });
    });
</script>
<div id="RequestSize"  class="tab-pane active"></div>
</#if>