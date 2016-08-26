package it.poliba.giorgiobasile.markerar.markerdetection;

import it.poliba.giorgiobasile.markerar.properties.Property;
import it.poliba.giorgiobasile.markerar.schemas.PropertiesManager;
import it.poliba.giorgiobasile.markerar.utils.GeometryUtils;

/*
 * @author Giorgio 
 * 
 * 
 * 
 */

import java.util.ArrayList;

import org.opencv.core.Point;
import org.opencv.core.Point3;

public class MarkerObject{
	
	/**
	 * 
	 * @author Giorgio
	 * Classe che definisce il concetto di Marker arricchito da proprietà, etichetta e coordinate dei centroidi; semplifica le operazioni di
	 * lettura, scrittura, salvataggio
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private Marker marker;
	private Point3 coord3d;
	private Point centroid;
	private String label;
	private ArrayList<Property> properties;
	
	@SuppressWarnings("unchecked")
	public MarkerObject(Marker marker, int originId){
		this.marker = marker;
		if(marker.getMarkerId() != originId){
			this.properties = (ArrayList<Property>) PropertiesManager.getProperties().clone();
		}else{
			this.properties = new ArrayList<Property>();
			this.coord3d = new Point3(0, 0, 0);
		}
		this.centroid = GeometryUtils.findCentroid(marker);
		this.label = String.valueOf(this.marker.getMarkerId());
	}
	
	public MarkerObject(Marker marker, ArrayList<Property> properties, String label){
		this.marker = marker;
		this.centroid = GeometryUtils.findCentroid(marker);
		this.properties = properties;
		this.label = label;
	}
	
	public Marker getMarker() {
		return marker;
	}
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	public ArrayList<Property> getProperties() {
		return properties;
	}
	public void setProperties(ArrayList<Property> properties) {
		this.properties = properties;
	}

	public Point3 getCoord3d() {
		return coord3d;
	}

	public void setCoord3d(Point3 coord3d) {
		this.coord3d = coord3d;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
