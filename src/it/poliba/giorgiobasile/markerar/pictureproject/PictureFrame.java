package it.poliba.giorgiobasile.markerar.pictureproject;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.markerdetection.CameraParameters;
import it.poliba.giorgiobasile.markerar.markerdetection.Marker;
import it.poliba.giorgiobasile.markerar.markerdetection.MarkerObject;
import it.poliba.giorgiobasile.markerar.markerdetection.MarkerSave;
import it.poliba.giorgiobasile.markerar.properties.Property;
import it.poliba.giorgiobasile.markerar.utils.GeometryUtils;
import it.poliba.giorgiobasile.markerar.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.samples.cameracalibration.CalibrationResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

/**
 * 
 * @author Giorgio
 * Classe che contiene un frame scattato dall'utente
 */
public class PictureFrame extends CameraFrame{

	public static String MARKERS_NAME = "markers.bmp";
	public static String ORIGINAL_NAME = "original.bmp";
	public static String PROPERTIES_NAME = "properties.bin";
	public static String PROPERTIES_XML = "properties.xml";

	private Mat originalFrame;
	private ArrayList<MarkerObject> markerObjects;

	public PictureFrame(Mat frame, int originId, float markerSize, CameraParameters cp) {
		super(frame, originId, markerSize, cp);
		originalFrame = frame.clone();
		markerObjects = new ArrayList<MarkerObject>();
	}

	public PictureFrame(Mat frame, int originId, float markerSize) {
		super(frame, originId, markerSize);
		originalFrame = frame.clone();
		markerObjects = new ArrayList<MarkerObject>();
	}

	public PictureFrame() {
		super();
	}

	public void drawMarkerLabels(Mat editFrame, Marker m){
		int id = m.getMarkerId();
		/*if(!markers.containsKey(m)){
			labels.put(m, String.valueOf(id));
		}*/
		//m.draw(frame, new Scalar(0,120,255), 2, labels.get(m));
		m.draw(frame, new Scalar(0,120,255), 2, String.valueOf(id));
	}

	/*public HashMap<Marker, String> getLabels() {
		return labels;
	}

	public void setLabels(HashMap<Marker, String> labels) {
		this.labels = labels;
	}*/

	public ArrayList<MarkerObject> getMarkerObjects() {
		return markerObjects;
	}

	public void setMarkerObjects(ArrayList<MarkerObject> markerObjects) {
		this.markerObjects = markerObjects;
	}

	@Override
	public void drawMarkers(){
		for(MarkerObject m : markerObjects){
			Marker mark = m.getMarker();
			int id = mark.getMarkerId();
			if(id == originId){
				drawOrigin(frame, mark);
			}else{
				//if(originMarkersCount == 1){
				//if(cp != null)
				//drawMarkerCoord(frame, mark);
				//else
				drawMarkerContour(frame, m);
			}
		}
	}

	public Mat drawMarkerContour(Mat editFrame, MarkerObject mo){
		System.out.println(mo.getCoord3d());
		mo.getMarker().draw(editFrame, new Scalar(0,120,255), 2, mo.getLabel());
		return editFrame;
	}

	@Override
	public int detectMarkers(){
		int originMarkerCount = super.detectMarkers();
		markerObjects.clear();
		for(Marker m : markers){
			MarkerObject mobj = new MarkerObject(m, originId);
			if(m.getMarkerId() != originId){
				if(cp != null)
					mobj.setCoord3d(GeometryUtils.findCoords(m, originMarker));
				else
					mobj.setCoord3d(new Point3(0,0,0));
			}
			markerObjects.add(mobj);
		}
		return originMarkerCount;
	} 

	public Marker touchedMarker(float x, float y){
		for(MarkerObject m : markerObjects){
			Marker mark = m.getMarker();
			if(Imgproc.pointPolygonTest(mark, new Point(x,y), false) >= 0){
				return mark;
			}else{
				//Log.i("POINTS",m.toList().toString());
			}
		}
		return null;
	}

	public void resetFrame(){
		if(frame != null){
			frame.release();
			frame = null;
		}
		frame = originalFrame.clone();
	}

	public Mat getOriginalFrame() {
		return originalFrame;
	}

	public void setOriginalFrame(Mat originalFrame) {
		this.originalFrame = originalFrame;
	}

	public void replace(MarkerObject mo){
		for(int i = 0; i < markerObjects.size(); i++){
			if(markerObjects.get(i).getMarker() == mo.getMarker()){
				markerObjects.remove(i);
				markerObjects.add(i, mo);
			}
		}
	}

	public ArrayList<MarkerSave> getMarkersData(){
		ArrayList<MarkerSave> data = new ArrayList<MarkerSave>();
		for(int i = 0; i < getMarkerObjects().size(); i++){
			MarkerObject mo = getMarkerObjects().get(i);
			MarkerSave ps = new MarkerSave(mo.getCoord3d(), mo.getCentroid(), mo.getProperties(), mo.getLabel());
			data.add(ps);
		}

		return data;
	}

	@SuppressWarnings("unchecked")
	public void loadProperties(String filePath){
		try{
			ArrayList<MarkerSave> ls = new ArrayList<MarkerSave>();
			FileInputStream fin = new FileInputStream(filePath + File.separator + PROPERTIES_NAME);
			ObjectInputStream ois;
			ois = new ObjectInputStream(fin);
			ls = (ArrayList<MarkerSave>) ois.readObject();
			ois.close();
			for(MarkerObject mo : getMarkerObjects()){
				Point p = GeometryUtils.findCentroid(mo.getMarker());
				for(MarkerSave l : ls){
					if(p.x == l.getU() && p.y == l.getV()){
						mo.setProperties(l.getProperties());
						mo.setLabel(l.getLabel());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
	}


	public void saveOriginal(String filePath) {
		Bitmap img = GeometryUtils.matToBitmap(originalFrame);
		File f = new File(filePath + File.separator + ORIGINAL_NAME);
		try {
			FileOutputStream out = new FileOutputStream(f);
			img.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void saveEdited(Context context, String filePath){	
		Bitmap img = GeometryUtils.matToBitmap(frame);
		File f = new File(filePath + File.separator + MARKERS_NAME);
		FileOutputStream out;
		try {
			out = new FileOutputStream(f);
			img.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
			Toast.makeText(context, f.getParentFile().getName() + " " + context.getString(R.string.save_success), Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Salva proprietà
		ArrayList<MarkerSave> data = getMarkersData();

		if(!data.isEmpty()){
			try {
				FileOutputStream fout = new FileOutputStream(filePath + File.separator + PROPERTIES_NAME);
				ObjectOutputStream oos;
				oos = new ObjectOutputStream(fout);
				oos.writeObject(data);
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void exportXML(String filePath){
		ArrayList<MarkerSave> data = getMarkersData();

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element markers = doc.createElement("markers");
			doc.appendChild(markers);

			for(MarkerSave ps : data){
				// staff elements
				Element marker = doc.createElement("marker");
				markers.appendChild(marker);

				/*// set attribute to staff element
			Attr attr = doc.createAttribute("id");
			attr.setValue("1");
			staff.setAttributeNode(attr);

			// shorten way
			// staff.setAttribute("id", "1");*/

				// firstname elements
				Element u = doc.createElement("u");
				u.appendChild(doc.createTextNode(String.valueOf(ps.getU())));
				marker.appendChild(u);

				// lastname elements
				Element v = doc.createElement("v");
				v.appendChild(doc.createTextNode(String.valueOf(ps.getV())));
				marker.appendChild(v);

				Element x = doc.createElement("x");
				x.appendChild(doc.createTextNode(String.valueOf(ps.getX())));
				marker.appendChild(x);

				Element y = doc.createElement("y");
				y.appendChild(doc.createTextNode(String.valueOf(ps.getY())));
				marker.appendChild(y);

				Element z = doc.createElement("z");
				z.appendChild(doc.createTextNode(String.valueOf(ps.getZ())));
				marker.appendChild(z);

				Element properties = doc.createElement("properties");
				marker.appendChild(properties);

				Element label = doc.createElement("label");
				label.appendChild(doc.createTextNode(ps.getLabel()));
				marker.appendChild(label);

				for(Property p : ps.getProperties()){
					Element property = doc.createElement("property");
					properties.appendChild(property);
					Element name = doc.createElement("name");
					name.appendChild(doc.createTextNode(p.getName()));
					property.appendChild(name);
					Element value = doc.createElement("value");
					value.appendChild(doc.createTextNode(p.getCurrentValue()));
					property.appendChild(value);
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			try {
				File xml = new File(filePath + File.separator + PROPERTIES_XML);
				xml.createNewFile();
				StreamResult result;
				result = new StreamResult(new PrintWriter(
						new FileOutputStream(xml, false)));
				transformer.transform(source, result);
				System.out.println("File saved!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public void saveCalibration(String destPath){
		File source = new File(CalibrationResult.CAMERA_PATH);
		File dest = new File(destPath + File.separator + CalibrationResult.CAMERA_NAME);
		Utils.copyFile(source, dest);
	}

	public boolean savePrefs(Context context, String path){

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element preferences = doc.createElement("preferences");
			doc.appendChild(preferences);

			Element origin = doc.createElement("origin");
			origin.appendChild(doc.createTextNode(String.valueOf(getOriginId())));
			preferences.appendChild(origin);

			Element size = doc.createElement("size");
			size.appendChild(doc.createTextNode(String.valueOf(getMarkerSize())));

			preferences.appendChild(size);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			try {
				File xml = new File(path + File.separator + Utils.PREFS_NAME);
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
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return false;
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
			return false;
		}	
	}

	public void save(Context context, String path, boolean newProject){
		if(newProject){
			saveOriginal(path);
			saveCalibration(path);
			savePrefs(context, path);
		}
		saveEdited(context, path);
		exportXML(path);
	}

}
