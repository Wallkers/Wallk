package com.imac.wallk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.imac.wallk.R;
import com.parse.ParseUser;

public class WallkActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// hide the title in the action bar
        getActionBar().setDisplayShowTitleEnabled(false);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
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
				//TODO : Load camera fragment 
				break;
			}
	
			case R.id.action_map: {
				//TODO : Load map fragment 
				break;
			}
	
			case R.id.action_gallery: {
				//TODO : Load gallery fragment 
				break;
			}
	
			//unauthenticated user
			case R.id.action_login: {
				//TODO : Load login fragment 
				break;
			}
			
			case R.id.action_signup: {
				//TODO : Load signup fragment 
				break;
			}
			
			//authenticated user
			case R.id.action_myAccount: {
				//TODO : Load profil fragment 
				break;
			}
			
			case R.id.action_myGallery: {
				//TODO : Load gallery fragment with owner filter 
				break;
			}
			
			case R.id.action_logOut: {
				ParseUser.logOut();
				//TODO : Reload current fragment
				break;
			}
			
		}
		
		return super.onOptionsItemSelected(item);
	}
}
