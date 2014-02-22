package com.imac.wallk.activity;

import com.imac.wallk.R;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class CameraActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// hide the title in the action bar
        getActionBar().setDisplayShowTitleEnabled(false);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_camera);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(ParseUser.getCurrentUser() != null){
			getMenuInflater().inflate(R.menu.authenticated_menu, menu);
		}else{
			getMenuInflater().inflate(R.menu.unauthenticated_menu, menu);
		}
		return true;
	}

	/*
	 * Handler function for ActionBar's buttons click event
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.action_camera: {
			// Don't open himself
			break;
		}

		case R.id.action_map: {
			Intent intent = new Intent(this, StreetMapActivity.class);
			startActivity(intent);
			break;
		}

		case R.id.action_gallery: {
			Intent intent = new Intent(this, GalleryActivity.class);
			startActivity(intent);
			break;
		}

		//unauthenticated user
		case R.id.action_login: {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			break;
		}
		
		case R.id.action_signup: {
			Intent intent = new Intent(this, SignUpActivity.class);
			startActivity(intent);
			break;
		}
		
		//authenticated user
		case R.id.action_myAccount: {
			Intent intent = new Intent(this, GalleryActivity.class);
			startActivity(intent);
			break;
		}
		
		case R.id.action_myGallery: {
			Intent intent = new Intent(this, GalleryActivity.class);
			startActivity(intent);
			break;
		}
		
		case R.id.action_logOut: {
			ParseUser.logOut();
			Intent intent = new Intent(this, GalleryActivity.class);
			startActivity(intent);
			break;
		}

		}
		
		return super.onOptionsItemSelected(item);
	}
}
