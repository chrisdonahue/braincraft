package com.cdo.visualizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class VisualizerActivity extends Activity {

	private static final String TAG = "Visualizer";
	private VisualizerView view;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

		view = new VisualizerView(this);
		//thread = view.getThread();
		
		setContentView(view);
	}

	protected void onPause() {
		super.onPause();
		//thread.onPause();
	}
}