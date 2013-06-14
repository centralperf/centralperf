$(document).ready(function() {

  $.displayPieChart(colors, data);

});

var colors = ["#1f77b4", "#aec7e8", "#ff7f0e", "#ffbb78", "#2ca02c", "#98df8a", "#d62728", "#ff9896", "#9467bd", "#c5b0d5", "#8c564b", "#c49c94", "#e377c2", "#f7b6d2", "#7f7f7f", "#c7c7c7", "#bcbd22", "#dbdb8d", "#17becf", "#9edae5"];

$.displayPieChart = function(colors, data) {

		var chart;

		var chart = new Highcharts.Chart({
      chart: {
         renderTo: 'pieChart',
         width: 600,
         height: 600,
         marginLeft: 10,
         marginRight: 10,
      },
      colors: colors,
      title: {
         text: "Graphe de test",
         margin: 10
      },
      tooltip: {
         formatter: function() {
            return "Depense : " + number_format(this.y,0,',',' ') + "€" ;
         }
      },
      plotOptions: {
         pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
               enabled: true,
               formatter: function() {
                  return "" + this.point.name.toLowerCase() + "";
               }
            }
         },
         series: {
            dataLabels: {
                enabled: true,
                color: 'black',
                fontSize: 3
            }
        }
      },
       series: [{
         type: 'pie',
         data: data
      }]
   });

	}