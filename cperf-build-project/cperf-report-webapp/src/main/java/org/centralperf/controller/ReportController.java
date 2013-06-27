package org.centralperf.controller;

import javax.annotation.Resource;

import org.centralperf.model.Run;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.SampleRepository;
import org.centralperf.service.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes
public class ReportController {
	
	@Resource
	private RunRepository runRepository;
	
	@Resource
	private SampleRepository sampleRepository;
	
	@Resource
	private GraphService graphService;
	
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
    	Run run = runRepository.findOne(id);
    	model.addAttribute("run",run);
    	model.addAttribute("series",graphService.getSumSeries(run));
    	model.addAttribute("pie", graphService.getCodeRepartition(run));
    	model.addAttribute("menu","report_sum");
    	
   
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
