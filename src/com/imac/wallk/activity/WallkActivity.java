package com.imac.wallk.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.imac.wallk.R;
import com.imac.wallk.fragment.AccountFragment;
import com.imac.wallk.fragment.CameraFragment;
import com.imac.wallk.fragment.GalleryFragment;
import com.imac.wallk.fragment.LoginFragment;
import com.imac.wallk.fragment.MapFragment;
import com.imac.wallk.fragment.SignupFragment;
import com.parse.ParseUser;

public class WallkActivity extends FragmentActivity {
	private GalleryFragment galleryFrag = null;
	private LoginFragment loginFrag = null;
	private SignupFragment signupFrag = null;
	private MapFragment mapFrag = null;

	private MenuItem cameraItem = null;
	private MenuItem mapItem = null;
	private MenuItem galleryItem = null;
	private MenuItem accountItem = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// hide the title in the action bar
		getActionBar().setDisplayShowTitleEnabled(false);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setupFragments();
		showFragment(this.galleryFrag);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (ParseUser.getCurrentUser() != null) {
			getMenuInflater().inflate(R.menu.authenticated_menu, menu);
		} else {
			getMenuInflater().inflate(R.menu.unauthenticated_menu, menu);
		}

		cameraItem = (MenuItem) menu.findItem(R.id.action_camera);
		mapItem = (MenuItem) menu.findItem(R.id.action_map);
		galleryItem = (MenuItem) menu.findItem(R.id.action_gallery);
		accountItem = (MenuItem) menu.findItem(R.id.action_submenu_account);
		
		galleryItem.setIcon(R.drawable.ic_action_view_as_grid_selected);
		return true;
	}

	/*
	 * Handler function for ActionBar's buttons click event
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_camera: {
			//have to be connected to take a picture
			if (ParseUser.getCurrentUser() != null) {
				showFragment(new CameraFragment());
				colorMenuIcon(R.drawable.ic_action_camera);
			} else {
				showFragment(this.loginFrag);
				colorMenuIcon(R.drawable.ic_action_person);
				Toast.makeText(this, "You have to be connected to take a picture.", Toast.LENGTH_LONG).show();
			}
			break;
		}

		case R.id.action_map: {
			showFragment(this.mapFrag);
			colorMenuIcon(R.drawable.ic_action_place);
			break;
		}

		case R.id.action_gallery: {
			showFragment(this.galleryFrag);
			this.galleryFrag.updateArtworkList();
			colorMenuIcon(R.drawable.ic_action_view_as_grid);
			break;
		}

		// unauthenticated user
		case R.id.action_login: {
			showFragment(this.loginFrag);
			colorMenuIcon(R.drawable.ic_action_person);
			break;
		}

		case R.id.action_signup: {
			showFragment(this.signupFrag);
			colorMenuIcon(R.drawable.ic_action_person);
			break;
		}

		// authenticated user
		case R.id.action_myAccount: {
			showFragment(new AccountFragment());
			colorMenuIcon(R.drawable.ic_action_person);
			break;
		}

		case R.id.action_myGallery: {
			showFragment(this.galleryFrag);
			this.galleryFrag.showUserArtworks();
			colorMenuIcon(R.drawable.ic_action_person);
			break;
		}

		case R.id.action_logOut: {
			ParseUser.logOut();
			invalidateOptionsMenu();//recreate the menu (have to do it because of logout)
			showFragment(this.galleryFrag);
			colorMenuIcon(R.drawable.ic_action_view_as_grid);
			break;
		}

		}
		return super.onOptionsItemSelected(item);
	}

	private void setupFragments() {
		this.galleryFrag = new GalleryFragment();
		this.loginFrag = new LoginFragment();
		this.signupFrag = new SignupFragment();
		this.mapFrag = new MapFragment();
	}

	public void showFragment(Fragment fragment) {
		if (fragment == null)
			return;

		final FragmentManager fm = getSupportFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();

		ft.replace(R.id.fragment_container, fragment);

		ft.commit();
	}
	
	private void uncolorMenuIcon() {
		cameraItem.setIcon(R.drawable.ic_action_camera);
		mapItem.setIcon(R.drawable.ic_action_place);
		galleryItem.setIcon(R.drawable.ic_action_view_as_grid);
		accountItem.setIcon(R.drawable.ic_action_person);
	}
	
	public void colorMenuIcon(int iconID) {
		uncolorMenuIcon();
		
		switch(iconID) {
		case R.drawable.ic_action_camera:
			cameraItem.setIcon(R.drawable.ic_action_camera_selected);
			break;

		case R.drawable.ic_action_place:
			mapItem.setIcon(R.drawable.ic_action_place_selected);
			break;

		case R.drawable.ic_action_person:
			accountItem.setIcon(R.drawable.ic_action_person_selected);
			break;

		case R.drawable.ic_action_view_as_grid:
		default:
			galleryItem.setIcon(R.drawable.ic_action_view_as_grid_selected);
			break;
		}
	}
	
	/* GETTERS */
	public GalleryFragment getGalleryFrag() {
		return galleryFrag;
	}

	public LoginFragment getLoginFrag() {
		return loginFrag;
	}

	public SignupFragment getSignupFrag() {
		return signupFrag;
	}

	public MapFragment getMapFrag() {
		return mapFrag;
	}
	
	
	
	public void logFilesSaved(){//list the files present in our directory
	    String path = getFilesDir().toString();
	    Log.d("Files", "Path: " + path);      
	    String[] fileList = fileList();
	    Log.d("Files", "Size: "+ fileList.length);
	    for (int i=0; i < fileList.length; i++)
	    {
	        Log.d("Files", "FileName:" + fileList[i]);
	    }
	}
}
