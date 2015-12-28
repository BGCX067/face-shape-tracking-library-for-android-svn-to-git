package com.jeikei.facesdk.demo01;

import android.graphics.Bitmap;
import android.util.Log;

public class InvaderAndroid {
	
	private static final String TAG = "faceSDK:demo04:invader";
	
	public static final int STATE_NOTHING = 0;
	public static final int STATE_FALLING = 1;
	public static final int STATE_FALLEN = 2;

	private int[] color = {0, 0, 0};
	private int mSpeed;
	private Bitmap mImg;
	
	private long startTime;
	private int mWidth, mHeight;
	private int mState = STATE_NOTHING;
	private int mX, mY;
	private int androidWidth, androidHeight;
	
	public InvaderAndroid(Bitmap img, int _width, int _height)
	{
		androidWidth = (int)(Math.random()*200) + 50;
		androidHeight = (int)(Math.random()*200) + 50;
		int androidFallingTime = (int)(Math.random()*2000) + 1000;
		
		mImg = Bitmap.createScaledBitmap(img, androidWidth, androidHeight, true);
		mSpeed = androidFallingTime;
		mWidth = _width;
		mHeight = _height;

		mX = (int)(Math.random()*mWidth-androidWidth) +androidWidth;
		//mY = (int)(Math.random()*mHeight-imgHeight) +imgHeight;
	}
	public InvaderAndroid(Bitmap img, int imgWidth, int imgHeight, int speedMill, int _width, int _height)
	{
		mImg = Bitmap.createScaledBitmap(img, imgWidth, imgHeight, true);
		mSpeed = speedMill;
		mWidth = _width;
		mHeight = _height;
		
		mX = (int)(Math.random()*mWidth-imgWidth) +imgWidth;
		//mY = (int)(Math.random()*mHeight-imgHeight) +imgHeight;
	}
	public InvaderAndroid(Bitmap img, int[] _color, int imgWidth, int imgHeight, int speedMill, int _width, int _height)
	{
		if(this.color.length != 3)
		{
			this.color[0] = 30;
			this.color[1] = 200;
			this.color[2] = 30;
		}
		else
		{
			this.color[0] = _color[0];
			this.color[1] = _color[1];
			this.color[2] = _color[2];
		}
		
		mImg = Bitmap.createScaledBitmap(img, imgWidth, imgHeight, true);
		mSpeed = speedMill;
		mWidth = _width;
		mHeight = _height;
	}
	
	public Bitmap getImage()
	{
		return mImg;
	}
	public void startFalling()
	{
		startTime = System.currentTimeMillis();
		mState = STATE_FALLING;
	}
	public double getProgressRatio()
	{
		double progressRatio = (double)((double)(System.currentTimeMillis()-startTime) / (double)mSpeed);
		if(progressRatio > 1){
			progressRatio = 1;
			mState = STATE_FALLEN;
		}
		else if(progressRatio < 0)
			progressRatio = 0;
		
		return progressRatio;
	}
	public boolean isHit(int _x, int _y)
	{
		mY = (int)(getProgressRatio() * (double)mHeight);
		
//		Log.d(TAG, "droid x : " + mX+" to " + (mX+androidWidth) + ", droid y : " + mY + " to " + (mY+androidHeight) +"\n" +
//					"ball x : " + _x + " , ball y : " + _y);
		if((_x > mX) && (_x < (mX+androidWidth)) &&
		   (_y > mY) && (_y < (mY+androidHeight)) )
		{
			return true;
		}
		
		return false;
	}
	public int getCurrentLocationX()
	{
		return mX;
		//return (int)(getProgressRatio() * (double)mWidth);
	}
	public int getCurrentLocationY()
	{
		mY = (int)(getProgressRatio() * (double)mHeight);
		return mY;
	}
	public int getState()
	{
		return mState;
	}
	public void uninitialize()
	{
		mImg.recycle();
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
