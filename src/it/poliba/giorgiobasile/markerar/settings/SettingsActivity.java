package it.poliba.giorgiobasile.markerar.settings;


import it.poliba.giorgiobasile.markerar.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * 
 * @author Giorgio
 * Activity delle impostazioni
 */

public class SettingsActivity extends Activity {
	
	public static int PICK_SCHEMA = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.action_settings);
		// Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
	}
	
	@Override
    public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
			
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	
}