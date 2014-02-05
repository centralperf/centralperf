
<script type='text/javascript'>

var errorChart;
$(document).ready(function () {
        errorChart = new Highcharts.Chart({
            chart: {renderTo: 'ErrChart',type: 'column', zoomType: 'x', spacingRight: 20},
            title: {text: 'Errors for run ${run.label}[${run.id}]'},
            xAxis: {
                categories: ['Undefined'],
                labels: { rotation: -45,align: 'right',style: {fontSize: '13px',fontFamily: 'Verdana, sans-serif'}}
            },
            yAxis: {min: 0,title: {text: 'Error rate'},stackLabels: {enabled: true,style: {fontWeight: 'bold', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'}}},
            legend: {align: 'right',x: -70,verticalAlign: 'top',y: 20,floating: true,backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColorSolid) || 'white',borderColor: '#CCC',borderWidth: 1,shadow: false},
            colors: ['#DD3E38', '#5AB446'],
            exporting: {
                buttons : {
                    custom : {
                        symbol: 'circle',
                        x : -30,
                        align : 'right',
                        _titleKey : 'Custom Menu',
                        menuItems : [{
                                text : 'percent',
                                onclick : function()
                                {
                                    var chart = $('#ErrChart').highcharts(),
                                    s = chart.series,
                                    sLen = s.length;
                                    
                                    for(var i =0; i < sLen; i++){
                                        s[i].update({stacking: 'percent'}, false);
                                    }
                                    chart.redraw();
                                    
                                }
                        },{
                                text : 'absolute values',
                                onclick : function()
                                {
                                    var chart = $('#ErrChart').highcharts(),
                                    s = chart.series,
                                    sLen = s.length;
                                    
                                    for(var i =0; i < sLen; i++){
                                        s[i].update({stacking: 'normal'}, false);
                                    }
                                    chart.redraw();
                                }
                        }]
                    }
                },
                url : ''
            },
            tooltip: {formatter: function() {return '<b>'+ this.x +':</b><br/>'+this.series.name +': '+ this.y +'<br/><b>Total: '+ this.point.stackTotal+' requests</b>';}},
            plotOptions: {column: {stacking: 'normal',dataLabels: {enabled: true,color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'}}},
            series: [{
                name: 'Requests KO',
                data: [0],
                dataLabels: {enabled: true,rotation: -90,color: '#FFFFFF',align: 'middle',x: 4, y: 10,style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif',textShadow: '0 0 3px black'}}
            },{
                name: 'Requests OK',
                data: [0],
                dataLabels: {enabled: true,rotation: -90,color: '#FFFFFF',align: 'middle',x: 4, y: 10,style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif',textShadow: '0 0 3px black'}}
            }]
        });
    });
</script>
<div id="ErrChart"  class="tab-pane active"></div>
