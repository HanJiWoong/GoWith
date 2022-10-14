package com.mtis.gowith.widget.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebSettings;

public class P {
	private static final String PREFERENCE_NAME = "com.mtis.gowith";

	/*
	 *
	 * System Setting
	 *
	 */
	public static boolean getLogDialgGravity(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("log_dialog_gravity",false);
	}

	public static void setLogDialgGravity(Context ctx, boolean isTop) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("log_dialog_gravity", isTop);
		editor.commit();
	}

	public static String getFcmToken(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getString("mtis_user_fcm_token",null);
	}

	public static void setFcmToken(Context ctx, String token) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("mtis_user_fcm_token", token);
		editor.commit();
	}

	public static String getServerUrl(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getString("sever_host_url", "https://app.gwith.co.kr/");
	}

	public static void setServerUrl(Context ctx, String hostUrl) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("sever_host_url", hostUrl);
		editor.commit();
	}

	public static boolean getUseDebugLog(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("use_debug_log_list",true);
	}

	public static void setUseDebugLog(Context ctx, boolean isUse) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("use_debug_log_list", isUse);
		editor.commit();
	}

	/*
	 *
	 * WebView
	 *
	 */

	public static boolean getClearCache(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("webview_clear_cache",true);
	}

	public static void setClearCache(Context ctx, boolean option) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("webview_clear_cache", option);
		editor.commit();
	}

	public static boolean getDomStorage(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("webview_dom_storage",true);
	}

	public static void setDomStorage(Context ctx, boolean option) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("webview_dom_storage", option);
		editor.commit();
	}

	public static boolean getAllowFileAccessFromUrl(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("webview_allow_file_access_from_url",true);
	}

	public static void setAllowFileAccessFromUrl(Context ctx, boolean option) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("webview_allow_file_access_from_url", option);
		editor.commit();
	}

	public static boolean getAllowUniversalAccess(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("webview_allow_universal_access",true);
	}

	public static void setAllowUniversalAccess(Context ctx, boolean option) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("webview_allow_universal_access", option);
		editor.commit();
	}

	public static boolean getAllowFileAccess(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("webview_allow_file_access",true);
	}

	public static void setAllowFileAccess(Context ctx, boolean option) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("webview_allow_file_access", option);
		editor.commit();
	}

	public static boolean getAllowContentAccess(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("webview_allow_content_access",true);
	}

	public static void setAllowContentAccess(Context ctx, boolean option) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("webview_allow_content_access", option);
		editor.commit();
	}

	public static int getCacheMode(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getInt("webview_cache_mode",WebSettings.LOAD_NO_CACHE);
	}

	public static void setCacheMode(Context ctx, int mode) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("webview_cache_mode", mode);
		editor.commit();
	}
	/*
	 *
	 * Permissions
	 *
	 */

	// -----
	public static boolean getPermissionRejectCountBLE(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("permission_reject_count_ble", false);
	}

	public static void setPermissionRejectCountBLE(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("permission_reject_count_ble", true);
		editor.commit();
	}

	// -----
	public static boolean getPermissionRejectCountGPS(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("permission_reject_count_gps", false);
	}

	public static void setPermissionRejectCountGPS(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("permission_reject_count_gps", true);
		editor.commit();
	}

	// -----
	public static boolean getPermissionRejectPhoneNumber(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("permission_reject_phone_number", false);
	}

	public static void setPermissionRejectPhoneNumber(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("permission_reject_phone_number", true);
		editor.commit();
	}

	// -----
	public static boolean getPermissionRejectCountContact(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("permission_reject_count_contact", false);
	}

	public static void setPermissionRejectCountContact(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("permission_reject_count_contact", true);
		editor.commit();
	}

	// -----
	public static boolean getPermissionRejectCountPhoto(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("permission_reject_count_photo", false);
	}

	public static void setPermissionRejectCountPhoto(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("permission_reject_count_photo", true);
		editor.commit();
	}

	// -----
	public static boolean getPermissionRejectCountNoti(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean("permission_reject_count_noti", false);
	}

	public static void setPermissionRejectCountNoti(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("permission_reject_count_noti", true);
		editor.commit();
//	}

	/*
	 *
	 * ???
	 *
	 */

//	// -----
//	public static String getApiScheduleID(Context ctx) {
//		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
//		return preferences.getString("api_schedule_id", "");
//	}
//
//	public static void setApiScheduleID(Context ctx, String apiScheduleID) {
//		SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = preferences.edit();
//		editor.putString("api_schedule_id", apiScheduleID);
//		editor.commit();
	}

}