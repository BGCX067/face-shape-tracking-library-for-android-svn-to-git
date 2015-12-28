package com.jeikei.facelibrary;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {
    private static final String TAG = "Sample::Activity";

    fstLibrary mView;
    
    Button btn01;
    ShapeWrapper sWrapper;
    
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(com.jeikei.facelibrary.R.layout.main);
        mView = new fstLibrary(this, fstLibraryBase.FRONT_CAMERA);
        setContentView(mView);
       
        /*
        btn01 = (Button)findViewById(com.jeikei.facelibrary.R.id.btn01);
        sWrapper = ShapeWrapper.getInstance();
        btn01.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 btn01.setText(sWrapper.getFaceRelativeLocationX() + "");
			}
		});
        */
        
    }
    
    @Override
	protected void onResume() {		//prevent camera won't work. modified by JeiKei
		super.onResume();
		mView.onResume();
	}
    
	@Override
	protected void onPause() {		//prevent camera won't work. modified by JeiKei
		super.onPause();
		mView.onPause();
	}
    
}
