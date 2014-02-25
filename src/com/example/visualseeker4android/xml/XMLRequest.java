package com.example.visualseeker4android.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class XMLRequest {
	
	private String content = null;
	private URL request_url = null;
	
	public XMLRequest(URL url){
		
		request_url = url;
	}
	
	public String request(){
		
		try {
			Object object = request_url.getContent();
			if( object instanceof InputStream){
				BufferedReader bf = new BufferedReader(new InputStreamReader((InputStream)object));
				String line ;
				StringBuffer sb = new StringBuffer();
				while((line = bf.readLine()) != null){
					sb.append(line);
				}
				content = sb.toString();
			}
			return content;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	

}
