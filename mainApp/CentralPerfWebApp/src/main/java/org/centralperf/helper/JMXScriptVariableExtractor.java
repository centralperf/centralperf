package org.centralperf.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.centralperf.model.ScriptVariable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class JMXScriptVariableExtractor {

	public static List<ScriptVariable> extractVariables(File jmxFile) {
		List<ScriptVariable> variables = new ArrayList<ScriptVariable>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(jmxFile);
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression xPathExpr = xpath.compile("//Arguments");
			Object result = xPathExpr.evaluate(doc, XPathConstants.NODESET);
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
		return variables;

	}
	
	public static void main(){
		JMXScriptVariableExtractor.extractVariables(new File("E:\\Users\\charles\\Desktop\\Documents\\LensysDirectopSC01.jmx"));
	}
}
