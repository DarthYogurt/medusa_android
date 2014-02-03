package com.medusa.checkit.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class HTTPPostRequest {
	
	private static final String POST_URL = "http://dev.darthyogurt.com:8000/upload/";
	
	Context context;
	HttpClient client;
	HttpPost post;
	MultipartEntityBuilder multipartEntity;
	int responseCode;
	
	public HTTPPostRequest(Context context) {
		this.context = context;
	}
	
	public void createNewPost() {
		client = new DefaultHttpClient();
		post = new HttpPost(POST_URL);
		post.setHeader("enctype", "multipart/form-data");

		multipartEntity = MultipartEntityBuilder.create();
		multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	}
	
	public void sendPost() {
		post.setEntity(multipartEntity.build());
		
		try {
			HttpResponse response = client.execute(post);
			
			responseCode = response.getStatusLine().getStatusCode();
			Log.v("POST RESPONSE CODE", Integer.toString(responseCode));
			
			String responseBody = EntityUtils.toString(response.getEntity());
			Log.v("POST RESPONSE BODY", responseBody);
		} 
		catch (ClientProtocolException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void addJSON(String filename) {
		File json = new File(context.getFilesDir() + File.separator + filename);
		multipartEntity.addPart("data", new FileBody(json));
	}
	
	public void addPictures(ArrayList<String> imageFilenamesArray) {
		for (int i = 0; i < imageFilenamesArray.size(); i++) {
			String filename = imageFilenamesArray.get(i);
			File file = new File(context.getFilesDir() + File.separator + filename);
			multipartEntity.addPart(filename, new FileBody(file));
		}
	}
	
	public int getResponseCode() {
		return responseCode;
	}

}
