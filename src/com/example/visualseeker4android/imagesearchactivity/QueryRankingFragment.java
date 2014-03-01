package com.example.visualseeker4android.imagesearchactivity;

import java.util.ArrayList;
import java.util.List;

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.utils.AsyncTaskRunner;
import com.example.visualseeker4android.xml.KeyWardRankingXMLParser;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QueryRankingFragment extends Fragment {

	List<TextView> itemViews = new ArrayList<TextView>();
	LinearLayout even_layout = null;
	LinearLayout odd_layout = null;
	LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_query_ranking,
				container, false);

		even_layout = (LinearLayout) view.findViewById(R.id.evenLayout);
		odd_layout = (LinearLayout) view.findViewById(R.id.oddLayout);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		AsyncTaskRunner.execute(new Task(), new UiUpdateListener());
	}
	
	private class Task implements AsyncTaskRunner.Task{
		@Override
		public Object run() {
			return KeyWardRankingXMLParser.getRequest();
		}
	}
	
	private class UiUpdateListener implements AsyncTaskRunner.OnFinishUiUpdateListener {
		@Override
		public void onFinish(Object result) {
			List<String> result_list = (List<String>)result;
			int result_size = result_list.size();
			int view_size = itemViews.size();

			if( view_size < result_size ){
				for( int i = view_size-1; i<result_size; i++ ){
					itemViews.add(new TextView(getActivity()));
					if( i % 2 == 0 ){
						even_layout.addView(itemViews.get(i), layout_params);
					}
					else {
						odd_layout.addView(itemViews.get(i), layout_params);
					}
				}
			}

			for( int i=0; i<itemViews.size(); i++ ){
			itemViews.get(i).setText(result_list.get(i));
		}
		}
	}

}
