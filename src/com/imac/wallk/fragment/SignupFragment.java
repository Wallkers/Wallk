package com.imac.wallk.fragment;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.imac.wallk.R;
import com.imac.wallk.activity.WallkActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupFragment extends Fragment {

	// UI references.
	private EditText usernameView;
	private EditText passwordView;
	private EditText passwordAgainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_signup, container, false);

		// change font
		TextView titleView = (TextView) v.findViewById(R.id.signup_title_label);
		Typeface titleFont = Typeface.createFromAsset(
				getActivity().getAssets(), "PermanentMarker.ttf");
		titleView.setTypeface(titleFont);

		// Set up the signup form.
		usernameView = (EditText) v.findViewById(R.id.username);
		passwordView = (EditText) v.findViewById(R.id.password);
		passwordAgainView = (EditText) v.findViewById(R.id.passwordAgain);

		// Set up the submit button click handler
		v.findViewById(R.id.action_button).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {

						// Validate the sign up data
						boolean validationError = false;
						StringBuilder validationErrorMessage = new StringBuilder(
								getResources().getString(R.string.error_intro));
						if (isEmpty(usernameView)) {
							validationError = true;
							validationErrorMessage.append(getResources()
									.getString(R.string.error_blank_username));
						}
						if (isEmpty(passwordView)) {
							if (validationError) {
								validationErrorMessage.append(getResources()
										.getString(R.string.error_join));
							}
							validationError = true;
							validationErrorMessage.append(getResources()
									.getString(R.string.error_blank_password));
						}
						if (!isMatching(passwordView, passwordAgainView)) {
							if (validationError) {
								validationErrorMessage.append(getResources()
										.getString(R.string.error_join));
							}
							validationError = true;
							validationErrorMessage
									.append(getResources()
											.getString(
													R.string.error_mismatched_passwords));
						}
						validationErrorMessage.append(getResources().getString(
								R.string.error_end));

						// If there is a validation error, display the error
						if (validationError) {
							Toast.makeText(getActivity(),
									validationErrorMessage.toString(),
									Toast.LENGTH_LONG).show();
							return;
						}

						// Set up a progress dialog
						final ProgressDialog dlg = new ProgressDialog(
								getActivity());
						dlg.setTitle("Please wait.");
						dlg.setMessage("Signing up.  Please wait.");
						dlg.show();

						// Set up a new Parse user
						ParseUser user = new ParseUser();
						user.setUsername(usernameView.getText().toString());
						user.setPassword(passwordView.getText().toString());
						// Call the Parse signup method
						user.signUpInBackground(new SignUpCallback() {

							@Override
							public void done(ParseException e) {
								dlg.dismiss();
								if (e != null) {
									// Show the error message
									Toast.makeText(getActivity(),
											e.getMessage(), Toast.LENGTH_LONG)
											.show();
								} else {
									// change the fragment
									WallkActivity parentActivity = (WallkActivity) getActivity();
									parentActivity.showFragment(parentActivity.getGalleryFrag());
									parentActivity.getGalleryFrag().showUserArtworks();
									parentActivity.invalidateOptionsMenu();//recreate the menu now that we are logged
								}
							}
						});
					}
				});

		return v;
	}
	
	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isMatching(EditText etText1, EditText etText2) {
		if (etText1.getText().toString().equals(etText2.getText().toString())) {
			return true;
		} else {
			return false;
		}
	}
}
