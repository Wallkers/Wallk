package com.imac.wallk.fragment;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.imac.wallk.NewArtworkActivity;
import com.imac.wallk.R;
import com.imac.wallk.activity.WallkActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

public class CameraFragment extends Fragment{
	public static final String TAG = "CameraFragment";

	private Camera camera;
	private SurfaceView surfaceView;
	private ImageButton photoButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_camera, parent, false);

		photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);

		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.e(TAG, "No camera with exception: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}

		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (camera == null)
					return;
				camera.takePicture(new Camera.ShutterCallback() {

					@Override
					public void onShutter() {
						// nothing to do
					}

				}, null, new Camera.PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						//Here we store the picture in local database, because we are not sure the user will save it online
						//and we pass it to the fragment PictureConfirmFragment to display it, we will save it on Parse only if the user wants to publish it
				        FileOutputStream outStream = null;
				        try {
				            // write to local sandbox file system
				            outStream = getActivity().openFileOutput("temporaryPicture.jpg", 0);
				            outStream.write(data);
				            outStream.close();
				            Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				            //launch picture fragment to display the picture taken
				            ((WallkActivity) getActivity()).showFragment(new PictureConfirmFragment());
				            
				        } catch (FileNotFoundException e) {
				            e.printStackTrace();
				        } catch (IOException e) {
				            e.printStackTrace();
				        } finally {
				        }
				        
				        //debug
				        ((WallkActivity)getActivity()).logFilesSaved();
					}
				});
			}
		});

		surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera != null) {
						camera.setDisplayOrientation(90);
						camera.setPreviewDisplay(holder);
						camera.startPreview();
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview", e);
				}
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// nothing to do here
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// nothing here
			}

		});

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.i(TAG, "No camera: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onPause() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
		super.onPause();
	}

}
