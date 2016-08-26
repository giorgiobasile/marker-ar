package it.poliba.giorgiobasile.markerar.schemas;


import java.io.File;
import java.util.ArrayList;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.properties.Property;
import it.poliba.giorgiobasile.markerar.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SaveSchemaDialog extends DialogFragment{
	/**
	 * 
	 * @author Giorgio
	 * DialogFragment per il salvataggio di uno schema
	 */

	private Dialog dialog;
	private ArrayList<Property> prop;
	private EditText edit;

	public static String fragment_tag = "SAVE_SCHEMA_FRAGMENT";


	SaveSchemaDialog(ArrayList<Property> prop){
		this.prop = prop;
	}

	public static SaveSchemaDialog newInstance(ArrayList<Property> prop) {
		SaveSchemaDialog frag = new SaveSchemaDialog(prop);
		return frag;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new Builder(getActivity());
		b.setTitle(getString(R.string.save_as));
		b.setIcon(R.drawable.icon2);
		b.setCancelable(false);
		b.setNegativeButton(android.R.string.cancel, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});
		b.setPositiveButton(android.R.string.ok, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				boolean success = PropertiesManager.saveSchemaToXML(Utils.SCHEMAS_PATH + File.separator + edit.getText().toString() + ".xml", prop);
				if(success)
					Toast.makeText(getActivity(), R.string.schema_saved, Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getActivity(), R.string.schema_not_saved, Toast.LENGTH_SHORT).show();
				
				dialog.dismiss();
			}
		});
		LinearLayout dialogLayout = (LinearLayout)View.inflate(getActivity(), R.layout.schema_name, null);
		edit = (EditText) dialogLayout.findViewById(R.id.edit_label);
		b.setView(dialogLayout);
		dialog = b.create();
		return dialog;
	}
}
