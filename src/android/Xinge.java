package com.iiunknown.cordova.xinge;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.content.pm.PackageManager;

import com.tencent.android.tpush.*;

public class Xinge extends CordovaPlugin {

    private Context mContext;

    @Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        this.mContext = this.cordova.getActivity().getApplicationContext();
		// Context context = cordova.getActivity().getApplicationContext();

		XGPushConfig.enableDebug(this.mContext, true);
        int id = getMetadataInt(this.mContext, "XG_V2_ACCESS_ID");
        XGPushConfig.setAccessId(this.cordova.getActivity(), id);
        String key = getMetadataString(this.mContext, "XG_V2_ACCESS_KEY");
        XGPushConfig.setAccessKey(this.cordova.getActivity(), key);
        XGPushManager.onActivityStarted(this.cordova.getActivity());

	}

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("XingePush", "execute action:"+action+" with args:"+args);
         if ("register".equals(action)) {
             return register(args, callbackContext);
         }
         else if ("unregister".equals(action)){
             return unregister(callbackContext);
         }
         else if ("setAccessId".equals(action)){
            return setAccessId(args);
         }
         else if ("setAccessKey".equals(action)){
            return setAccessKey(args);
         }
         else if ("config".equals(action)){
            return config(args, callbackContext);
         }
         else if ("getToken".equals(action)){
            return getToken(callbackContext);
         }
         else if("onMessage".equals(action)) {
             return onMessage(callbackContext);
         }
         else if("notify".equals(action)) {
             String title = args.getString(0);
             String content = args.getString(1);
             return notify(title,content,callbackContext);
         }
         else if ("getPackageName".equals(action)){
             return getPackageName(callbackContext);
         }
         else if ("getVersion".equals(action)){
             return getVersion(callbackContext);
         }
         return false;
    }
    

    public boolean register(JSONArray args,final CallbackContext callbackContext){
        try{
            int count = args.length();
            if (count == 0){
                XGPushManager.registerPush(this.cordova.getActivity());
            }
            else if (count == 1){
                String account = args.getString(0);
                XGPushManager.registerPush(this.cordova.getActivity(), account,
                        new XGIOperateCallback() {
                            @Override
                            public void onSuccess(Object data, int flag) {
                                Log.d("TPush", "注册成功，设备token为：" + data);
                            }

                            @Override
                            public void onFail(Object data, int errCode, String msg) {
                                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                            }
                        });
            }
            callbackContext.success();
        } catch(Exception e) {
            callbackContext.error("Exception: " + e.getMessage());
            System.err.println("Exception: " + e.getMessage());
            return false;
        } 
        return true;
    }

    public boolean unregister(final CallbackContext callbackContext) {
        XGPushManager.unregisterPush(this.cordova.getActivity());
        callbackContext.success();
        Log.d("TPush", "unregister push sucess");
        return true;
    }

    public boolean config(JSONArray args, final CallbackContext callbackContext){
        try{
            Long accessId = args.getLong(0);
            String accessKey = args.getString(1);
            XGPushConfig.setAccessId(this.cordova.getActivity(), accessId);
            XGPushConfig.setAccessKey(this.cordova.getActivity(), accessKey);
            XGPushManager.onActivityStarted(this.cordova.getActivity());
            callbackContext.success();
        }
        catch(Exception e){
            callbackContext.error("Exception: " + e.getMessage());
            System.err.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean setAccessId(JSONArray args){
        try{
            long id = args.getLong(0);
            XGPushConfig.setAccessId(this.cordova.getActivity(), id);
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            return false;
        } 
        return true;
    }

    public boolean setAccessKey(JSONArray args){
        try{
            String key = args.getString(0);
            XGPushConfig.setAccessKey(this.cordova.getActivity(), key);
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            return false;
        } 
        return true;
    }

    public boolean getToken(final CallbackContext callbackContext){
        try{
            String token = XGPushConfig.getToken(this.cordova.getActivity());
            callbackContext.success(""+token);
        } catch (Exception e){
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(""+e.getMessage());
            return false;
        }
        return true;
    }


    public boolean onMessage(final CallbackContext callbackContext) {
        XGPushCordovaReceiver.msgCallbackContext = callbackContext;
        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
        return true;
    }

    public boolean notify(String title,String content,final CallbackContext callbackContext) {
        XGLocalMessage localMessage = new XGLocalMessage();
        localMessage.setContent(content);
        localMessage.setTitle(title);
        localMessage.setType(1);
        //localMessage.setBuilderId(XGPushConfig.);
        Long msgId = XGPushManager.addLocalNotification(this.cordova.getActivity(),localMessage);
        callbackContext.success(""+msgId);
        return true;
    }

    public boolean getVersion(final CallbackContext callbackContext){
        try{
            String packageName = this.mContext.getPackageName();
            String currentVerName = this.mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
            callbackContext.success(""+currentVerName);
        } catch (Exception e){
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(""+e.getMessage());
            return false;
        }
        return true;
    }
    public boolean getPackageName(final CallbackContext callbackContext){
        try{
            String packageName = this.mContext.getPackageName();
            callbackContext.success(""+packageName);
        } catch (Exception e){
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(""+e.getMessage());
            return false;
        }
        return true;
    }

    public static String getMetadataString(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                String a = appInfo.metaData.getString(name);
                return a;
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    public static int getMetadataInt(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                int id = appInfo.metaData.getInt(name);
                return id;
            } else {
                return 0;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}
