package com.example.visualseeker4android.xml;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;

public class AsyncXMLParser {

	XMLParser parser = null;
	List<VisualSeekerResult> listResult = null;
	OnReturnResultListener uiListener = null;
	ExecutorService service = Executors.newSingleThreadExecutor();
	
	public AsyncXMLParser(){
		
	}
	
	public void setOnReturnResultListener( OnReturnResultListener listener ){
		uiListener = listener;
	}
	
	public void executeInitial(){
		service.execute(new XMLParserRunnable());
	}
	public void execute(String url){
		service.execute(new XMLParserRunnable(url));
	}
	

	private class XMLParserRunnable implements Runnable {
		
		Handler handle = null;
		String url = null;
		
		public XMLParserRunnable() {
			handle = new Handler();
			url = XMLParser.INITIAL_URL;
		}
		
		public XMLParserRunnable( String url ){
			handle = new Handler();
			this.url = url;
		}

		@Override
		public void run() {
			parser = new XMLParser();
			parser.getRequest(url);
			listResult = parser.getResultList();

			if(uiListener == null) return;
			handle.post(new UIPostRunnable());
		}

	}
	
	private class UIPostRunnable implements Runnable {

		@Override
		public void run() {
			uiListener.onReturnResult(listResult);
		}
		
	}
	
	public interface OnReturnResultListener {
		public void onReturnResult(List<VisualSeekerResult> result);
	}
	
}
