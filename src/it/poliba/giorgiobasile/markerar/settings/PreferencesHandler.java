package it.poliba.giorgiobasile.markerar.settings;

import it.poliba.giorgiobasile.markerar.R;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.os.Bundle;

/**
 * 
 * @author Giorgio
 * Handler per la lettura del file XML delle preferenze di un progetto caricato della memoria del dispositivo
 */

public class PreferencesHandler extends DefaultHandler{

	boolean borigin = false;
	boolean bsize = false;
	
	private int origin = 0;
	private float size = 0;
	private Context context;

	public PreferencesHandler(Context context){
		this.context = context;
	}
	
	public Bundle getPreferences() {
		Bundle b = new Bundle();
		b.putInt(context.getString(R.string.origin_marker_id_key), origin);
		b.putFloat(context.getString(R.string.markers_size_key), size);
		return b;
	}

	public void startElement(String uri, String localName,String qName, 
			Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase("preferences")){
			
		}else if (qName.equalsIgnoreCase("origin")){
			borigin = true;
		}
		else if (qName.equalsIgnoreCase("size")){
			bsize = true;
		}	
		else
			throw new SAXException();

	}

	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		if (qName.equalsIgnoreCase("preferences")){

		}else if (qName.equalsIgnoreCase("origin")){

		}else if (qName.equalsIgnoreCase("size")){
			
		}	
	}

	public void characters(char ch[], int start, int length) throws SAXException {

		if (borigin) {
			origin = Integer.valueOf(new String(ch, start, length));
			borigin = false;
		}

		if (bsize) {
			size = Float.parseFloat(new String(ch, start, length));
			bsize = false;
		}

	}

};
