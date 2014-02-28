package com.example.visualseeker4android.imagesearch;

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

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.xml.AsyncXMLParser;
import com.example.visualseeker4android.xml.AsyncXMLParser.OnReturnResultListener;
import com.example.visualseeker4android.xml.VisualSeekerResult;
import com.example.visualseeker4android.xml.XMLParser;
import com.example.visualseeker4android.xml.XMLRequest;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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
	Bitmap[] bitmaps = new Bitmap[imageview_id.length];
	ImageView[] imageViews = null;
	String[] ids = null;
	AsyncXMLParser asyncParser = null;
	
	Animation[] anim_open_result = null;
	Animation[] anim_close_result = null;
	
	Animation anim_on_touch_down = null;

    List<VisualSeekerResult> result_list = null;

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
			String query_url = XMLParser.SEARCH_URL_BASE+ids[index];
			asyncParser.execute(query_url);
			
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

	private class UiUpdateListener implements OnReturnResultListener {
		ExecutorService service = Executors.newFixedThreadPool(2);
		public UiUpdateListener() {
		}
		
		@Override
		public void onReturnResult(List<VisualSeekerResult> result) {

            result_list = result;

			Log.d("onReturn","return result");
			for( int i=0; i<result.size(); i++){
				String id = result.get(i).getId();
				ids[i] = id;

				String url = result.get(i).getUrl();
				service.execute(new RequestImageURL(url, i) );
				//TODO:すべてのスレッドが終了したらViewを更新するスレッドを作る
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		}
	}
	
	


}
