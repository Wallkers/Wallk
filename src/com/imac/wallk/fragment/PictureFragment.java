package com.imac.wallk.fragment;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.imac.wallk.Artwork;
import com.imac.wallk.NewArtworkActivity;
import com.imac.wallk.R;
import com.imac.wallk.NewArtworkFragment.ErrorDialogFragment;
import com.imac.wallk.activity.WallkActivity;
import com.imac.wallk.adapter.FavoriteArtworkAdapter;
import com.imac.wallk.adapter.UserArtworkAdapter;
import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseException;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PictureFragment extends Fragment implements LocationListener,
							GooglePlayServicesClient.ConnectionCallbacks,
							GooglePlayServicesClient.OnConnectionFailedListener {
	
	private Artwork artwork;
	private ParseFile photoFile;
	private EditText pictureTitle;
	
	//Data for localisation
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
		
		 // Create a new global location parameters object, used to locate the user
	    locationRequest = LocationRequest.create();
	    locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
	    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
	 
	    // Create a new location client, using the enclosing class to handle callbacks.
	    locationClient = new LocationClient(getActivity(), this, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_picture, container, false);
		
		pictureTitle = ((EditText) v.findViewById(R.id.picture_title));
		
		//Read the picture taken by the user
		Bitmap tempPicture;
        try {
			// Get the last picture created in CameraFragment
			tempPicture = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/temporaryPicture.jpg");
			//display it
            ImageView imgView=(ImageView)v.findViewById(R.id.picture_image_view);
            imgView.setImageBitmap(tempPicture);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        //Manage button actions
        //Cancel
        Button cancelButton = (Button) v.findViewById(R.id.cancel_picture_button);
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//delete the temporary picture and come back to the cameraFragment
				getActivity().deleteFile("temporaryPicture.jpg");
				
				//debug, see if deleted
				((WallkActivity)getActivity()).logFilesSaved();
				
				//come back to camera fragment to take a new picture
				((WallkActivity) getActivity()).showFragment(new CameraFragment());
			}
		});
        
        //Publish
        Button publishButton = (Button) v.findViewById(R.id.publish_picture_button);
        
        publishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//save the picture online
				saveScaledPhotoOnParse("temporaryPicture.jpg");
			}
		});

		return v;
	}
	

	/*
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one.
	 * If saving succeed, we go to the user gallery
	 */
	private void saveScaledPhotoOnParse(String fileName) {

		Bitmap artworkImage = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + fileName);
		//Resize photo from camera byte array
		//Bitmap artworkImage = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap artworkImageScaled = Bitmap.createScaledBitmap(artworkImage, 200, 200
				* artworkImage.getHeight() / artworkImage.getWidth(), false);

		// Override Android default landscape orientation and save portrait
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap rotatedScaledArtworkImage = Bitmap.createBitmap(artworkImageScaled, 0,
				0, artworkImageScaled.getWidth(), artworkImageScaled.getHeight(),
				matrix, true);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedScaledArtworkImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();
		// Save the scaled image to Parse
		photoFile = new ParseFile("artwork_photo.jpg", scaledData);
		
		artwork = new Artwork();
		
		// Add data to the artwork object
		artwork.setPhotoFile(photoFile);

		artwork.setTitle(pictureTitle.getText().toString());

		// Associate the artwork with the current user
		artwork.setPictureAuthor(ParseUser.getCurrentUser());

		//Add the location
		final ParseGeoPoint userLocation = geoPointFromLocation(getLocation());
		if(userLocation != null) 
			artwork.setLocation(userLocation);
		
		// If the user added a photo, that data will be added in the CameraFragment

		// Save the artwork and return
		artwork.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null) {
					Toast.makeText(
							getActivity().getApplicationContext(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				} else {

					//delete the temporary picture and come back to the cameraFragment
					getActivity().deleteFile("temporaryPicture.jpg");
					
					//debug, see if deleted
					((WallkActivity)getActivity()).logFilesSaved();
					
					//go to user pictures gallery
					GalleryFragment galleryFragment = ((WallkActivity)getActivity()).getGalleryFrag();
					((WallkActivity) getActivity()).showFragment(galleryFragment);
					galleryFragment.showUserArtworks();
				}
			}

		});
	}
	
	/*
	 * On resume, check and see if a artwork photo has been set from the
	 * CameraFragment. If it has, load the image in this fragment and make the
	 * preview image visible.
	 */
	
	//location of the user
	
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
		
	private ParseGeoPoint geoPointFromLocation(Location loc) {
		if(loc != null)
			return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
		else
			return null;
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

}