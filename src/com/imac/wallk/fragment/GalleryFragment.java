package com.imac.wallk.fragment;

import java.util.List;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imac.wallk.Artwork;
import com.imac.wallk.R;
import com.imac.wallk.adapter.AllArtworkAdapter;
import com.imac.wallk.adapter.UserArtworkAdapter;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;

public class GalleryFragment extends ListFragment {
	
	//filters
	private AllArtworkAdapter mainAdapter;
	private UserArtworkAdapter userAdapter;
	private ProgressDialog progressDialog = null;
	
	private FrameLayout loadingPage = null;
	private FrameLayout galleryToShow = null;
	private TextView titleView = null;
	private ListView listOfPictures =  null;
	private ParseImageView parseImgView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.listfragment_gallery, container, false);
		super.onCreate(savedInstanceState);
		
		// Get layout elements
		loadingPage = (FrameLayout) v.findViewById(R.id.loading_page);
		galleryToShow = (FrameLayout) v.findViewById(R.id.gallery_to_show);
		titleView = (TextView) galleryToShow.findViewById(R.id.gallery_title);
		Typeface titleFont = Typeface.createFromAsset(
				getActivity().getAssets(), "PermanentMarker.ttf");
		titleView.setTypeface(titleFont);
		parseImgView = (ParseImageView) v.findViewById(R.id.picture_to_display);

		if(listOfPictures == null){
			listOfPictures = (ListView)v.findViewById(android.R.id.list);
		}
		
		mainAdapter = new AllArtworkAdapter(this.getActivity());
		
		//sort pictures by user
		userAdapter = new UserArtworkAdapter(this.getActivity());
		
		if(progressDialog == null){
			progressDialog = new ProgressDialog(getActivity());
			//the first time we open the fragment we charge all artworks
			showAllArtworks();
		}

		return v;
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
					clickReactionOnGallery();
					progressDialog.dismiss();
					showGallery();
					titleView.setText(R.string.title_activity_gallery);
				}
		});
		setListAdapter(mainAdapter);
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
					clickReactionOnGallery();
					progressDialog.dismiss();
					showGallery();
					titleView.setText(R.string.title_activity_mygallery);
				}
		});
		
		setListAdapter(userAdapter);
	}
	

	private void clickReactionOnGallery(){
		Log.d("COUNT", Integer.toString(listOfPictures.getCount()));
		listOfPictures.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
		    {
		      Artwork artworkSelected = (Artwork)listOfPictures.getItemAtPosition(position);
		      //String title = artworkSelected.getTitle();
		      ParseFile picture = artworkSelected.getPhotoFile();
		      parseImgView.setParseFile(picture);
		      parseImgView.loadInBackground(new GetDataCallback() {
					@Override
					public void done(byte[] data, ParseException e) {
						parseImgView.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
					}
				});
				
		}});
	}

	private void showGallery() {
		loadingPage.setVisibility(View.GONE);
		galleryToShow.setVisibility(View.VISIBLE);

	}

}
