package com.imac.wallk;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Artwork")
public class Artwork extends ParseObject implements ClusterItem{
	
	public Artwork() {
    }
 
	//title
    public String getTitle() {
        return getString("title");
    }
 
    public void setTitle(String title) {
        put("title", title);
    }
 
    //author
    public ParseUser getPictureAuthor() {
        return getParseUser("author");
    }
 
    public void setPictureAuthor(ParseUser user) {
        put("author", user);
    }
 
    //rating
    public String getRating() {
        return getString("rating");
    }
 
    public void setRating(String rating) {
        put("rating", rating);
    }
 
    //photo
    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }
 
    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }
	
    //location
    public ParseGeoPoint getLocation(){
    	return getParseGeoPoint("location");
    }
	
    public void setLocation(ParseGeoPoint geoPoint) {
        put("location", geoPoint);
    }
    
    //date
    public Date getDate(){
    	return getDate("date");
    }
	
    public void setLocation(Date date) {
        put("date", date);
    }
    
    public static ParseQuery<Artwork> getQuery() {
        return ParseQuery.getQuery(Artwork.class);
      }

	@Override
	public LatLng getPosition() {
		return new LatLng(getParseGeoPoint("location").getLatitude(), getParseGeoPoint("location").getLongitude());
	}

	
}
