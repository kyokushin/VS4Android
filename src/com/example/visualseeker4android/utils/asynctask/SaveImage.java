package com.example.visualseeker4android.utils.asynctask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.utils.AsyncTaskRunner;
import com.example.visualseeker4android.utils.AsyncTaskRunner.OnFinishUiUpdateListener;
import com.example.visualseeker4android.utils.AsyncTaskRunner.Task;

public class SaveImage {

	public static void saveImage(Bitmap bitmap, File file, Activity activity) {
		AsyncTaskRunner.execute(new Task(bitmap, file), new UiUpdateListener(activity));
	}

	private enum FileState {
		NOT_STARTED, SUCCESS, FILE_NOT_FOUND, IO_EXCEPTION
	}

	private static class Task implements AsyncTaskRunner.Task {

		final Bitmap bitmap;
		final File file;

		public Task(Bitmap bitmap, File file) {
			this.bitmap = bitmap;
			this.file = file;
		}

		FileState state;

		@Override
		public Object run() {

			state = FileState.NOT_STARTED;
			try {
				OutputStream ostr = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostr);
				ostr.close();

				state = FileState.SUCCESS;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				state = FileState.FILE_NOT_FOUND;
			} catch (IOException e) {
				e.printStackTrace();
				state = FileState.IO_EXCEPTION;
			}

			return state;
		}
	}

	private static class UiUpdateListener implements
			AsyncTaskRunner.OnFinishUiUpdateListener {
		private final Activity activity;

		public UiUpdateListener(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onFinish(Object result) {
			FileState state = (FileState) result;

			Notification.Builder builder = new Notification.Builder(activity);
			builder.setContentTitle("画像のダウンロード");
			builder.setSmallIcon(R.drawable.nagara_app);
			switch (state) {
			case FILE_NOT_FOUND:
				builder.setTicker("保存先がありません！");
				builder.setContentText("保存先がありません！");
				break;
			case IO_EXCEPTION:
				builder.setTicker("保存に失敗しました！");
				builder.setContentText("保存に失敗しました！");
				break;
			case NOT_STARTED:
				builder.setTicker("なぜか失敗！");
				builder.setContentText("なぜか失敗！");
				break;
			case SUCCESS:
				builder.setTicker("保存しました！");
				builder.setContentText("保存しました！");
				break;
			}

			NotificationManager manager = (NotificationManager) activity
					.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(Integer.valueOf(0), builder.getNotification());
		}
	};

}
