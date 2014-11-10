var xingePlugin = {};

// XGPushManager功能类方法代理开始
//启动并注册APP，同时绑定账号。
xingePlugin.register = function (account, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Xinge", "register", [account]);
};
//反注册，建议在不需要接收推送的时候调用。
xingePlugin.unregister = function(successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, "Xinge", "unregister", []);
};

// XGPushManager功能类方法代理结束

//XGPushConfig配置类开始
//配置accessId
xingePlugin.setAccessId = function(accessId){
    cordova.exec(null, null, "Xinge", "setAccessId", [accessId]);
}
//配置accessKey
xingePlugin.setAccessKey = function(accessKey){
    cordova.exec(null, null, "Xinge", "setAccessKey", [accessKey]);
}
//获取设备的token，只有注册成功才能获取到正常的结果
xingePlugin.getToken = function(successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, "Xinge", "getToken", []);
}
//配置accessId和accessKey
xingePlugin.config = function(accessId, accessKey, successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, "Xinge", "config", [accessId, accessKey]);
};
//获取当前应用包名
xingePlugin.getPackageName = function(successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, "Xinge", "getPackageName", []);
};
//获取当前应用版本号
xingePlugin.getVersion = function(successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, "Xinge", "getVersion", []);
};
//XGPushConfig配置类结束


xingePlugin.onMessage = function (successCallback, errorCallback) {
    if (errorCallback == null) {
        errorCallback = function () {
        }
    }

    if (typeof successCallback != "function") {
        console.log("Xinge.onMessage failure: success callback parameter must be a function");
        return;
    }
    
    cordova.exec(successCallback, errorCallback, "Xinge", "onMessage", []);
};
xingePlugin.notify = function (title,content,successCallback, errorCallback) {
    if (errorCallback == null) {
        errorCallback = function () {
        }
    }
    if (successCallback == null) {
        successCallback = function (data) {
            console.log("notify "+title+":"+data);
        }
    }
    cordova.exec(successCallback, errorCallback, "Xinge", "notify", [title,content]);
};

//for iOS only
xingePlugin.registerDevice = function(successCallback, errorCallback){
    cordova.exec(successCallback, errorCallback, "Xinge", "registerDevice", []);
};

module.exports = xingePlugin;

