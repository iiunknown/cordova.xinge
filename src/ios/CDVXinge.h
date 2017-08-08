
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AudioToolbox/AudioServices.h>
#import <Cordova/CDVPlugin.h>

@interface CDVXinge:CDVPlugin

//设备编码，由AppDelegete反向设置。
@property (nonatomic) NSData* deviceToken;
@property NSMutableArray* callbackIdArray;

- (uint32_t) getXingeAccessID;
- (NSString*) getXingeAccessKey;

//注册设备信息

//设置别名（帐号）
- (void)register:(CDVInvokedUrlCommand*)command;
//获取设备识别码字符串
- (void)getToken:(CDVInvokedUrlCommand*)command;
//注销设备
- (void)unregister:(CDVInvokedUrlCommand*)command;
//设置图标脚标
- (void)setBadge:(CDVInvokedUrlCommand*)command;
//获取当前应用的包名称
- (void)getPackageName:(CDVInvokedUrlCommand*)command;
//获取当前应用的版本号
- (void)getVersion:(CDVInvokedUrlCommand*)command;
//当app收到推送信息时触发的方法
- (void)onMessage:(CDVInvokedUrlCommand*)command;

@end
