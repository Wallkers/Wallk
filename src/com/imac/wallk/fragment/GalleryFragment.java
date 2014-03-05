package com.imac.wallk.fragment;

import com.imac.wallk.Artwork;
import com.imac.wallk.R;
import com.imac.wallk.adapter.FavoriteArtworkAdapter;
import com.imac.wallk.adapter.UserArtworkAdapter;
import com.parse.ParseQueryAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GalleryFragment extends ListFragment {
	
	//filters
	private ParseQueryAdapter<Artwork> mainAdapter;
	private UserArtworkAdapter userAdapter;
	private FavoriteArtworkAdapter favoritesAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		mainAdapter = new ParseQueryAdapter<Artwork>(this.getActivity(), Artwork.class);
		mainAdapter.setTextKey("title");
		mainAdapter.setImageKey("photo");

		// Subclass of ParseQueryAdapter
		favoritesAdapter = new FavoriteArtworkAdapter(this.getActivity());
		userAdapter = new UserArtworkAdapter(this.getActivity());

		// Default view is all artworks
		setListAdapter(mainAdapter);
		
		
		return inflater.inflate(R.layout.listfragment_gallery, container, false);
	}
	
	private void updateArtworkList() {
		mainAdapter.loadObjects();
		setListAdapter(mainAdapter);
	}

	private void showFavoritesArtworks() {
		favoritesAdapter.loadObjects();
		setListAdapter(favoritesAdapter);
	}
	
	public void showUserArtworks(){
		userAdapter.loadObjects();
		setListAdapter(userAdapter);
	}
}
