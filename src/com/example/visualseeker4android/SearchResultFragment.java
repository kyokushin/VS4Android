package com.example.visualseeker4android;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.visualseeker4android.xml.AsyncXMLParser;
import com.example.visualseeker4android.xml.AsyncXMLParser.OnReturnResultListener;
import com.example.visualseeker4android.xml.VisualSeekerResult;
import com.example.visualseeker4android.xml.XMLParser;
import com.example.visualseeker4android.xml.XMLRequest;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SearchResultFragment extends Fragment {
	
	ExecutorService service = Executors.newSingleThreadExecutor();
	
	int[] imageview_id = new int[]{
		R.id.imageView1,
		R.id.imageView2,
		R.id.imageView3,
		R.id.imageView4,
		R.id.imageView5,
		R.id.imageView6,
		R.id.imageView7,
		R.id.imageView8,
		R.id.imageView9
	};
	ImageView[] imageViews = null;
	String[] ids = null;
	AsyncXMLParser asyncParser = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_search_result, container, false);
		
		imageViews = new ImageView[imageview_id.length];
		ids = new String[imageview_id.length];
		for( int i=0; i<imageview_id.length; i++){
			ImageView imageView = (ImageView)view.findViewById(imageview_id[i]);
			imageView.setOnClickListener(new OnClickItemListener(ids,i));
			imageViews[i] = imageView;
		}

		asyncParser = new AsyncXMLParser();
		asyncParser.setOnReturnResultListener(new UiUpdateListener());
		asyncParser.executeInitial();
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search_result, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_reset){
			asyncParser.executeInitial();
			return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}



	private class OnClickItemListener implements OnClickListener {
		private String[] ids = null;
		int index = -1;
		public OnClickItemListener(String[] ids , int index){
			this.ids = ids;
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			asyncParser.execute(XMLParser.SEARCH_URL_BASE+ids[index]);

			Log.d("click","item clicked. url:" + ids[index]);
		}
	}


	private class UiUpdateListener implements OnReturnResultListener {
		ExecutorService service = Executors.newFixedThreadPool(2);
		public UiUpdateListener() {
		}
		
		@Override
		public void onReturnResult(List<VisualSeekerResult> result) {
			Log.d("onReturn","return result");
			for( int i=0; i<result.size(); i++){
				String id = result.get(i).getId();
				ids[i] = id;

				String url = result.get(i).getUrl();
				service.execute(new RequestImageURL(url, i) );
			}
		}
		
		private class RequestImageURL implements Runnable{
			Handler handle;
			URL url= null;
			int index = -1;
			byte[] imageByte = null;
			public RequestImageURL(String url, int index ) {
				this.index = index;
				handle = new Handler();
				try {
					this.url = new URL(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					this.url = null;
				}
			}
			
			@Override
			public void run() {
				if(url == null){//TODO:nullがなぜ発生するのか調査が必要
					handle.post(new Runnable() {
						@Override
						public void run() {
							imageViews[index].setImageBitmap(null);
						}
					});
					return;
				}
				InputStream istr = null;
				ByteArrayOutputStream ostr = null;
				try {
					istr = url.openStream();
					ostr = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len;
					while( (len = istr.read(buffer)) > 0 ){
						ostr.write(buffer, 0, len);
					}
					imageByte = ostr.toByteArray();
					
					handle.post(new Runnable() {
						@Override
						public void run() {
							Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
							imageViews[index].setImageBitmap(bitmap);
						}
					});
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					try {
						if(istr != null)istr.close();
						if(ostr != null)ostr.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		}
	}
	
	


}
