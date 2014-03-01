package com.example.visualseeker4android.imagesearchactivity;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.utils.asynctask.SaveImage;
import com.example.visualseeker4android.xml.SearchResultContainer;

/**
 * Created by u-ta on 14/02/28.
 */
public class ResultItemDialogFragment extends DialogFragment {

	int index;
	Bitmap bitmap;
	SearchResultContainer result;

	private static File save_dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

	public ResultItemDialogFragment() {
	}

	public void setResult(int index, SearchResultContainer result, Bitmap bitmap) {
		this.bitmap = bitmap;
		this.result = result;
		this.index = index;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_result_item, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
		imageView.setImageBitmap(bitmap);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(result.getTitle());
		TextView tagName = (TextView) view.findViewById(R.id.tagName);
		tagName.setText(result.getTag());

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);
		builder.setNegativeButton("閉じる", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

			}
		});
		builder.setNeutralButton("検索", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SearchResultFragment fragment = (SearchResultFragment) getActivity()
						.getFragmentManager().findFragmentById(
								R.id.search_result);
				fragment.searchAndUpdateUi(index);
			}
		});
		builder.setPositiveButton("保存", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String url = result.getUrl();
				String filename = url.substring(url.lastIndexOf('/') + 1);
				File save_file = new File(save_dir, filename);

				SaveImage.saveImage(bitmap, save_file, getActivity());
			}
		});

		return builder.create();
	}
}
