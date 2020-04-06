/*
 * Copyright (C) 2014  The Central Perf authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.centralperf.sampler.driver.jmeter.helper;

import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVariableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;

/**
 * Extract variables declared in a JMeter JMX file.
 * Variables are locate in &lt;Arguments tags.
 * This class returns variables grouped by set (a set by Argument blocks)
 *
 * @since 1.0
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
        List<ScriptVariableSet> variableSets = new ArrayList<>();
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

                if (variablesNodes.getLength() == 0)
                    break; // Skip empty variables sets (when used a separator for example)

                // Loop over variables
                for (int j = 0; j < variablesNodes.getLength(); j++) {
                    Node variableNode = variablesNodes.item(j);
                    ScriptVariable scriptVariable = new ScriptVariable();
                    NodeList childs = variableNode.getChildNodes();

                    // Loop over variable properties
                    for (int k = 0; k < childs.getLength(); k++) {
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
			log.error("Parsing Error on variable extraction:"+e.getMessage(), e);
		} catch (SAXException e) {
			log.error("SAX Error on variable extraction:"+e.getMessage(), e);
		} catch (IOException e) {
			log.error("IO Error on variable extraction:"+e.getMessage(), e);
		} catch (XPathExpressionException e) {
			log.error("XPath Error on variable extraction:"+e.getMessage(), e);
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
			log.error("Parsing Error on variable replacement:"+e.getMessage(), e);
		} catch (SAXException e) {
			log.error("SAX Error on variable replacement:"+e.getMessage(), e);
        } catch (IOException e) {
            log.error("IO Error on variable replacement:" + e.getMessage(), e);
        } catch (XPathExpressionException e) {
            log.error("XPath Error on variable replacement:" + e.getMessage(), e);
        } catch (TransformerException e) {
            log.error("Transformer Error on variable replacement:" + e.getMessage(), e);
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File("E:\\Users\\charles\\Desktop\\sonde.jmx");
            String jmxContent = new Scanner(inputFile).useDelimiter("\\Z").next();
            JMXScriptVariableExtractor.extractVariables(jmxContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
