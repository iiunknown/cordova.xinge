package com.iiunknown.cordova.xinge;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class XGPushCordovaReceiver extends XGPushBaseReceiver {
	public static final String LogTag = "XGPushCordovaReceiver";
	public static  CallbackContext msgCallbackContext =null;
	public static  CallbackContext openCallbackContext =null;

	
	public  String getTopActivityName(Context context){
        String topActivityClassName=null;
         ActivityManager activityManager =
        (ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE )) ;
         List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;
         if(runningTaskInfos != null){
             ComponentName f=runningTaskInfos.get(0).topActivity;
             topActivityClassName=f.getClassName();
         }
         return topActivityClassName;
    }

	public void onTextMessage(Context context, XGPushTextMessage message) {
		if (context == null || message == null) {
			return;
		}
		//消息内容
		String content = message.getContent();
		//消息标题
		String title = message.getTitle();
		//消息自定义kv值
		JSONObject customObj = null;
		try {
			String customContent =message.getCustomContent();
			if(customContent!=null&&!customContent.trim().equals("")) {
				customObj = new JSONObject(message.getCustomContent());
			}
		} catch (JSONException e) {
			Log.e(LogTag, "create json falied", e);
		}
		
		//表示app正在运行								
		if (getTopActivityName(context).equals("com.shuishantech.s2mobile.s2mobile")){
			
			if(title.equals("shuishan.s2.apps.chat.newmessage")){
				if(msgCallbackContext!=null) {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, content);
					pluginResult.setKeepCallback(true);
					msgCallbackContext.sendPluginResult(pluginResult);
				}
			} else if (title.equals("shuishan.s2.apps.workflow.newmessage")){
				if(msgCallbackContext!=null) {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, content);
					pluginResult.setKeepCallback(true);
					msgCallbackContext.sendPluginResult(pluginResult);
				}
			}
    	}
		//app在后台运行或者已经被kill
		else{
			//收到新消息
			if(title.equals("shuishan.s2.apps.chat.newmessage")){
				try {
					content = java.net.URLDecoder.decode(content , "utf-8");
					JSONObject contentObject = new JSONObject(content);
					String userName = contentObject.getString("sender");
					String messageContent = contentObject.getString("content");
					XGLocalMessage localMessage = new XGLocalMessage();
					localMessage.setContent(messageContent);
					localMessage.setTitle(userName);
					localMessage.setType(1);
					XGPushManager.clearLocalNotifications(context);
			        Long msgId = XGPushManager.addLocalNotification(context, localMessage);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (title.equals("shuishan.s2.apps.workflow.newmessage")){
				try {
					content = java.net.URLDecoder.decode(content , "utf-8");
					JSONObject contentObject = new JSONObject(content);
					String messageTitle = contentObject.getString("title");
					String messageContent = contentObject.getString("content");
					XGLocalMessage localMessage = new XGLocalMessage();
					localMessage.setContent(messageContent);
					localMessage.setTitle(messageTitle);
					localMessage.setType(1);
					XGPushManager.clearLocalNotifications(context);
			        Long msgId = XGPushManager.addLocalNotification(context, localMessage);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
