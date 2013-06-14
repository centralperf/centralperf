package org.centralperf.controller;

import java.util.Arrays;

import javax.annotation.Resource;

import org.centralperf.repository.RunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.LegendLayout;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;

@Controller
@SessionAttributes
public class ReportController {
	
	@Resource
	private RunRepository runRepository;
	
	private static final Logger log = LoggerFactory.getLogger(ReportController.class);
     
    @RequestMapping("/report")
    public String showReport(Model model) {
    	log.debug("Displaying runs reports");
    	//TODO: Only runs with some results
    	model.addAttribute("runs",runRepository.findAll());
        return "main";
    }
    
    @RequestMapping(value = "/report/{id}/sum")
    public String showSum(@PathVariable("id") Long id, Model model) {
    	log.debug("Run report SUM for run ["+id+"]");
    	model.addAttribute("run",runRepository.findOne(id));
    	model.addAttribute("menu","report_sum");
    	
    	Options options = new Options();
        options.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
        options.setTitle(new Title("My very own chart."));
        options.setxAxis(new Axis().setCategories(Arrays.asList(new String[] { "Jan", "Feb", "Mar", "Apr", "May","Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" })));
        options.setyAxis(new Axis().setTitle(new Title("Temperature (C)")));
        options.setLegend(new Legend()
               .setLayout(LegendLayout.VERTICAL)
               .setAlign(HorizontalAlignment.RIGHT)
               .setVerticalAlign(VerticalAlignment.TOP)
               .setX(-10)
               .setY(100)
               .setBorderWidth(0));
        options
            .addSeries(new SimpleSeries()
            .setName("Tokyo")
            .setData(
                    Arrays
                        .asList(new Number[] { 7.0, 6.9, 9.5, 14.5, 18.2, 21.5,
                            25.2, 26.5, 23.3, 18.3, 13.9, 9.6 })));

        options
            .addSeries(new SimpleSeries()
                .setName("New York")
                .setData(
                    Arrays
                        .asList(new Number[] { -0.2, 0.8, 5.7, 11.3, 17.0, 22.0,
                            24.8, 24.1, 20.1, 14.1, 8.6, 2.5 })));
    	
        model.addAttribute("chart",options);
        return "report_sum";
    }
    @RequestMapping(value = "/report/{id}/grt")
    public String showGrt(@PathVariable("id") Long id, Model model) {
    	log.debug("Run report GRT for run ["+id+"]");
    	model.addAttribute("run",runRepository.findOne(id));
    	model.addAttribute("menu","report_grt");
        return "report_grt";
    }
    @RequestMapping(value = "/report/{id}/prt")
    public String showPrt(@PathVariable("id") Long id, Model model) {
    	log.debug("Run report PRT for run ["+id+"]");
    	model.addAttribute("run",runRepository.findOne(id));
    	model.addAttribute("menu","report_prt");
        return "report_prt";
    }
    @RequestMapping(value = "/report/{id}/ert")
    public String showErt(@PathVariable("id") Long id, Model model) {
    	log.debug("Run report ERT for run ["+id+"]");
    	model.addAttribute("run",runRepository.findOne(id));
    	model.addAttribute("menu","report_ert");
        return "report_ert";
    }
    @RequestMapping(value = "/report/{id}/exp")
    public String showExp(@PathVariable("id") Long id, Model model) {
    	log.debug("Run report EXP for run ["+id+"]");
    	model.addAttribute("run",runRepository.findOne(id));
    	model.addAttribute("menu","report_exp");
        return "report_exp";
    }

}
