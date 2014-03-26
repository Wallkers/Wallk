package com.imac.wallk.fragment;

import java.util.List;


import com.imac.wallk.Artwork;
import com.imac.wallk.R;
import com.imac.wallk.adapter.FavoriteArtworkAdapter;
import com.imac.wallk.adapter.UserArtworkAdapter;
import com.imac.wallk.adapter.AllArtworkAdapter;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseException;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GalleryFragment extends ListFragment {
	
	//filters
	private Object firstCreation = null;
	private AllArtworkAdapter mainAdapter;
	private UserArtworkAdapter userAdapter;
	private FavoriteArtworkAdapter favoritesAdapter;
	private ProgressDialog progressDialog = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		mainAdapter = new AllArtworkAdapter(this.getActivity());

		//adapters allow to sort pictures
		favoritesAdapter = new FavoriteArtworkAdapter(this.getActivity());
		//sort pictures by user
		userAdapter = new UserArtworkAdapter(this.getActivity());
		
		if(progressDialog == null){
			progressDialog = new ProgressDialog(getActivity());
			showAllArtworks();
		}

		return inflater.inflate(R.layout.listfragment_gallery, container, false);
	}
	
	public void showAllArtworks() {
		// Show progressDialog
		progressDialog.setTitle("Please wait.");
		progressDialog.setMessage("Charging all pictures. Please wait.");
		progressDialog.show();
		mainAdapter.loadObjects();
		
		mainAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Artwork>() {
			   public void onLoading() {
			     // Trigger any "loading" UI
			   }
			
				@Override
				public void onLoaded(List<Artwork> objects, Exception e) {
					progressDialog.dismiss();
				}
		});
		
		setListAdapter(mainAdapter);
	}

	private void showFavoritesArtworks() {
		progressDialog.setTitle("Please wait.");
		progressDialog.setMessage("Charging favorite pictures. Please wait.");
		progressDialog.show();
		mainAdapter.loadObjects();
				
		favoritesAdapter.loadObjects();
		favoritesAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Artwork>() {
		   public void onLoading() {
		     // Trigger any "loading" UI
		   }
		
			@Override
			public void onLoaded(List<Artwork> objects, Exception e) {
				progressDialog.dismiss();
			}
		});
		setListAdapter(favoritesAdapter);
	}
	
	public void showUserArtworks(){
		progressDialog.setTitle("Please wait.");
		progressDialog.setMessage("Charging your pictures. Please wait.");
		progressDialog.show();
		
		userAdapter.loadObjects();
		userAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Artwork>() {
		   public void onLoading() {
			     // Trigger any "loading" UI
			   }
			
				@Override
				public void onLoaded(List<Artwork> objects, Exception e) {
					progressDialog.dismiss();
				}
		});
		
		setListAdapter(userAdapter);
	}

}
