package it.poliba.giorgiobasile.markerar.pictureproject;

import it.poliba.giorgiobasile.markerar.utils.GeometryUtils;

import it.poliba.giorgiobasile.markerar.markerdetection.*;

import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;

import android.graphics.Bitmap;

/**
 * 
 * @author Giorgio
 * Classe che contiene un frame catturato dalla fotocamera in preview
 */

public class CameraFrame {

	protected Mat frame;
	protected Vector<Marker> markers;
	protected Marker originMarker;
	protected int originId;
	private float markerSize;
	protected CameraParameters cp;
	public CameraParameters getCp() {
		return cp;
	}

	public void setCp(CameraParameters cp) {
		this.cp = cp;
	}

	protected int originMarkersCount = 0;

	public CameraFrame(Mat frame, int originId, float markerSize, CameraParameters cp){
		this.frame = frame;
		this.originId = originId;
		this.markerSize = markerSize;
		this.cp = cp;
		markers = new Vector<Marker>();

	}
	
	public CameraFrame(Mat frame, int originId, float markerSize){
		this.frame = frame;
		this.originId = originId;
		this.markerSize = markerSize;

		markers = new Vector<Marker>();

	}

	public CameraFrame() {
	}

	
	/**
	 * funzione di detection dei marker, determina anche il numero di marker origine nella scena
	 */
	public int detectMarkers(){
		MarkerDetector md = new MarkerDetector();
		markers.clear();
		md.detect(frame, markers, cp, markerSize, null);
		originMarkersCount = 0;
		for(Marker m : markers){
			if(m.getMarkerId() == originId){
				originMarker = m;
				originMarkersCount++;
			}
		}
		if(originMarkersCount > 1)
			originMarker = null;

		return originMarkersCount;
	}

	/**
	 * disegna i marker sull'immagine
	 */
	public void drawMarkers(){
		for(Marker m : markers){
			int id = m.getMarkerId();
			if(id == originId){
				drawOrigin(frame, m);
			}else{
				if(originMarkersCount == 1){
					if(cp != null)
						//ho una calibrazione, posso scrivere le coordinate 3d sul marker
						drawMarkerCoord(frame, m);
					else
						//non ho una calibrazione, scrivo l'id del marker
						drawMarkerContour(frame, m);
				}else{
					//ci sono più marker origine, scrivo l'id del marker
					drawMarkerContour(frame, m);
				}
			}
		}
	}

	/**
	 * disegna il contorno e le coordinate del marker sull'immagine
	 */
	public Mat drawMarkerCoord(Mat editFrame, Marker m){
		Point3 coords = GeometryUtils.findCoords(m, originMarker);
		m.draw(editFrame, new Scalar(0,120,255), 2, "(" + coords.x + "," + coords.y + "," + coords.z + ")");
		//m.draw(editFrame, new Scalar(0,120,255), 2, String.valueOf(m.getMarkerId()));
		//double distance = Math.sqrt(x*x + y*y + z*z);
		//m.draw(f, new Scalar(0,255,255), 2, String.valueOf(distance));
		//Log.i("MarkerAR", "ID: " + m.getMarkerId() + " distance: " + distance);
		//m.draw3dAxis(f, cp, new Scalar(0,255,255));
		return editFrame;
	}

	/**
	 * disegna il contorno e l'id del marker sull'immagine
	 */
	public Mat drawMarkerContour(Mat editFrame, Marker m){
		int id = m.getMarkerId();
		m.draw(editFrame, new Scalar(0,120,255), 2, String.valueOf(id));
		return editFrame;
	}

	/**
	 * disegna il contorno e le coordinate del marker origine, eventualmente anche gli assi del sistema di riferimento
	 */
	protected void drawOrigin(Mat editFrame, Marker origin){
		origin.draw(editFrame, new Scalar(255,0,0), 2, "(0,0,0)");
		if(cp != null)
			origin.draw3dAxis(editFrame, cp, new Scalar(255,0,0));
	}
	
	public Bitmap toBitmap(){
		Bitmap img = Bitmap.createBitmap(frame.cols(), frame.rows(),Bitmap.Config.ARGB_8888); //crea una bitmap e visualizzala nella ImageView
		Utils.matToBitmap(frame, img);
		return img;
	}

	public Mat getFrame(){
		return frame;
	}
	
	public Vector<Marker> getMarkers() {
		return markers;
	}

	public void setMarkers(Vector<Marker> markers) {
		this.markers = markers;
	}
	
	public int getOriginCount(){
		return originMarkersCount;
	}
	
	public int getOriginId() {
		return originId;
	}

	public void setOriginId(int originId) {
		this.originId = originId;
	}
	
	public float getMarkerSize() {
		return markerSize;
	}

	public void setMarkerSize(float markerSize) {
		this.markerSize = markerSize;
	}
	
	public Marker getOriginMarker() {
		return originMarker;
	}

	public void setOriginMarker(Marker originMarker) {
		this.originMarker = originMarker;
	}
}
