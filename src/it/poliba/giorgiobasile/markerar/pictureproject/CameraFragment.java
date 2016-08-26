package it.poliba.giorgiobasile.markerar.pictureproject;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.markerdetection.CalibrationDialogFragment;
import it.poliba.giorgiobasile.markerar.markerdetection.CameraParameters;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.samples.cameracalibration.CalibrationResult;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 
 * @author Giorgio
 * Fragment che implementa la fotocamera di OpenCV per la visualizzazione dei marker in realtà aumentata
 * e lo scatto della foto
 */

public class CameraFragment extends Fragment implements CvCameraViewListener2{
	
	
	public CameraFragment(){}

	public static final String TAG = "CAMERA_FRAGMENT";
	//private PortraitCameraView mOpenCvPortraitView;
	private JavaCameraView mOpenCvLandscapeView;
	private Mat lastFrame;
	private CameraFrame prevCf;
	private PictureFrame pf;
	private int originId;
	private float markerSize;
	private boolean flash = false;
	CameraParameters cp;
	int width = 0, height = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {    
		View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

		/*if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			mOpenCvPortraitView = (PortraitCameraView) rootView.findViewById(R.id.portrait);
			mOpenCvPortraitView.setVisibility(SurfaceView.VISIBLE);
			mOpenCvPortraitView.setCvCameraViewListener(this);

		}else{*/
		mOpenCvLandscapeView = (JavaCameraView) rootView.findViewById(R.id.landscape);
		mOpenCvLandscapeView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvLandscapeView.setCvCameraViewListener(this);

		//}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}

	private BaseLoaderCallback mLoaderCallback;

	public void onAttach(Activity activity){
		super.onAttach(activity);
		//setta il callback per il caricamento della libreria, cioè il codice all'interno del callback verrà chiamato a libreria caricata
		mLoaderCallback = new BaseLoaderCallback(getActivity()) {
			@Override
			public void onManagerConnected(int status) {
				switch (status) {
				case LoaderCallbackInterface.SUCCESS:

					Log.i(TAG, "OpenCV loaded successfully");
					if(mOpenCvLandscapeView != null){
						mOpenCvLandscapeView.enableView();
						try{
							//caricamento file di calibrazione della fotocamera
							cp = new CameraParameters();
							cp.readFromXML(CalibrationResult.CAMERA_PATH);
						}catch(Exception e){
							cp = null;
							//se non trovato, richiede all'utente la calibrazione
							CalibrationDialogFragment calib = CalibrationDialogFragment.newInstance();
							calib.show(getActivity().getSupportFragmentManager(), CalibrationDialogFragment.TAG);
						}

						//
					}	
					break;
				default:
					super.onManagerConnected(status);
					break;
				}
			}
		};
	}


	@Override
	public void onResume()
	{
		super.onResume();
		if(isAdded()){
			//carica la libreria di OpenCV
			OpenCVInit(getActivity());
		}	

	}

	public void OpenCVInit(Activity activity){
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, activity, mLoaderCallback);
		//if(mOpenCvPortraitView != null)
		//mOpenCvPortraitView.enableView();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		originId = Integer.valueOf(sp.getString(getString(R.string.origin_marker_id_key), getString(R.string.origin_marker_id_default)));
		markerSize = Float.valueOf(sp.getString(getString(R.string.markers_size_key), getString(R.string.markers_size_default)));
		mOpenCvLandscapeView.setKeepScreenOn(true);
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_photo) {
			takeShot();
			return true;
		}else if(id == R.id.action_flash_on){
			mOpenCvLandscapeView.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			flash = false;
			getActivity().invalidateOptionsMenu();
			return true;
		}else if(id == R.id.action_flash_off){
			mOpenCvLandscapeView.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			flash = true;
			getActivity().invalidateOptionsMenu();

			return true;
		}	
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		MenuItem register = menu.findItem(R.id.action_flash_on);
		register.setVisible(flash);
		register = menu.findItem(R.id.action_flash_off);
		register.setVisible(!flash);
	}	
	private void takeShot(){
		//scatta la foto e apre il fragment per il settaggio delle proprietà dei marker
		int originCount = prevCf.getOriginCount();
		if(originCount == 1){ //verifica che l'ultimo frame ricevuto abbia il marker origine
			mOpenCvLandscapeView.disableView();//disabilita la view Camera e attiva l'ImageView per il frame
			System.out.println(lastFrame.width() + " " + lastFrame.height());
			pf = new PictureFrame(lastFrame.clone(), prevCf.getOriginId(), prevCf.getMarkerSize(), prevCf.getCp());
			pf.detectMarkers();
			pf.drawMarkers();
			getActivity().getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, new PictureFragment(pf), PictureFragment.TAG)
			.commit();
		}else if(originCount == 0){
			Toast.makeText(getActivity(), R.string.origin_lack, Toast.LENGTH_SHORT).show();
		}else if(originCount > 1){
			Toast.makeText(getActivity(), R.string.origin_too_many, Toast.LENGTH_SHORT).show();

		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		//if (mOpenCvPortraitView != null)
		//mOpenCvPortraitView.disableView();
		if (mOpenCvLandscapeView != null)
			mOpenCvLandscapeView.disableView();
	}

	public void onDestroy() {
		super.onDestroy();
		//if (mOpenCvPortraitView != null)
		//mOpenCvPortraitView.disableView();
		if (mOpenCvLandscapeView != null)
			mOpenCvLandscapeView.disableView();

		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		mOpenCvLandscapeView.setKeepScreenOn(false);
	}

	public void onCameraViewStarted(int width, int height) {
	}

	public void onCameraViewStopped() {
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		//ciclo di capturing dei frame dalla fotocamera, essi vengono modificati 
		//e restituiti all'oggetto JavaCameraView per la visualizzazione (realtà aumentata)
		
		Mat f = inputFrame.rgba();
		//f = rotate(f, -90);
		//Imgproc.resize(f, f, new Size(f.cols(), f.rows()));
		width = f.width();
		height = f.height();

		if(lastFrame != null){
			lastFrame.release();
			lastFrame = null;
		}
		/*if(cp != null){
			lastFrame = new Mat(new Size(f.width(), f.height()), f.type());
			Mat optimalCameraMatrix = Calib3d.getOptimalNewCameraMatrix(cp.getCameraMatrix(),cp.getDistCoeff(),f.size(),1);
			Imgproc.undistort(f, lastFrame, cp.getCameraMatrix(), cp.getDistCoeff(), optimalCameraMatrix);
		}else{*/
			lastFrame = f.clone(); 
		//}	
		
		
		CameraFrame cf = new CameraFrame(f, originId, markerSize, cp);
		cf.detectMarkers();
		prevCf = cf;
		cf.drawMarkers();
		return cf.getFrame();
	}

	void rotate(Mat src, double angle, Mat dst)
	{
		int len = Math.max(src.cols(), src.rows());
		Point pt = new Point(len/2., len/2.);
		Mat r = Imgproc.getRotationMatrix2D(pt, angle, 1.0);

		Imgproc.warpAffine(src, dst, r, new Size(len, len));
	}

	Mat rotate(Mat image, double angle)
	{
		Point src_center = new Point(image.cols()/2.0F, image.rows()/2.0F);

		Mat rot_matrix = Imgproc.getRotationMatrix2D(src_center, angle, 1.0);

		Mat rotated_img = new Mat(new Size(image.size().height, image.size().width), image.type());

		Imgproc.warpAffine(image, rotated_img, rot_matrix, rotated_img.size());

		return rotated_img;
	}



}
