package com.example.visualseeker4android.xml;

public class VisualSeekerResult {
	String title;
	String id;
	String tag;
	String url;
	
	public VisualSeekerResult(String title, String id, String tag, String url){
		this.title = title;
		this.id = id;
		this.tag = tag;
		this.url = url;
	}
	
	public String getTitle(){return title;}
	public String getId(){return id;}
	public String getTag(){return tag;}
	public String getUrl(){return url;}

	@Override
	public String toString() {
		return "title:" + title + ",id:" + id + ",tag:" + tag + ",url:" + url;
	}
	
	
}
