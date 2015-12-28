package com.jeikei.facelibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;

//import android.util.Log;

public class fstLibrary extends fstLibraryBase {
	
	private int mFrameSize;
	private Bitmap mBitmap;
	private int[] mRGBA;
	ShapeWrapper sWrapper = ShapeWrapper.getInstance();
	
	
	private final static String TAG = "faceSDK:View";
	private final static Boolean D = true;
	
	/**
	 * 
	 * @param context - android.content.Context
	 * @param cIdx
	 * <br />
	 * 			- Camera Index<br />
	 * 			- fstLibraryBase.BACK_CAMERA  - (0) <br />
	 * 			- fstLibraryBase.FRONT_CAMERA - (1)
	 * 
	 * <br /><br />
	 * @see
	 * <b>Usage</b><br />
	 *  fstLibrary mFstLibrary = fstLibrary(this, fstLibraryBase.FRONT_CAMERA);<br />
	 *  frameLayout.addView(mFstLibrary);   Or<br />
	 *  setContentView(mFstLibrary);
	 *  
	 */
	
    public fstLibrary(Context context, int cIdx) {
        super(context, cIdx);
        
        init(context);
    }
    
    private void init(Context context)
    {
    	try {
            InputStream is = context.getResources().openRawResource(com.jeikei.facelibrary.R.raw.haarcascade_frontalface_alt);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            
            
            InputStream is2 = context.getResources().openRawResource(com.jeikei.facelibrary.R.raw.muct76);
            File cascadeDir2 = context.getDir("muct76", Context.MODE_PRIVATE);
            File cascadeFile2 = new File(cascadeDir2, "muct76.model");
            FileOutputStream os2 = new FileOutputStream(cascadeFile2);
            /*
            if(D)  Log.d("TAG", String.valueOf(cascadeFile2.length()));
            if(!cascadeFile2.canRead()){
            	if(D)  Log.d("TAG", "yomenaiyo!!!");
            }

            is2.close();
            */
            
            byte[] buffer2 = new byte[4096];
            int bytesRead2;
            while ((bytesRead2 = is2.read(buffer2)) != -1) {
                os2.write(buffer2, 0, bytesRead2);
            }
            is2.close();
            os2.close();
            
            //if(D)  Log.d("TAG", "01Loaded cascade classifier from " + cascadeFile.getAbsolutePath());
            readASMModel(cascadeFile2.getAbsolutePath(), cascadeFile.getAbsolutePath());
            //if(D)  Log.d("TAG", "02Loaded cascade classifier from " + cascadeFile2.getAbsolutePath());
            
            cascadeFile.delete();
            cascadeDir.delete();
            
            cascadeFile2.delete();
            cascadeDir2.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void onResume()
    {
    	surfaceCreated(getHolder());
    }
    public void onPause()
    {
    	surfaceDestroyed(getHolder());
    }

	@Override
	protected void onPreviewStared(int previewWidtd, int previewHeight) {
		mFrameSize = previewWidtd * previewHeight;
		mRGBA = new int[mFrameSize];
		mBitmap = Bitmap.createBitmap(previewWidtd, previewHeight, Bitmap.Config.ARGB_8888);
		
		if(D)  Log.d(TAG, "width : " + getFrameWidth() + ", height : " + getFrameHeight());
		sWrapper.initialise(getFrameWidth(), getFrameHeight());
	}

	@Override
	protected void onPreviewStopped() {
		if(mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		mRGBA = null;
	}

    @Override
    protected Bitmap processFrame(byte[] data) {
        int[] rgba = mRGBA;

        long start = System.currentTimeMillis();
        if( FindFeatures(getFrameWidth(), getFrameHeight(), data, rgba) )      getShapeFromNative();
        long end = System.currentTimeMillis();
       //if(D)  Log.d(TAG, "runTime : " + (end - start) / 1000.0);
        
        Bitmap bmp = mBitmap; 
        bmp.setPixels(rgba, 0/* offset */, getFrameWidth() /* stride */, 0, 0, getFrameWidth(), getFrameHeight());
        
        return bmp;
    }
    
    private void getShapeFromNative()
    {
    	for(int i=0; i<76; i++) 
    	{
    		sWrapper.putShape(0, i, (double)getShape(0, i) );
    		sWrapper.putShape(1, i, (double)getShape(1, i) );
    	}
    	
    	if(D)  Log.d(ShapeWrapper.TAG, "LocationX : " + sWrapper.getFaceRelativeLocationX() );
    	if(D)  Log.d(ShapeWrapper.TAG, "LocationY : " + sWrapper.getFaceRelativeLocationY() );
    	if(D)  Log.d(ShapeWrapper.TAG, "Distance : " + sWrapper.getFaceDistance() );
    }

    public native boolean FindFeatures(int width, int height, byte yuv[], int[] rgba);
    public native void readASMModel(String c, String d);
    public native int getShape(int x_or_y, int idx);
    
    static {
        System.loadLibrary("native_sample");
    }
}
