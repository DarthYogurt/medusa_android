package com.medusa.checkit.android;

import java.io.File;
import java.io.FileWriter;
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
	private static final String ERROR_URL = "http://dev.darthyogurt.com:8000/uploadError/";
	private static final String ERROR_FILENAME = "error.txt";
	
	Context context;
	HttpClient client;
	HttpPost post;
	MultipartEntityBuilder multipartEntity;
	int responseCode;
	String responseBody;
	
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
			Log.i("POST RESPONSE CODE", Integer.toString(responseCode));
			
			responseBody = EntityUtils.toString(response.getEntity());
			Log.i("POST RESPONSE BODY", responseBody);
			
			checkIfError();
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
			File file = new File(context.getExternalFilesDir(null) + File.separator + filename);
			multipartEntity.addPart(filename, new FileBody(file));
		}
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	private void checkIfError() {
		if (responseCode != 200) {
			sendErrorPost();
		}
	}
	
	private void sendErrorPost() {
		File errorFile = new File(context.getFilesDir() + File.separator + ERROR_FILENAME);
		
		try {
			FileWriter fw = new FileWriter(errorFile);
			fw.write(responseBody);
			fw.close();
		} catch (IOException e) { e.printStackTrace(); }
		
		HttpClient errorClient = new DefaultHttpClient();
		HttpPost errorPost = new HttpPost(ERROR_URL);
		errorPost.setHeader("enctype", "multipart/form-data");

		MultipartEntityBuilder entity = MultipartEntityBuilder.create();
		entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		entity.addPart("error", new FileBody(errorFile));
		
		errorPost.setEntity(entity.build());
		
		try { 
			HttpResponse errorResponse = errorClient.execute(errorPost);
			
			int errorResponseCode = errorResponse.getStatusLine().getStatusCode();
			Log.i("ERROR RESPONSE CODE", Integer.toString(errorResponseCode));
			
			String errorResponseBody = EntityUtils.toString(errorResponse.getEntity());
			Log.i("ERROR RESPONSE BODY", errorResponseBody);
		} 
		catch (ClientProtocolException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
	}

}
