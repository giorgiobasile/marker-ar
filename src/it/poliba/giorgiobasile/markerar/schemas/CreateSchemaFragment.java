package it.poliba.giorgiobasile.markerar.schemas;

import java.util.ArrayList;


import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.browse.BrowseFileActivity;
import it.poliba.giorgiobasile.markerar.properties.AddPropertyFragment;
import it.poliba.giorgiobasile.markerar.properties.Property;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 
 * @author Giorgio
 * Fragment di creazione di un nuovo schema
 */

public class CreateSchemaFragment extends Fragment{

	private ListView propListView;
	private ArrayList<Property> propList = new ArrayList<Property>();
	private PropertiesAdapter adapter;
	public CreateSchemaFragment(){}

	public static String TAG = "CREATE_SCHEMA_FRAG";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {    
		View rootView = inflater.inflate(R.layout.fragment_create_schema, container, false);

		propListView = (ListView) rootView.findViewById(R.id.properties_list);
		adapter = new PropertiesAdapter(getActivity(), propList);
		propListView.setAdapter(adapter);

		propListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				PopupMenu popup = new PopupMenu(getActivity(), view);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.prop_menu, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
					public boolean onMenuItemClick(MenuItem item) {
						propList.remove(position);
						adapter.notifyDataSetChanged();
						return true;
					}
				});
				popup.show();


				return true;
			}});

		Button addProp = (Button) rootView.findViewById(R.id.add_button);
		addProp.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AddPropertyFragment add = AddPropertyFragment.newInstance();
				add.show(getActivity().getSupportFragmentManager(), AddPropertyFragment.TAG);
			}

		});
		
		Button loadSchema = (Button) rootView.findViewById(R.id.load_button);
		loadSchema.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getActivity(), BrowseFileActivity.class), SettingsActivity.PICK_SCHEMA);
			}
		});

		Button saveSchema = (Button) rootView.findViewById(R.id.save_button);
		saveSchema.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(propList.size() > 0){
					SaveSchemaDialog save = SaveSchemaDialog.newInstance(propList);
					save.show(getActivity().getSupportFragmentManager(), SaveSchemaDialog.fragment_tag);
				}
			}
		});

		return rootView;
	}

	public void onPropertyAdded(Property prop){
		String name = prop.getName();
		boolean find = false;
		for(Property p : propList){
			if(p.getName().equalsIgnoreCase(name)){
				find = true;
				break;
			}	
		}
		if(find)
			Toast.makeText(getActivity(), R.string.prop_duplicate, Toast.LENGTH_SHORT).show();
		else{
			propList.add(prop);
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
					propList.clear();
					propList.addAll(newProp);
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

			String type = "";
			if(prop.getAcceptedValues().size() > 0)
				type = "select";
			else
				type = "string";
			text2.setText(type);	
			/*ImageView icon = (ImageView) convertView
					.findViewById(R.id.icon_item);

			int icon_id = 0;		
			icon.setImageResource(icon_id);*/

			return convertView;
		}
	}

}
