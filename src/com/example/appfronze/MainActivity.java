package com.example.appfronze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Context mContext;
	private static final int APP_ADDED = 1;

	private List<Map<String, Object>> list1 = new LinkedList<Map<String,Object>>();
	private List<Map<String, Object>> list2 = new LinkedList<Map<String,Object>>();
	private List<Map<String, Object>> list3 = new LinkedList<Map<String,Object>>();
	
	private static List<String> googleFrameworkAppList = new ArrayList<String>();
	private static List<String> banList = new ArrayList<String>();
	
	ViewPager viewPager;
	ViewPagerAdapter pagerAdapter;
	View view1, view2, view3; // 页面
	ListView listview1, listview2, listview3;
	MyListAdapter mla1, mla2, mla3;
	List<View> views;				 // Tab页面列表

	TextView tv_guid1, tv_guid2, tv_guid3; // 页卡头标
	int offset = 0; 		// 偏移量
	int currIndex = 0; 		// 当前页卡编号
	int bmpW; 				// 图片宽度

	static{
		googleFrameworkAppList.add("com.google.android.gms");
		googleFrameworkAppList.add("com.google.android.gsf");
		googleFrameworkAppList.add("com.google.android.gsf.login");
		googleFrameworkAppList.add("com.android.vending");
	}
	
	static{
		banList.add("android");
		banList.add("android.product.res.overlay");
		banList.add("android.res.overlay");
		banList.add("com.example.appfronze");
	}
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case APP_ADDED:
				MessagePackage mp = (MessagePackage)msg.obj;
				mp.appList.add(mp.appInfo);
				Collections.sort(mp.appList,AppComparator.getInstance());
				mp.adapter.notifyDataSetChanged();
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);

		initTextView();

		LayoutInflater inflater = LayoutInflater.from(this);
		view1 = inflater.inflate(R.layout.viewpager, null);
		view2 = inflater.inflate(R.layout.viewpager, null);
		view3 = inflater.inflate(R.layout.viewpager, null);
		
		listview1 = (ListView)view1.findViewById(R.id.listview);
		listview2 = (ListView)view2.findViewById(R.id.listview);
		listview3 = (ListView)view3.findViewById(R.id.listview);
		
		mla1 = new MyListAdapter(this, list1);
		mla2 = new MyListAdapter(this, list2);
		mla3 = new MyListAdapter(this, list3);
		listview1.setAdapter(mla1);
		listview2.setAdapter(mla2);
		listview3.setAdapter(mla3);
		
		views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);

		pagerAdapter = new ViewPagerAdapter(views);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(pagerAdapter);

		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		new AsyncTask<Void,Void,Void>(){
			
			@Override
			protected void onPostExecute(Void result) {
				Toast.makeText(mContext, "应用信息加载完成", Toast.LENGTH_SHORT).show();
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				Toast.makeText(mContext, "正在加载应用信息，请稍后...", Toast.LENGTH_SHORT).show();
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				initAppInfo();
				return null;
			}}.execute();
	}

	/**
	 * 
	 * 初始化TextView控件，和注册监听器
	 */
	private void initTextView() {
		tv_guid1 = (TextView) findViewById(R.id.tv_guid1);
		tv_guid2 = (TextView) findViewById(R.id.tv_guid2);
		tv_guid3 = (TextView) findViewById(R.id.tv_guid3);

		tv_guid1.setBackgroundColor(Color.DKGRAY);
		tv_guid2.setBackgroundColor(Color.LTGRAY);
		tv_guid3.setBackgroundColor(Color.LTGRAY);
		tv_guid1.setTextColor(Color.WHITE);
		tv_guid2.setTextColor(Color.DKGRAY);
		tv_guid3.setTextColor(Color.DKGRAY);
		
		tv_guid1.setOnClickListener(listener);
		tv_guid2.setOnClickListener(listener);
		tv_guid3.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_guid1:
				viewPager.setCurrentItem(0);
				break;
			case R.id.tv_guid2:
				viewPager.setCurrentItem(1);
				break;
			case R.id.tv_guid3:
				viewPager.setCurrentItem(2);
				break;
			default:
				break;
			}
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		private int one = offset * 2 + bmpW;

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(200);
			showSelected(currIndex);
		}
		
		private void showSelected(int id) {
			switch (id) {
			case 0:
				tv_guid1.setBackgroundColor(Color.DKGRAY);
				tv_guid2.setBackgroundColor(Color.LTGRAY);
				tv_guid3.setBackgroundColor(Color.LTGRAY);
				tv_guid1.setTextColor(Color.WHITE);
				tv_guid2.setTextColor(Color.DKGRAY);
				tv_guid3.setTextColor(Color.DKGRAY);
				break;
			case 1:
				tv_guid1.setBackgroundColor(Color.LTGRAY);
				tv_guid2.setBackgroundColor(Color.DKGRAY);
				tv_guid3.setBackgroundColor(Color.LTGRAY);
				tv_guid1.setTextColor(Color.DKGRAY);
				tv_guid2.setTextColor(Color.WHITE);
				tv_guid3.setTextColor(Color.DKGRAY);
				break;
			case 2:
				tv_guid1.setBackgroundColor(Color.LTGRAY);
				tv_guid2.setBackgroundColor(Color.LTGRAY);
				tv_guid3.setBackgroundColor(Color.DKGRAY);
				tv_guid1.setTextColor(Color.DKGRAY);
				tv_guid2.setTextColor(Color.DKGRAY);
				tv_guid3.setTextColor(Color.WHITE);
				break;
			case 3:
				tv_guid1.setBackgroundColor(Color.LTGRAY);
				tv_guid2.setBackgroundColor(Color.LTGRAY);
				tv_guid3.setBackgroundColor(Color.LTGRAY);
				tv_guid1.setTextColor(Color.DKGRAY);
				tv_guid2.setTextColor(Color.DKGRAY);
				tv_guid3.setTextColor(Color.DKGRAY);
				break;
			}
		}
	}
	
	private void initAppInfo() {
		PackageManager pm = this.getPackageManager();
		List<ApplicationInfo> aiList = pm.getInstalledApplications(0);
		for (ApplicationInfo ai : aiList) {
			if (banList.contains(ai.packageName)) {
				continue;
			}
			HashMap<String,Object> app = new HashMap<String, Object>();
			app.put(Constant.PACKAGE_NAME, ai.packageName);
			app.put(Constant.IMAGE, ai.loadIcon(pm));
			app.put(Constant.LABLE, ai.loadLabel(pm).toString());
			app.put(Constant.STATE, ai.enabled);
			MessagePackage mp = new MessagePackage();
			mp.appInfo = app;
			if (googleFrameworkAppList.contains(ai.packageName)) {
				mp.adapter = mla2;
				mp.appList = list2;
			} else if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				mp.adapter = mla1;
				mp.appList = list1;
			} else {
				mp.adapter = mla3;
				mp.appList = list3;
			}
			addApp(mp);
//			log("add " + ai.packageName);
		}
	}
	
	private class MessagePackage {
		public MyListAdapter adapter;
		public List<Map<String, Object>> appList;
		public HashMap<String, Object> appInfo;
	}
	
	private synchronized void addApp(MessagePackage mp) {
		Message msg = new Message();
		msg.what = APP_ADDED;
		msg.obj = mp;
		mHandler.sendMessage(msg);
	}
	
	private void log(String s){
		Log.d("AF",s);
	}
}
