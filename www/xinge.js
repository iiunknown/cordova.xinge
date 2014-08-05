var exec = require("cordova/exec");
var xingePlugin = {};

// XGPushManager功能类方法代理开始
xingePlugin.register = function (account, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "Xinge", "register", [account]);
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
//XGPushConfig配置类结束

module.exports = xingePlugin;
