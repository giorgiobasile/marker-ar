package it.poliba.giorgiobasile.markerar.properties;

import java.util.ArrayList;
import it.poliba.giorgiobasile.markerar.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

/**
 * 
 * @author Giorgio
 * Activity in formato "dialog" per la visualizzazione e l'editing di proprietà sui marker
 */

public class PropertiesActivity extends FragmentActivity implements EditPropertyFragment.OnEditListener, AddPropertyFragment.OnAddPropertyListener, ChoosePropertyFragment.OnEditListener, AddValueFragment.OnAddValueListener{

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setFinishOnTouchOutside(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_properties);
		Intent intent = getIntent();
		ArrayList<Property> properties = (ArrayList<Property>) intent.getSerializableExtra("properties");
		String label = intent.getStringExtra("label");
		if (savedInstanceState == null) {

			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PropertiesFragment(properties, label), PropertiesFragment.TAG).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.properties, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	/**
	 * Queste tre funzioni catturano l'evento di aggiunta o modifica di proprietà dai dialogFragment relativi verso i fragment che li hanno lanciati
	 * */
	@Override
	public void onPropertyEdited(Property prop) {
		PropertiesFragment ff = (PropertiesFragment) getSupportFragmentManager().findFragmentByTag(PropertiesFragment.TAG);
		ff.onPropertyEdited(prop);
	}

	@Override
	public void onPropertyAdded(Property prop) {
		PropertiesFragment ff = (PropertiesFragment) getSupportFragmentManager().findFragmentByTag(PropertiesFragment.TAG);
		ff.onPropertyAdded(prop);
	}

	@Override
	public void onValueAdded(String value) {
		AddPropertyFragment frag = (AddPropertyFragment) getSupportFragmentManager().findFragmentByTag(AddPropertyFragment.TAG);
		frag.onValueAdded(value);
	}

	@Override
	public void onBackPressed(){
		
	}
	
}
