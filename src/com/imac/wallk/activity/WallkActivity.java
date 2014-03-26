package com.imac.wallk.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.imac.wallk.R;
import com.imac.wallk.fragment.AccountFragment;
import com.imac.wallk.fragment.CameraFragment;
import com.imac.wallk.fragment.GalleryFragment;
import com.imac.wallk.fragment.LoginFragment;
import com.imac.wallk.fragment.MapFragment;
import com.imac.wallk.fragment.SignupFragment;
import com.parse.ParseUser;

public class WallkActivity extends FragmentActivity {
	public GalleryFragment galleryFrag = null;
	public LoginFragment loginFrag = null;
	public SignupFragment signupFrag = null;
	public MapFragment mapFrag = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// hide the title in the action bar
		//getActionBar().setDisplayShowTitleEnabled(false);
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
		return true;
	}

	/*
	 * Handler function for ActionBar's buttons click event
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_camera: {
			showFragment(new CameraFragment());
			break;
		}

		case R.id.action_map: {
			showFragment(this.mapFrag);
			break;
		}

		case R.id.action_gallery: {
			showFragment(this.galleryFrag);
			this.galleryFrag.updateArtworkList();
			break;
		}

		// unauthenticated user
		case R.id.action_login: {
			showFragment(this.loginFrag);
			break;
		}

		case R.id.action_signup: {
			showFragment(this.signupFrag);
			break;
		}

		// authenticated user
		case R.id.action_myAccount: {
			showFragment(new AccountFragment());
			break;
		}

		case R.id.action_myGallery: {
			showFragment(this.galleryFrag);
			this.galleryFrag.showUserArtworks();
			break;
		}

		case R.id.action_logOut: {
			ParseUser.logOut();
			invalidateOptionsMenu();//recreate the menu (have to do it because of logout)
			showFragment(this.galleryFrag);
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
