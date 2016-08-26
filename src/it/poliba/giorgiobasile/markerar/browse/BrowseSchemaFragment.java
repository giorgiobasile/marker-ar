package it.poliba.giorgiobasile.markerar.browse;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.utils.Utils;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author Giorgio
 * Fragment per la lettura di uno schema da file
 */

public class BrowseSchemaFragment extends Fragment{

	private ListView fileListView;
	private BrowseAdapter adapter;
	private ArrayList<String> filePaths;
	private File currentFolder;

	private static final File sdRoot = Environment.getExternalStorageDirectory();

	public BrowseSchemaFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {    
		View rootView = inflater.inflate(R.layout.fragment_browse_schema_sett, container, false);
		
		fileListView = (ListView) rootView.findViewById(R.id.file_list);

		if(savedInstanceState == null){
			String startPath = Utils.SCHEMAS_PATH;
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
					}else{
						//try{
							Intent intent = new Intent();
							intent.putExtra("schemaPath", file.getAbsolutePath());
							getActivity().setResult(Activity.RESULT_OK, intent);
							/*PropertiesManager.loadSchemaFromXML(file.getAbsolutePath());
							Toast.makeText(getActivity(), R.string.success_load_properties, Toast.LENGTH_SHORT).show();
							SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
							editor.putString(getString(R.string.prop_file_key), file.getAbsolutePath());
							editor.commit();*/
							getActivity().finish();
						/*}catch(Exception e){
							e.printStackTrace();
							Toast.makeText(getActivity(), R.string.error_load_properties, Toast.LENGTH_SHORT).show();
						}*/
						
					}
				}
			}

		});
		return rootView;
	}

	private void getFileNames(ArrayList<String> filePaths, File parent){		
		File[] subFiles = parent.listFiles();
		filePaths.clear();
		for(int i = 0; i < subFiles.length; i++){
			if(subFiles[i].isDirectory() || subFiles[i].getName().endsWith(".xml")){
				filePaths.add(subFiles[i].getAbsolutePath());
			}	
		}
		sortList(filePaths);
		if(!parent.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
			filePaths.add(0, "..");
		}
	}

	public void sortList(ArrayList<String> fileNames){
		ArrayList<String> directories = new ArrayList<String>();
		ArrayList<String> files = new ArrayList<String>();
		
		for(String name : fileNames){
			File f = new File(name);
			if(f.isDirectory())
				directories.add(name);
			else
				files.add(name);
		}
		
		Collections.sort(directories, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
		Collections.sort(files, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
		
		fileNames.clear();
		
		fileNames.addAll(directories);
		fileNames.addAll(files);
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
