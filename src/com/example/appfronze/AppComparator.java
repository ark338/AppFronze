package com.example.appfronze;

import java.text.Collator;
import java.util.Comparator;
import java.util.Map;

public class AppComparator implements Comparator{
	private static AppComparator mInstance = null;
	
	public static AppComparator getInstance(){
		if (mInstance == null) {
			mInstance = new AppComparator();
		}
		return mInstance;
	}
	
	@Override
	public int compare(Object lhs, Object rhs) {
		Map<String, Object> first = (Map<String, Object>)lhs;
		Map<String, Object> second = (Map<String, Object>)rhs;
		if ((Boolean)first.get(Constant.STATE) != (Boolean)second.get(Constant.STATE)) {
			if ((Boolean)first.get(Constant.STATE) == false) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return Collator.getInstance(java.util.Locale.CHINA).compare(first.get(Constant.LABLE), second.get(Constant.LABLE));
		}
	}
}