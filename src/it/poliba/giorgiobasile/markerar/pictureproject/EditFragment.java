package it.poliba.giorgiobasile.markerar.pictureproject;


import java.io.Serializable;
import java.util.ArrayList;

import it.poliba.giorgiobasile.markerar.R;

import it.poliba.giorgiobasile.markerar.browse.BrowsePictureFragment;
import it.poliba.giorgiobasile.markerar.properties.PropertiesActivity;
import it.poliba.giorgiobasile.markerar.properties.Property;
import it.poliba.giorgiobasile.markerar.settings.SettingsActivity;

import org.opencv.android.Utils;
import org.opencv.core.Mat;


import it.poliba.giorgiobasile.markerar.markerdetection.Marker;
import it.poliba.giorgiobasile.markerar.markerdetection.MarkerObject;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 
 * @author Giorgio
 * Fragment di editing di un progetto caricato dalla memoria del dispositivo
 */

public class EditFragment extends Fragment implements OnTouchListener{

	public static final String TAG = "FRAME_FRAGMENT";
	private PictureFrame pf;
	private ImageView frameView;
	private Bitmap img;
	private String filePath;

	public EditFragment(){}

	public EditFragment(PictureFrame pf, String filePath){
		this.pf = pf;
		this.filePath = filePath;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {    
		View rootView = inflater.inflate(R.layout.fragment_frame, container, false);

		frameView = (ImageView) rootView.findViewById(R.id.frame_view);
		frameView.setOnTouchListener(this);

		if(savedInstanceState == null){
			if(pf.getCp() == null){
				Toast.makeText(getActivity(), R.string.calibration_missed, Toast.LENGTH_SHORT).show();
			}
			pf.detectMarkers();
			pf.loadProperties(filePath);
			pf.drawMarkers();
		}


		showFrame(pf.getFrame());

		return rootView;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//apre impostazioni
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			return true;
		}else if(id == R.id.action_photo_cancel){
			//rimuove lo scatto e torna al browsing
			cancelShot();
			return true;
		}else if(id == R.id.action_photo_save){
			//sovrascrive il progetto
			pf.save(getActivity(), filePath, false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	MarkerObject touched;


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		//devo rimappare le coodinate del tocco sulla view con quelle dell'immagine ivi mostrata
		float remapped[] = remap(x, y);
		x = remapped[0];
		y = remapped[1];
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Marker m = pf.touchedMarker(x, y);
			if(m != null && pf.getOriginMarker() != m){
				/*EditDialogFragment edit = EditDialogFragment.newInstance(m, pf.getLabels());
				edit.show(getActivity().getSupportFragmentManager(), EditDialogFragment.fragment_tag);*/
				//Toast.makeText(this, String.valueOf(idSelected), Toast.LENGTH_SHORT).show();
				ArrayList<Property> props = null;
				for(MarkerObject mark : pf.getMarkerObjects()){
					if(mark.getMarker() == m){
						touched = mark;
						props = touched.getProperties();
					}
				}
				Intent intent = new Intent(getActivity(), PropertiesActivity.class);
				intent.putExtra("properties", (Serializable) props);
				intent.putExtra("label", touched.getLabel());
				//faccio partire l'activity delle proprietà, al termine delle modifiche verrà richiamato il metodo onActivityResult
				startActivityForResult(intent, 0);
			}else{
				//Toast.makeText(this, "No marker, " + String.valueOf(x) + " " + String.valueOf(y), Toast.LENGTH_SHORT).show();
				//Toast.makeText(this, String.valueOf(mOpenCvLandscapeView.getWidth()) + " " + String.valueOf(width)+ " " + String.valueOf(mOpenCvLandscapeView.getHeight()) + " " + height, Toast.LENGTH_SHORT).show();
			}
			return true;
		}	
		return false;
	}

	private float[] remap(float x, float y){
		float distanceX = 0, distanceY = 0;
		if(frameView != null){
			float[] scale = it.poliba.giorgiobasile.markerar.utils.Utils.getScale(frameView);
			final int actW = Math.round(pf.getFrame().width() * scale[0]);
	        final int actH = Math.round(pf.getFrame().height() * scale[1]);
			
			distanceX = (frameView.getWidth() - actW) / 2;
			x = x - distanceX;
			distanceY = (frameView.getHeight() - actH) / 2;
			y = y - distanceY;

			x = x/scale[0];
			y = y/scale[1];
		}
		return new float[]{x,y};
	}

	private void showFrame(Mat m){
		img = Bitmap.createBitmap(m.cols(), m.rows(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(m, img);
		frameView.setImageBitmap(img);
		frameView.setVisibility(SurfaceView.VISIBLE);
	}



	private void cancelShot(){
		getActivity().getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, new BrowsePictureFragment())
		.commit();
	}

	public void onPropertiesEdited(ArrayList<Property> properties, String label){
		touched.setProperties(properties);
		if(!label.equals(touched.getLabel())){
			//se è cambiata l'etichetta, devo ridisegnare i marker sull'immagine originale
			touched.setLabel(label);
			pf.resetFrame();
			pf.drawMarkers();
			showFrame(pf.getFrame());
		}
		
		pf.replace(touched);
		touched = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == 0) {
			// Make sure the request was successful
			if (resultCode == Activity.RESULT_OK) {
				onPropertiesEdited((ArrayList<Property>) data.getSerializableExtra("properties"), data.getStringExtra("label"));
			}
		}
	}

}
