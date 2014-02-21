package com.imac.wallk.activity;

import com.imac.wallk.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MapActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// hide the title in the action bar
        getActionBar().setDisplayShowTitleEnabled(false);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_map);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * Handler function for ActionBar's buttons click event
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.action_camera: {
			//TODO
			break;
		}

		case R.id.action_map: {
			Intent intent = new Intent(this, MapActivity.class);
			startActivity(intent);
			break;
		}

		case R.id.action_gallery: {
			Intent intent = new Intent(this, GalleryActivity.class);
			startActivity(intent);
			break;
		}

		case R.id.action_account: {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			break;
		}

		}
		
		return super.onOptionsItemSelected(item);
	}
}
