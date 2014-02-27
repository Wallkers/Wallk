package com.imac.wallk.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.imac.wallk.R;
import com.parse.ParseUser;


public class AccountFragment extends Fragment{

	public static final String TAG = "AccountFragment";
	private TextView usernameView;
	
	private final static int CHANGE_USERNAME  = 0;
	private final static int CHANGE_PASSWORD  = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_account, parent, false);
		usernameView = (TextView) v.findViewById(R.id.username);
		usernameView.setText(ParseUser.getCurrentUser().getUsername());
		
		Button changeUsername = (Button)v.findViewById(R.id.changeUsername);
		 changeUsername.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  MyDialogFragment dialog = MyDialogFragment.newInstance(CHANGE_USERNAME);
		          dialog.show(getFragmentManager(), "fragmentDialog");
		      }
		    });
		 
		 Button changePassword = (Button)v.findViewById(R.id.changePassword);
		 changePassword.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  MyDialogFragment dialog = MyDialogFragment.newInstance(CHANGE_PASSWORD);
		          dialog.show(getFragmentManager(), "fragmentDialog");
		  }
		});
		return v;
	}
}
