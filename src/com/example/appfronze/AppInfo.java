package com.example.appfronze;

import java.text.Collator;
import java.util.HashMap;

public class AppInfo extends HashMap<String, Object> implements Comparable<AppInfo>{
	private static final long serialVersionUID = 3229679453492080871L;
	private String lable;
	private boolean state;
	@Override
	public Object put(String key, Object val) {
		if (key.equals(Constant.LABLE)) {
			lable = (String)val;
		} else if (key.equals(Constant.STATE)) {
			state = (Boolean)val;
		}
		return super.put(key, val);
	}
	
	@Override
	public int compareTo(AppInfo arg0) {
		if (this.state != arg0.state) {
			if (this.state == false) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return Collator.getInstance(java.util.Locale.CHINA).compare(this.lable, arg0.lable);
		}
	}
}
