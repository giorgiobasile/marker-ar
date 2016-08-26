package it.poliba.giorgiobasile.markerar.utils;

import it.poliba.giorgiobasile.markerar.settings.PreferencesHandler;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.os.Bundle;

/**
 * 
 * @author Giorgio
 * Loader per il parsing XML del file delle preferenze di un progetto aperto dalla memoria del dispositivo
 */

public class ProjectLoader {

	public static Bundle loadPrefs(Context context, String filePath){
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			InputStream xmlInput = new FileInputStream(filePath);

			SAXParser saxParser = factory.newSAXParser();
			PreferencesHandler handler   = new PreferencesHandler(context);
			saxParser.parse(xmlInput, handler);
			return handler.getPreferences();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
