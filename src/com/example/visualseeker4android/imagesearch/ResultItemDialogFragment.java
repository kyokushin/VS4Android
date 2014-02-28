package com.example.visualseeker4android.imagesearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.xml.VisualSeekerResult;

/**
 * Created by u-ta on 14/02/28.
 */
public class ResultItemDialogFragment extends DialogFragment {

	int index;
	Bitmap bitmap;
	VisualSeekerResult result;

	public ResultItemDialogFragment() {
	}

	public void setResult(int index, VisualSeekerResult result, Bitmap bitmap) {
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
				Toast.makeText(getActivity(), "まだ実装されてません", Toast.LENGTH_LONG)
						.show();
			}
		});

		return builder.create();
	}
}
