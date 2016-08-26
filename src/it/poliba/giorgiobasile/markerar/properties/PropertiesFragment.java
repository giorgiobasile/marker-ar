package it.poliba.giorgiobasile.markerar.properties;


import java.util.ArrayList;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.browse.BrowseFileActivity;
import it.poliba.giorgiobasile.markerar.schemas.PropertiesManager;
import it.poliba.giorgiobasile.markerar.schemas.SaveSchemaDialog;
import it.poliba.giorgiobasile.markerar.settings.SettingsActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Giorgio
 * Fragment di visualizzazione delle proprietà
 */

public class PropertiesFragment extends Fragment implements EditPropertyFragment.OnEditListener{

	private ArrayList<Property> properties;
	private String label;
	private ListView propView;
	private PropertiesAdapter adapter;
	private Property currentProp;

	private EditText labelEdit;
	
	public static String TAG = "PROPERTIES_FRAGMENT";

	public PropertiesFragment() {
	}

	public PropertiesFragment(ArrayList<Property> properties, String label){
		this.properties = properties;
		this.label = label;
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
		View rootView = inflater.inflate(R.layout.fragment_properties,
				container, false);
		
		labelEdit = (EditText) rootView.findViewById(R.id.label_view);
		labelEdit.setText(label);
		propView = (ListView) rootView.findViewById(R.id.prop_list);
		propView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				currentProp = properties.get(position);
				if(currentProp.getAcceptedValues().size() > 0){
					ChoosePropertyFragment edit = ChoosePropertyFragment.newInstance(currentProp);
					edit.show(getActivity().getSupportFragmentManager(), ChoosePropertyFragment.fragment_tag);
				}else{
					EditPropertyFragment edit = EditPropertyFragment.newInstance(currentProp);
					edit.show(getActivity().getSupportFragmentManager(), EditPropertyFragment.fragment_tag);
				}
			}});

		propView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				PopupMenu popup = new PopupMenu(getActivity(), view);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.prop_menu, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
					public boolean onMenuItemClick(MenuItem item) {
						properties.remove(position);
						adapter.notifyDataSetChanged();
						return true;
					}
				});
				popup.show();


				return true;
			}});


		adapter = new PropertiesAdapter(getActivity(), properties);
		propView.setAdapter(adapter);

		Button okButton = (Button) rootView.findViewById(R.id.okButton);
		okButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String label = labelEdit.getText().toString();
				if(!label.trim().equalsIgnoreCase("")){
					Intent intent = new Intent();
					intent.putExtra("properties", properties);
					intent.putExtra("label", label);
					getActivity().setResult(Activity.RESULT_OK, intent);
					getActivity().finish();
				}else{
					Toast.makeText(getActivity(), R.string.empty_label, Toast.LENGTH_SHORT).show();
				}	
			}
		});

		Button cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}});

		Button addButton = (Button) rootView.findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AddPropertyFragment add = AddPropertyFragment.newInstance();
				add.show(getActivity().getSupportFragmentManager(), AddPropertyFragment.TAG);
			}});

		Button saveSchemaButton = (Button) rootView.findViewById(R.id.saveSchemaButton);
		saveSchemaButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(properties.size() > 0){
					SaveSchemaDialog save = SaveSchemaDialog.newInstance(properties);
					save.show(getActivity().getSupportFragmentManager(), SaveSchemaDialog.fragment_tag);
				}
			}});

		Button loadSchemaButton = (Button) rootView.findViewById(R.id.loadSchemaButton);
		loadSchemaButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getActivity(), BrowseFileActivity.class), SettingsActivity.PICK_SCHEMA);
			}});

		return rootView;
	}

	public void onPropertyEdited(Property prop){

		adapter.notifyDataSetChanged();
	}

	public void onPropertyAdded(Property prop){
		String name = prop.getName();
		boolean find = false;
		for(Property p : properties){
			if(p.getName().equalsIgnoreCase(name)){
				find = true;
				break;
			}	
		}
		if(find)
			Toast.makeText(getActivity(), R.string.prop_duplicate, Toast.LENGTH_SHORT).show();
		else{
			properties.add(prop);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SettingsActivity.PICK_SCHEMA) {
			if (resultCode == Activity.RESULT_OK) {
				try{
					String filePath = data.getStringExtra("schemaPath");
					ArrayList<Property> newProp = PropertiesManager.loadMarkerSchemaFromXML(filePath);
					for(Property np : newProp){
						for(Property p : properties){
							if(p.getName().equalsIgnoreCase(np.getName())){
								if(np.getAcceptedValues().size() == 0 || np.getAcceptedValues().contains(p.getCurrentValue())){
									np.setCurrentValue(p.getCurrentValue());
								}
								break;
							}
						}
					}
					properties.clear();
					properties.addAll(newProp);
					adapter.notifyDataSetChanged();
					Toast.makeText(getActivity(), R.string.success_load_properties, Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
					Toast.makeText(getActivity(), R.string.error_load_properties, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}


	private class PropertiesAdapter extends BaseAdapter{

		private Context context;
		private ArrayList<Property> properties;

		public PropertiesAdapter(Context context, ArrayList<Property> properties) {
			this.context = context;
			this.properties = properties;
		}

		@Override
		public int getCount() {
			return properties.size();
		}

		@Override
		public Object getItem(int position) {
			return properties.get(position);
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
				convertView = inflater.inflate(android.R.layout.simple_list_item_2,
						parent, false);
			}

			Property prop = properties.get(position);

			// DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			TextView text1 = ((TextView) convertView
					.findViewById(android.R.id.text1));
			text1.setText(prop.getName());

			TextView text2 = ((TextView) convertView
					.findViewById(android.R.id.text2));
			text2.setText(prop.getCurrentValue());
			/*ImageView icon = (ImageView) convertView
					.findViewById(R.id.icon_item);

			int icon_id = 0;		
			icon.setImageResource(icon_id);*/

			return convertView;
		}
	}
}
