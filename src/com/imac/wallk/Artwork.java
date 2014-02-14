package com.imac.wallk;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Artwork")
public class Artwork extends ParseObject{
	
	public Artwork() {
    }
 
    public String getTitle() {
        return getString("title");
    }
 
    public void setTitle(String title) {
        put("title", title);
    }
 
    public ParseUser getPictureAuthor() {
        return getParseUser("author");
    }
 
    public void setAuthor(ParseUser user) {
        put("author", user);
    }
 
    public String getRating() {
        return getString("rating");
    }
 
    public void setRating(String rating) {
        put("rating", rating);
    }
 
    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }
 
    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }
	
    public ParseGeoPoint getLocation(){
    	return getParseGeoPoint("location");
    }
	
    public void setLocation(ParseGeoPoint geoPoint) {
        put("location", geoPoint);
    }
	
}
