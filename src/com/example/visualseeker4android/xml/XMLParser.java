package com.example.visualseeker4android.xml;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class XMLParser {
	
	public static final String INITIAL_URL= "http://visseeker2.yahoo-labs.jp/yipr/search?results=8&query=0&weight=null&start=1&mode=id";
	public static final String SEARCH_URL_BASE= "http://visseeker2.yahoo-labs.jp/yipr/search?results=9&weight=0:50:50:100:100:0:30&start=1&mode=id&query=";
	
	List<VisualSeekerResult> resultList = new ArrayList<VisualSeekerResult>();
	
	public XMLParser(){
		
	}
	
	public List<VisualSeekerResult> getResultList(){
		return resultList;
	}
	
	public void getInitialRequest(){
		getRequest(INITIAL_URL);
	}
	
	public void getRequest( String strUrl){
		
		try {
			URL url = new URL(strUrl);
			URLConnection connection = url.openConnection();
			
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(connection.getInputStream(), "UTF-8");
			
			String title = null;
			String id = null;
			String tag = null;
			String image_url = null;

			boolean inThumb = false;
			boolean inUrl = false;
			int eventType;
				Log.d("imageUrl","testtest");
			while((eventType = parser.next()) != XmlPullParser.END_DOCUMENT){
				Log.d("imageUrl","testtest");
				switch(eventType){
				case XmlPullParser.START_TAG:{
					String tagName = parser.getName();
					Log.d("imageURL",tagName);
					if( tagName.toLowerCase(Locale.US).equals("result")){
						
					}
					else if( tagName.toLowerCase(Locale.US).equals("title") ){
						title = parser.nextText();
					}
					else if(tagName.toLowerCase(Locale.US).equals("id")){ 
						id = parser.nextText();
					}
					else if(tagName.toLowerCase(Locale.US).equals("tags")){ 
						tag = parser.nextText();
						String[] tags = tag.split("\n");
						if(tags.length == 2) tag = tags[0];
					}
					else if(tagName.toLowerCase(Locale.US).equals("url")){ 
						if(!inThumb) inUrl = true;
					}
					else if(tagName.toLowerCase(Locale.US).equals("thumbnail")){
						inThumb = true;
					}

					break;
				}
				case XmlPullParser.END_TAG:{
					String tagName = parser.getName();
					if( tagName.toLowerCase(Locale.US).equals("result")){
						VisualSeekerResult result = new VisualSeekerResult(title,id,tag,image_url);
						Log.d("imageURL", "result" + result);
						resultList.add(result);
						title = null;
						id = null;
						tag = null;
						image_url = null;
					}
					else if( tagName.toLowerCase(Locale.US).equals("title") ){
					}
					else if(tagName.toLowerCase(Locale.US).equals("id")){ 
					}
					else if(tagName.toLowerCase(Locale.US).equals("tags")){ 
					}
					else if(tagName.toLowerCase(Locale.US).equals("url")){ 
						if(!inThumb) inUrl = false;
					}
					else if(tagName.toLowerCase(Locale.US).equals("thumbnail")){
						inThumb = false;
					}

					break;
				}
				case XmlPullParser.TEXT:{
					String text = parser.getText();
					Log.d("imageURL", "text:"+text);
					if( !inThumb && inUrl ){
						if(text.startsWith("http")){
							image_url = text;
							Log.d("imageURL", "found!" + image_url);
						}
					}
					break;
				}
				}
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
