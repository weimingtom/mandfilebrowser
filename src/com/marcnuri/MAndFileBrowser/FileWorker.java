package com.marcnuri.MAndFileBrowser;

import android.app.ProgressDialog;
import android.os.Handler;

public abstract class FileWorker {
	private ProgressDialog progressDialog;
	private Runnable workerRunnable;
	private Exception workerException;
	private Handler futureTaskHandler;
	private Runnable futureTaskRunnable;
	public FileWorker (ProgressDialog progressDialog){
		this.progressDialog = progressDialog;
		workerException = null;
		futureTaskHandler = new Handler();
		workerRunnable = new Runnable() {
			
			public void run() {
				try {
					doInBackGround();
				} catch (Exception e) {
					FileWorker.this.workerException = e;
				}
				futureTaskHandler.post(futureTaskRunnable);
			}
		};
		futureTaskRunnable = new Runnable() {
			
			public void run() {
				done(FileWorker.this.workerException);
				FileWorker.this.progressDialog.dismiss();
			}
		};
	}
	
	public void execute(){
		new Thread(workerRunnable).start();
	}
	protected abstract void doInBackGround() throws Exception;
	protected abstract void done(Exception exception);
}
