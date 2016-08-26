package it.poliba.giorgiobasile.markerar.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * 
 * @author Giorgio
 * Classe che definisce una singola proprietà
 */

public class Property implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6335369096997197175L;
	private String name;
	//private String type;
	private ArrayList<String> acceptedValues;
	private String currentValue;
 	
	Property(String name, String type, ArrayList<String> values){
		this.name = name;
		//this.type = type;
		this.acceptedValues = values;
		this.currentValue = "";
	}
	
	Property(String name, ArrayList<String> values){
		this.name = name;
		this.acceptedValues = values;
		this.currentValue = "";
	}
	
	public Property(String name){
		this.name = name;
		this.acceptedValues = new ArrayList<String>();
		this.currentValue = "";
	}
	
	/*public Property(String name, String type){
		this.name = name;
		this.type = type;
	}*/

	public Property() {
		this.name = "";
		//this.type = "";
		this.acceptedValues = new ArrayList<String>();
		this.currentValue = "";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}*/

	public ArrayList<String> getAcceptedValues() {
		return acceptedValues;
	}

	public void setAcceptedValues(ArrayList<String> values) {
		this.acceptedValues = values;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	
	private void readObject(
			ObjectInputStream aInputStream
			) throws ClassNotFoundException, IOException {
		//always perform the default de-serialization first
		aInputStream.defaultReadObject();
	}

	/**
	 * This is the default implementation of writeObject.
	 * Customise if necessary.
	 */
	private void writeObject(
			ObjectOutputStream aOutputStream
			) throws IOException {
		//perform the default serialization for all non-transient, non-static fields
		aOutputStream.defaultWriteObject();
	}
}
