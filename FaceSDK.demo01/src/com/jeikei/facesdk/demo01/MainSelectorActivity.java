package com.jeikei.facesdk.demo01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainSelectorActivity extends Activity implements OnClickListener{

	Button btn01, btn02, btn03, btn04;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		
		setContentView(R.layout.main_selector_activity_layout);
		
		btn01 = (Button)findViewById(R.id.selector_btn01);
		btn02 = (Button)findViewById(R.id.selector_btn02);
		btn03 = (Button)findViewById(R.id.selector_btn03);
		btn04 = (Button)findViewById(R.id.selector_btn04);
		
		btn01.setOnClickListener(this);
		btn02.setOnClickListener(this);
		btn03.setOnClickListener(this);
		btn04.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent mIntent;
		
		switch (v.getId()) {
		case R.id.selector_btn01:
			mIntent = new Intent(MainSelectorActivity.this, DebugActivity.class);
			break;
		case R.id.selector_btn02:
			mIntent = new Intent(MainSelectorActivity.this, SphereDemo.class);
			break;
		case R.id.selector_btn03:
			mIntent = new Intent(MainSelectorActivity.this, PortraitDemo.class);
			break;
		case R.id.selector_btn04:
			mIntent = new Intent(MainSelectorActivity.this, InvaderDemo.class);
			break;
		default:
			mIntent = new Intent(MainSelectorActivity.this, DebugActivity.class);
			break;
		}
		
		startActivity(mIntent);
	}
}