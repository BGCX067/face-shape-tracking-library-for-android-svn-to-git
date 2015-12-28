package com.jeikei.facesdk.demo01;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jeikei.facelibrary.ShapeWrapper;
import com.jeikei.facelibrary.fstLibrary;
import com.jeikei.facelibrary.fstLibraryBase;

public class DebugActivity extends Activity {

	TextView txtView01, txtView02, txtView03, txtView04;
	FrameLayout frameLayout;
	
	fstLibrary face_sdkView;
	ShapeWrapper sWrapper;
	
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			txtView01.setText(sWrapper.getEstimatedFaceLocationX() + "");
			txtView02.setText(sWrapper.getEstimatedFaceLocationY() + "");
			txtView03.setText(sWrapper.getEstimatedFaceDistance() + "");
			txtView04.setText(sWrapper.getTimeGap() + "");
		
			this.sendEmptyMessageDelayed(0, 50);
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        face_sdkView = new fstLibrary(this, fstLibraryBase.FRONT_CAMERA);
        
        setContentView(R.layout.debug_acivity_layout);
        
        //setContentView(face_sdkView);
        
        txtView01 = (TextView)findViewById(R.id.demo01_txtView01);
        txtView02 = (TextView)findViewById(R.id.demo01_txtView02);
        txtView03 = (TextView)findViewById(R.id.demo01_txtView03);
        txtView04 = (TextView)findViewById(R.id.demo01_txtView04);
        
        frameLayout = (FrameLayout)findViewById(R.id.faceSDK_demo01View);
        frameLayout.addView(face_sdkView);
          
        mHandler.sendEmptyMessage(0);
    }
     
    
    @Override
	protected void onResume() {
		super.onResume();
		
		sWrapper = ShapeWrapper.getInstance();
		sWrapper.resume();
	}


	@Override
	protected void onPause() {
		super.onPause();
		sWrapper.release();
		mHandler.removeMessages(0);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
