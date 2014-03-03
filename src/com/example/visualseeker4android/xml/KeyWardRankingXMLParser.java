package com.example.visualseeker4android.xml;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class KeyWardRankingXMLParser {
	
	private static final String RSS_URL = "http://searchranking.yahoo.co.jp/rss/burst_ranking-rss.xml"; 

	
	private static URL genRequestUrl(){
		
		try {
			return new URL(RSS_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static List<String> getRequest(){
		
		List<String> result_list = new ArrayList<String>();
		try {
			URL url = genRequestUrl();
			URLConnection connection = url.openConnection();
			
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(connection.getInputStream(), "UTF-8");
			
			boolean inItem = false;
			boolean inTitle = false;

			int event_type;
			while((event_type = parser.next()) != XmlPullParser.END_DOCUMENT){
				Log.d("Query Ranking", "testest");
				
				switch(event_type){
				case XmlPullParser.START_TAG:{
					String tagName = parser.getName().toLowerCase(Locale.US);
					if( tagName.equals("item") ){
						inItem = true;
					}
					else if( tagName.equals("title")){
						inTitle = true;
					}
				}
				case XmlPullParser.END_TAG:{
					String tagName = parser.getName();
					if( tagName.toLowerCase(Locale.US).equals("item") ){
						inItem = false;
					}
					else if( tagName.equals("title")){
						inTitle = false;
					}
					
				}
				case XmlPullParser.TEXT:{
					if( inItem && inTitle ){
						String title = parser.getText();
						if( !title.equals("") ){
							result_list.add(title);
						}
					}
				}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
		return result_list;
		
	}
	
}
