package com.imac.wallk;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewArtworkFragment extends Fragment implements LocationListener,
						GooglePlayServicesClient.ConnectionCallbacks,
						GooglePlayServicesClient.OnConnectionFailedListener{
	private ImageButton photoButton;
	private Button saveButton;
	private Button cancelButton;
	private TextView artworkName;
	private Spinner artworkRating;
	private ParseImageView artworkPreview;
	
	private Location lastLocation = null;
	private Location currentLocation = null;
	private LocationRequest locationRequest;
	private LocationClient locationClient;
	
	/*
	  * Define a request code to send to Google Play services This code is returned in
	  * Activity.onActivityResult
	  */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	  
	/*
	 * Constants for location update parameters
	 * */
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	
	// The update interval
	private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	
	// A fast interval ceiling
	private static final int FAST_CEILING_IN_SECONDS = 1;
	
	// Update interval in milliseconds
	private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	
	// A fast ceiling of update intervals, used when the app is visible
	private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		 // Create a new global location parameters object
	    locationRequest = LocationRequest.create();
	    locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
	    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
	 
	    // Create a new location client, using the enclosing class to handle callbacks.
	    locationClient = new LocationClient(getActivity(), this, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_new_artwork, parent, false);

		artworkName = ((EditText) v.findViewById(R.id.artwork_name));

		// The artworkRating spinner lets people assign favorites artworks .
		// artworks with 4 or 5 ratings will appear in the Favorites view.
		artworkRating = ((Spinner) v.findViewById(R.id.rating_spinner));
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.ratings_array,
						android.R.layout.simple_spinner_dropdown_item);
		artworkRating.setAdapter(spinnerAdapter);

		photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(artworkName.getWindowToken(), 0);
				startCamera();
			}
		});

		saveButton = ((Button) v.findViewById(R.id.save_button));
		saveButton.setOnClickListener(new View.OnClickListener() {

			//HERE IS MANAGED THE SAVING OF OBJECTS
			@Override
			public void onClick(View v) {
				Artwork artwork = ((NewArtworkActivity) getActivity()).getCurrentArtwork();

				// When the user clicks "Save," upload the artwork to Parse
				// Add data to the artwork object:
				artwork.setTitle(artworkName.getText().toString());

				// Associate the artwork with the current user
				artwork.setAuthor(ParseUser.getCurrentUser());

				// Add the rating
				artwork.setRating(artworkRating.getSelectedItem().toString());

				//Add the location
				final ParseGeoPoint userLocation = geoPointFromLocation(getLocation());
				if(userLocation != null) 
					artwork.setLocation(userLocation);
				
				// If the user added a photo, that data will be added in the CameraFragment

				// Save the artwork and return
				artwork.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							getActivity().setResult(Activity.RESULT_OK);
							getActivity().finish();
						} else {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Error saving: " + e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});

			}
		});

		cancelButton = ((Button) v.findViewById(R.id.cancel_button));
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
			}
		});

		// Until the user has taken a photo, hide the preview
		artworkPreview = (ParseImageView) v.findViewById(R.id.artwork_preview_image);
		artworkPreview.setVisibility(View.INVISIBLE);

		return v;
	}

	/*
	 * All data entry about a artwork object is managed from the NewartworkActivity.
	 * When the user wants to add a photo, we'll start up a custom
	 * CameraFragment that will let them take the photo and save it to the artwork
	 * object owned by the NewartworkActivity. Create a new CameraFragment, swap
	 * the contents of the fragmentContainer (see activity_new_artwork.xml), then
	 * add the NewartworkFragment to the back stack so we can return to it when the
	 * camera is finished.
	 */
	public void startCamera() {
		android.support.v4.app.Fragment cameraFragment = new CameraFragment();
		android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragmentContainer, cameraFragment);
		transaction.addToBackStack("NewArtworkFragment");
		transaction.commit();
	}

	/*
	 * On resume, check and see if a artwork photo has been set from the
	 * CameraFragment. If it has, load the image in this fragment and make the
	 * preview image visible.
	 */
	@Override
	public void onResume() {
		super.onResume();
		ParseFile photoFile = ((NewArtworkActivity) getActivity())
				.getCurrentArtwork().getPhotoFile();
		if (photoFile != null) {
			artworkPreview.setParseFile(photoFile);
			artworkPreview.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					artworkPreview.setVisibility(View.VISIBLE);
				}
			});
		}
	}
	
	
	//functions used to verify that google play services are available :
	public static class ErrorDialogFragment extends DialogFragment {
	    private Dialog mDialog;
	 
	    public ErrorDialogFragment() {
	      super();
	      mDialog = null;
	    }
	 
	    public void setDialog(Dialog dialog) {
	      mDialog = dialog;
	    }
	 
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	      return mDialog;
	    }
	}
	
	private boolean servicesConnected() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
	 
	    if (ConnectionResult.SUCCESS == resultCode) {
	      return true;
	    } else {
	      Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this.getActivity(), 0);
	      if (dialog != null) {
	        ErrorDialogFragment errorFragment = new ErrorDialogFragment();
	        errorFragment.setDialog(dialog);
	        errorFragment.show(getFragmentManager(), "Wallk");
	      }
	      return false;
	    }
	}

	/*
	* Show a dialog returned by Google Play services for the connection error code
	*/
	private void showErrorDialog(int errorCode) {
		// Get the error dialog from Google Play services
		Dialog errorDialog =
		    GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(),
		        CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
	
		  // Create a new DialogFragment in which to show the error dialog
		  ErrorDialogFragment errorFragment = new ErrorDialogFragment();
	
		  // Set the dialog in the DialogFragment
		  errorFragment.setDialog(errorDialog);
	
		  // Show the error dialog in the DialogFragment
		  errorFragment.show(getFragmentManager(), "Wallk");
		}
	}

	private void startPeriodicUpdates() {
	  locationClient.requestLocationUpdates(locationRequest, this);
	}
		 
	private void stopPeriodicUpdates() {
	  locationClient.removeLocationUpdates(this);
	}
		 
	private Location getLocation() {
	  if (servicesConnected()) {
		  Log.d("NewArtworkFragment", "services of google are connected");
		  Log.d("NewArtworkFragment", "lastLocation" + locationClient.isConnected());
		  Log.d("NewArtworkFragment", "lastLocation" + locationClient.getLastLocation());
		  if(locationClient.getLastLocation() == null){
			  Toast.makeText(getActivity().getApplicationContext(),
						"Error location, location is null, maybe gps is desactivated ",
						Toast.LENGTH_SHORT).show();
		  }
		  return locationClient.getLastLocation();
	  } else {
		  Log.d("NewArtworkFragment", "services of google are not connected");
	    return null;
	  }
	}
		
	/*
	* Helper method to get the Parse GEO point representation of a location
	*/
	private ParseGeoPoint geoPointFromLocation(Location loc) {
		if(loc != null)
			return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
		else
			return null;
	}
		
	//LocationListener
	@Override
	public void onConnected(Bundle bundle) {
		currentLocation = getLocation();
		startPeriodicUpdates();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
		    try {
		      connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
		    } catch (IntentSender.SendIntentException e) {
		    }
		  } else {
		    showErrorDialog(connectionResult.getErrorCode());
		  }
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStop() {
	    if (locationClient.isConnected()) {
	    	stopPeriodicUpdates();
	    }
	    locationClient.disconnect();
 
    	super.onStop();
	}
 
	@Override
	public void onStart() {
		super.onStart();

		locationClient.connect();
	}
}
