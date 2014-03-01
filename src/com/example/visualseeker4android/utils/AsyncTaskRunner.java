package com.example.visualseeker4android.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;

public class AsyncTaskRunner {

	private static ExecutorService service = Executors.newSingleThreadExecutor();
	
	private AsyncTaskRunner(){}
	
	public static void execute(Task task){
		service.execute(new TaskRunnable(task));
	}
	
	public static void execute(Task task, OnFinishUiUpdateListener listener){
		service.execute(new TaskRunnable(task, listener));
	}
	
	private static class TaskRunnable implements Runnable {
		private final Task task;
		private final OnFinishUiUpdateListener listener;
		private final Handler handler;
		
		private Object task_result;

		private TaskRunnable(Task task){
			this.task = task;
			this.listener = null;
			this.handler = new Handler();
		}

		private TaskRunnable(Task task, OnFinishUiUpdateListener listener){
			this.task = task;
			this.listener = listener;
			this.handler = new Handler();
		}
		
		@Override
		public void run() {
			task_result = task.run();
			
			if( listener == null ) return;
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					listener.onFinish(task_result);
				}
			});
		}
	}
	
	public interface Task{
		public Object run();
	}
	
	public interface OnFinishUiUpdateListener {
		public void onFinish(Object result);
	}
}
