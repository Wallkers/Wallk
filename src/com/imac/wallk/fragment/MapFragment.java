package com.imac.wallk.fragment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imac.wallk.Artwork;
import com.imac.wallk.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class MapFragment extends Fragment implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	/*
	* Define a request code to send to Google Play services This code is returned in
	* Activity.onActivityResult
	*/
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private float m_searchDistance = 250;
	/*
	* Constants for location update parameters
	*/
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;

	// The update interval
	private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

	// A fast interval ceiling
	private static final int FAST_CEILING_IN_SECONDS = 1;

	// Update interval in milliseconds
	private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
	  * UPDATE_INTERVAL_IN_SECONDS;

	// A fast ceiling of update intervals, used when the app is visible
	private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
	  * FAST_CEILING_IN_SECONDS;

	/*
	* Constants for handling location results
	*/
	// Conversion from feet to meters
	private static final float METERS_PER_FEET = 0.3048f;

	// Conversion from kilometers to meters
	private static final int METERS_PER_KILOMETER = 1000;

	// Initial offset for calculating the map bounds
	private static final double OFFSET_CALCULATION_INIT_DIFF = 1.0;

	// Accuracy for calculating the map bounds
	private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;

	// Maximum results returned from a Parse query
	private static final int MAX_POST_SEARCH_RESULTS = 20;

	// Maximum post search radius for map in kilometers
	private static final int MAX_POST_SEARCH_DISTANCE = 100;

	/*
	* Other class member variables
	*/
	// Map fragment
	private SupportMapFragment map;

	// Represents the circle around a map
	private Circle mapCircle;

	// Fields for the map radius in feet
	private float radius;
	private float lastRadius;

	// Fields for helping process map and location changes
	private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>();
	private int mostRecentMapUpdate = 0;
	private boolean hasSetUpInitialLocation = false;
	private String selectedObjectId;
	private Location lastLocation = null;
	private Location currentLocation = null;
	
	//If the user click on a different location from his own
	private Location otherLocation = null;

	// A request to connect to Location Services
	private LocationRequest locationRequest;

	// Stores the current instantiation of the location client in this object
	private LocationClient locationClient;

	// Adapter for the Parse query
	private ParseQueryAdapter<Artwork> posts;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_map, container, false);
		
		radius = m_searchDistance;
		lastRadius = radius;
		
		// Create a new global location parameters object
				locationRequest = LocationRequest.create();

				// Set the update interval
				locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

				// Use high accuracy
				locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

				// Set the interval ceiling to one minute
				locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

				// Create a new location client, using the enclosing class to handle callbacks.
				locationClient = new LocationClient(this.getActivity(), this, this);

				// Set up the map fragment
				map = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
				// Enable the current location "blue dot"
				map.getMap().setMyLocationEnabled(true);
				// Set up the camera change handler
				map.getMap().setOnCameraChangeListener(new OnCameraChangeListener() {
					public void onCameraChange(CameraPosition position) {
						// When the camera changes, update the query
						doMapQuery();
					}
				});
				
				// When the user click on the map, we lead a new research for 
				// artworks centered on the clicked point
				map.getMap().setOnMapClickListener(new OnMapClickListener() {
			        @Override
			        public void onMapClick(LatLng point) {
			        	otherLocation = new Location("Other");
			    		otherLocation.setLatitude(point.latitude);
			    		otherLocation.setLongitude(point.longitude);
			    		onResume();
			        }
			    });
		
		return v;
	}
	
	/*
	 * Called when the Fragment is no longer visible at all. Stop updates and disconnect.
	 */
	@Override
	public void onStop() {
		// If the client is connected
		if (locationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		locationClient.disconnect();

		super.onStop();
	}

	/*
	 * Called when the Fragment is restarted, even before it becomes visible.
	 */
	@Override
	public void onStart() {
		super.onStart();

		// Connect to the location services client
		locationClient.connect();
	}

	/*
	 * Called when the Fragment is resumed. Updates the view.
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Get the latest search distance preference
		radius = m_searchDistance;
		// Checks the last saved location to show cached data if it's available
		if (lastLocation != null && otherLocation == null) {
			LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			// If the search distance preference has been changed, move
			// map to new bounds.
			if (lastRadius != radius) {
				updateZoom(myLatLng);
			}
			// Update the circle map
			updateCircle(myLatLng);
		}else if (otherLocation != null){
			LatLng myLatLng = new LatLng(otherLocation.getLatitude(), otherLocation.getLongitude());
			// If the search distance preference has been changed, move
			// map to new bounds.
			if (lastRadius != radius) {
				updateZoom(myLatLng);
			}
			// Update the circle map
			updateCircle(myLatLng);
		}
		// Save the current radius
		lastRadius = radius;
		// Query for the latest data to update the views.
		doMapQuery();
	}
	
	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Google Play services can resolve some errors it detects. If the error has a resolution, try
		// sending an Intent to start a Google Play services activity that can resolve error.
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this.getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

			} catch (IntentSender.SendIntentException e) {

				// Thrown if Google Play services canceled the original PendingIntent
				System.out.println("An error occurred when connecting to location services.");
			}
		} else {

			// If no resolution is available, display a dialog to the user with the error.
			System.out.println(connectionResult);
		}
	}
	
	/*
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			System.out.println( "Google play services available");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			//  Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
			//  if (dialog != null) {
			//    ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			//    errorFragment.setDialog(dialog);
			//    errorFragment.show(getSupportFragmentManager(), Application.APPTAG);
			//  }
			return false;
		}
	}

	/*
	 * Called by Location Services when the request to connect the client finishes successfully. At
	 * this point, you can request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {
		System.out.println("Connected to location services");
		// If the user use hid own location, then update it
		if(otherLocation == null){
			currentLocation = getLocation();
			startPeriodicUpdates();
		}
	}

	/*
	 * Called by Location Services if the connection to the location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		System.out.println("Disconnected from location services");
	}

	/*
	 * Report location updates to the UI.
	 */
	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
		if (lastLocation != null
				&& geoPointFromLocation(location)
				.distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
			// If the location hasn't changed by more than 10 meters, ignore it.
			return;
		}
		lastLocation = location;
		LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		if (!hasSetUpInitialLocation) {
			// Zoom to the current location.
			updateZoom(myLatLng);
			hasSetUpInitialLocation = true;
		}
		// Update map radius indicator
		updateCircle(myLatLng);
		doMapQuery();
	}
	
	/*
	 * In response to a request to start updates, send a request to Location Services
	 */
	private void startPeriodicUpdates() {
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	/*
	 * In response to a request to stop updates, send a request to Location Services
	 */
	private void stopPeriodicUpdates() {
		locationClient.removeLocationUpdates( this);
	}

	/*
	 * Get the current location
	 */
	private Location getLocation() {
		// If Google Play Services is available
		if (servicesConnected()) {
			if(otherLocation == null){
				// Get the current location
				return locationClient.getLastLocation();
			}else{
				return otherLocation;
			}
		} else {
			return null;
		}
	}

	/*
	 * Set up the query to update the map view
	 */
	private void doMapQuery() {
		final int myUpdateNumber = ++mostRecentMapUpdate;
		Location myLoc;
		if(otherLocation == null){
			myLoc = (currentLocation == null) ? lastLocation : currentLocation;
		}else{
			myLoc = otherLocation;
		}
		// If location info isn't available, clean up any existing markers
		if (myLoc == null) {
			cleanUpMarkers(new HashSet<String>());
			return;
		}
		final ParseGeoPoint myPoint = geoPointFromLocation(myLoc);
		// Create the map Parse query
		ParseQuery<Artwork> mapQuery = Artwork.getQuery();
		// Set up additional query filters
		mapQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);
		//mapQuery.include("user");
		mapQuery.orderByDescending("createdAt");
		//mapQuery.setLimit(MAX_POST_SEARCH_RESULTS);
		// Kick off the query in the background
		mapQuery.findInBackground(new FindCallback<Artwork>() {
			@Override
			public void done(List<Artwork> objects, ParseException e) {
				if (e != null) {
					System.out.println("An error occurred while querying for map posts.");
					return;
				}
				System.out.println("size : " + objects.size());
				/*
				 * Make sure we're processing results from
				 * the most recent update, in case there
				 * may be more than one in progress.
				 */
				if (myUpdateNumber != mostRecentMapUpdate) {
					return;
				}
				// Posts to show on the map
				Set<String> toKeep = new HashSet<String>();
				// Loop through the results of the search
				for (Artwork post : objects) {
					// Add this post to the list of map pins to keep
					toKeep.add(post.getObjectId());
					// Check for an existing marker for this post
					Marker oldMarker = mapMarkers.get(post.getObjectId());
					// Set up the map marker's location
					MarkerOptions markerOpts =
							new MarkerOptions().position(new LatLng(post.getLocation().getLatitude(), post
									.getLocation().getLongitude()));
					// Set up the marker properties based on if it is within the search radius
					if (post.getLocation().distanceInKilometersTo(myPoint) > radius * METERS_PER_FEET
							/ METERS_PER_KILOMETER) {
						// Check for an existing out of range marker
						if (oldMarker != null) {
							if (oldMarker.getSnippet() == null) {
								// Out of range marker already exists, skip adding it
								continue;
							} else {
								// Marker now out of range, needs to be refreshed
								oldMarker.remove();
							}
						}
						// Display a red marker with a predefined title and no snippet
						markerOpts =
								markerOpts.title(getResources().getString(R.string.post_out_of_range)).icon(
										BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					} else {
						// Check for an existing in range marker
						if (oldMarker != null) {
							if (oldMarker.getSnippet() != null) {
								// In range marker already exists, skip adding it
								continue;
							} else {
								// Marker now in range, needs to be refreshed
								oldMarker.remove();
							}
						}
						// Display a green marker with the post information
						markerOpts =
								markerOpts.title(post.getTitle()).snippet(post.getTitle())
								.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					}
					// Add a new marker
					Marker marker = map.getMap().addMarker(markerOpts);
					mapMarkers.put(post.getObjectId(), marker);
					if (post.getObjectId().equals(selectedObjectId)) {
						marker.showInfoWindow();
						selectedObjectId = null;
					}
				}
				// Clean up old markers.
				cleanUpMarkers(toKeep);
			}
		});
	}

	/*
	 * Helper method to clean up old markers
	 */
	private void cleanUpMarkers(Set<String> markersToKeep) {
		for (String objId : new HashSet<String>(mapMarkers.keySet())) {
			if (!markersToKeep.contains(objId)) {
				Marker marker = mapMarkers.get(objId);
				marker.remove();
				mapMarkers.get(objId).remove();
				mapMarkers.remove(objId);
			}
		}
	}

	/*
	 * Helper method to get the Parse GEO point representation of a location
	 */
	private ParseGeoPoint geoPointFromLocation(Location loc) {
		return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	}

	/*
	 * Displays a circle on the map representing the search radius
	 */
	private void updateCircle(LatLng myLatLng) {
		if (mapCircle == null) {
			mapCircle =
					map.getMap().addCircle(
							new CircleOptions().center(myLatLng).radius(radius * METERS_PER_FEET));
			int baseColor = Color.DKGRAY;
			mapCircle.setStrokeColor(baseColor);
			mapCircle.setStrokeWidth(2);
			mapCircle.setFillColor(Color.argb(50, Color.red(baseColor), Color.green(baseColor),
					Color.blue(baseColor)));
		}
		mapCircle.setCenter(myLatLng);
		mapCircle.setRadius(radius * METERS_PER_FEET); // Convert radius in feet to meters.
	}

	/*
	 * Zooms the map to show the area of interest based on the search radius
	 */
	private void updateZoom(LatLng myLatLng) {
		// Get the bounds to zoom to
		LatLngBounds bounds = calculateBoundsWithCenter(myLatLng);
		// Zoom to the given bounds
		map.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
	}

	/*
	 * Helper method to calculate the offset for the bounds used in map zooming
	 */
	private double calculateLatLngOffset(LatLng myLatLng, boolean bLatOffset) {
		// The return offset, initialized to the default difference
		double latLngOffset = OFFSET_CALCULATION_INIT_DIFF;
		// Set up the desired offset distance in meters
		float desiredOffsetInMeters = radius * METERS_PER_FEET;
		// Variables for the distance calculation
		float[] distance = new float[1];
		boolean foundMax = false;
		double foundMinDiff = 0;
		// Loop through and get the offset
		do {
			// Calculate the distance between the point of interest
			// and the current offset in the latitude or longitude direction
			if (bLatOffset) {
				Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude
						+ latLngOffset, myLatLng.longitude, distance);
			} else {
				Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude,
						myLatLng.longitude + latLngOffset, distance);
			}
			// Compare the current difference with the desired one
			float distanceDiff = distance[0] - desiredOffsetInMeters;
			if (distanceDiff < 0) {
				// Need to catch up to the desired distance
				if (!foundMax) {
					foundMinDiff = latLngOffset;
					// Increase the calculated offset
					latLngOffset *= 2;
				} else {
					double tmp = latLngOffset;
					// Increase the calculated offset, at a slower pace
					latLngOffset += (latLngOffset - foundMinDiff) / 2;
					foundMinDiff = tmp;
				}
			} else {
				// Overshot the desired distance
				// Decrease the calculated offset
				latLngOffset -= (latLngOffset - foundMinDiff) / 2;
				foundMax = true;
			}
		} while (Math.abs(distance[0] - desiredOffsetInMeters) > OFFSET_CALCULATION_ACCURACY);
		return latLngOffset;
	}

	/*
	 * Helper method to calculate the bounds for map zooming
	 */
	public LatLngBounds calculateBoundsWithCenter(LatLng myLatLng) {
		// Create a bounds
		LatLngBounds.Builder builder = LatLngBounds.builder();

		// Calculate east/west points that should to be included
		// in the bounds
		double lngDifference = calculateLatLngOffset(myLatLng, false);
		LatLng east = new LatLng(myLatLng.latitude, myLatLng.longitude + lngDifference);
		builder.include(east);
		LatLng west = new LatLng(myLatLng.latitude, myLatLng.longitude - lngDifference);
		builder.include(west);

		// Calculate north/south points that should to be included
		// in the bounds
		double latDifference = calculateLatLngOffset(myLatLng, true);
		LatLng north = new LatLng(myLatLng.latitude + latDifference, myLatLng.longitude);
		builder.include(north);
		LatLng south = new LatLng(myLatLng.latitude - latDifference, myLatLng.longitude);
		builder.include(south);

		return builder.build();
	}

}
