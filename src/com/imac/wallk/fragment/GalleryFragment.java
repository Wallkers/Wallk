package com.imac.wallk.fragment;

import java.util.List;

import com.imac.wallk.Artwork;
import com.imac.wallk.R;
import com.imac.wallk.adapter.FavoriteArtworkAdapter;
import com.imac.wallk.adapter.UserArtworkAdapter;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseException;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.app.ProgressDialog;
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

		// Set up a progress dialog
		final ProgressDialog dlg = new ProgressDialog(
				getActivity());
		dlg.setTitle("Please wait.");
		dlg.setMessage("Charging all pictures. Please wait.");
		dlg.show();
		mainAdapter = new ParseQueryAdapter<Artwork>(this.getActivity(), Artwork.class);
		mainAdapter.setTextKey("title");
		mainAdapter.setImageKey("photo");

		// Subclass of ParseQueryAdapter
		favoritesAdapter = new FavoriteArtworkAdapter(this.getActivity());
		userAdapter = new UserArtworkAdapter(this.getActivity());
		
		mainAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Artwork>() {
		   public void onLoading() {
		     // Trigger any "loading" UI
		   }
		
			@Override
			public void onLoaded(List<Artwork> objects, Exception e) {
				dlg.dismiss();
			}
		});
		// Default view is all artworks
		setListAdapter(mainAdapter);
		
		
		return inflater.inflate(R.layout.listfragment_gallery, container, false);
	}
	
	public void updateArtworkList() {
		// Set up a progress dialog
		final ProgressDialog dlg = new ProgressDialog(
				getActivity());
		dlg.setTitle("Please wait.");
		dlg.setMessage("Charging all pictures. Please wait.");
		dlg.show();
		mainAdapter.loadObjects();
		
		mainAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Artwork>() {
			   public void onLoading() {
			     // Trigger any "loading" UI
			   }
			
				@Override
				public void onLoaded(List<Artwork> objects, Exception e) {
					dlg.dismiss();
				}
		});
		
		setListAdapter(mainAdapter);
	}

	private void showFavoritesArtworks() {
		// Set up a progress dialog
		final ProgressDialog dlg = new ProgressDialog(
				getActivity());
		dlg.setTitle("Please wait.");
		dlg.setMessage("Charging favorite pictures. Please wait.");
		dlg.show();
		mainAdapter.loadObjects();
				
		favoritesAdapter.loadObjects();
		favoritesAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Artwork>() {
		   public void onLoading() {
		     // Trigger any "loading" UI
		   }
		
			@Override
			public void onLoaded(List<Artwork> objects, Exception e) {
				dlg.dismiss();
			}
		});
		setListAdapter(favoritesAdapter);
	}
	
	public void showUserArtworks(){
		
		final ProgressDialog dlg = new ProgressDialog(
				getActivity());
		dlg.setTitle("Please wait.");
		dlg.setMessage("Charging your pictures. Please wait.");
		dlg.show();
		
		userAdapter.loadObjects();
		userAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Artwork>() {
		   public void onLoading() {
			     // Trigger any "loading" UI
			   }
			
				@Override
				public void onLoaded(List<Artwork> objects, Exception e) {
					dlg.dismiss();
				}
		});
		
		setListAdapter(userAdapter);
	}
}
