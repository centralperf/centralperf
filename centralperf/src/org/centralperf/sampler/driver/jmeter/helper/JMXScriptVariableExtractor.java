package org.centralperf.sampler.driver.jmeter.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVariableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Extract variables declared in a JMeter JMX file.
 * Variables are locate in &lt;Arguments tags.
 * This class returns variables grouped by set (a set by Argument blocks)  
 * @author Charles Le Gallic
 *
 */
public class JMXScriptVariableExtractor {

	private static final Logger log = LoggerFactory.getLogger(JMXScriptVariableExtractor.class);
	
	/**
	 * Parse a JMX file to extract all variables declared in it
	 * @param jmxFile
	 * @return
	 * @throws FileNotFoundException 
	 */	
	public static List<ScriptVariableSet> extractVariables(File jmxFile) throws FileNotFoundException {
		return extractVariables(new InputSource(new FileInputStream(jmxFile)));
	}
	
	/**
	 * Parse a JMX file to extract all variables declared in it
	 * @param jmxFile
	 * @return
	 */	
	public static List<ScriptVariableSet> extractVariables(String jmxString) {
		log.debug(jmxString);
		return extractVariables(new InputSource(new StringReader(jmxString)));
	}	
	
	/**
	 * Parse a JMX file to extract all variables declared in it
	 * @param jmxFile
	 * @return
	 */
	public static List<ScriptVariableSet> extractVariables(InputSource jmxFileContent) {
		List<ScriptVariableSet> variableSets = new ArrayList<ScriptVariableSet>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			
			// Parse JMX File
			Document doc = builder.parse(jmxFileContent);
			
			// Build XSLT Factory and xpath queries
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression xPathExprArguments = xpath.compile("//Arguments[@enabled='true']");
			XPathExpression xPathExprVariables = xpath.compile("collectionProp/elementProp");

			// Search "Arguments" nodes
			NodeList argumentsNodes = (NodeList) xPathExprArguments.evaluate(doc, XPathConstants.NODESET);
			
			// Loop over found arguments
			for (int i = 0; i < argumentsNodes.getLength(); i++) {
				Node argumentNode = argumentsNodes.item(i);
				String variableSetName = argumentNode.getAttributes().getNamedItem("testname").getNodeValue(); 
				
				// Create the variables set
				ScriptVariableSet scriptVariableSet = new ScriptVariableSet();
				scriptVariableSet.setName(variableSetName);
				
				NodeList variablesNodes = (NodeList) xPathExprVariables.evaluate(argumentNode, XPathConstants.NODESET);
				
				// Loop over variables
				for (int j = 0; j < variablesNodes.getLength(); j++) {
					Node variableNode = variablesNodes.item(j);
					ScriptVariable scriptVariable = new ScriptVariable();
					NodeList childs = variableNode.getChildNodes();
					
					// Loop over variable properties
					for(int k = 0; k < childs.getLength(); k++){
						Node childNode = childs.item(k);
						if(childNode.getNodeType() == Node.ELEMENT_NODE){
							String childType = childNode.getAttributes().getNamedItem("name").getNodeValue();
							String childValue = childNode.getTextContent().trim();
							if("Argument.name".equals(childType)){
								scriptVariable.setName(childValue);
							}
							else if("Argument.value".equals(childType)){
								scriptVariable.setDefaultValue(childValue);
							}
							else if("Argument.desc".equals(childType)){
								scriptVariable.setDescription(childValue);
							}
						}
					}
					scriptVariableSet.getScriptVariables().add(scriptVariable);
				}
				
				variableSets.add(scriptVariableSet);
			}
			
			return variableSets;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Parse a JMX file to extract all variables declared in it
	 * @param jmxFile
	 * @return
	 */	
	public static String replaceVariables(String jmxString, List<ScriptVariable> scriptVariables) {
		return replaceVariables(new InputSource(new StringReader(jmxString)), scriptVariables);
	}	
	
	/**
	 * Replaces variables by their new values in JMX File
	 * @param jmxFileContent
	 * @param scriptVariables
	 * @return
	 */
	public static String replaceVariables(InputSource jmxFileContent, List<ScriptVariable> scriptVariables){
		
		Map<String, ScriptVariable> scriptVariableMap = new HashMap<String, ScriptVariable>();
		
		// build a hashtable of scriptVariable
		for(ScriptVariable scriptVariable : scriptVariables){
			scriptVariableMap.put(scriptVariable.getName(), scriptVariable);
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			
			// Parse JMX File
			Document doc = builder.parse(jmxFileContent);
			
			// Build XSLT Factory and xpath queries
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression xPathExprArguments = xpath.compile("//Arguments");
			XPathExpression xPathExprVariables = xpath.compile("collectionProp/elementProp");

			// Search "Arguments" nodes
			NodeList argumentsNodes = (NodeList) xPathExprArguments.evaluate(doc, XPathConstants.NODESET);
			
			// Loop over found arguments
			for (int i = 0; i < argumentsNodes.getLength(); i++) {
				Node argumentNode = argumentsNodes.item(i);				
				NodeList variablesNodes = (NodeList) xPathExprVariables.evaluate(argumentNode, XPathConstants.NODESET);
				
				// Loop over variables
				for (int j = 0; j < variablesNodes.getLength(); j++) {
					Node variableNode = variablesNodes.item(j);
					NodeList childs = variableNode.getChildNodes();
						
					String variableValue = null;
						
					// Loop over variable properties
					for(int k = 0; k < childs.getLength(); k++){
						Node childNode = childs.item(k);
						if(childNode.getNodeType() == Node.ELEMENT_NODE){
							String childType = childNode.getAttributes().getNamedItem("name").getNodeValue();
							String childValue = childNode.getTextContent().trim();
							if("Argument.name".equals(childType)){
								ScriptVariable scriptVariable = scriptVariableMap.get(childValue);
								if(scriptVariable != null){
									variableValue = scriptVariable.getValue();
								}
							}
							else if("Argument.value".equals(childType)){
								if(variableValue != null){
									childNode.setTextContent(variableValue);
								}
							}
						}
					}
				}
			}
			
			// Write back XML to String
			Source source = new DOMSource(doc);
	        StringWriter stringWriter = new StringWriter();
	        Result result = new StreamResult(stringWriter);
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.transform(source, result);
	        return stringWriter.getBuffer().toString();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	public static void main(String args[]){
		try {
			File inputFile = new File("E:\\Users\\charles\\Desktop\\sonde.jmx");
			String jmxContent = new Scanner(inputFile).useDelimiter("\\Z").next();
			JMXScriptVariableExtractor.extractVariables(jmxContent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
