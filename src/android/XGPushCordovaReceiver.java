package com.iiunknown.cordova.xinge;

import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class XGPushCordovaReceiver extends XGPushBaseReceiver {
	public static final String LogTag = "XGPushCordovaReceiver";
	public static  CallbackContext msgCallbackContext =null;
	public static  CallbackContext openCallbackContext =null;

	public void onTextMessage(Context context, XGPushTextMessage message) {
		if (context == null || message == null) {
			return;
		}
		String text = "onTextMessage:" + message.toString();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xtype", "message");
		map.put("title", message.getTitle());
		map.put("content", message.getContent());
		JSONObject msg = new JSONObject(map);
		JSONObject customObj;
		try {
			String content =message.getCustomContent();
			if(content!=null&&!content.trim().equals("")) {
				customObj = new JSONObject(message.getCustomContent());
				msg.putOpt("custom", customObj);
			}
		} catch (JSONException e) {
			Log.e(LogTag, "create json falied", e);
		}
		Log.d(LogTag, text);
		if(msgCallbackContext!=null) {
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, msg);
			pluginResult.setKeepCallback(true);
			msgCallbackContext.sendPluginResult(pluginResult);
		}
	}
	@Override
	public void onNotifactionShowedResult(Context context, XGPushShowedResult message) {
		if (context == null || message == null) {
			return;
		}
		String text = "onNotifactionShowedResult:" + message.toString();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xtype", "notifaction");
		map.put("msgId", message.getMsgId());
		map.put("title", message.getTitle());
		map.put("content", message.getContent());
		JSONObject msg = new JSONObject(map);
		JSONObject customObj;
		try {
			String content =message.getCustomContent();
			if(content!=null&&!content.trim().equals("")) {
				customObj = new JSONObject(message.getCustomContent());
				msg.putOpt("custom", customObj);
			}
		} catch (JSONException e) {
			Log.e(LogTag, "create json falied", e);
		}
		Log.d(LogTag, text);
		if(msgCallbackContext!=null) {
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, msg);
			pluginResult.setKeepCallback(true);
			msgCallbackContext.sendPluginResult(pluginResult);
		}
	}
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
		if (context == null || message == null) {
			return;
		}
		String text = "msg opened :" + message;
		Log.d(LogTag, text);
		if(openCallbackContext!=null) {
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message.toString());
			pluginResult.setKeepCallback(true);
			openCallbackContext.sendPluginResult(pluginResult);
		}
	}
	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		String text = "onDeleteTagResult:" +arg1+":"+ arg2.toString();
		Log.d(LogTag, text);
		
	}
	
	@Override
	public void onRegisterResult(Context arg0, int arg1,
			XGPushRegisterResult arg2) {
			String text = "onRegisterResult:" +arg1+":"+ arg2.toString();
			Log.d(LogTag, text);
	}
	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
			String text = "onSetTagResult:"+arg1+":" + arg2;
			Log.d(LogTag, text);
	}
	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		String text = "onUnregisterResult:" + arg1;
		Log.d(LogTag, text);
	}

}
