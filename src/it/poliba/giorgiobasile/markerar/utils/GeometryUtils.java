package it.poliba.giorgiobasile.markerar.utils;

import it.poliba.giorgiobasile.markerar.markerdetection.Marker;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;

import android.graphics.Bitmap;

/**
 * 
 * @author Giorgio
 * Routines di utilità per calcoli geometrici sulle immagini
 */


public class GeometryUtils {
	public static Mat rotationCorrection(Mat Rvec, Mat Tvec){
		Mat m = new Mat(3,1,CvType.CV_64FC1);
		Mat R = new Mat(3,3,CvType.CV_64FC1);
		Calib3d.Rodrigues(Rvec, R);
		Core.gemm(R.inv(), Tvec, 1, new Mat(),1, m);
		return m;
	}


	public static Mat coordDistance(Mat m1, Mat m2){
		Mat m = new Mat(3,1,CvType.CV_64FC1);
		Core.subtract(m1, m2, m);
		return m;
	}

	public static void round(Mat m){
		DecimalFormat df = new DecimalFormat("#.#");
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(dfs);
		for(int i = 0; i < 3; i++){
			String number = df.format(m.get(i, 0)[0] * 100);
			Double num = Double.valueOf(number);
			m.put(i, 0, new double[]{num});
		}
	}

	public static Mat swap(Mat m, int i, int j){
		Mat d = m.clone();
		double x = m.get(i, 0)[0];
		double y = m.get(j, 0)[0];
		d.put(i, 0, y);
		d.put(j, 0, x);
		return d;
	}

	public static Point findCentroid(Marker m){
		Vector<Point> points = m.points;
		Point cent = new Point(0,0);
		for(int i=0;i<4;i++){
			cent.x += points.get(i).x;
			cent.y += points.get(i).y;
		}
		cent.x/=4.;
		cent.y/=4.;
		return cent;
	}
	
	public static Bitmap matToBitmap(Mat f){
		Bitmap img = Bitmap.createBitmap(f.cols(), f.rows(),Bitmap.Config.ARGB_8888); //crea una bitmap e visualizzala nella ImageView
		Utils.matToBitmap(f, img);
		return img;
	}
	
	public static Point3 findCoords(Marker m, Marker originMarker){
		double x, y, z;
		Mat coord = GeometryUtils.coordDistance(m.Tvec, originMarker.Tvec);
		//Mat Rvec = GeometryUtils.swap(originMarker.Rvec, 0,2);
		coord = GeometryUtils.rotationCorrection(originMarker.Rvec, coord);
		try{
			GeometryUtils.round(coord);
		}catch(Exception e){
			e.printStackTrace();
		}
		x = coord.get(0, 0)[0];
		y = coord.get(1, 0)[0];
		z = -coord.get(2, 0)[0];
		
		return new Point3(x, y, z);
	}
}
