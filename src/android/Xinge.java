package com.iiunknown.cordova.xinge;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.tencent.android.tpush.*;
/*
腾讯信鸽Cordova插件Android代理类，继承CordovaPlugin类。
代理腾讯信鸽下列类中的方法：
    XGPushManager功能类
    XGPushConfig配置类
    XGPushBaseReceiver广播类
供js脚本调用。
*/
public class Xinge extends CordovaPlugin {
    /**
     * 执行js传递过来的请求。
     *
     * @param action            需要执行的命令。
     * @param args              命令所需参数，JSONArry格式。
     * @param callbackContext   执行后调用的js中的回调函数。
     * @return                  bool值。
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("TPush", "execute action:"+action+" with args:"+args);
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
    
    // XGPushManager功能类方法代理开始

    //启动并注册APP
    public boolean register(JSONArray args,final CallbackContext callbackContext){
        try{
            int count = args.length();
            if (count == 0){
                XGPushManager.registerPush(this.cordova.getActivity());
            }
            else if (count == 1){
                String account = args.getString(0);
                XGPushManager.registerPush(this.cordova.getActivity(), account);
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
    // XGPushManager功能类方法代理结束

    //XGPushConfig配置类开始
    //XGPushConfig提供信鸽服务的对外配置API列表，方法默认为public static类型，对于本类提供的set和enable方法，要在XGPushManager接口前调用才能及时生效。

    //配置App，设置Xinge的AccessId和AccessKey。
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
    //配置accessId
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
    //配置accessKey
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
    //获取设备的token，只有注册成功才能获取到正常的结果
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

    //XGPushConfig配置类结束


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
}
