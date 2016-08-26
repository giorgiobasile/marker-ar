package it.poliba.giorgiobasile.markerar.markerdetection;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.utils.Utils;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 
 * @author Giorgio
 * Fragment per la creazione e salvataggio su file di un marker
 */

public class CreateMarkerFragment extends Fragment{
	
	private EditText idView;
	private ImageView markerView;
	private Button genButton, saveButton;
	private Bitmap markerImg;
	private Mat marker;
	private int id;
	
	
	public CreateMarkerFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {    
		View rootView = inflater.inflate(R.layout.fragment_create_marker, container, false);
		
		idView = (EditText) rootView.findViewById(R.id.edit_id);
		markerView = (ImageView) rootView.findViewById(R.id.marker_view);
		markerView.setVisibility(View.GONE);
		if(markerImg != null){
			markerView.setImageBitmap(markerImg);
			markerView.setVisibility(View.VISIBLE);
		}
		
		saveButton = (Button) rootView.findViewById(R.id.button_save);
		saveButton.setEnabled(marker != null);
		saveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				saveMarker();
			}});
		
		
		genButton = (Button) rootView.findViewById(R.id.button_gen);
		genButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String text = idView.getText().toString();
				if(!text.equals("")){
					id = Integer.valueOf(text);
					//crea il marker e lo mostra a video
					generateMarker();
				}	
			}});
		
		
		
		return rootView;
	}	
	
	private void generateMarker(){
		try{
			marker = Marker.createMarkerImage(Integer.valueOf(id),280);
			markerImg = Bitmap.createBitmap(marker.cols(), marker.rows(), Bitmap.Config.ARGB_8888);
			Imgproc.cvtColor(marker, marker, Imgproc.COLOR_GRAY2RGBA, 4);
			org.opencv.android.Utils.matToBitmap(marker, markerImg);
			markerView.setImageBitmap(markerImg);
			markerView.setVisibility(View.VISIBLE);
			saveButton.setEnabled(true);
		} catch(Exception e){
			markerView.setVisibility(View.GONE);
			saveButton.setEnabled(false);
			Toast.makeText(getActivity(), "Invalid ID for marker", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void saveMarker(){
		String filePath = Utils.MARKER_IMGS_PATH + File.separator + "marker" + String.valueOf(id) + ".bmp";
		File f = new File(filePath);
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		Highgui.imwrite(filePath, marker);
		Toast.makeText(getActivity(), R.string.marker_saved, Toast.LENGTH_SHORT).show();

	}
}
