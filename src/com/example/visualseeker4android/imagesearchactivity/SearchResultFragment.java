package com.example.visualseeker4android.imagesearchactivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.utils.AsyncTaskRunner;
import com.example.visualseeker4android.xml.SearchResultContainer;
import com.example.visualseeker4android.xml.SearchResultXMLParser;

public class SearchResultFragment extends Fragment {
	
	public static final String FRAGMENT_TAG = "SearchResult";
	
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
	Bitmap[] bitmaps = new Bitmap[imageview_id.length];
	ImageView[] imageViews = null;
	String[] ids = null;
	
	Animation[] anim_open_result = null;
	Animation[] anim_close_result = null;
	
	Animation anim_on_touch_down = null;

    List<SearchResultContainer> result_list = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_search_result, container, false);
		
		imageViews = new ImageView[imageview_id.length];
		ids = new String[imageview_id.length];
		for( int i=0; i<imageview_id.length; i++){
			ImageView imageView = (ImageView)view.findViewById(imageview_id[i]);
			imageView.setOnClickListener(new OnClickItemListener(i));
            imageView.setOnLongClickListener(new OnLongClickItemListener(i));
			imageViews[i] = imageView;
		}
		
		//アニメーションの読み込み
		anim_open_result = new Animation[imageview_id.length];
		for( int i=0; i<anim_open_result.length; i++){
			anim_open_result[i] = AnimationUtils.loadAnimation(getActivity(), R.anim.open_search_result);
			anim_open_result[i].setAnimationListener(new OpenAnimationListener(i));
		}
		anim_close_result = new Animation[imageview_id.length];
		for( int i=0; i<anim_close_result.length; i++){
			anim_close_result[i] = AnimationUtils.loadAnimation(getActivity(), R.anim.close_search_result);
			anim_close_result[i].setAnimationListener(new CloseAnimationListener(i));
		}
		anim_on_touch_down = AnimationUtils.loadAnimation(getActivity(), R.anim.on_touch_down);
		
		AsyncTaskRunner.execute(new XMLParseTask(), new UiUpdateListener());
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search_result, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_reset){
			//asyncParser.executeInitial();
			AsyncTaskRunner.execute(new XMLParseTask(), new UiUpdateListener());
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	class OpenAnimationListener implements AnimationListener {
		private static final String TAG = "OpenAnimation";
		int index;
		OpenAnimationListener(int index){
			this.index = index;
		}
		@Override
		public void onAnimationStart(Animation arg0) {
			imageViews[index].setVisibility(View.VISIBLE);
			Log.d(TAG, "animation start and view is visible:" + index);
		}
		@Override
		public void onAnimationRepeat(Animation arg0) { }
		@Override
		public void onAnimationEnd(Animation arg0) { }
	}
	class CloseAnimationListener implements AnimationListener {
		int index;
		CloseAnimationListener(int index){
			this.index = index;
		}
		@Override
		public void onAnimationStart(Animation arg0) {
		}
		@Override
		public void onAnimationRepeat(Animation arg0) { }
		@Override
		public void onAnimationEnd(Animation arg0) {
			imageViews[index].setVisibility(View.INVISIBLE);
		}
	}

	private class OnClickItemListener implements OnClickListener {
		int index = -1;
		public OnClickItemListener(int index){
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			searchAndUpdateUi(index);
		}
	}
	
	public void searchAndUpdateUi( int index ){
			for( int i=0; i<imageview_id.length; i++){
				if( i == index) continue;
				imageViews[i].startAnimation(anim_close_result[i]);
			}
			anim_on_touch_down.setAnimationListener(new CloseAnimationListener(index));
			imageViews[index].startAnimation(anim_on_touch_down);
			String query_url = SearchResultXMLParser.SEARCH_URL_BASE+ids[index];
			//asyncParser.execute(query_url);
			AsyncTaskRunner.execute(new XMLParseTask(query_url), new UiUpdateListener());
			
			Log.d("click","item clicked. url:" + ids[index]);
	}

    private class OnLongClickItemListener implements View.OnLongClickListener {

        private int index = -1;
        public OnLongClickItemListener( int index ){
            this.index = index;
        }

        @Override
        public boolean onLongClick(View view) {

            ResultItemDialogFragment dialogFragment = new ResultItemDialogFragment();
            dialogFragment.setResult(index, result_list.get(index), bitmaps[index]);
            dialogFragment.show(getFragmentManager(),"dialog");

            return true;
        }
    }
    private static class XMLParseTask implements AsyncTaskRunner.Task {
    	static SearchResultXMLParser parser = null;
    	static List<SearchResultContainer> listResult = null;
    	String url = null;

    	public XMLParseTask() {
    		url = SearchResultXMLParser.INITIAL_URL;
    	}

    	public XMLParseTask( String url ){
    		this.url = url;
    	}

    	@Override
    	public Object run() {
    		return SearchResultXMLParser.getRequest(url);

    	}

    }
	private class UiUpdateListener implements AsyncTaskRunner.OnFinishUiUpdateListener{
		ExecutorService service = Executors.newFixedThreadPool(2);
		
		@Override
		public void onFinish(Object result) {

            result_list = (List<SearchResultContainer>)result;

			Log.d("onReturn","return result");
			for( int i=0; i<result_list.size(); i++){
				String id = result_list.get(i).getId();
				ids[i] = id;

				String url = result_list.get(i).getUrl();
				service.execute(new RequestImageURL(url, i) );
			}
		}
		
		private class RequestImageURL implements Runnable{
			final static String TAG = "RequestImageURL";
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
				if(url == null){
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
                            Bitmap bitmap = bitmaps[index];
							if( bitmap != null) bitmap.recycle();
                            bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
							bitmaps[index] = bitmap;
							imageViews[index].setImageBitmap(bitmap);
							imageViews[index].startAnimation(anim_open_result[index]);
						}
					});
					
					
				} catch (IOException e) {
					// エラー時に表示しない処理
					e.printStackTrace();
					Log.d(TAG, "failed to open URL" + index);
					handle.post(new Runnable() {
						@Override
						public void run() {
							imageViews[index].setImageBitmap(null);
							if(bitmaps[index] != null) bitmaps[index].recycle();
							bitmaps[index] = null;
							imageViews[index].setVisibility(View.INVISIBLE);
						}
					});
				}
				finally{
					try {
						if(istr != null)istr.close();
						if(ostr != null)ostr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			
		}
	}
	
	


}
