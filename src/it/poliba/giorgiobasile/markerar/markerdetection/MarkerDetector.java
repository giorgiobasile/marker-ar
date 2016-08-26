package it.poliba.giorgiobasile.markerar.markerdetection;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Classe per la marker detection. Verranno utilizzate tecniche di sogliatura 
 * e di estrazione dei contorni, alla ricerca di marker contenenti un codice valido
 * 
 * N.B. IL CODICE E' STATO SCRITTO RIFERENDOSI AL CODICE Objective-C PRESENTE IN
 * 
 * Mastering OpenCV with Practical Computer Vision Projects, Capitolo 2
 * 
 * N.B.2 Al termine del libro è presente nei riferimenti la libreria Aruco
 * Alcune parti del codice utilizzato per la marker detection provengono dalla libreria Aruco per Android
 * https://code.google.com/p/aruco-android/
 *
 */

public class MarkerDetector {

	private Mat grey, thres, thres2, hierarchy2;
	private Vector<MatOfPoint> contours2;
	private final static double MIN_DISTANCE = 100;

	public MarkerDetector(){
		grey = new Mat();
		thres = new Mat();
		thres2 = new Mat();
		hierarchy2 = new Mat();
		contours2 = new Vector<MatOfPoint>();
	}

	void prepareImage(Mat in, Mat greyscale){
		Imgproc.cvtColor(in, greyscale, Imgproc.COLOR_RGBA2GRAY);
	}

	void performThreshold(Mat greyscale, Mat thresholdImg){

		Imgproc.threshold(greyscale, thresholdImg, 127, 255, Imgproc.THRESH_BINARY_INV);
	}

	void findContours(Mat thresholdImg, Vector<MatOfPoint> contours, int minContourPointsAllowed){
		Imgproc.findContours(thresholdImg, contours, hierarchy2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
		contours.clear();
		for (int i = 0; i < contours.size(); i++)
		{
			Size contourSize = contours.get(i).size();
			if (contourSize.area() > minContourPointsAllowed)
			{
				contours.addElement(contours.get(i));
			}
		}

	}

	void findCandidates(Vector<MatOfPoint> contours, Vector<Marker> detectedMarkers, int markerSizeMeters){
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		Vector<Marker> candidateMarkers = new Vector<Marker>();
		for(int i=0;i<contours2.size();i++){
			MatOfPoint2f contour = new MatOfPoint2f();
			contours2.get(i).convertTo(contour, CvType.CV_32FC2);
			if (approxCurve.toList().size() != 4)
				continue;
			MatOfPoint mat = new MatOfPoint();
			approxCurve.convertTo(mat, CvType.CV_32SC2);
			if (!Imgproc.isContourConvex(mat))
				continue;
			double minDistFound = Double.MAX_VALUE;
			float[] points = new float[8];// [x1 y1 x2 y2 x3 y3 x4 y4]
			approxCurve.get(0,0,points);
			// look for the min distance
			for(int j=0;j<=4;j+=2){
				double d = Math.sqrt( (points[j]-points[(j+2)%4])*(points[j]-points[(j+2)%4]) +
						(points[j+1]-points[(j+3)%4])*(points[j+1]-points[(j+3)%4]));
				if(d<minDistFound)
					minDistFound = d;
			}
			if(minDistFound > MIN_DISTANCE){
				// create a candidate marker
				Vector<Point> p = new Vector<Point>();
				p.add(new Point(points[0],points[1]));
				p.add(new Point(points[2],points[3]));
				p.add(new Point(points[4],points[5]));
				p.add(new Point(points[6],points[7]));
				candidateMarkers.add(new Marker(markerSizeMeters, p));
			}
		}	
	}

	/**
	 * Method to find markers in a Mat given.
	 * @param in input color Mat to find the markers in.
	 * @param detectedMarkers output vector with the markers that have been detected.
	 * @param camMatrix --
	 * @param distCoeff --
	 * @param markerSizeMeters --
	 * @param frameDebug used for debug issues, delete this
	 */
	public void detect(Mat in, Vector<Marker> detectedMarkers, CameraParameters cp,//Mat camMatrix, Mat distCoeff,
			float markerSizeMeters, Mat frameDebug){
		Vector<Marker> candidateMarkers = new Vector<Marker>();
		// the detection in the incoming frame will be done in a different vector
		// because this will allow the ontouchlistener in View
		// to have a valid detectedMarkers vector longer
		Vector<Marker> newMarkers = new Vector<Marker>();

		// do the threshold of image and detect contours

		prepareImage(in, grey);

		performThreshold(grey, thres);

		// pass a copy because it modifies the src image
		thres.copyTo(thres2);
		Imgproc.findContours(thres2, contours2, hierarchy2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

		// to each contour analyze if it is a paralelepiped likely to be a marker
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		//		List<Point> approxPoints = new ArrayList<Point>();
		for(int i=0;i<contours2.size();i++){
			MatOfPoint2f contour = new MatOfPoint2f();
			contours2.get(i).convertTo(contour, CvType.CV_32FC2);
			// first check if it has enough points
			int contourSize = (int)contour.total();
			if(contourSize > in.cols()/5){
				Imgproc.approxPolyDP(contour, approxCurve, contourSize*0.05, true);
				//				Converters.Mat_to_vector_Point(approxCurve, approxPoints);
				// check the polygon has 4 points
				if(approxCurve.total()== 4){
					// and if it is convex
					MatOfPoint mat = new MatOfPoint();
					approxCurve.convertTo(mat, CvType.CV_32SC2);
					if(Imgproc.isContourConvex(mat)){
						// ensure the distance between consecutive points is large enough
						//double minDistFound = Double.MAX_VALUE;
						float[] points = new float[8];// [x1 y1 x2 y2 x3 y3 x4 y4]
						approxCurve.get(0,0,points);
						
						// create a candidate marker
						Vector<Point> p = new Vector<Point>();
						p.add(new Point(points[0],points[1]));
						p.add(new Point(points[2],points[3]));
						p.add(new Point(points[4],points[5]));
						p.add(new Point(points[6],points[7]));
						candidateMarkers.add(new Marker(markerSizeMeters, p));

					}
				}
			}
		}// all contours processed, now we have the candidateMarkers
		int nCandidates = candidateMarkers.size();

		// sort the points in anti-clockwise order
		for(int i=0;i<nCandidates;i++){
			Marker marker = candidateMarkers.get(i);
			List<Point> p = new Vector<Point>();
			p = marker.toList();
			// trace a line between the first and second point.
			// if the third point is at the right side, then the points are anti-clockwise
			double dx1 = p.get(1).x - p.get(0).x;
			double dy1 = p.get(1).y - p.get(0).y;
			double dx2 = p.get(2).x - p.get(0).x;
			double dy2 = p.get(2).y - p.get(0).y;
			double o = dx1*dy2 - dy1*dx2;
			if(o < 0.0){ // the third point is in the left side, we have to swap
				Collections.swap(p, 1, 3);
				marker.setPoints(p);
			}
		}// points sorted in anti-clockwise order

		Vector<Integer> toRemove = new Vector<Integer>();// 1 means to remove
		for(int i=0;i<nCandidates;i++)
			toRemove.add(0);

		// identify the markers
		for(int i=0;i<nCandidates;i++){
			if(toRemove.get(i) == 0){
				Marker marker = candidateMarkers.get(i);
				Mat canonicalMarker = new Mat();
				warp(in, canonicalMarker, new Size(50,50), marker.toList());
				marker.setMat(canonicalMarker);
				marker.extractCode();
				if(marker.checkBorder()){
					int id = marker.calculateMarkerId();
					if(id != -1){
						newMarkers.add(marker);
						// rotate the points of the marker so they are always in the same order no matter the camera orientation
						Collections.rotate(marker.points, 4-marker.getRotations());
						marker.fromList(marker.points);
					}
				}
			}
		}

		// now sort by id and check that each marker is only detected once
		Collections.sort(newMarkers);
		toRemove.clear();
		// detect the position of markers if desired
		for(int i=0;i<newMarkers.size();i++){
			if(cp != null)
				if(cp.isValid())
					newMarkers.get(i).calculateExtrinsics(cp.getCameraMatrix(), cp.getDistCoeff(), markerSizeMeters);
		}
		detectedMarkers.setSize(newMarkers.size());
		Collections.copy(detectedMarkers, newMarkers);
	}


	/**
	 * This fits a mat containing 4 vertices captured through the camera
	 * into a canonical mat.
	 * @param in the frame captured
	 * @param out the canonical mat
	 * @param size the size of the canonical mat we want to create
	 * @param points the coordinates of the points in the "in" mat 
	 */
	private void warp(Mat in, Mat out, Size size, List<Point> points){
		Mat pointsIn = new Mat(4,1,CvType.CV_32FC2);
		Mat pointsRes = new Mat(4,1,CvType.CV_32FC2);
		pointsIn.put(0,0, points.get(0).x,points.get(0).y,
				points.get(1).x,points.get(1).y,
				points.get(2).x,points.get(2).y,
				points.get(3).x,points.get(3).y);
		pointsRes.put(0,0, 0,0,
				size.width-1,0,
				size.width-1,size.height-1,
				0,size.height-1);
		Mat m = new Mat();
		m = Imgproc.getPerspectiveTransform(pointsIn, pointsRes);
		Imgproc.warpPerspective(in, out, m, size);
	}
}
