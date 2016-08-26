package it.poliba.giorgiobasile.markerar.schemas;


import it.poliba.giorgiobasile.markerar.properties.Property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Giorgio
 * Gestore delle proprietà di default da assegnare a un nuovo marker
 */


public class PropertiesManager {

	private static ArrayList<Property> properties;

	public static ArrayList<Property> getProperties() {
		if(properties == null)
			properties = new ArrayList<Property>();
		return properties;
	}
	public static void setProperties(ArrayList<Property> properties) {
		PropertiesManager.properties = properties;
	}

	//carica lo schema da XML, utilizzando il PropertiesHandler, che implementa il parsing vero e proprio
	//ritorna le proprietà caricate per l'assegnazione al singolo marker
	public static ArrayList<Property> loadMarkerSchemaFromXML(String filePath) throws Exception{
		if(filePath != null){
			SAXParserFactory factory = SAXParserFactory.newInstance();
			InputStream xmlInput = new FileInputStream(filePath);
	
			SAXParser saxParser = factory.newSAXParser();
			PropertiesHandler handler   = new PropertiesHandler();
			saxParser.parse(xmlInput, handler);
			return handler.getPropertiesList();
		}
		return null;

	}
	
	//carica lo schema da XML, utilizzando il PropertiesHandler, che implementa il parsing vero e proprio
	//le proprietà caricate saranno usate di default nelle nuove foto
	public static void loadSchemaFromXML(String filePath) throws Exception{
		if(filePath != null){
			SAXParserFactory factory = SAXParserFactory.newInstance();
			InputStream xmlInput = new FileInputStream(filePath);
	
			SAXParser saxParser = factory.newSAXParser();
			PropertiesHandler handler   = new PropertiesHandler();
			saxParser.parse(xmlInput, handler);
			properties = handler.getPropertiesList();
		}	

	}

	public static boolean saveSchemaToXML(String filePath, ArrayList<Property> propList){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element properties = doc.createElement("properties");
			doc.appendChild(properties);

			for(Property p : propList){
				// staff elements
				Element property = doc.createElement("property");
				properties.appendChild(property);

				Element name = doc.createElement("name");
				name.appendChild(doc.createTextNode(p.getName()));
				property.appendChild(name);

				if(p.getAcceptedValues().size() > 0){
					Element values = doc.createElement("values");
					property.appendChild(values);
					for(String accValue : p.getAcceptedValues()){
						Element value = doc.createElement("value");
						value.appendChild(doc.createTextNode(accValue));
						values.appendChild(value);
					}					
				}
				/*// set attribute to staff element
				Attr attr = doc.createAttribute("id");
				attr.setValue("1");
				staff.setAttributeNode(attr);

				// shorten way
				// staff.setAttribute("id", "1");*/

				// firstname elements

			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			try {
				File xml = new File(filePath);
				xml.getParentFile().mkdirs();
				xml.createNewFile();
				StreamResult result;
				result = new StreamResult(new PrintWriter(
						new FileOutputStream(xml, false)));
				transformer.transform(source, result);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return false;
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
			return false;
		}

	}

}
