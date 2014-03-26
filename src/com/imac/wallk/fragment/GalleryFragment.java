package com.imac.wallk.fragment;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.imac.wallk.Artwork;
import com.imac.wallk.R;
import com.imac.wallk.adapter.AllArtworkAdapter;
import com.imac.wallk.adapter.FavoriteArtworkAdapter;
import com.imac.wallk.adapter.UserArtworkAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;

public class GalleryFragment extends ListFragment {
	
	//filters
	private AllArtworkAdapter mainAdapter;
	private UserArtworkAdapter userAdapter;
	private FavoriteArtworkAdapter favoritesAdapter;
	private ProgressDialog progressDialog = null;
	
	private FrameLayout loadingPage = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.listfragment_gallery, container, false);
		super.onCreate(savedInstanceState);
		
		// Get layout elements
		loadingPage = (FrameLayout) v.findViewById(R.id.loading_page);
		
		// Set up a progress dialog
		progressDialog = new ProgressDialog(getActivity());
		mainAdapter = new AllArtworkAdapter(this.getActivity());

		//adapters allow to sort pictures
		favoritesAdapter = new FavoriteArtworkAdapter(this.getActivity());
		//sort pictures by user
		userAdapter = new UserArtworkAdapter(this.getActivity());
		
		updateArtworkList();
		
		return v;
	}
	
	public void updateArtworkList() {
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
					loadingPage.setVisibility(View.GONE);
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
