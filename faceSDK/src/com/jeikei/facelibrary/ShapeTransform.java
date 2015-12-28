package com.jeikei.facelibrary;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class ShapeTransform {
	static private double MODEL_RATIO = 1.6; 
	private double modelShapeX[] = {-1, -0.960632362, -0.892436454, -0.770303782, -0.603533788, -0.398636082, -0.184748915, 0, 0.184748915, 0.398636082, 0.603533788, 0.770303782, 0.892436454, 0.960632362, 1, 0.758214507, 0, 0, 0.326720397, 0, 0, -0.758214507, 0, 0, -0.326720397, 0, 0, -0.706447613, -0.462182269, -0.293552387, -0.482951023, -0.474891507, 0.706447613, 0.462182269, 0.293552387, 0.482951023, 0.474891507, -0.098884067, -0.160260384, -0.27216367, -0.240545567, 0, 0.240545567, 0.27216367, 0.160260384, 0.098884067, -0.122442653, 0.122442653, -0.321450713, -0.17265964, -0.05300682, 0, 0.05300682, 0.17265964, 0.321450713, 0.204587725, 0.090204588, 0, -0.090204588, -0.204587725, -0.164600124, 0, 0.164600124, 0.157470552, 0, -0.157470552, 0, 0, -0.651580905, -0.318040918, -0.353688779, -0.6416615, 0.651580905, 0.318040918, 0.353688779, 0.6416615};
	private double modelShapeY[] = {-0.082145071, -0.478611283, -0.775883447, -1.155610663, -1.335089895, -1.448233106, -1.561376317, -1.600743955, -1.561376317, -1.448233106, -1.335089895, -1.155610663, -0.775883447, -0.478611283, -0.082145071, 0.283013019, 0, 0, 0.314941104, 0, 0, 0.283013019, 0, 0, 0.314941104, 0, 0, -0.059516429, 0.104773714, -0.079355239, -0.153440794, -0.055796652, -0.059516429, 0.104773714, -0.079355239, -0.153440794, -0.055796652, 0, -0.445443273, -0.562306262, -0.661810291, -0.722876627, -0.661810291, -0.562306262, -0.445443273, 0, -0.694978301, -0.694978301, -1.034407936, -0.940793552, -0.908245505, -0.93149411, -0.908245505, -0.940793552, -1.034407936, -1.115313081, -1.143521389, -1.158090515, -1.143521389, -1.115313081, -1.047737136, -1.032548047, -1.047737136, -1.02882827, -1.024178549, -1.02882827, 0, -0.522318661, 0.070675759, 0.024178549, -0.120272784, -0.134531928, 0.070675759, 0.024178549, -0.120272784, -0.134531928};
	
	private double x[], y[], z[];
	
	private double c_eye[] = new double[2];
	private double p_upper[] = new double[2];
	private double c_faceX;
	
	private double minMaxX[] = new double[2];
	private double minMaxY[] = new double[2];
	
	private double faceRatio;
	
	private String str_idx = new String();
	
	public ShapeTransform() {
	}
	
	ShapeTransform(int c_x[], int c_y[])
	{
		str_idx = "0";
		
		putNewShape(c_x, c_y);
	}
	
	ShapeTransform(int c_x[], int c_y[], String key)
	{
		str_idx = key;
		putNewShape(c_x, c_y);
	}
	
	private double[] getInitModelDataX()
	{
		double[] modelDataX = {-1, -0.960632362, -0.892436454, -0.770303782, -0.603533788, -0.398636082, -0.184748915, 0, 0.184748915, 0.398636082, 0.603533788, 0.770303782, 0.892436454, 0.960632362, 1, 0.758214507, 0, 0, 0.326720397, 0, 0, -0.758214507, 0, 0, -0.326720397, 0, 0, -0.706447613, -0.462182269, -0.293552387, -0.482951023, -0.474891507, 0.706447613, 0.462182269, 0.293552387, 0.482951023, 0.474891507, -0.098884067, -0.160260384, -0.27216367, -0.240545567, 0, 0.240545567, 0.27216367, 0.160260384, 0.098884067, -0.122442653, 0.122442653, -0.321450713, -0.17265964, -0.05300682, 0, 0.05300682, 0.17265964, 0.321450713, 0.204587725, 0.090204588, 0, -0.090204588, -0.204587725, -0.164600124, 0, 0.164600124, 0.157470552, 0, -0.157470552, 0, 0, -0.651580905, -0.318040918, -0.353688779, -0.6416615, 0.651580905, 0.318040918, 0.353688779, 0.6416615};
		return modelDataX;
	}
	private double[] getInitModelDataY()
	{
		double[] modelShapeY = {-0.082145071, -0.478611283, -0.775883447, -1.155610663, -1.335089895, -1.448233106, -1.561376317, -1.600743955, -1.561376317, -1.448233106, -1.335089895, -1.155610663, -0.775883447, -0.478611283, -0.082145071, 0.283013019, 0, 0, 0.314941104, 0, 0, 0.283013019, 0, 0, 0.314941104, 0, 0, -0.059516429, 0.104773714, -0.079355239, -0.153440794, -0.055796652, -0.059516429, 0.104773714, -0.079355239, -0.153440794, -0.055796652, 0, -0.445443273, -0.562306262, -0.661810291, -0.722876627, -0.661810291, -0.562306262, -0.445443273, 0, -0.694978301, -0.694978301, -1.034407936, -0.940793552, -0.908245505, -0.93149411, -0.908245505, -0.940793552, -1.034407936, -1.115313081, -1.143521389, -1.158090515, -1.143521389, -1.115313081, -1.047737136, -1.032548047, -1.047737136, -1.02882827, -1.024178549, -1.02882827, 0, -0.522318661, 0.070675759, 0.024178549, -0.120272784, -0.134531928, 0.070675759, 0.024178549, -0.120272784, -0.134531928};
		return modelShapeY;
	}
	public void putNewShape(int c_x[], int c_y[])
	{
		x = null;
		y = null;
		
		x = integerArrToDoubleArr(c_x).clone();
		y = integerArrToDoubleArr(c_y).clone();
		
		processAll();
	}
	
	public void putNewShape(double c_x[], double c_y[])
	{
		x = null;
		y = null;
		
		x = c_x.clone();
		y = c_y.clone();
		
		processAll();
	}
	
	private void processAll()
	{
		modelShapeX = getInitModelDataX().clone();
		modelShapeY = getInitModelDataY().clone();
		
		faceRatio = getFaceRatio();
		
		normalizeShape();
		
		//shutMouthUp();		
		completeModelShape();
		centerLineSymmetry();
		applyModelShapeToFace(0.3);
		//rescaleEyes(1.1, 1.2);
		//rescaleNose(0.8, 0.8);
		
		minMaxX = getMinMax(x);
		minMaxY = getMinMax(y);
		
		//printLogData();
		//mShape = new ModelShape();
		//mShape.compareShape(x, y);
		
		//////////////////// ���ؼ� ���õ� ��ǥ�� ����.
		//x = mShape.getSelectedShapeX();
		//y = mShape.getSelectedShapeY();
		
		
	}
	
	public void normalizeShape()
	{
		normScale();
		degreeNorm();
		centerTransform();
		normScale();
	}
	public double[] getMinAxis()
	{
		double tmp[] = new double[2];
		
		tmp[0] = minMaxX[0];
		tmp[1] = minMaxY[0];

		return tmp;
	}
	public double[] getMaxAxis()
	{
		double tmp[] = new double[2];
		
		tmp[0] = minMaxX[1];
		tmp[1] = minMaxY[1];
		
		return tmp;
	}
	/*
	public int[] getSfeature()
	{
		return mShape.getSelectedFeatureNumber();
	}
	*/
	private void shutMouthUp()
	{
		double alpha = 0.2;
		double beta = 1-alpha;
		
		double mouthOpenThreshold = 0.02;
		
		double mouthOpenedLevel = y[62]-y[63];
		////���� ������� ��쿡�� �Ʒ� ����
		if(mouthOpenedLevel < mouthOpenThreshold)	return;
		
		mouthOpenedLevel -= mouthOpenThreshold;
		
		for(int i=55; i<63; i++)	//���� ��� �ֱ� ������ �� ũ�� ����
		{
			y[i] = y[i] - mouthOpenedLevel;
		}

		/*
		for(int i=60; i<63; i++)
		{
			y[i] = alpha*y[i] + beta*y[i+3];
		}
		*/
	}
	private void completeModelShape()
	{
		double tmpX[] = new double[modelShapeX.length];
		double tmpY[] = new double[modelShapeY.length];
		
		for(int i=0; i<modelShapeX.length; i++)
		{
			tmpX[i] = 0;
			tmpY[i] = 0;
			
			////�� shape�� �������� ���� �� �˻� (x=0, y=0)
			if( (modelShapeX[i]==0) && (modelShapeY[i]==0) )
			{
				tmpX[i] = x[i];
				tmpY[i] = -y[i];
			}
		}
		
		///������ ���� �������� ���� ���� �󱼿��� ���� ����Ʈ�� ä������
		for(int i=0; i<modelShapeX.length; i++)
		{
			modelShapeX[i] += tmpX[i];
			modelShapeY[i] += tmpY[i];
		}
		
		double modelRatioValue = faceRatio / 1.6;
		for(int i=0; i<modelShapeX.length; i++)
		{
			modelShapeY[i] = -(modelShapeY[i] * modelRatioValue);
		}
	}
	private void applyModelShapeToFace(double alpha)
	{
		if(alpha > 1 || alpha < 0)	alpha = 0.3;
		
		double beta = 1-alpha;
		
		for(int i=0; i<x.length; i++)
		{
			x[i] = alpha*modelShapeX[i] + beta*x[i];
			y[i] = alpha*modelShapeY[i] + beta*y[i];
		}
	}
	private double getFaceRatio()
	{
		double STANDARD_RATIO = MODEL_RATIO;
		
		return ( (y[7]-y[32])/(x[14]-x[0]) ) * STANDARD_RATIO;
	}
	private void printLogData()
	{
		String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
		
		String  FILE_PATH = SD_PATH + "/TOC/test"+str_idx+".txt";
		
		try {
			FileOutputStream fos = new FileOutputStream(FILE_PATH);
			
			for(int i=0; i<x.length; i++)
			{
				int j=i+1;
				//Log.i("TEST", "x("+j+")="+x[i]+"; y("+j+")="+y[i]+"; z("+j+")="+z[i]+";");
				String msg = "x_"+str_idx+"("+j+")="+x[i]+"; y_str_idx("+j+")="+y[i]+";\n";
				
				Log.i("TEST", msg);
				fos.write(msg.getBytes());				
			}
			
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();   
		}
				
	}
	static public double[] integerArrToDoubleArr(int value[])
	{
		double d_value[] = new double[value.length];
		for(int i=0; i<value.length; i++)
		{
			d_value[i] = (double)value[i];
		}
		
		return d_value;
	}
	
	public double[] getXcoord()
	{
		return x;
	}
	public double[] getYcoord()
	{
		return y;
	}
	public int setXcoord(double c_x[])
	{
		if(c_x.length != y.length)	return -1;
		else x = c_x;
		
		return 1;
	}
	public int setYcoord(double c_y[])
	{
		if(c_y.length != x.length) return -1;
		else y = c_y;
		
		return 1;
	}
	
	private void centerTransform()
	{
		double _c_nose, _c_lips, _c_eye;
		//////////////////�� �߽��� ���ϱ�
		c_faceX = 0;
		for(int i=0; i<7; i++)
		{
			c_faceX += (x[i]+x[14-i])/2;
		}
		c_faceX = c_faceX/7;
		////////////////
		
		_c_eye = (x[34]+x[29])/2;		////////////// �� �߽���(�̰�) ���ϱ�
		_c_lips = (x[51]+x[61])/2;		//�� �߽���
		_c_nose = x[41];
		
		Log.d("CENTER", "c_eye = " + _c_eye + ", c_lips = " + _c_lips + ", c_nose = " + _c_nose);
		//������� ������
		for(int i=15; i<37; i++)
		{
			x[i] = x[i]-(_c_eye);			
		}
		//�߰� ��
		for(int i=68; i<x.length; i++)
		{
			x[i] = x[i]-(_c_eye);			
		}
		
		///��
		for(int i=37; i<48; i++)
		{
			x[i] = x[i]-(_c_nose);
		}
		
		///��
		for(int i=48; i<67; i++)
		{
			x[i] = x[i]-(_c_lips);
		}
	}
	
	private void normScale()
	{
		int nPoints = x.length;
		double[] xMinMax = new double[2];
		double[] yMinMax = new double[2];
		
		xMinMax = getMinMax(x);
		yMinMax = getMinMax(y);
		
		c_eye[0] = (x[34]+x[29])/2;
		c_eye[1] = (y[34]+y[29])/2;
		
		p_upper[0] = c_eye[0];
		p_upper[1] = c_eye[1] - Math.abs(y[7] - c_eye[1]);
		
		//Log.i("TEST", "p[0] : "+p_upper[0]+"p[1] : "+p_upper[1]);
		//Log.i("TEST", "c[0] : "+c_eye[0]+"c[1] : "+c_eye[1]);
		
		for(int i=0; i<nPoints; i++)
		{
			x[i] = x[i]-xMinMax[0];
			y[i] = y[i]-p_upper[1];
		}
		
		xMinMax = getMinMax(x);
		yMinMax = getMinMax(y);
		
		for(int i=0; i<nPoints; i++)
		{
			x[i] = ((x[i] / xMinMax[1])-0.5)*2;
			y[i] = ( ((y[i] / yMinMax[1])-0.5)*2 ) * faceRatio;
		}
	}
	
	private void degreeNorm()
	{
		int nPoints = x.length;
		double[] maxValue = new double[2];
		double[] minValue = new double[2];
		z = new double[nPoints];
		double theta;
		double xc, yc, zc=9999;
		double ALPHA = 0.8;
		double BETA = 1-ALPHA;
		
		for(int i=0; i<nPoints; i++)
		{
			z[i] = 1;
		}
		/*
		for(int i=0; i<nPoints; i++)
		{
			z[i] = ALPHA*(1-Math.pow(x[67]-x[i], 2)) + BETA*(1-Math.pow(y[67]-y[i], 2));
			if(zc > z[i])	zc = z[i];
		}
		zc = zc/2;
		*/
		
		double[] tmp = new double[2];
		//0 = x, 1 = y
		tmp = getMinMax(x);
		minValue[0] = tmp[0];
		maxValue[0] = tmp[1];
		tmp = getMinMax(y);
		minValue[1] = tmp[0];
		maxValue[1] = tmp[1];
		
		xc = (maxValue[0] - minValue[0])/2;
		yc = (maxValue[1] - minValue[1])/2;
		
		//now compute theta
		theta = (180*Math.atan2((x[14]-x[0]), (y[14]-y[0])) / Math.PI) - 90;
		rotateShape(theta, 2);
		//theta = (180*Math.atan2((z[67]-zc), (x[67]-xc)) / Math.PI) - 90;
		//rotateShape(theta, 1); 
		
		//normScale();
	}
	
	private double[] getMinMax(double value[])
	{
		double[] result = new double[2];	//0 = min, max = 1
		
		result[0] = 9999;
		result[1] = -9999;
		
		for(int i=0; i<value.length; i++)
		{
			if(result[0] > value[i]) result[0] = value[i];
			if(result[1] < value[i]) result[1] = value[i];		
		}
		
		return result;
	}
	
	private void rotateShape(double theta, int mode)
	{
		int nPoints = x.length;
		theta = ((theta/180)*Math.PI);
		
		for(int i=0; i<nPoints; i++)
		{
			switch(mode)
			{
			case 0:
				y[i] = ((y[i]*Math.cos(theta)) - (z[i]*Math.sin(theta)));
				z[i] = ((y[i]*Math.sin(theta)) - (z[i]*Math.cos(theta)));
				break;
			case 1:
				x[i] = ((x[i]*Math.cos(theta)) - (z[i]*Math.sin(theta)));
				z[i] = ((-x[i]*Math.sin(theta)) + (z[i]*Math.cos(theta)));
				break;
			default:	//which will be mode 2
				x[i] = ((x[i]*Math.cos(theta)) - (y[i]*Math.sin(theta)));
				y[i] = ((x[i]*Math.sin(theta)) + (y[i]*Math.cos(theta)));
				break;
			}
		}
	}

	/*
	 xRatio, yRatio
	 1 is normal scale which does not change the shape.
	*/
	private void rescaleEyes(double xRatio, double yRatio)
	{
		
		double xAlpha = xRatio - 1;
		double yBeta = yRatio - 1;
		
		x[32] += (x[32] * xAlpha);
		x[34] += (x[34] *-xAlpha);
		x[27] += (x[27] * xAlpha);
		x[29] += (x[29] * -xAlpha);
		
		y[33] += (y[33] * yBeta);
		y[35] += (y[35] * yBeta);
		y[28] += (y[28] * yBeta);
		y[30] += (y[30] * yBeta);
		
		for(int i=68; i<76; i++)
		{
			y[i] += (y[i] * yBeta);
		}
	}
	
	private void rescaleNose(double xRatio, double yRatio)
	{
		double xAlpha = xRatio - 1;
		double yBeta = yRatio - 1;
		
		for(int i=37; i<48; i++)
		{
			x[i] += ((x[i] - x[67]) * xAlpha);
			y[i] += ((y[i] - y[67]) * yBeta);
		}
	}
	
	
	private void centerLineSymmetry()
	{
		// ��Ī�� �� ���庯��
		double[][] SyemmetryX = new double[34][2];
		double[][] SyemmetryY = new double[34][2];
		
		//�¿��Ī ��յ� ���庯��
		double[] centerSyemmetryX = new double[34];
		double[] centerSyemmetryY = new double[34];
		
		//�ܰ���
		for(int i=0; i<7; i++)
		{
			SyemmetryX[i][0] = x[i]; 
			SyemmetryX[i][1] = x[14-i]; 
			
			SyemmetryY[i][0] = y[i]; 
			SyemmetryY[i][1] = y[14-i]; 
		}
		
		//����
		for(int i=7; i<13; i++)
		{
			SyemmetryX[i][0] = x[i+14]; 
			SyemmetryX[i][1] = x[i+8]; 
			
			SyemmetryY[i][0] = y[i+14]; 
			SyemmetryY[i][1] = y[i+8]; 
		}
	
		
		//������
		for(int i=13; i<18; i++)
		{
			SyemmetryX[i][0] = x[i+14]; 
			SyemmetryX[i][1] = x[i+19]; 
			
			SyemmetryY[i][0] = y[i+14]; 
			SyemmetryY[i][1] = y[i+19]; 
		}
		
		//��
		for(int i=18; i<22; i++)
		{
			SyemmetryX[i][0] = x[i+19]; 
			SyemmetryX[i][1] = x[63-i]; 
			
			SyemmetryY[i][0] = y[i+19]; 
			SyemmetryY[i][1] = y[63-i]; 
		}
		
		SyemmetryX[22][0] = x[46]; 
		SyemmetryX[22][1] = x[47]; 
		
		SyemmetryY[22][0] = y[46]; 
		SyemmetryY[22][1] = y[47]; 
		
		//��
		
		SyemmetryX[23][0] = x[50]; 
		SyemmetryX[23][1] = x[52]; 
		
		SyemmetryY[23][0] = y[50]; 
		SyemmetryY[23][1] = y[52]; 
		
		SyemmetryX[24][0] = x[49]; 
		SyemmetryX[24][1] = x[53]; 
		
		SyemmetryY[24][0] = y[49]; 
		SyemmetryY[24][1] = y[53]; 
		
		SyemmetryX[25][0] = x[48]; 
		SyemmetryX[25][1] = x[54]; 
		
		SyemmetryY[25][0] = y[48]; 
		SyemmetryY[25][1] = y[54]; 
		
		SyemmetryX[26][0] = x[59]; 
		SyemmetryX[26][1] = x[55]; 
		
		SyemmetryY[26][0] = y[59]; 
		SyemmetryY[26][1] = y[55]; 
		
		SyemmetryX[27][0] = x[58]; 
		SyemmetryX[27][1] = x[56]; 
		
		SyemmetryY[27][0] = y[58]; 
		SyemmetryY[27][1] = y[56]; 
		
		SyemmetryX[28][0] = x[65]; 
		SyemmetryX[28][1] = x[63]; 
		
		SyemmetryY[28][0] = y[65]; 
		SyemmetryY[28][1] = y[63]; 
		
		SyemmetryX[29][0] = x[60]; 
		SyemmetryX[29][1] = x[62]; 
		
		SyemmetryY[29][0] = y[60]; 
		SyemmetryY[29][1] = y[62]; 
		
		for(int i=0; i<30; i++)
		{

			centerSyemmetryX[i] = ((SyemmetryX[i][0] * -1) + SyemmetryX[i][1]) / 2;
			centerSyemmetryY[i] = (SyemmetryY[i][0] + SyemmetryY[i][1]) / 2;
		
		}
		
		// �¿� ��Ī �� �� �� �Է� �ϱ�
		
		
		//�ܰ���
		for(int i=0; i<7; i++)
		{
			x[i] = (centerSyemmetryX[i] * -1); 
			x[14-i] = centerSyemmetryX[i]; 
			
			y[i] = centerSyemmetryY[i]; 
			y[14-i] = centerSyemmetryY[i]; 
		}
		
		//����
		for(int i=7; i<13; i++)
		{
			x[i+14] = (centerSyemmetryX[i] * -1); 
			x[i+8] = centerSyemmetryX[i]; 
			
			y[i+14] = centerSyemmetryY[i]; 
			y[i+8] = centerSyemmetryY[i]; 

		}
	
		
		//������
		for(int i=13; i<18; i++)
		{
			x[i+14] = (centerSyemmetryX[i] * -1); 
			x[i+19] = centerSyemmetryX[i]; 
			
			y[i+14] = centerSyemmetryY[i]; 
			y[i+19] = centerSyemmetryY[i]; 

		}
		
		//��
		for(int i=18; i<22; i++)
		{
			x[i+19] = (centerSyemmetryX[i] * -1); 
			x[63-i] = centerSyemmetryX[i]; 
			
			y[i+19] = centerSyemmetryY[i]; 
			y[63-i] = centerSyemmetryY[i]; 

		}
		
		x[46] =centerSyemmetryX[22] * -1; 
		x[47] =centerSyemmetryX[22]; 
		
		y[46] =centerSyemmetryY[22]; 
		y[47] =centerSyemmetryY[22]; 
		
		//��
		
		
		x[50] =centerSyemmetryX[23] * -1; 
		x[52] =centerSyemmetryX[23]; 
		
		y[50] =centerSyemmetryY[23]; 
		y[52] =centerSyemmetryY[23]; 
		
		x[49] =centerSyemmetryX[24] * -1; 
		x[53] =centerSyemmetryX[24]; 
		
		y[49] =centerSyemmetryY[24]; 
		y[53] =centerSyemmetryY[24]; 
		
		x[48] =centerSyemmetryX[25] * -1; 
		x[54] =centerSyemmetryX[25]; 
		
		y[48] =centerSyemmetryY[25]; 
		y[54] =centerSyemmetryY[25]; 
		
		x[59] =centerSyemmetryX[26] * -1; 
		x[55] =centerSyemmetryX[26]; 
		
		y[59] =centerSyemmetryY[26]; 
		y[55] =centerSyemmetryY[26]; 
		
		x[58] =centerSyemmetryX[27] * -1; 
		x[56] =centerSyemmetryX[27]; 
		
		y[58] =centerSyemmetryY[27]; 
		y[56] =centerSyemmetryY[27]; 
		
		x[65] =centerSyemmetryX[28] * -1; 
		x[63] =centerSyemmetryX[28]; 
		
		y[65] =centerSyemmetryY[28]; 
		y[63] =centerSyemmetryY[28]; 
		
		x[60] =centerSyemmetryX[29] * -1; 
		x[62] =centerSyemmetryX[29]; 
		
		y[60] =centerSyemmetryY[29]; 
		y[62] =centerSyemmetryY[29]; 
		
		x[7] = 0;
	}//centerLineSymetry
	
}
