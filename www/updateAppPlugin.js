/**
 * 检查并更新APP
 * version.js
 * [{'verCode':2,'verName':'1.2.1','apkPath':'http://****.com/your.apk'}]
 * verCode 版本号
 * verName 版本名称
 * apkPath APK下载路径
 * @author walker
 */
var updateAppPlugin = {};

updateAppPlugin.checkAndUpdate = function(checkPath, successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, "UpdateApp", "checkAndUpdate", [checkPath]);
};
updateAppPlugin.getCurrentVerInfo = function(successCallback) {
	cordova.exec(successCallback, null, "UpdateApp", "getCurrentVersion", []);
};
updateAppPlugin.getServerVerInfo = function(successCallback, failureCallback, checkPath) {
	cordova.exec(successCallback, failureCallback, "UpdateApp", "getServerVersion", [checkPath]);
};
updateAppPlugin.getCurrentVersionName = function(successCallback){
	cordova.exec(successCallback, null, "UpdateApp", "getCurrentVersionName", []);
};

module.exports = updateAppPlugin;