package com.imac.wallk;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseQueryAdapter;

public class ArtworkListActivity extends ListActivity{
	
	private ParseQueryAdapter<Artwork> mainAdapter;
	private FavoriteArtworkAdapter favoritesAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setClickable(false);

		mainAdapter = new ParseQueryAdapter<Artwork>(this, Artwork.class);
		mainAdapter.setTextKey("title");
		mainAdapter.setImageKey("photo");

		// Subclass of ParseQueryAdapter
		favoritesAdapter = new FavoriteArtworkAdapter(this);

		// Default view is all artworks
		setListAdapter(mainAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_artwork_list, menu);
		return true;
	}

	/*
	 * Posting artworks and refreshing the list will be controlled from the Action
	 * Bar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_refresh: {
			updateArtworkList();
			break;
		}

		case R.id.action_favorites: {
			showFavorites();
			break;
		}

		case R.id.action_new: {
			newArtwork();
			break;
		}
		
		case R.id.action_account: {
			showAccountPage();
		}
		
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateArtworkList() {
		mainAdapter.loadObjects();
		setListAdapter(mainAdapter);
	}

	private void showFavorites() {
		favoritesAdapter.loadObjects();
		setListAdapter(favoritesAdapter);
	}

	private void newArtwork() {
		Intent i = new Intent(this, NewArtworkActivity.class);
		startActivityForResult(i, 0);
	}
	
	private void showAccountPage() {
		//autre chose que Dispat
		Intent i = new Intent(this, DispatchActivity.class);
		startActivityForResult(i, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// If a new post has been added, update
			// the list of posts
			updateArtworkList();
		}
	}
}
