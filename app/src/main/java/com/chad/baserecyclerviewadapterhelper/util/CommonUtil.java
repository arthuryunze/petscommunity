package com.chad.baserecyclerviewadapterhelper.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yunze on 2019/8/26 9:27
 * Email: arthur.yunze@gmail.com
 */

public class CommonUtil {
	/**
	 * 检查是否有网络
	 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		return info != null && info.isAvailable();
	}

	private static NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}



}
