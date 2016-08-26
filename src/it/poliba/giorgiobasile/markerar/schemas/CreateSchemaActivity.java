package it.poliba.giorgiobasile.markerar.schemas;

import it.poliba.giorgiobasile.markerar.R;

import it.poliba.giorgiobasile.markerar.properties.AddPropertyFragment;
import it.poliba.giorgiobasile.markerar.properties.AddValueFragment;
import it.poliba.giorgiobasile.markerar.properties.Property;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.view.MenuItem;

/**
 * 
 * @author Giorgio
 * Activity per la creazione di uno schema
 */

public class CreateSchemaActivity extends FragmentActivity implements AddPropertyFragment.OnAddPropertyListener, AddValueFragment.OnAddValueListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_schema);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new CreateSchemaFragment(), CreateSchemaFragment.TAG).commit();
		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_schema, menu);
		return true;
	}*/

	
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
	public void onPropertyAdded(Property prop) {
		CreateSchemaFragment frag = (CreateSchemaFragment) getSupportFragmentManager().findFragmentByTag(CreateSchemaFragment.TAG);
		frag.onPropertyAdded(prop);
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
