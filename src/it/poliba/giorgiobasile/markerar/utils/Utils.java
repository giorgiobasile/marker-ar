package it.poliba.giorgiobasile.markerar.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

/**
 * 
 * @author Giorgio
 * Routines di utilità
 */

public class Utils {
	
	
	public static String ROOT_DIR = "MarkerAR";
	public static String SCHEMAS_DIR = "Schemas";
	public static String CALIB_DIR = "Calibration";
	public static String MARKER_IMGS_DIR = "MarkerImages";
	public static String PROJECT_DIR = "Projects";
	public static String PREFS_NAME = "preferences.xml";
	 	
	public static String SCHEMAS_PATH = Environment.getExternalStorageDirectory() + File.separator + ROOT_DIR + File.separator + SCHEMAS_DIR; 
	public static String CALIB_PATH = Environment.getExternalStorageDirectory() + File.separator + ROOT_DIR + File.separator + CALIB_DIR; 
	public static String MARKER_IMGS_PATH = Environment.getExternalStorageDirectory() + File.separator + ROOT_DIR + File.separator + MARKER_IMGS_DIR; 
	public static String ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator + ROOT_DIR;
	public static String PROJECT_PATH = Environment.getExternalStorageDirectory() + File.separator + ROOT_DIR + File.separator + PROJECT_DIR;
	
	
	public static void copyFile(File source, File dest){
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try{
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			
			inputChannel.close();
			outputChannel.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static float[] remap(int x, int y, ImageView frameView){
		int distanceX = 0, distanceY = 0;
		if(frameView != null){
			distanceX = (frameView.getWidth() - Utils.imageScaledSize(frameView)[0]) / 2;
			x = x - distanceX;
			distanceY = (frameView.getHeight() - Utils.imageScaledSize(frameView)[1]) / 2;
			y = y - distanceY;
		}
		return new float[]{x,y};
	}
	
	public static double[] remap(double x, double y, ImageView frameView){
		int distanceX = 0, distanceY = 0;
		if(frameView != null){
			distanceX = (frameView.getWidth() - Utils.imageScaledSize(frameView)[0]) / 2;
			x = x - distanceX;
			distanceY = (frameView.getHeight() - Utils.imageScaledSize(frameView)[1]) / 2;
			y = y - distanceY;
		}
		return new double[]{x,y};
	}
	
	public static int[] imageScaledSize(ImageView iv){
		// Get image matrix values and place them in an array
        float[] f = new float[9];
        iv.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = iv.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);
		return new int[]{actW, actH};
	}
	
	public static float[] getScale(ImageView iv){
		float[] f = new float[9];
        iv.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];
        
        return new float[]{scaleX, scaleY};
	}
}
