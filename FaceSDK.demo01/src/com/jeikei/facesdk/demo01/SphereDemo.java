package com.jeikei.facesdk.demo01;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.jeikei.facelibrary.ShapeWrapper;
import com.jeikei.facelibrary.fstLibrary;
import com.jeikei.facelibrary.fstLibraryBase;

public class SphereDemo extends Activity{

	fstLibrary face_sdkView;
	ShapeWrapper sWrapper;
	
	FrameLayout faceSDK_layout;
	FrameLayout mainFrameLayout;
	
	String TAG = "faceSDK:demo02";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sphere_activity_layout);
		
		face_sdkView = new fstLibrary(this, fstLibraryBase.FRONT_CAMERA);
		faceSDK_layout = (FrameLayout)findViewById(R.id.demo02_faceSDKView);
		mainFrameLayout  = (FrameLayout)findViewById(R.id.demo02_frameLayout);
		
		faceSDK_layout.addView(face_sdkView);
		mainFrameLayout.addView(new rectangleSurface(this));
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		sWrapper.release();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		sWrapper = ShapeWrapper.getInstance();
		sWrapper.resume();
	}


	class rectangleSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable
	{
		private int mWidth, mHeight;
		
		private Bitmap img;
		private Bitmap src;
		private Paint mPaint;
		
		private SurfaceHolder mSurfaceHolder;
		private Thread mThread;
		private boolean isThreadRunning;
		
		private int x, y;
		
		public rectangleSurface(Context context) {
			super(context);
			
			mPaint = new Paint();
			src = BitmapFactory.decodeResource(this.getResources(), R.drawable.yellow_sphere);
			
			mSurfaceHolder = getHolder();
			mSurfaceHolder.addCallback(this);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mThread = new Thread(this);
			mThread.start();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
			mWidth = getWidth();
			mHeight = getHeight();
			
			x = mWidth / 2;
			y = mHeight / 2;	
		}
		
		public void surfaceDestroyed(SurfaceHolder holder) {
			holder.removeCallback(this);
			isThreadRunning = false;
		}
		
		public void run() {
			Canvas c = null;
			isThreadRunning = true;
			
			while(isThreadRunning)
			{
				try{
					c = mSurfaceHolder.lockCanvas();
					
					synchronized (mSurfaceHolder) {
						doDraw(c);
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(c !=null)
					{
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
				
			} //while
		}// run()
		
		private void doDraw(Canvas c)
		{
			//Log.i(TAG, "x : " + x + ", y : " + y + ", width : " + mWidth + ", height : " + mHeight);
			//Log.i(TAG, "bitmapWidth : " + src.getWidth() + ", bitmapHeight : " + src.getHeight() + ", pixel : " + src.getPixel(30, 30));
			//img = Bitmap.createBitmap(src, x, y, mWidth, mHeight);
			
			//int coordX = x + (int)(x*-sWrapper.getEstimatedFaceLocationX());
			//int coordY = y + (int)(y*-sWrapper.getEstimatedFaceLocationY());
			
			int coordX = x + (int)(-x*sWrapper.getFaceRelativeLocationX());
			int coordY = y + (int)(-y*sWrapper.getFaceRelativeLocationY());
			
			c.drawColor(Color.WHITE);
			
			//drawFace(c);
			c.drawBitmap(src, coordX, coordY, mPaint);
		}
		
		private void drawFace(Canvas c)
		{
			int coordX = x + (int)(-x*sWrapper.getFaceRelativeLocationX());
			int coordY = y + (int)(-y*sWrapper.getFaceRelativeLocationY());
			
			float[] newX = sWrapper.getNormalizedShapeX(500, coordX);
			float[] newY = sWrapper.getNormalizedShapeY(500, coordY);

			for(int i=0; i<75; i++)
			{
				mPaint.setColor(Color.BLACK);
				mPaint.setStrokeWidth(2);
				c.drawLine(newX[i], newY[i], newX[i+1], newY[i+1], mPaint);
			}
		}
		
	} //rectangSurface Class

}
