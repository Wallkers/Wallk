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
import com.imac.wallk.Artwork;
import com.imac.wallk.LocationActivity;
import com.imac.wallk.R;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class StreetMapActivity extends Activity {
		private GoogleMap map;
		private LocationActivity location;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_map);

	        //Temporary ! 
//	        Intent intent = new Intent(this, LocationActivity.class);
//	        startActivity(intent);

			//Add the location
		    location = new LocationActivity();
	        // Get a handle to the Map Fragment
	        map = ((MapFragment) getFragmentManager()
	                .findFragmentById(R.id.map)).getMap();
	
	        map.setMyLocationEnabled(true);
//	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
//	                new LatLng(location.getLocation().getLatitude(),location.getLocation().getLongitude()), 16));
	        
	        //Temporary !
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
	                new LatLng(45.8336, 1.2611), 16));
	        
	        displayMarkers();
	        

	    }
	    
    // Temporary : retrieve all the artworks and place a marker for it
    public void displayMarkers(){
    	ParseQueryAdapter<Artwork> mainAdapter = new ParseQueryAdapter<Artwork>(this, Artwork.class);
	    System.out.println("cout " + mainAdapter.getCount()) ;
    	for (int i = 0; i < mainAdapter.getCount(); ++i){
	    	System.out.println("plop");
	    	//Display marker
		    map.addMarker(new MarkerOptions()
		                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place))
		                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
		                .position(new LatLng(mainAdapter.getItem(i).getLocation().getLatitude(),mainAdapter.getItem(i).getLocation().getLongitude())));
	    }
    	
    	
    	
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