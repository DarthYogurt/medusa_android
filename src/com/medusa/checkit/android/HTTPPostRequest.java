package com.medusa.checkit.android;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class HTTPPostRequest {
	
	static final File EXTERNALSTORAGE = Environment.getExternalStorageDirectory();
	static final String POST_URL = "http://dev.darthyogurt.com:8000/upload/";
	
	Context context;
	MultipartEntityBuilder multipartEntity;
	
	public HTTPPostRequest(Context context) {
		this.context = context;
	}
	
	// For sending files using MultipartEntity
	public void multipartPost(String jsonFilename) throws ClientProtocolException, IOException {
		File json = new File(context.getFilesDir() + File.separator + "new_checklist.json");
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(POST_URL);
		post.setHeader("enctype", "multipart/form-data");

		multipartEntity = MultipartEntityBuilder.create();
		multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		multipartEntity.addPart("data", new FileBody(json));
		post.setEntity(multipartEntity.build());
		
		HttpResponse response = client.execute(post);
		String responseBody = EntityUtils.toString(response.getEntity());
		Log.v("HTTP POST Response", responseBody);
	}
	
	private void addPictures() {
	}
			
}
