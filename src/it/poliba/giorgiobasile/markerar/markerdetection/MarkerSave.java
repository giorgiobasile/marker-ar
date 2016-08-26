package it.poliba.giorgiobasile.markerar.markerdetection;


import it.poliba.giorgiobasile.markerar.properties.Property;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.opencv.core.Point;
import org.opencv.core.Point3;

/**
 * 
 * @author Giorgio
 * Classe di utilità che gestisce il salvataggio dei dati relativi a un singolo marker
 * Le informazioni necessarie sono:
 * coordinate del centroide
 * coordinate in 3d
 * etichetta
 * proprietà
 */

public class MarkerSave implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String label;
	private ArrayList<Property> properties;
	/*private Point3 coords3d;
	private Point centroid;*/
	
	private double u,v;
	private double x,y,z;
	
	/*public PropertySave(double x, double y , ArrayList<Property> properties){
		this.x = x;
		this.y = y;
		this.properties = properties;
	}*/
	
	public MarkerSave(Point3 coords3d, Point centroid , ArrayList<Property> properties, String label){
		this.u = centroid.x;
		this.v = centroid.y;
		
		this.x = coords3d.x;
		this.y = coords3d.y;
		this.z = coords3d.z;
		
		this.properties = properties;
		this.label = label;
	}

	/*public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}*/

	public ArrayList<Property>  getProperties() {
		return properties;
	}

	public void setLabel(ArrayList<Property> properties) {
		this.properties = properties;
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

	public double getU() {
		return u;
	}

	public void setU(double u) {
		this.u = u;
	}

	public double getV() {
		return v;
	}

	public void setV(double v) {
		this.v = v;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}