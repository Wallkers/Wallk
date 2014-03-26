package com.imac.wallk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.imac.wallk.Artwork;
import com.imac.wallk.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQueryAdapter;

public class AllArtworkAdapter extends ParseQueryAdapter<Artwork> {

	public AllArtworkAdapter(Context context) {
		super(context, Artwork.class);
	}
	
	@Override
	public View getItemView(Artwork artwork, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_gallery, null);
		}

		super.getItemView(artwork, v, parent);

		ParseImageView artworkImage = (ParseImageView) v.findViewById(R.id.icon);
		ParseFile photoFile = artwork.getParseFile("photo");
		if (photoFile != null) {
			artworkImage.setParseFile(photoFile);
			artworkImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		return v;
	}
}
