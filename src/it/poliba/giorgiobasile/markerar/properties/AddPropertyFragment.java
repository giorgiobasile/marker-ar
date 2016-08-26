package it.poliba.giorgiobasile.markerar.properties;


import it.poliba.giorgiobasile.markerar.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Giorgio
 * DialogFragment per l'aggiunta di una singola proprietà ad uno schema
 */

public class AddPropertyFragment extends DialogFragment{

	private Dialog dialog;
	private Property prop;
	private EditText propName;
	private ListView listValues;
	private ValuesAdapter adapter;
	private OnAddPropertyListener mCallback;
	private LinearLayout dialogLayout;

	public static String TAG = "ADD_PROPERTY_FRAGMENT";


	AddPropertyFragment(){
	}

	public static AddPropertyFragment newInstance() {
		AddPropertyFragment frag = new AddPropertyFragment();
		return frag;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnAddPropertyListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnAddPropertyListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		prop = new Property();

		AlertDialog.Builder b = new Builder(getActivity());
		b.setTitle(getString(R.string.add_property));
		b.setIcon(R.drawable.icon2);
		b.setCancelable(false);
		b.setNegativeButton(android.R.string.cancel, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});
		b.setPositiveButton(android.R.string.ok, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				if(!propName.getText().toString().trim().equals("")){
					prop.setName(propName.getText().toString());
					mCallback.onPropertyAdded(prop);
					dialog.dismiss();
				}
			}
		});
		dialogLayout = (LinearLayout)View.inflate(getActivity(), R.layout.add_property_dialog, null);
		propName = (EditText) dialogLayout.findViewById(R.id.add_label);
		listValues = (ListView) dialogLayout.findViewById(R.id.list_values);
		adapter = new ValuesAdapter(getActivity(), prop);
		listValues.setAdapter(adapter);
		listValues.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				PopupMenu popup = new PopupMenu(getActivity(), view);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.prop_menu, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
					public boolean onMenuItemClick(MenuItem item) {
						prop.getAcceptedValues().remove(position);
						adapter.notifyDataSetChanged();
						return true;
					}
				});
				popup.show();
			}});

		Button addValue = (Button) dialogLayout.findViewById(R.id.add_value);
		addValue.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AddValueFragment add = AddValueFragment.newInstance();
				add.show(getActivity().getSupportFragmentManager(), AddValueFragment.fragment_tag);
			}
		});

		b.setView(dialogLayout);
		dialog = b.create();
		return dialog;
	}

	public void onValueAdded(String value){
		boolean find = false;
		for(String v : prop.getAcceptedValues()){
			if(v.equalsIgnoreCase(value)){
				find = true;
				break;
			}	
		}
		if(find)
			Toast.makeText(getActivity(), R.string.prop_duplicate, Toast.LENGTH_SHORT).show();
		else{
			prop.getAcceptedValues().add(value);
			adapter.notifyDataSetChanged();
		}
	}

	// Container Activity must implement this interface
	public interface OnAddPropertyListener {
		public void onPropertyAdded(Property prop);
	}

	private class ValuesAdapter extends BaseAdapter{

		private Context context;
		private Property prop;

		public ValuesAdapter(Context context, Property prop) {
			this.context = context;
			this.prop = prop;;
		}

		@Override
		public int getCount() {
			return prop.getAcceptedValues().size();
		}

		@Override
		public Object getItem(int position) {
			return prop.getAcceptedValues().get(position);
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
				convertView = inflater.inflate(android.R.layout.simple_list_item_1,
						parent, false);
			}

			String value = prop.getAcceptedValues().get(position);

			// DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			TextView text1 = ((TextView) convertView
					.findViewById(android.R.id.text1));
			text1.setText(value);
			/*ImageView icon = (ImageView) convertView
					.findViewById(R.id.icon_item);

			int icon_id = 0;		
			icon.setImageResource(icon_id);*/

			return convertView;
		}
	}

}
