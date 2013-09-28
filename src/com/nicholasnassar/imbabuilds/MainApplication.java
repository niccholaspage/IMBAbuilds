package com.nicholasnassar.imbabuilds;

import android.app.Application;

public class MainApplication extends Application {
	private DataRetrieverTask currentTask = null;
	
	public void setCurrentTask(DataRetrieverTask currentTask){
		if (this.currentTask != null){
			this.currentTask.cancel(true);
		}
		
		this.currentTask = currentTask;
	}
	
	public DataRetrieverTask getCurrentTask(){
		return currentTask;
	}
}
