package com.imac.wallk.fragment;


import com.imac.wallk.R;

import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureFragment extends Fragment{
	// UI references.
		private TextView pictureTitle;
		private ImageView imageView;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_picture, container, false);
			pictureTitle = (TextView) v.findViewById(R.id.picture_title);
			return v;
		}


		public void setPictureTitle(String text) {
			this.pictureTitle = new TextView(getActivity());
			this.pictureTitle.setText(text);
		}


		public void setImageBitmap(Bitmap bitmap) {
			this.imageView.setImageBitmap(bitmap);
		}
		
}
