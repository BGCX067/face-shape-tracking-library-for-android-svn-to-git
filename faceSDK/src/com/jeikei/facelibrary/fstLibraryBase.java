package com.jeikei.facelibrary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class fstLibraryBase extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    //private static final String TAG = "Sample::SurfaceView";

    private Camera              mCamera;
    private SurfaceHolder       mHolder;
    private int                 mFrameWidth;
    private int                 mFrameHeight;
    private int 				mPreviewWidth;
    private int					mPreviewHeight;
    private byte[]              mFrame;
    private boolean             mThreadRun;
    private byte[]              mBuffer;

    private static final String TAG = "faceSDK:ViewBase";
    private static final boolean D = true;
    
    public static final int BACK_CAMERA = 0;
    public static final int FRONT_CAMERA = 1;
    
    private int usingCameraIdx;

    public fstLibraryBase(Context context, int cIdx) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        
        usingCameraIdx = cIdx;
        if(D)  Log.d(TAG, "Instantiated new " + this.getClass());
    }
    public fstLibraryBase(Context context, AttributeSet attrs)
    {
    	super(context, attrs);
    	mHolder = getHolder();
        mHolder.addCallback(this);
        
        usingCameraIdx = FRONT_CAMERA;
        if(D)  Log.d(TAG, "Instantiated new " + this.getClass());
    }
    public fstLibraryBase(Context context, AttributeSet attrs, int defStyle)
    {
    	super(context, attrs, defStyle);
    	mHolder = getHolder();
        mHolder.addCallback(this);
        
        usingCameraIdx = FRONT_CAMERA;
        if(D)  Log.d(TAG, "Instantiated new " + this.getClass());
    }

    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }
    public void setPreviewWidth(int _width)
    {
    	mPreviewWidth = _width;
    	//if(D)  Log.d(TAG, "setFrameWidth : " + mFrameWidth);
    }
    public void setPreviewHeight(int _height)
    {
    	mPreviewHeight = _height;
    	//if(D)  Log.d(TAG, "setFrameHeight : " + mFrameHeight);
    }

    public void setPreview() throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mCamera.setPreviewTexture( new SurfaceTexture(10) );
        else
        	mCamera.setPreviewDisplay(null);
	}

    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        if(D)  Log.d(TAG, "surfaceChanged");
        if (mCamera != null) {
   	
            Camera.Parameters params = mCamera.getParameters();
            //mCamera.setDisplayOrientation(90);
            //params.setRotation(90);

            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            
            width = 640;
            height = 480;
            
            mFrameWidth = width;
            mFrameHeight = height;
            
            if(D)  Log.d(TAG, "width : " + width + ", height : " + height);
            // selecting optimal camera preview size
            
            {
                int  minDiff = Integer.MAX_VALUE;
                for (Camera.Size size : sizes) {
                	//if(D)  Log.d(TAG, "Sizewidth : " + size.width + ", Sizeheight : " + size.height);
                	
                    if ((Math.abs(size.height - height) + Math.abs(size.width - width)) < minDiff) {
                        mFrameWidth = size.width;
                        mFrameHeight = size.height;
                        minDiff = Math.abs(size.height - height) + Math.abs(size.width - width);
                    }
                }
            }
            mPreviewWidth = mFrameWidth;
            mPreviewHeight = mFrameHeight;
            if(D)  Log.d(TAG, "mFramewidth : " + mFrameWidth + ", mFrameheight : " + mFrameHeight);
            
            params.setPreviewSize(getFrameWidth(), getFrameHeight());

            List<String> FocusModes = params.getSupportedFocusModes();
            if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            {
            	params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            mCamera.setParameters(params);

            /* Now allocate the buffer */
            params = mCamera.getParameters();
            int size = params.getPreviewSize().width * params.getPreviewSize().height;
            size  = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
            mBuffer = new byte[size];
            /* The buffer where the current frame will be coppied */
            mFrame = new byte [size];
            mCamera.addCallbackBuffer(mBuffer);

			try {
				setPreview();
			} catch (IOException e) {
				Log.e(TAG, "mCamera.setPreviewDisplay/setPreviewTexture fails: " + e);
			}

            /* Notify that the preview is about to be started and deliver preview size */
            onPreviewStared(params.getPreviewSize().width, params.getPreviewSize().height);

            /* Now we can start a preview */
            mCamera.startPreview();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if(D)  Log.d(TAG, "surfaceCreated");
        if(mCamera == null)	mCamera = Camera.open( usingCameraIdx );		//prevent camera won't work. modified by JeiKei

        mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                synchronized (fstLibraryBase.this) {
                    System.arraycopy(data, 0, mFrame, 0, data.length);
                    fstLibraryBase.this.notify();
                }
                camera.addCallbackBuffer(mBuffer);
            }
        });
        (new Thread(this)).start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if(D)  Log.d(TAG, "surfaceDestroyed");
        mThreadRun = false;
        if (mCamera != null) {
            synchronized (this) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            }
        }
        onPreviewStopped();
    }

    /* The bitmap returned by this method shall be owned by the child and released in onPreviewStopped() */
    protected abstract Bitmap processFrame(byte[] data);

    /**
     * This method is called when the preview process is beeing started. It is called before the first frame delivered and processFrame is called
     * It is called with the width and height parameters of the preview process. It can be used to prepare the data needed during the frame processing.
     * @param previewWidth - the width of the preview frames that will be delivered via processFrame
     * @param previewHeight - the height of the preview frames that will be delivered via processFrame
     */
    protected abstract void onPreviewStared(int previewWidtd, int previewHeight);

    /**
     * This method is called when preview is stopped. When this method is called the preview stopped and all the processing of frames already completed.
     * If the Bitmap object returned via processFrame is cached - it is a good time to recycle it.
     * Any other resourcses used during the preview can be released.
     */
    protected abstract void onPreviewStopped();
    
    public byte[] bitmapToByteArray( Bitmap $bitmap ) {  
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
        $bitmap.compress( CompressFormat.PNG, 100, stream) ;  
        byte[] byteArray = stream.toByteArray() ;  
        return byteArray ;  
    }
    
    public void run() {
        mThreadRun = true;
        if(D)  Log.d(TAG, "Starting processing thread");
        while (mThreadRun) {
            Bitmap bmp = null;
            Bitmap resizedBmp = null;
            //Bitmap face_bmp = BitmapFactory.decodeResource(getResources(), org.opencv.samples.tutorial3.R.drawable.face_test);	//Added on 0706 by JeiKei
            //byte[] bmp2byte = bitmapToByteArray(face_bmp);
            //ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            
            
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 3;
            
            synchronized (this) {
                try {
                    this.wait();
                    /*
                    String tmp_str = "";
                    for(int i=250000; i<250100; i++)
        			{
        				tmp_str = tmp_str + String.valueOf( mFrame[i] );
        			}
                    if(D)  Log.d(TAG, tmp_str);
                    */
                    
                    bmp = processFrame(mFrame);
                    resizedBmp = Bitmap.createScaledBitmap(bmp, mPreviewWidth, mPreviewHeight, true);
                    //if(D)  Log.d(TAG, "bitmap width : " + mPreviewWidth+ ", height : " + mPreviewHeight);
                 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if (resizedBmp != null) {
                Canvas canvas = mHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawBitmap(resizedBmp, 0, 0, null);
                    		//(canvas.getWidth() - getFrameWidth()) / 2, (canvas.getHeight() - getFrameHeight()) / 2, null);
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}