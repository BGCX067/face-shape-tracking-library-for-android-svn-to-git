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

public class PortraitDemo extends Activity{

	fstLibrary face_sdkView;
	ShapeWrapper sWrapper;
	
	FrameLayout faceSDK_layout;
	FrameLayout mainFrameLayout;
	
	String TAG = "faceSDK:demo03";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.portrait_activity_layout);
		
		face_sdkView = new fstLibrary(this, fstLibraryBase.FRONT_CAMERA);
		faceSDK_layout = (FrameLayout)findViewById(R.id.demo03_faceSDKView);
		mainFrameLayout  = (FrameLayout)findViewById(R.id.demo03_frameLayout);
		
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
			
			drawFace(c);
			//c.drawBitmap(src, coordX, coordY, mPaint);
		}
		
		private void drawFace(Canvas c)
		{
			int coordX = x + (int)(-x*sWrapper.getFaceRelativeLocationX());
			int coordY = y + (int)(-y*sWrapper.getFaceRelativeLocationY());
			
			float[] newX = sWrapper.getNormalizedShapeX(500, coordX);
			float[] newY = sWrapper.getNormalizedShapeY(500, coordY);

			mPaint.setStrokeWidth(2);
			mPaint.setColor(0xffaa8822);
			draw_shape_line(c, newX, newY, 0, 14, false, mPaint);	//Face outline
			mPaint.setColor(Color.BLACK);
			draw_shape_line(c, newX, newY, 37, 45, false, mPaint);	//nose
			draw_shape_line(c, newX, newY, 15, 20, true, mPaint);	//left eyebrow
			draw_shape_line(c, newX, newY, 21, 26, true, mPaint);	//right eyebrow
			draw_shape_line(c, newX, newY, 32, 35, true, mPaint);	//left eye
			draw_shape_line(c, newX, newY, 27, 30, true, mPaint);	//right eye
			mPaint.setColor(Color.RED);
			draw_shape_line(c, newX, newY, 60, 65, true, mPaint);	//inner lip
			draw_shape_line(c, newX, newY, 48, 59, true, mPaint);	//outer lip
			mPaint.setColor(Color.BLACK);
			c.drawCircle(newX[31], newY[31], (float) 4, mPaint);
			c.drawCircle(newX[36], newY[36], (float) 4, mPaint);
			
			/*
			for(int i=0; i<75; i++)
			{
				mPaint.setColor(Color.BLACK);
				c.drawLine(newX[i], newY[i], newX[i+1], newY[i+1], mPaint);
			}
			*/
		}
		
		void draw_shape_line(Canvas c, float[] _x, float[] _y, int S_POINT, int E_POINT, boolean isCloseDraw, Paint _mPaint)
		{
			for(int i=S_POINT; i<E_POINT; i++)	c.drawLine(_x[i], _y[i], _x[i+1], _y[i+1], mPaint);
			if(isCloseDraw)	c.drawLine(_x[E_POINT], _y[E_POINT], _x[S_POINT], _y[S_POINT], mPaint);
		}
	} //rectangSurface Class

}
