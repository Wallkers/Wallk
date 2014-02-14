package com.imac.wallk;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class NewArtworkActivity extends FragmentActivity {
	
	private Artwork artwork;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        artwork = new Artwork();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
 
        // Begin with main data entry view,
        // NewArtworkFragment
        setContentView(R.layout.activity_new_artwork);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
 
        if (fragment == null) {
            fragment = new NewArtworkFragment();
            manager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
 
    public Artwork getCurrentArtwork() {
        return artwork;
    }
	
}
