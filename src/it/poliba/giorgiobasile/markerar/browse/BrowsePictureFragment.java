package it.poliba.giorgiobasile.markerar.browse;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.markerdetection.CameraParameters;
import it.poliba.giorgiobasile.markerar.pictureproject.EditFragment;
import it.poliba.giorgiobasile.markerar.pictureproject.PictureFragment;
import it.poliba.giorgiobasile.markerar.pictureproject.PictureFrame;
import it.poliba.giorgiobasile.markerar.utils.ProjectLoader;
import it.poliba.giorgiobasile.markerar.utils.Utils;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.samples.cameracalibration.CalibrationResult;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Giorgio
 * Fragment per il recupero di un progetto da file
 */

public class BrowsePictureFragment extends Fragment{

	private ListView fileListView;
	private BrowseAdapter adapter;
	private ArrayList<String> filePaths;
	private File currentFolder;
	private Button openButton;

	private static final File sdRoot = Environment.getExternalStorageDirectory();

	public BrowsePictureFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {    
		View rootView = inflater.inflate(R.layout.fragment_browse_picture, container, false);

		fileListView = (ListView) rootView.findViewById(R.id.file_list);

		if(savedInstanceState == null){
			String startPath = Utils.PROJECT_PATH;
			currentFolder = new File(startPath);
			if(!currentFolder.exists()){
				currentFolder.mkdirs();
			}
			filePaths = new ArrayList<String>();}
		getFileNames(filePaths, currentFolder);
		adapter = new BrowseAdapter(getActivity(), filePaths);
		fileListView.setAdapter(adapter);

		fileListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File parentFile = currentFolder.getParentFile();

				if(position == 0 && !currentFolder.getAbsolutePath().equals(sdRoot.getAbsolutePath())){
					currentFolder = parentFile;
					getFileNames(filePaths, currentFolder);
					adapter.notifyDataSetChanged();
				}else if(position != 0 || (position == 0 && currentFolder.getAbsolutePath().equals(sdRoot.getAbsolutePath()))){
					File file = new File(filePaths.get(position));
					if(file.isDirectory()){
						currentFolder = file;
						getFileNames(filePaths, currentFolder);
						adapter.notifyDataSetChanged();

					}/*else{
						//Toast.makeText(getActivity(), "Apertura " + file.getName(), Toast.LENGTH_SHORT).show();
						load(file);*/

				}
				
				if(isValidProject(currentFolder)){
					openButton.setEnabled(true);
				}else{
					openButton.setEnabled(false);
				}
			}

		});


		openButton = (Button) rootView.findViewById(R.id.open_button);
		openButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				load(currentFolder);
			}});
		
		if(isValidProject(currentFolder)){
			openButton.setEnabled(true);
		}else{
			openButton.setEnabled(false);
		}
		return rootView;
	}

	private boolean isValidProject(File currentFolder){
		File originalFile = new File(currentFolder.getAbsolutePath() + File.separator + PictureFrame.ORIGINAL_NAME);
		File markersFile = new File(currentFolder.getAbsolutePath() + File.separator + PictureFrame.MARKERS_NAME);
		File propertiesFile = new File(currentFolder.getAbsolutePath() + File.separator + PictureFrame.PROPERTIES_NAME);
		if(originalFile.exists() && markersFile.exists() && propertiesFile.exists())
			return true;

		return false;
	}

	protected void load(File folder) {
		PictureFrame pf = null;

		File originalFile = new File(folder.getAbsolutePath() + File.separator + PictureFrame.ORIGINAL_NAME);
		File markersFile = new File(folder.getAbsolutePath() + File.separator + PictureFrame.MARKERS_NAME);
		File propertiesFile = new File(folder.getAbsolutePath() + File.separator + PictureFrame.PROPERTIES_NAME);

		if(!originalFile.exists() || !markersFile.exists() || !propertiesFile.exists()){
			Toast.makeText(getActivity(), R.string.edit_open_error, Toast.LENGTH_SHORT).show();
		}else{
			try {
				//carica immagine originale
				Mat frame = Highgui.imread(originalFile.getAbsolutePath());
				Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);

				Bundle prefs = ProjectLoader.loadPrefs(getActivity(), folder.getAbsolutePath() + File.separator + Utils.PREFS_NAME);
				
				int originId = prefs.getInt(getString(R.string.origin_marker_id_key), Integer.valueOf(getString(R.string.origin_marker_id_default)));
				float markerSize = prefs.getFloat(getString(R.string.markers_size_key), Float.valueOf(getString(R.string.markers_size_default)));
				CameraParameters cp = new CameraParameters();
				try{
					cp.readFromXML(folder.getAbsolutePath() + File.separator + CalibrationResult.CAMERA_NAME);
				}catch(Exception e){
					cp = null;
				}
				pf = new PictureFrame(frame, originId, markerSize, cp);
				getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new EditFragment(pf, folder.getAbsolutePath()), PictureFragment.TAG)
				.commit();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void getFileNames(ArrayList<String> filePaths, File parent){		
		File[] subFiles = parent.listFiles();
		filePaths.clear();
		boolean valid = isValidProject(currentFolder);
		for(int i = 0; i < subFiles.length; i++){
			if(subFiles[i].isDirectory() || valid){
				filePaths.add(subFiles[i].getAbsolutePath());
			}
		}
		sortList(filePaths);
		if(!parent.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
			filePaths.add(0, "..");
		}
	}

	public void sortList(ArrayList<String> fileNames){
		Collections.sort(fileNames, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
	}

	static class BrowseAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<String> items;

		public BrowseAdapter(Context context, ArrayList<String> items) {
			this.context = context;
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.browse_list_item,
						parent, false);
			}
			String path = items.get(position);
			File file = new File(path);
			// DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			TextView text = ((TextView) convertView
					.findViewById(android.R.id.text1));
			text.setText(file.getName());
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.icon_item);
			int icon_id = 0;

			if(file.isDirectory() || (position == 0 && !file.getParentFile().getAbsolutePath().equals(sdRoot))){
				icon_id = R.drawable.folder;
			}else{
				icon_id = R.drawable.file;
			}
			icon.setImageResource(icon_id);

			return convertView;
		}
	}
}
