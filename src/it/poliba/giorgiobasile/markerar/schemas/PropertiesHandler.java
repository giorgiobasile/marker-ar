package it.poliba.giorgiobasile.markerar.schemas;

import it.poliba.giorgiobasile.markerar.properties.Property;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Giorgio
 * Handler per il caricamento da file XML di uno schema
 */

public class PropertiesHandler extends DefaultHandler{

	boolean bname = false;
	boolean btype = false;
	boolean bvalues = false;
	boolean bvalue = false;
	
	private ArrayList<String> values = null;
	private Property prop = null;
	private ArrayList<Property> properties = new ArrayList<Property>();

	public ArrayList<Property> getPropertiesList() {
		return properties;
	}

	public void startElement(String uri, String localName,String qName, 
			Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase("properties")){
			
		}else if (qName.equalsIgnoreCase("property")){
			prop = new Property();
			values = new ArrayList<String>();
		}
		else if (qName.equalsIgnoreCase("values")){
			bvalues = true;
		}	
		
		else if (qName.equalsIgnoreCase("name")) {
			bname = true;
		}

		else if (qName.equalsIgnoreCase("type")) {
			btype = true;
		}
		else if (qName.equalsIgnoreCase("value")) {
			bvalue = true;
		}else
			throw new SAXException();

	}

	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		if (qName.equalsIgnoreCase("property")){
			prop.setAcceptedValues(values);
			properties.add(prop);
		}else if (qName.equalsIgnoreCase("values")){
			
			bvalues = false;
		}	
	}

	public void characters(char ch[], int start, int length) throws SAXException {

		if (bname) {
			prop.setName(new String(ch, start, length));
			bname = false;
		}

		if (btype) {
			//prop.setType(new String(ch, start, length));
			btype = false;
		}
		
		if (bvalues && bvalue) {
			values.add(new String(ch, start, length));
			bvalue = false;
		}

	}

};
