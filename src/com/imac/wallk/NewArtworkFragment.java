package com.imac.wallk;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
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

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewArtworkFragment extends Fragment{
	private ImageButton photoButton;
	private Button saveButton;
	private Button cancelButton;
	private TextView artworkName;
	private Spinner artworkRating;
	private ParseImageView artworkPreview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_new_artwork, parent, false);

		artworkName = ((EditText) v.findViewById(R.id.artwork_name));

		// The artworkRating spinner lets people assign favorites of artworks they've
		// eaten.
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

				// If the user added a photo, that data will be
				// added in the CameraFragment

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
}
