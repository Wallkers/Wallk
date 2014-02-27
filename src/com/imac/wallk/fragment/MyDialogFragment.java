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
import com.imac.wallk.fragment.AccountFragment;
import com.parse.ParseException;
import com.parse.ParseUser;
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
        /*View tv = v.findViewById(R.id.dialogTitle);
        ((TextView)tv).setText("Dialog");
        getDialog().setTitle("dialog");*/
        
        TextView formerTitle = (TextView) v.findViewById(R.id.formerTitle);
        TextView formerContent = (TextView) v.findViewById(R.id.formerContent);
        TextView newTitle = (TextView) v.findViewById(R.id.newTitle);
        final EditText newContent = (EditText) v.findViewById(R.id.newContent);
        
        Button changeButton = (Button) v.findViewById(R.id.changeButton);
        
        switch (getArguments().getInt("type")) {
			case CHANGE_USERNAME:
				getDialog().setTitle("Change your username");
				
				formerTitle.setText("Former username :");
				
				formerContent.setText(ParseUser.getCurrentUser().getUsername());
				
				newTitle.setText("New username :");
				
				changeButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String newUsername = newContent.getText().toString();
						//change the user name
						if (newUsername.matches("")) {
						    Toast.makeText(getActivity(), "You did not enter a valid username", Toast.LENGTH_SHORT).show();
						    return;
						}
						
						ParseUser user = ParseUser.getCurrentUser();
						user.setUsername(newContent.getText().toString());
						user.saveInBackground();
						
						
					}
				});
				
				break;
				
			case CHANGE_PASSWORD:
				getDialog().setTitle("Change your password");
				
				formerTitle.setText("Former password :");
				
				//formerContent.setText(R.string.fakePassword);
				
				newTitle.setText("New password :");
				
				//modify the editText to show text like password
				newContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				
				break;
		}
        
        // Watch for button clicks.
       /* Button button = (Button)v.findViewById(R.id.change_button);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	//((AccountFragment)getActivity()).showDialog();
            }
        });*/

        return v;
    }
	
}