package com.imac.wallk.fragment;

import com.imac.wallk.DispatchActivity;
import com.imac.wallk.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.imac.wallk.activity.SignUpActivity;
import com.imac.wallk.activity.WallkActivity;
import com.imac.wallk.fragment.AccountFragment;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MyDialogFragment extends DialogFragment {
	
	private final static int CHANGE_USERNAME  = 0;
	private final static int CHANGE_PASSWORD  = 1;
	
	public static MyDialogFragment newInstance(int type) {
		MyDialogFragment dialog = new MyDialogFragment();
		
		Bundle args = new Bundle();
		args.putInt("type", type);
		dialog.setArguments(args);
		
		return dialog;
	}

	 @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        
        switch (getArguments().getInt("type")) {
        
			case CHANGE_USERNAME:
				getDialog().setTitle("Change your username");
				
				View vUsername = inflater.inflate(R.layout.fragment_dialog_change_username, container, false);
				
		        final TextView formerUsernameContent = (TextView) vUsername.findViewById(R.id.formerUsernameContent);
		        final EditText newUsernameContent = (EditText) vUsername.findViewById(R.id.newUsernameContent);
		        Button changeUsernameButton = (Button) vUsername.findViewById(R.id.changeUsernameButton);
		        
		        formerUsernameContent.setText(ParseUser.getCurrentUser().getUsername());

		        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						manageUsername(formerUsernameContent, newUsernameContent);
					}
				});
				return vUsername;
				
			case CHANGE_PASSWORD:
				
				View vPassword = inflater.inflate(R.layout.fragment_dialog_change_password, container, false);
				getDialog().setTitle("Change your password");
				
				final EditText userMail = (EditText) vPassword.findViewById(R.id.user_mail);
				Button changePasswordButton = (Button) vPassword.findViewById(R.id.changePasswordButton);
				
				changePasswordButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						// Set up a progress dialog
						final ProgressDialog dlg = new ProgressDialog(
								getActivity());
						dlg.setTitle("Please wait.");
						dlg.setMessage("Sending you an email.  Please wait.");
						dlg.show();
						
						ParseUser.getCurrentUser().setEmail(userMail.getText().toString());
						ParseUser.getCurrentUser().saveInBackground();
						
						ParseUser.requestPasswordResetInBackground(userMail.getText().toString(),
                                new RequestPasswordResetCallback() {
									public void done(ParseException e) {
										dlg.dismiss();
										getDialog().dismiss();
										if (e != null) {
											//problem sending email
											Toast.makeText(getActivity(),
													e.getMessage(), Toast.LENGTH_LONG)
													.show();
										} else {
											Toast.makeText(getActivity(),
													getResources().getString(R.string.password_mail_ok),
													Toast.LENGTH_LONG).show();
										}
								}
						});
						
					}
				});
				
				
				return vPassword;
		}
        
        //this point should normally not be reached
        return v;
        
    }
	
	
	private void manageUsername(TextView formerUsername, EditText newUsername){
		// Validate the sign up data
		boolean validationError = false;
		StringBuilder validationErrorMessage = new StringBuilder(
				getResources().getString(R.string.error_intro));
		if (isEmpty(newUsername)) {
			validationError = true;
			validationErrorMessage.append(getResources()
					.getString(R.string.error_blank_username));
		}
		if (isMatching(formerUsername, newUsername)) {
			validationError = true;
			validationErrorMessage.append(getResources()
									.getString(R.string.error_same_username));
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
		dlg.setMessage("Changing your username.  Please wait.");
		dlg.show();

		//change username 
		ParseUser user = ParseUser.getCurrentUser();
		user.setUsername(newUsername.getText().toString());
		user.saveInBackground(new SaveCallback() {

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
					parentActivity.showFragment(new AccountFragment());
					parentActivity.invalidateOptionsMenu();//recreate the menu now that we are logged
				}
				getDialog().dismiss();
			}
		});
		
	}
	
	//public static allows to call the function from other classes (in MyDialogFragment for example)
	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isMatching(TextView etText1, EditText etText2) {
		if (etText1.getText().toString().equals(etText2.getText().toString())) {
			return true;
		} else {
			return false;
		}
	}
}