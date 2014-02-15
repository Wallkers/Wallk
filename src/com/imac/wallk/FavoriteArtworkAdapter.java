package com.imac.wallk;

import java.util.Arrays;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/*
 * The FavoriteArtworkAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for favorite artworks, including a 
 * bigger preview image, the artwork's rating, and a "favorite"
 * star. 
 */
public class FavoriteArtworkAdapter extends ParseQueryAdapter<Artwork>{
	
	public FavoriteArtworkAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Artwork>() {
			public ParseQuery<Artwork> create() {
				// Here we can configure a ParseQuery to display
				// only top-rated artworks.
				ParseQuery query = new ParseQuery("Artwork");
				query.whereContainedIn("rating", Arrays.asList("5", "4"));
				query.orderByDescending("rating");
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