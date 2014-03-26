package com.imac.wallk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imac.wallk.Artwork;
import com.imac.wallk.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/*
 * The UserArtworkAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for artworks of the user
 */
public class UserArtworkAdapter extends ParseQueryAdapter<Artwork>{
	
	public UserArtworkAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Artwork>() {
			public ParseQuery<Artwork> create() {
				// Here we can configure a ParseQuery to display
				// only user artworks.
				ParseQuery<Artwork> query = new ParseQuery<Artwork>("Artwork");
				query.whereEqualTo("author", ParseUser.getCurrentUser());
				query.orderByAscending("date");
				return query;
			}
		});
	}

	@Override
	public View getItemView(Artwork artwork, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_favorites, null);
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

		TextView titleTextView = (TextView) v.findViewById(R.id.text1);
		titleTextView.setText(artwork.getTitle());
		TextView ratingTextView = (TextView) v
				.findViewById(R.id.favorite_artwork_rating);
		ratingTextView.setText(artwork.getRating());
		return v;
	}
}
