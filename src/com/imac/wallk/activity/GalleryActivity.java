package com.imac.wallk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.imac.wallk.R;
import com.parse.ParseUser;

public class GalleryActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// hide the title in the action bar
		getActionBar().setDisplayShowTitleEnabled(false);
		
		setContentView(R.layout.activity_gallery);
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
				Intent intent = new Intent(this, CameraActivity.class);
				startActivity(intent);
				break;
			}
	
			case R.id.action_map: {
				Intent intent = new Intent(this, StreetMapActivity.class);
				startActivity(intent);
				break;
			}
	
			case R.id.action_gallery: {
				// Don't open himself
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
