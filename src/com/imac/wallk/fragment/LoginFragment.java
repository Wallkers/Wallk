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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginFragment extends Fragment {

	// UI references.
	private EditText usernameView;
	private EditText passwordView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_login, container, false);

		// change font
		TextView titleView = (TextView) v.findViewById(R.id.login_title_label);
		Typeface titleFont = Typeface.createFromAsset(
				getActivity().getAssets(), "PermanentMarker.ttf");
		titleView.setTypeface(titleFont);

		// Set up the login form.
		usernameView = (EditText) v.findViewById(R.id.username);
		passwordView = (EditText) v.findViewById(R.id.password);

		// Set up the submit button click handler
		v.findViewById(R.id.action_button).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						// Validate the log in data
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
						validationErrorMessage.append(getResources().getString(
								R.string.error_end));

						// If there is a validation error, display the error
						if (validationError) {
							Toast.makeText(
									getActivity().getApplicationContext(),
									validationErrorMessage.toString(),
									Toast.LENGTH_LONG).show();
							return;
						}

						// Set up a progress dialog
						final ProgressDialog dlg = new ProgressDialog(
								getActivity());
						dlg.setTitle("Please wait.");
						dlg.setMessage("Logging in.  Please wait.");
						dlg.show();
						// Call the Parse login method
						ParseUser.logInInBackground(usernameView.getText()
								.toString(), passwordView.getText().toString(),
								new LogInCallback() {

									@Override
									public void done(ParseUser user,
											ParseException e) {
										dlg.dismiss();
										if (e != null) {
											// Show the error message
											Toast.makeText(
													getActivity()
															.getApplicationContext(),
													e.getMessage(),
													Toast.LENGTH_LONG).show();
										} else {
											// change the fragment
											WallkActivity parentActivity = (WallkActivity) getActivity();
											parentActivity.getGalleryFrag().showUserArtworks();
											parentActivity.showFragment(parentActivity.getGalleryFrag());
											parentActivity.invalidateOptionsMenu();
										}
									}
								});
					}
				});

		return v;
	}

	//check if string is empty
	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}
}
