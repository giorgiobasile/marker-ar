package it.poliba.giorgiobasile.markerar.settings;


import org.opencv.samples.cameracalibration.CameraCalibrationActivity;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.browse.BrowseFileActivity;
import it.poliba.giorgiobasile.markerar.schemas.PropertiesManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * 
 * @author Giorgio
 * Fragment delle impostazioni
 */

public class SettingsFragment extends PreferenceFragment{

	SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings);
		/*Preference credits = (Preference)findPreference(getResources().getString(R.string.credits_key));
        credits.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	        @Override
	        public boolean onPreferenceClick(Preference arg0) { 
	            startActivity(new Intent(arg0.getContext(), AboutActivity.class));   
	            return true;
	        }
        });*/
		Preference feedback = (Preference)findPreference(getResources().getString(R.string.sendfeedback_key));
		feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				sendEmail(getResources().getString(R.string.app_name));  
				return true;
			}
		});

		Preference calib = (Preference)findPreference(getResources().getString(R.string.calibration_key));
		calib.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				startActivity(new Intent(getActivity(), CameraCalibrationActivity.class));
				return true;
			}
		});

		Preference properties = (Preference)findPreference(getResources().getString(R.string.load_properties_key));
		properties.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				//pickFile(READ_SCHEMA_CODE);
				//try{
					//PropertiesManager.loadSchemaFromXML(PropertiesManager.DEFAULT_PATH);
					/*for(Property p : mp.getProperties()){
	    				System.out.println(p.getName() + " " + p.getType());
	    				for(String s : p.getValues()){
	    					System.out.println(s);
	    				}
	    			}*/
					//Toast.makeText(getActivity(), R.string.success_load_properties, Toast.LENGTH_SHORT).show();
					/*File file = new File(Environment.getExternalStorageDirectory().getPath());
	    			Intent intent = new Intent();
	    			intent.setAction(android.content.Intent.ACTION_VIEW);
	    			Uri data = Uri.fromFile(file);*/
					//String type = "*/*";
					//intent.setDataAndType(data, type);
					//startActivity(intent);
					/*Intent intent = new Intent(getActivity(), FileSelectionActivity.class);
	                startActivityForResult(intent, 0);*/
				/*}catch(Exception e){
					e.printStackTrace();
					Toast.makeText(getActivity(), R.string.error_load_properties, Toast.LENGTH_SHORT).show();
				}*/
				startActivityForResult(new Intent(getActivity(), BrowseFileActivity.class), SettingsActivity.PICK_SCHEMA);
				return true;
			}
		});

	}


	public void sendEmail(String subject){
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		/* Fill it with Data */
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);


		/* Send it off to the Activity-Chooser */
		startActivity(emailIntent);
	}

	public void pickFile(int requestCode){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

		// Filter to only show results that can be "opened", such as a
		// file (as opposed to a list of contacts or timezones)
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		// Filter to show only images, using the image MIME data type.
		// If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
		// To search for all documents available via installed storage providers,
		// it would be "*/*".
		intent.setType("application/xml");

		startActivityForResult(intent, requestCode);
	} 
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("OK");
		if (requestCode == SettingsActivity.PICK_SCHEMA) {
			if (resultCode == Activity.RESULT_OK) {
				try{
					String filePath = data.getStringExtra("schemaPath");
					PropertiesManager.loadSchemaFromXML(filePath);
					Toast.makeText(getActivity(), R.string.success_load_properties, Toast.LENGTH_SHORT).show();
					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
					editor.putString(getString(R.string.prop_file_key), filePath);
					editor.commit();
				}catch(Exception e){
					e.printStackTrace();
					Toast.makeText(getActivity(), R.string.error_load_properties, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
