package org.centralperf.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.centralperf.model.LastScriptLabel;
import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Script;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.repository.ScriptVersionRepository;
import org.centralperf.service.SamplerService;
import org.centralperf.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Controller
@SessionAttributes
public class ScriptController {
	
	@Resource
	private ScriptRepository scriptRepository;

	@Resource
	private ScriptVersionRepository scriptVersionRepository;
	
	@Resource
	private ScriptService scriptService;

	@Resource
	private SamplerService samplerService;	
	
	@Resource
	private RunRepository runRepository;

    @Resource
    private ProjectRepository projectRepository;

	private static final Logger log = LoggerFactory.getLogger(ScriptController.class);

    @ExceptionHandler(Throwable.class)
    public @ResponseBody String handleValidationFailure(Throwable exception) {
        return exception.getMessage();
    }
    
    @RequestMapping(value = "/project/{projectId}/script/new", method = RequestMethod.POST)
    public String addScript(
                            @PathVariable("projectId") Long projectId,
    						@ModelAttribute("script") Script script,
                            @RequestParam("jmxFile") MultipartFile file,
                            BindingResult result) {
    	String scriptContent = null;

		// Get the script File
		try {
			scriptContent = new String(file.getBytes());
		} catch (IOException e) {
		}
		Script newScript = scriptService.addScript(script.getProject(), script.getSamplerUID(), script.getLabel(), script.getDescription(), scriptContent);
        return "redirect:/project/" + projectId + "/script/" + newScript.getId() + "/detail";
    }
    
    @RequestMapping(value = "/project/{projectId}/script/{scriptId}/version/new", method = RequestMethod.POST)
    public String addScriptVersion(
    					@PathVariable("projectId") Long projectId, 
    					@PathVariable("scriptId") Long scriptId, 
    					@ModelAttribute("scriptVersion") ScriptVersion scriptVersion,
    					@RequestParam("scriptFile") MultipartFile file,
                            BindingResult result) {
    	String scriptContent = null;

		// Get the script File
		try {
			scriptContent = new String(file.getBytes());
		} catch (IOException e) {
		}
		ScriptVersion newScriptVersion = scriptService.addScriptVersion(scriptRepository.findOne(scriptId), scriptVersion.getDescription(), scriptContent);
        return "redirect:/project/" + projectId + "/script/" + scriptId + "/detail#" + newScriptVersion.getId();
    }    
    
    @RequestMapping("/project/{projectId}/script")
    public String showProjectScripts(@PathVariable("projectId") Long projectId, Model model) {
        model.addAttribute("newScript",new Script());
        model.addAttribute("scripts",scriptRepository.findAll());
        model.addAttribute("project",projectRepository.findOne(projectId));
        return "scripts";
    }
    
	@RequestMapping(value ="/project/{projectId}/script/json/list", method=RequestMethod.GET)  
	public @ResponseBody List<LastScriptLabel> getJsonScriptList(@PathVariable("projectId") Long projectId, Model model){
		Project p = projectRepository.findOne(projectId);
		List<LastScriptLabel> lst = new ArrayList<LastScriptLabel>();
		for (Script s : p.getScripts()) {
			List<ScriptVersion> versions = scriptVersionRepository.findByScriptIdOrderByNumberDesc(s.getId());
			ScriptVersion scriptVersion = versions.get(0);
			lst.add(new LastScriptLabel(scriptVersion.getId(), s.getLabel()+" (version "+scriptVersion.getNumber()+")" + " - " + scriptVersion.getScript().getSamplerUID()));
		}
	    return lst;
	}
    
    @RequestMapping(value="/project/{projectId}/script/{id}/detail", method = RequestMethod.GET)
    public String showScriptDetail(
            @PathVariable("id") Long id,
            @PathVariable("projectId") Long projectId,
            Model model){
    	log.debug("script details for script ["+id+"]");
    	model.addAttribute("script",scriptRepository.findOne(id));
        model.addAttribute("project",projectRepository.findOne(projectId));
        model.addAttribute("scriptVersions",scriptVersionRepository.findByScriptIdOrderByNumberDesc(id));
        model.addAttribute("newScriptVersion",new ScriptVersion());        
        return "scriptDetail";
    }    
    
	@RequestMapping(value = "/project/{projectId}/script/{id}/delete", method = RequestMethod.GET)
    public String deleteScript(
            @PathVariable("id") Long id,
            @PathVariable("projectId") Long projectId,
            RedirectAttributes redirectAttrs) {
		List<Run> runsAttached = runRepository.findByScriptVersionScriptId(id);
		if(!runsAttached.isEmpty()){
			redirectAttrs.addFlashAttribute("error","Unable to delete this script because it's attached to at least one run");
		} else{
			scriptRepository.delete(id);
		}
        return "redirect:/project/" + projectId + "/script";
    }
	
	@RequestMapping(value = "/project/{projectId}/script/{scriptId}/version/{versionId}/delete", method = RequestMethod.GET)
    public String deleteScriptVersion(
    		@PathVariable("projectId") Long projectId,
    		@PathVariable("scriptId") Long scriptId,
            @PathVariable("versionId") Long versionId,
            RedirectAttributes redirectAttrs) {
		List<Run> runsAttached = runRepository.findByScriptVersionId(versionId);
		if(!runsAttached.isEmpty()){
			redirectAttrs.addFlashAttribute("error","Unable to delete this version because it's attached to at least one run");
		} else{
			scriptService.deleteScriptVersion(versionId);
		}
        return "redirect:/project/" + projectId + "/script/" + scriptId + "/detail";
    }	

    @RequestMapping("/script")
    public String showScripts(Model model) {
        model.addAttribute("scripts",scriptRepository.findAll());
        return "scripts";
    }
    
    @RequestMapping(value = "/script/{scriptId}", method = RequestMethod.POST)
    @ResponseBody
    public String updateScript(
                            @PathVariable("scriptId") Long scriptId,
                            @RequestParam(value="label",required=false) String label,
                            @RequestParam(value="description",required=false) String description
                            ) {
        Script script = scriptRepository.findOne(scriptId);
        String valueToReturn = label;
        if(label != null){
        	script.setLabel(label);
        }
        else 
        if(description != null){
        	script.setDescription(description);
        	valueToReturn = description;
        }        
        scriptRepository.save(script);
        return valueToReturn;
    }    
    
    @RequestMapping(value = "/script/{scriptId}/version/{versionNumber}/preview")
    public String previewScript(
    			Model model,
    			@PathVariable("scriptId") Long scriptId,
    			@PathVariable("versionNumber") int versionNumber
    		) {
    	Script script = scriptRepository.findOne(scriptId);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(script.getVersions().get(versionNumber - 1).getContent())));    	
			model.addAttribute("obj", doc);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		// Parse JMX File
        return "jmeter-test-plan-preview.xsl";
    }     
}
