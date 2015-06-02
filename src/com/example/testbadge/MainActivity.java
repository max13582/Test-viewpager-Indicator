package com.example.testbadge;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	private ViewPager topViewPager;//viewPager on the top of UI.
	//a int array that will be used in viewPager.
	private int [] viewPagerContent = {R.drawable.ic_2015_04_29_00008,
			  						   R.drawable.ic_2015_04_10_00021,
			  						   R.drawable.ic_2015_03_31_00003};
	//this field used to get a inflater of pager_item.xml.
	private LayoutInflater mLayoutInflater; 
	
	//field associated to pagerAdapter and indicator.
	public final static int AUTO_CHANGE_VIEWPAGER = 0;	
	private int currentPage = 0;
	private int currentIndicator = 0;
	
	private String tag = "Main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		topViewPager = (ViewPager) findViewById(R.id.topViewPager);	
		mLayoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
		topViewPager.setAdapter(topViewPagerAdapter);
		changeViewPagerThread.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private PagerAdapter topViewPagerAdapter = new PagerAdapter() {
						
		//if you move your pager from one to another,you should destroy last one.
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.d(tag, "destroyItem");
			 container.removeView((LinearLayout) object);
		}
		
		//create current page. 
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.d(tag, "instantiateItem");
			View itemView = mLayoutInflater.inflate(R.layout.pager_item, container,false);
			
			ImageView imageView = (ImageView) itemView.findViewById(R.id.pagerImageView);
			imageView.setImageResource(viewPagerContent[position]);
			
			container.addView(itemView);
			
			return itemView;
		}

		//check whether if view is associated with this key object. 
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((LinearLayout) object);
		}
		
		//get number of pages.
		@Override
		public int getCount() {
			return viewPagerContent.length;
		}

		//Test excute cycle
		@Override
		public void finishUpdate(ViewGroup container) {
			Log.d(tag, "finishUpdate");
		}

		@Override
		public int getItemPosition(Object object) {
			Log.d(tag, "getItemPosition");
			return 0;
		}

		//change indicator of viewpager here.
		@Override
		public void startUpdate(ViewGroup container) {
			Log.d(tag, "startUpdate");
			//judge whether if current had changed.
			int currentItem = topViewPager.getCurrentItem();
			currentPage = currentItem;
			updateIndicator(currentPage);
		}
		
	};
	
	public void updateIndicator(final int position){
		//get linearLayout that manager indicator.
		LinearLayout l = (LinearLayout) findViewById(R.id.imageGroupLayout);
		
		//get last indicator represented page that chosen and set it to off.
		ImageView lastIndicatorImageView = (ImageView) l.getChildAt(currentIndicator);
		lastIndicatorImageView.setImageResource(R.drawable.ic_toggle_radio_button_off);
		
		//update currentIndicator.
		currentIndicator = position;
		//set currentIndicator to new page that chosen.
		ImageView currentIndiImageView = (ImageView) l.getChildAt(position);
		currentIndiImageView.setImageResource(R.drawable.ic_toggle_radio_button_on);
	}
	
	//this used to change image which to be set to on.
	private Handler viewHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case AUTO_CHANGE_VIEWPAGER:
				int currentPage = (Integer) msg.obj;
				topViewPager.setCurrentItem(currentPage);
				break;
			}
		}
		
	};

	//use this thread to change viewpager by auto.
	private Thread changeViewPagerThread = new Thread(){
		
		@Override
		public void run() {
			while(true){
				viewHandler.obtainMessage(AUTO_CHANGE_VIEWPAGER, currentPage).sendToTarget();
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(currentPage < 2){
					currentPage += 1;
				}else{
					currentPage = 0;
				}
				
			}
		}
		
	};
}
