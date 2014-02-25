package com.imac.wallk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.imac.wallk.R;
import com.parse.ParseUser;


public class AccountFragment extends Fragment{

	public static final String TAG = "AccountFragment";
	private TextView usernameView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_account, parent, false);
		usernameView = (TextView) v.findViewById(R.id.username);
		usernameView.setText(ParseUser.getCurrentUser().getUsername());
		return v;
	}

}
