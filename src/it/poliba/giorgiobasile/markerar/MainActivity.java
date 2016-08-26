package it.poliba.giorgiobasile.markerar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import it.poliba.giorgiobasile.markerar.R;


import it.poliba.giorgiobasile.markerar.browse.BrowsePictureFragment;
import it.poliba.giorgiobasile.markerar.markerdetection.CreateMarkerFragment;
import it.poliba.giorgiobasile.markerar.pictureproject.CameraFragment;
import it.poliba.giorgiobasile.markerar.pictureproject.EditFragment;
import it.poliba.giorgiobasile.markerar.pictureproject.PictureFragment;
import it.poliba.giorgiobasile.markerar.properties.Property;
import it.poliba.giorgiobasile.markerar.schemas.CreateSchemaActivity;
import it.poliba.giorgiobasile.markerar.schemas.PropertiesManager;
import it.poliba.giorgiobasile.markerar.settings.SettingsActivity;
import it.poliba.giorgiobasile.markerar.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")

/**
 * 
 * @author Giorgio
 * Activity main per l'applicazione, dalla quale navigare nelle diverse funzionalità della stessa
 */

public class MainActivity extends FragmentActivity{

	protected static final String TAG = "MarkerAR";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	DrawerAdapter drawerAdapter;

	private int oldPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main3);
		//salva nella cartella /calibration/ l'immagine del pattern di calibrazione (utile per la stampa)
		savePattern();


		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		drawerAdapter = new DrawerAdapter(this, getResources().getStringArray(
				R.array.drawer_options));
		mDrawerList.setAdapter(drawerAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState == null) {
			try{
				//carica lo schema di default (ultimo caricato dalle impostazioni)
				String propFile = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.prop_file_key), null);
				PropertiesManager.loadSchemaFromXML(propFile);
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(this, R.string.error_load_properties, Toast.LENGTH_SHORT).show();
				PropertiesManager.setProperties(new ArrayList<Property>());
			}


			mDrawerList.setItemChecked(0, true);
			drawerAdapter.notifyDataSetChanged();
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, new CameraFragment())
			.commit();
		} else {
			oldPosition = savedInstanceState.getInt("position");
			mDrawerList.setItemChecked(oldPosition, true);
		}


	}	

	public void savePattern(){
		InputStream istr;
		AssetManager am = getAssets();
		try {
			String name = am.list("")[0];
			File f = new File(Utils.CALIB_PATH + File.separator + name);
			if(!f.exists()){
				Bitmap bitmap = null;

				istr = am.open(name);
				bitmap = BitmapFactory.decodeStream(istr);

				if(!f.getParentFile().exists()){
					f.getParentFile().mkdirs();
				}
				FileOutputStream fOut;
				fOut = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				fOut.flush();
				fOut.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}


	private class DrawerItemClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		if (position == 0) {
			//Nuovo
			if ((getSupportFragmentManager().findFragmentById(R.id.container)
					.getClass() != CameraFragment.class)) {
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new CameraFragment())
				.commit();
			}
			mDrawerLayout.closeDrawer(mDrawerList);
			oldPosition = position;
			mDrawerList.setItemChecked(position, true);	
		} else if (position == 1) {
			//Apri progetto
			if ((getSupportFragmentManager().findFragmentById(R.id.container)
					.getClass() != BrowsePictureFragment.class) && (getSupportFragmentManager().findFragmentById(R.id.container)
							.getClass() != EditFragment.class)) {
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new BrowsePictureFragment()).commit();
				//.replace(R.id.container, new FooFragment()).commit();
			}
			mDrawerLayout.closeDrawer(mDrawerList);
			oldPosition = position;
			mDrawerList.setItemChecked(position, true);	
		} else if (position == 2) {
			//Crea Marker
			if ((getSupportFragmentManager().findFragmentById(R.id.container)
					.getClass() != CreateMarkerFragment.class)) {
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new CreateMarkerFragment()).commit();
			}
			mDrawerLayout.closeDrawer(mDrawerList);
			oldPosition = position;
			mDrawerList.setItemChecked(position, true);	
		} else if (position == 3) {
			//Crea Schema
			startActivity(new Intent(this, CreateSchemaActivity.class));
			mDrawerLayout.closeDrawer(mDrawerList);
			//oldPosition = position;
			mDrawerList.setItemChecked(oldPosition, true);	
		} /*else if (position == 4) {
			AboutFragment prog = AboutFragment.newInstance();
			prog.show(getSupportFragmentManager(), AboutFragment.fragment_tag);
			// mDrawerLayout.closeDrawer(mDrawerList);
			// int pos = mDrawerList.getCheckedItemPosition();
			mDrawerList.setItemChecked(position, false);
			mDrawerList.setItemChecked(oldPosition, true);
		}
		// Highlight the selected item, update the title, and close the drawer
		// mDrawerList.setItemChecked(position, true);
		// setTitle(mPlanetTitles[position]);*/

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		/*
		 * if (id == R.id.action_settings) { return true; }
		 */
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//gestisce le icone da mostrare nella action bar a seconda del fragment visualizzato nella activity e dell'apertura del drawer
		boolean leftDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		MenuItem register;
		if(getSupportFragmentManager().findFragmentById(R.id.container) != null){

			if(leftDrawerOpen){
				register = menu.findItem(R.id.action_photo);
				register.setVisible(false);
				register = menu.findItem(R.id.action_photo_save);
				register.setVisible(false);
				register = menu.findItem(R.id.action_photo_cancel);
				register.setVisible(false);
				register = menu.findItem(R.id.action_flash_on);
				register.setVisible(false);
				register = menu.findItem(R.id.action_flash_off);
				register.setVisible(false);
				return true;
			}else{
				if(getSupportFragmentManager().findFragmentById(R.id.container).getClass() == CameraFragment.class){
					register = menu.findItem(R.id.action_photo);
					register.setVisible(true);
					register = menu.findItem(R.id.action_flash_on);
					register.setVisible(true);
					register = menu.findItem(R.id.action_flash_off);
					register.setVisible(true);

					register = menu.findItem(R.id.action_photo_save);
					register.setVisible(false);
					register = menu.findItem(R.id.action_photo_cancel);
					register.setVisible(false);
				}else if(getSupportFragmentManager().findFragmentById(R.id.container).getClass() == PictureFragment.class ||
						getSupportFragmentManager().findFragmentById(R.id.container).getClass() == EditFragment.class){
					register = menu.findItem(R.id.action_photo);
					register.setVisible(false);
					register = menu.findItem(R.id.action_photo_save);
					register.setVisible(true);
					register = menu.findItem(R.id.action_photo_cancel);
					register.setVisible(true);
				}
			}
		}

		/*if(getFragmentManager().findFragmentById(R.id.content_frame).getClass() == NavigationFragment.class){
	    	if(leftDrawerOpen || rightDrawerOpen){           
		        register.setVisible(false);
		    }else{
		        register.setVisible(true);
		    }
	    }*/
		return super.onPrepareOptionsMenu(menu);
	}
	/*public void onLabelEdited(Marker m, String label){
		if (getSupportFragmentManager().findFragmentById(R.id.container).getClass() == PictureFragment.class){
			PictureFragment ff = (PictureFragment) getSupportFragmentManager().findFragmentByTag(PictureFragment.TAG);
			ff.onLabelEdited(m,  label);
		}else if(getSupportFragmentManager().findFragmentById(R.id.container).getClass() == EditFragment.class){
			EditFragment ff = (EditFragment) getSupportFragmentManager().findFragmentByTag(EditFragment.TAG);
			ff.onLabelEdited(m,  label);
		}	
	}*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}



	static class DrawerAdapter extends BaseAdapter {

		private Context context;
		private String[] items;

		public DrawerAdapter(Context context, String[] items) {
			this.context = context;
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			return items[position];
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
				convertView = inflater.inflate(R.layout.drawer_list_item,
						parent, false);
			}
			String p = items[position];
			// DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			CheckedTextView text = ((CheckedTextView) convertView
					.findViewById(android.R.id.text1));
			text.setText(p);
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.icon_item);
			int icon_id = 0;
			switch (position) {
			case 0:
				icon_id = R.drawable.ic_action_new;
				break;
			case 1:
				icon_id = R.drawable.ic_action_edit;
				break;
			case 2:
				icon_id = R.drawable.marker36;
				break;
			case 3:
				icon_id = R.drawable.schema_icon;
				break;
			}

			icon.setImageResource(icon_id);

			return convertView;
		}
	}
	
	public void onBackPressed() {
			moveTaskToBack(true);
			//super.onBackPressed();
	}
	
}
