package com.cdo.visualizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VisualizerView extends SurfaceView implements
		SurfaceHolder.Callback {

	private DrawThread thread;
	private boolean running;

	private static final String TAG = "Visualizer";

	private enum Mode {
		RUNNING, PAUSED, STOPPED, CONSTUCTED
	}

	public VisualizerView(Context context) {
		super(context);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		thread = new DrawThread(holder, context);

		setFocusable(true);
	}

	public DrawThread getThread() {
		return thread;
	}
	
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.onPause();
    }
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!running)
		{
			running = true;
			thread.setMode(Mode.RUNNING);
			thread.start();
		}
		else
		{
			thread.resume();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setMode(Mode.STOPPED);
		
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	class DrawThread extends Thread {
		private Mode mode;
		
		private Bitmap drawArea;
		private int canWidth = 100;
		private int canHeight = 100;
		
		private SurfaceHolder holder;
		private Context context;

		public DrawThread(SurfaceHolder sur, Context con) {
			mode = Mode.CONSTUCTED;
			holder = sur;
			context = con;
			drawArea = Bitmap.createBitmap(canWidth, canHeight, Config.ARGB_8888);
		}
		

		public void onDraw(Canvas canvas) {
			int red = (int) (Math.random() * 255);
			int blue = (int) (Math.random() * 255);
			int green = (int) (Math.random() * 255);
			canvas.drawColor(Color.rgb(red, green, blue));
		}

		// ACTION METHODS
		public void run() {
			Canvas c;
			while (mode == Mode.RUNNING) {
				c = null;
				try {
					c = holder.lockCanvas(null);
					synchronized (holder) {
						onDraw(c);
					}
				} finally {
					if (c != null) {
						holder.unlockCanvasAndPost(c);
					}
				}
			}
			synchronized (mode)
			{
				while (mode == Mode.PAUSED) {
					try {
						mode.wait();
					} catch (InterruptedException e)
					{
						Log.v(TAG, "interrupted wait");
					}
				}
			}
		}
		
		public void onPause() {
			synchronized (mode)
			{
				mode = Mode.PAUSED;
			}
		}
		
		public void onResume() {
			synchronized (mode)
			{
				mode = Mode.RUNNING;
				mode.notifyAll();
			}
		}

		// STATE METHODS
		public void setMode(Mode m)
		{
			synchronized(mode)
			{
				mode = m;
			}
		}

		// ACCESSORS AND MODIFIERS
		public void setSurfaceSize(int width, int height) {
			Log.v(TAG, width + ", " + height);
			synchronized (holder) {
				canWidth = width;
				canHeight = height;

				drawArea = Bitmap.createScaledBitmap(drawArea, width, height,
						true);
			}
		}
	}
}