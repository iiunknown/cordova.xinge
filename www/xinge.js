var exec = require("cordova/exec");
var xinge = {};

// XGPushManager功能类方法代理开始
xinge.register = function (account) {
    cordova.exec(null, null, "Xinge", "register", [account]);
};
// XGPushManager功能类方法代理结束

//XGPushConfig配置类开始
//配置accessId
xinge.setAccessId = function(accessId){
    cordova.exec(null, null, "Xinge", "setAccessId", [accessId]);
}
//配置accessKey
xinge.setAccessKey = function(accessKey){
    cordova.exec(null, null, "Xinge", "setAccessKey", [accessKey]);
}
//XGPushConfig配置类结束

module.exports = xingeExports;
