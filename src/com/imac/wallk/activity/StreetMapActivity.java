package com.imac.wallk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imac.wallk.LocationActivity;
import com.imac.wallk.R;
import com.parse.ParseUser;

public class StreetMapActivity extends Activity {

	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_map);

	        //Temporary ! 
//	        Intent intent = new Intent(this, LocationActivity.class);
//	        startActivity(intent);
	        
	        // Get a handle to the Map Fragment
	        GoogleMap map = ((MapFragment) getFragmentManager()
	                .findFragmentById(R.id.map)).getMap();

	        map.setMyLocationEnabled(true);
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
	                new LatLng(41.889, -87.622), 16));
	        

	        
	        // You can customize the marker image using images bundled with
	        // your app, or dynamically generated bitmaps. 
	        map.addMarker(new MarkerOptions()
	                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place))
	                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
	                .position(new LatLng(41.889, -87.622)));

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

//	/** Google Map creation + Security check to ensure the map can be instantiated **/
//	private void setUpMapIfNeeded() {
//	    // Do a null check to confirm that we have not already instantiated the map.
//	    if (mMap == null) {
//	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//	                            .getMap();
////	        //Set up the options for the map
////	        GoogleMapOptions options = new GoogleMapOptions();
////	        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
////	        		.compassEnabled(false)
////	        		.rotateGesturesEnabled(false)
////	        		.tiltGesturesEnabled(false);
//
//	        
//	        // Check if we were successful in obtaining the map.
//	        if (mMap != null) {
//	            // The Map is verified. It is now safe to manipulate the map.
//
//	        }
//	    }
//	}

	
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
			// Don't open himself
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
