package com.jeikei.facelibrary;

import android.util.Log;

/**
 * <b> Comment </b><br />
 *  - This class is related to face shape tracking.
 *  <br /><br />
 *  
 *  <b> Usage of creating object </b><br />
 *  - ShapeWrapper sWrapper = ShapeWrapper.getInstance();
 *
 */
public class ShapeWrapper {
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	
	private static ShapeWrapper sWrapper = new ShapeWrapper();
	public static final String TAG = "faceSDK:ShapeWrapper";
	private int frameWidth, frameHeight;
	
	private double [][]shape = new double[2][76];
	private double []faceLocation = new double[2];
	private double faceDistance;
	
	private long timeGap;
	private long startTime = -1;
	
	TimerInterpolator mInterpolator;
	
	ShapeTransform sTransform;
	
	private ShapeWrapper()
	{
		mInterpolator = new TimerInterpolator();
		
		for(int i=0; i<76; i++)
		{
			shape[AXIS_X][i] = -1;
			shape[AXIS_Y][i] = -1;
		}
		
		sTransform = new ShapeTransform();
	}
	
	/**
	 * @return
	 * Its own class. <br />
	 * Since it is only class, it is possible to be called at anywhere<br />
	 * and has same data of the shape.
	 */
	public static ShapeWrapper getInstance()
	{
		return sWrapper;
	}
	/**
	 * @param _frameWidth
	 * @param _frameHeight
	 * 
	 * <br /><br />
	 * <b>Warning</b>
	 * This method would not need to be called.
	 */
	public void initialise(int _frameWidth, int _frameHeight)
	{
		frameWidth = _frameWidth;
		frameHeight = _frameHeight;
	}

	/**
	 * <b>Note</b><br />
	 * This method needs to be called on <b>onPause()</b><br />
	 * Otherwise, threads would keep running after application is closed.
	 */
	public void release()
	{
		mInterpolator.close();
	}
	
	/**
	 * <b>Note</b><br />
	 * This method needs to be called on <b>onResume()</b><br />
	 * So that threads keep running after onPause()
	 */
	public void resume()
	{
		if(mInterpolator.getState() == TimerInterpolator.STATE_STOP)
		{
			mInterpolator.init();
		}
	}
	
	/**
	 * <b>Note</b><br />
	 * This method would not need to be called.
	 * 
	 * @param
	 * _shape - The original shape from Active Shape Model. <br />
	 * _shape[0] : X coordinates.<br />
	 * _shape[1] : Y coordinates.
	 */
	public void putShape(double[][] _shape)
	{
		shape = _shape.clone();
		
		sTransform.putNewShape(shape[AXIS_X], shape[AXIS_Y]);
		//checkTimeGap();
		mInterpolator.putValues(faceLocation[AXIS_X], faceLocation[AXIS_Y], faceDistance);
		
	}
	
	/**
	 * <b>Note</b><br />
	 * This method would not need to be called.
	 * 
	 * @param x_or_y
	 * <br />
	 *  - 0 : put x coordinate.<br />
	 *  - 1 : put y coordinate.<br />
	 * <br />
	 * @param idx : index of the coordinates.
	 * @param value : The value.
	 * 
	 */	
	public int putShape(int x_or_y, int idx, double value)
	{
		if(x_or_y > 2 || x_or_y < 0)	return -1;	//wrong value.
		if(idx >= 76 || idx < 0)		return -1;
		
		shape[x_or_y][idx] = value;
		
	//	Log.i(TAG, "value : " + value);
		if(idx == 75 && x_or_y == AXIS_Y)
		{
			mInterpolator.putValues(getFaceRelativeLocationX(), getFaceRelativeLocationY(), faceDistance);
			
			sTransform.putNewShape(shape[AXIS_X], shape[AXIS_Y]);
		}
			//checkTimeGap();
		
		return 1;
	}
	
	/**
	 * @return
	 * Currently, it will return <b>double[2][76]</b>. <br />
	 * <b>shape[0][x]</b> : x coordinates.<br />
	 * <b>shape[1][x]</b> : y coordinates.
	 */
	public double[][] getShape()
	{
		return shape;
	}
	
	/**
	 * @param x_or_y
	 * <br /> 0 : x coordinate
	 * <br /> 1 : y coordinate
	 * <br />
	 * @param idx : Index number of the coordinate.
	 * <br /><br />
	 * 
	 * @return
	 * -1 : When input parameter is wrong.<br />
	 * Otherwise it returns proper value.
	 */
	public double getShape(int x_or_y, int idx)
	{
		if(x_or_y > 2 || x_or_y < 0)	return -1;	//wrong value.
		if(idx >= 76 || idx < 0)		return -1;
		
		return shape[x_or_y][idx];
	}
	
	/**
	 * @return
	 * Normalized array of X coordinates.
	 * <br /><br />
	 * Its shape is symmetric, size normalized( scope is 0~1 ) and non-rotated.
	 */
	public double[] getNormalizedShapeX()
	{
		return sTransform.getXcoord();
	}
	/**
	 * @return
	 * Normalized array of Y coordinates.
	 * <br /><br />
	 * Its shape is symmetric, size normalized( scope is 0~1 ) and non-rotated.
	 */
	public double[] getNormalizedShapeY()
	{
		return sTransform.getYcoord();
	}
	
	/**
	 * 
	 * @param fullSize
	 * <br /> - Maximum size of the face.
	 * @param locationX
	 * <br /> - The location of the coordinates that the face shape will be placed.
	 *  <br />
	 * @return
	 * Its shape is symmetric, size normalized( scope is 0~1 ) and non-rotated.<br />
	 * float[76]
	 */
	public float[] getNormalizedShapeX(int fullSize, int locationX)
	{		
		float[] tmpValue = new float[76];
		
		double[] normedValueX = sTransform.getXcoord();
		//double[] normedValueY = sTransform.getYcoord();
		
		for(int i=0; i<76; i++)
		{
			tmpValue[i] = (float)normedValueX[i] * ((float)this.getFaceDistance() * fullSize) + locationX;
		}
		
		return tmpValue;
	}
	/**
	 * 
	 * @param fullSize
	 * <br /> - Maximum size of the face.
	 * @param locationY
	 * <br /> - The location of the coordinates that the face shape will be placed.
	 *  <br />
	 * @return
	 * Its shape is symmetric, size normalized( scope is 0~1 ) and non-rotated.<br />
	 * float[76]
	 */
	public float[] getNormalizedShapeY(int fullSize, int locationX)
	{		
		float[] tmpValue = new float[76];
		
		double[] normedValueY = sTransform.getYcoord();
		//double[] normedValueY = sTransform.getYcoord();
		
		for(int i=0; i<76; i++)
		{
			tmpValue[i] = (float)normedValueY[i] * ((float)this.getFaceDistance() * fullSize) + locationX;
		}
		
		return tmpValue;
	}
	
	
	/**
	 * @return
	 * Return the relative axis of x.
	 * return range is [-1 ~ 1]
	 */
	public double getFaceRelativeLocationX()
	{
		double tmp;
		
		computeFaceLocation();
		
		tmp = faceLocation[AXIS_X] / (double)frameWidth;
		tmp -= 0.5;
		tmp *= 2;
		
		return tmp;
	}
	
	/**
	 * @return
	 * Return the relative axis of y.
	 * return range is [-1 ~ 1]
	 */
	public double getFaceRelativeLocationY()
	{
		double tmp;
		
		computeFaceLocation();
		
		tmp = faceLocation[AXIS_Y] / (double)frameHeight;
		tmp -= 0.5;
		tmp *= -2;
		
		//Log.i(TAG, "frameHeight : " + frameHeight +", faceLocation_Y : "+ faceLocation[AXIS_Y] +  ", tmp : " + tmp);
		
		return tmp;
	}
	
	/**
	 * @return
	 * Return the size of the face.
	 * return range is [0 ~ 1]
	 */
	public double getFaceDistance()
	{
		
		if(computeFaceLocation() == -1)
			return -1;
		else return faceDistance;
		
		//Log.i(TAG, "axisX1 : " + shape[AXIS_Y][7] + ", axisX2 : " + shape[AXIS_Y][15] + ", result : " + tmp);
	}

	/**
	 * @return
	 * no longer used.
	 */
	public double getEstimatedFaceLocationX()
	{
		return mInterpolator.getEstimatedValue(TimerInterpolator.IDX_FACE_LOCATION_X);
	}
	/**
	 * @return
	 * no longer used.
	 */
	public double getEstimatedFaceLocationY()
	{
		return mInterpolator.getEstimatedValue(TimerInterpolator.IDX_FACE_LOCATION_Y);
	}
	/**
	 * @return
	 * no longer used.
	 */
	public double getEstimatedFaceDistance()
	{
		return mInterpolator.getEstimatedValue(TimerInterpolator.IDX_FACE_DISTANCE);
	}
	
	/**
	 * @return
	 * Time gap between two shapes of a face are tracked.
	 */
	public double getTimeGap()
	{
		//Log.i(TAG, "timeGap = " + timeGap );
		//return timeGap;
		return mInterpolator.getTimeGap();
	}
	
	private int computeFaceLocation()
	{
		//Log.i(TAG, "timeGap = " + computeTimeRatio() );
		 //computeTimeRatio();
		 
		if(shape[AXIS_X][67] == -1 || shape[AXIS_Y][67] == -1)	return -1;
		
		faceLocation[AXIS_X] = (double)shape[AXIS_X][67];
		faceLocation[AXIS_Y] = (double)shape[AXIS_Y][67];
		
		//Log.i(TAG, "shape_y : " + faceLocation[AXIS_Y]);
		
		faceDistance = ( (shape[AXIS_X][14]-shape[AXIS_X][0]) / frameWidth ) * 0.5 + 
				( (shape[AXIS_Y][7]-shape[AXIS_Y][15]) / frameWidth ) * 0.5;
		
		return 1;
	}
}
