package com.example.visualseeker4android.imagesearch;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.R.id;
import com.example.visualseeker4android.R.layout;
import com.example.visualseeker4android.xml.VisualSeekerResult;
import com.example.visualseeker4android.xml.XMLParser;
import com.example.visualseeker4android.xml.XMLRequest;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DebugFragment extends Fragment {
	
	TextView debug_output = null;
	ExecutorService service = Executors.newSingleThreadExecutor();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_debug_text, container, false);
		debug_output = (TextView)view.findViewById(R.id.debugTextView);
		return view;
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		service.execute(new XMLParserRunnable());
	}



	public void setText(String string){
		debug_output.setText(string);
	}

	//DEBUG用
	private class XMLParserRunnable implements Runnable {
		
		public XMLParserRunnable(){
			handle = new Handler();
		}

		Handler handle = null;
		XMLParser parser = null;
		@Override
		public void run() {
			parser = new XMLParser();
			parser.getInitialRequest();
			
			handle.post(new Runnable() {
				
				@Override
				public void run() {
					StringBuffer sb = new StringBuffer();
					List<VisualSeekerResult> res = parser.getResultList();
					for( int i=0; i<res.size(); i++ ){
						sb.append("["+ i +"]" + res.get(i).toString() + "\n");
					}
					setText(sb.toString());
				}
			});
		}
		
	}
	
	//DEBUG用
	private class XMLRequestRunnable implements Runnable{
		Handler handle = null;
		String xml = null;
		public XMLRequestRunnable(){
			handle = new Handler();
		}

		@Override
		public void run() {
			URL url;
			try {
				url = new URL("http://visseeker2.yahoo-labs.jp/yipr/search?results=8&query=0&weight=null&start=1&mode=id");
				XMLRequest request = new XMLRequest(url);
				xml = request.request();
				
				handle.post(new Runnable() {
					@Override
					public void run() {
						setText(xml);
					}
				});
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
