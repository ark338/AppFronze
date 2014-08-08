package com.example.appfronze;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MyListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mAppList;
	private MyListAdapter mAdapter;

	public MyListAdapter(Context context, List<Map<String, Object>> appList) {
		this.mAdapter = this;
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mAppList = appList;
	}
	
	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private class ViewHolder{
		public ImageView img;
		public TextView label;
		public ToggleButton switcher;
	}
	
	private class AppState{
		public int position;
		public boolean enabled;
		public String packageName;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder=new ViewHolder();  
			convertView = mInflater.inflate(R.layout.item, null);
			holder.img = (ImageView)convertView.findViewById(R.id.itemimage);
			holder.label = (TextView)convertView.findViewById(R.id.itemlable);
			holder.switcher = (ToggleButton)convertView.findViewById(R.id.itemswitcher);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.img.setBackground((Drawable)mAppList.get(position).get(Constant.IMAGE));
		holder.label.setText((String)mAppList.get(position).get(Constant.LABLE));
		AppState as = new AppState();
		as.position = position;
		as.enabled = (Boolean)mAppList.get(position).get(Constant.STATE);
		as.packageName = (String)mAppList.get(position).get(Constant.PACKAGE_NAME);
		holder.switcher.setChecked(as.enabled);
		holder.switcher.setTag(as);
		holder.switcher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ToggleButton button = (ToggleButton)v;
				boolean ischecked = button.isChecked();
				AppState as = (AppState)v.getTag();
				if (ischecked != as.enabled) {
					PackageManager pm = mContext.getPackageManager();
					Log.d("AF", "change app state:" + as.packageName + " - " + ischecked);
					try {
						pm.setApplicationEnabledSetting(as.packageName, ischecked? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
						ApplicationInfo ai = pm.getApplicationInfo(as.packageName, 0);
						if (ai.enabled != ischecked) {
							// failed
							Toast.makeText(mContext, "应用状态设置失败，这种情况不该发生啊。。。", Toast.LENGTH_SHORT).show();
							button.setChecked(!ischecked);
						} else {
							// success
							as.enabled = ischecked;
							mAppList.get(as.position).put("enable", ischecked);
							Collections.sort(mAppList,AppComparator.getInstance());
							mAdapter.notifyDataSetChanged();
						}
					} catch (NameNotFoundException e) {
						// failed
						Toast.makeText(mContext, "应用状态设置失败，奇怪了居然找不到设置的应用？", Toast.LENGTH_SHORT).show();
						button.setChecked(!ischecked);
					} catch (SecurityException e1) {
						// failed
						Toast.makeText(mContext, "应用状态设置失败，没权限玩个蛋，想办法吧APK放进SYSTEM分区吧~~", Toast.LENGTH_SHORT).show();
						button.setChecked(!ischecked);
					}
				}
			}
		});
		
		return convertView;
	}
}
