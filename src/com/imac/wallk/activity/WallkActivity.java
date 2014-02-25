package com.imac.wallk.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.imac.wallk.R;
import com.imac.wallk.fragment.CameraFragment;
import com.imac.wallk.fragment.GalleryFragment;
import com.imac.wallk.fragment.LoginFragment;
import com.parse.ParseUser;

public class WallkActivity extends FragmentActivity {
	public GalleryFragment galleryFrag = null;
	public CameraFragment cameraFrag = null;
	public LoginFragment loginFrag = null;

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
			// TODO : Load map fragment
			break;
		}

		case R.id.action_gallery: {
			showFragment(this.galleryFrag);
			break;
		}

		// unauthenticated user
		case R.id.action_login: {
			showFragment(this.loginFrag);
			break;
		}

		case R.id.action_signup: {
			// TODO : Load signup fragment
			break;
		}

		// authenticated user
		case R.id.action_myAccount: {
			// TODO : Load profil fragment
			break;
		}

		case R.id.action_myGallery: {
			showFragment(this.galleryFrag);
			break;
		}

		case R.id.action_logOut: {
			ParseUser.logOut();
			// TODO : Reload current fragment
			break;
		}

		}

		return super.onOptionsItemSelected(item);
	}

	private void setupFragments() {
		this.galleryFrag = new GalleryFragment();
		this.cameraFrag = new CameraFragment();
		this.loginFrag = new LoginFragment();
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

	public CameraFragment getCameraFrag() {
		return cameraFrag;
	}

	public LoginFragment getLoginFrag() {
		return loginFrag;
	}
}
