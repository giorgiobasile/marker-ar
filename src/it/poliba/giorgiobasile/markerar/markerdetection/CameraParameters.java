package it.poliba.giorgiobasile.markerar.markerdetection;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Size;

/**
 * Camera parameters richiesti per il rendering 3d.
 * Saranno in un file XML generato dall'esempio di camera calibration di OpenCV
 * I parametri utilizzati saranno la camera matrix e la matrice dei coefficienti di distorsione.
 *
 */
public class CameraParameters {

	// cameraMatrix will be of the form
	// | Fx 0  Cx |
	// | 0  Fy Cy |
	// | 0  0   1 |
	private Mat cameraMatrix;
	private MatOfDouble distorsionMatrix;
	private Size camSize;
	
	public CameraParameters(){
		cameraMatrix = new Mat(3,3,CvType.CV_32FC1);
		distorsionMatrix = new MatOfDouble();
	}
	
    /**Indicates whether this object is valid
     */
    public boolean isValid(){
    	if(cameraMatrix != null)
    		return cameraMatrix.rows()!=0 && cameraMatrix.cols()!=0  && 
    		distorsionMatrix.total() > 0;
    	else
    		return false;
    }
	
	public Mat getCameraMatrix(){
		return cameraMatrix;
	}
	
	public MatOfDouble getDistCoeff(){
		return distorsionMatrix;
	}
	
	public void resize(Size size) throws CPException{
	    if (!isValid()) 
	    	throw new CPException("invalid object CameraParameters::resize");
	    if (size == camSize)
	    	return;
	    //now, read the camera size
	    //resize the camera parameters to fit this image size
	    float AxFactor= (float)(size.width)/ (float)(camSize.width);
	    float AyFactor= (float)(size.height)/ (float)(camSize.height);
		float[] current = new float[9];
	    cameraMatrix.get(0, 0, current);
		float[] buff = {current[0]*AxFactor, current[1],          current[2]*AxFactor,
				        current[3],          current[4]*AyFactor, current[5],
				        current[6],          current[7],          current[8]};
		cameraMatrix.put(0, 0, buff);
	}
	
	public void readFromXML(String filepath){		
		File file = new File(filepath);

		Configuration conf;
		try {
			conf = new XMLConfiguration(file);
			Configuration cameraConf = conf.subset("camera_matrix");
			String data = new String();
			data = cameraConf.getString("data");
			StringTokenizer st = new StringTokenizer(data);
			double[] array = new double[9];
			int i = 0;
			while(st.hasMoreElements()){
				array[i] = Double.valueOf(st.nextToken());
				i++;
			}
			cameraMatrix.put(0, 0, array[0], array[1], array[2],
								   array[3], array[4], array[5],
								   array[6], array[7], array[8]);
			// parse the distorsion matrix
			Configuration distortionConf = conf.subset("distortion_coefficients");
			String coeffData = new String();
			coeffData = distortionConf.getString("data");
			StringTokenizer std = new StringTokenizer(coeffData);
			double[] coeffArray = new double[5];
			i = 0;
			while(std.hasMoreElements()){
				coeffArray[i] = Double.valueOf(std.nextToken());
				i++;
			}
			distorsionMatrix.fromArray(coeffArray);	
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
