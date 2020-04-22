package com.example.helloworld.utils;

import android.content.Context;

public class DensityUtil {
	private static long lastClickTime;
	/**
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int dip2px(Context context,int value){
		float scaleing = context.getResources().getDisplayMetrics().density;
		return (int) (value*scaleing+0.5f);
	}

	/**
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static float sp2px(Context context, float sp){
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return sp * scale;
	}

	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();
		if ( time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

}