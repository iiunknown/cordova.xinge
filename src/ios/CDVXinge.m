#import "CDVXinge.h"
#import "CDVAppDelegate+CDVXinge.h"
#import "XGPush.h"
#import <Cordova/CDV.h>


@implementation CDVXinge

@synthesize deviceToken;
@synthesize callbackIdArray;

/**
 * 插件初始化
 */
- (void) pluginInitialize {
    // 注册 推送信息功能注册成功后的回调方法。
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didRegisterForRemoteNotificationsWithDeviceToken:) name:CDVRemoteNotification object:nil];
    
    // 注册 推送信息功能注册失败后的回调方法。
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didFailToRegisterForRemoteNotificationsWithError:) name:CDVRemoteNotificationError object:nil];
    
    // 注册 接收到推送信息后的回调方法。
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveRemoteNotification:) name: CDVXingeDidReceiveRemoteNotification object:nil];
    
    // 启动 XGPush
    [XGPush startApp:self.getXingeAccessID appKey:self.getXingeAccessKey];
    [XGPush initForReregister:^{
        [CDVXinge registerPush];
    }];
}

-(void) register:(CDVInvokedUrlCommand*)command{
    @try {
        NSString* account = [command.arguments objectAtIndex: 0];

        [XGPush setAccount: account];
        [XGPush registerDevice:self.deviceToken successCallback:^{
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: [XGPush getDeviceToken: self.deviceToken]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        } errorCallback:^{
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"registerDevice error."];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];

    }
    @catch (NSException *exception) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: exception.userInfo.description];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

-(void) getToken:(CDVInvokedUrlCommand *)command{
    CDVPluginResult* pluginResult = nil;
    @try {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: [XGPush getDeviceToken: self.deviceToken]];
    }
    @catch (NSException *exception) {
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: exception.userInfo.description];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void) unregister:(CDVInvokedUrlCommand *)command{
    CDVPluginResult* pluginResult = nil;
    [XGPush unRegisterDevice];

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

-(void) setBadge:(CDVInvokedUrlCommand *)command{
    
    CDVPluginResult* pluginResult = nil;
    @try {
        
        int badge = [[command.arguments objectAtIndex:0] intValue];
        [UIApplication sharedApplication].applicationIconBadgeNumber = badge;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    @catch (NSException *exception) {
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: exception.userInfo.description];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void) getPackageName:(CDVInvokedUrlCommand *) command{
    
    CDVPluginResult* pluginResult = nil;
    @try {
        //获取主plist文件信息。
        NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];
        NSString* packageName = [NSString stringWithFormat:@"%@", [infoDict objectForKey:@"CFBundleIdentifier"]];
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: packageName];
    }
    @catch (NSException *exception) {
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: exception.userInfo.description];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void) getVersion:(CDVInvokedUrlCommand*) command{
    
    CDVPluginResult* pluginResult = nil;
    @try {
        //获取主plist文件信息。
        NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];
        NSString* packageVersion = [NSString stringWithFormat:@"%@", [infoDict objectForKey:@"CFBundleVersion"]];
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: packageVersion];
    }
    @catch (NSException *exception) {
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: exception.userInfo.description];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void) onMessage:(CDVInvokedUrlCommand *)command{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
    [self.callbackIdArray addObject: command.callbackId];
    [pluginResult setKeepCallbackAsBool: true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
}
/**
 * 获取信鸽 AccessID
 *
 * @return {uint32_t}     AccessID
 */
- (uint32_t) getXingeAccessID {
  return [[[[NSBundle mainBundle] objectForInfoDictionaryKey:@"XingeVariables"] valueForKey:@"XG_V2_ACCESS_ID"] intValue];
}

/**
 * 获取信鸽 AccessKey
 *
 * @return  {NSString*}  AccessKey
 */
- (NSString*) getXingeAccessKey {
  return [[[NSBundle mainBundle] objectForInfoDictionaryKey:@"XingeVariables"] valueForKey:@"XG_V2_ACCESS_KEY"];
}

#pragma mark RegisterPush Methods
+ (void)registerPush{
    float sysVer = [[[UIDevice currentDevice] systemVersion] floatValue];
    if (sysVer < 8) {
        [CDVXinge registerPushLt8];
    } else {
        [CDVXinge registerPushGeq8];
    }
}
+ (void)registerPushGeq8{
    //Types
    UIUserNotificationType types = UIUserNotificationTypeBadge | UIUserNotificationTypeSound | UIUserNotificationTypeAlert;
    
    //Actions
    UIMutableUserNotificationAction *acceptAction = [[UIMutableUserNotificationAction alloc] init];
    
    acceptAction.identifier = @"ACCEPT_IDENTIFIER";
    acceptAction.title = @"Accept";
    
    acceptAction.activationMode = UIUserNotificationActivationModeForeground;
    acceptAction.destructive = NO;
    acceptAction.authenticationRequired = NO;
    
    //Categories
    UIMutableUserNotificationCategory *inviteCategory = [[UIMutableUserNotificationCategory alloc] init];
    
    inviteCategory.identifier = @"INVITE_CATEGORY";
    
    [inviteCategory setActions:@[acceptAction] forContext:UIUserNotificationActionContextDefault];
    
    [inviteCategory setActions:@[acceptAction] forContext:UIUserNotificationActionContextMinimal];
    
    //[acceptAction release];
    
    NSSet *categories = [NSSet setWithObjects:inviteCategory, nil];
    
    //[inviteCategory release];
    
    
    UIUserNotificationSettings *mySettings = [UIUserNotificationSettings settingsForTypes:types categories:categories];
    
    [[UIApplication sharedApplication] registerUserNotificationSettings:mySettings];
    
    
    [[UIApplication sharedApplication] registerForRemoteNotifications];
}

+ (void)registerPushLt8{
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:(UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound)];
}

#pragma mark NSNotificationCenter Event Methods

/**
 * 设备 接受推送信息功能 注册成功后的回调函数。事件会传递 DeviceToken 到此函数。
 * 此方法执行时会触发客户端脚本中的 CDVRemoteNotification 事件。
 * document.addEventListener('CDVRemoteNotification', function(event) { console.log(event.data.token); });
 *
 * @param  {NSNotification} 事件传递的参数，其中包含成功后返回的 DeviceToken。
 * 
 * @return {null}
 */
- (void) didRegisterForRemoteNotificationsWithDeviceToken:(NSNotification*)notification {
    NSLog(@"[CDVXinge] receive device token: %@", notification.object);
    
    [self setDeviceToken:notification.object];

    // 触发客户端事件
    NSString* jsString = [NSString stringWithFormat:@"cordova.fireDocumentEvent('CDVRemoteNotification', { 'token': '%@'});", [XGPush getDeviceToken:notification.object]];
    [self.commandDelegate evalJs:jsString];
}

/**
 * 设备 接受推送信息功能 注册失败后的回调函数。
 * 此方法执行时会触发客户端脚本中的 CDVRemoteNotificationError 事件。
 * document.addEventListener('CDVRemoteNotificationError', function(event) { console.log(event.data.error); });
 *
 * @param {NSNotification} 事件传递的参数，其中包含失败后返回的错误信息。
 * @return {null}
 */
- (void) didFailToRegisterForRemoteNotificationsWithError:(NSNotification*)notification {
    NSLog(@"[CDVXinge] register fail");
    NSError* error = notification.object;
    NSString* desc = [error localizedDescription];
    NSString* jsString = [NSString stringWithFormat:@"cordova.fireDocumentEvent('CDVRemoteNotificationError', { 'error': '%@'});", desc];
    [self.commandDelegate evalJs:jsString];
}

/**
 * 设备 接受到推送信息 的回调函数。
 * 此函数执行是会触发通过 onMessage 注册的客户端脚本的回调函数。
 * 此函数只有 APP 在前台运行时才会被触发，否则远程推送信息由 iOS 接管。
 *
 * @param  {[type]} void [description]
 * @return {[type]}      [description]
 */
- (void) didReceiveRemoteNotification:(NSNotification*)notification {
    NSLog(@"[CDVXinge] didReceiveRemoteNotification: %@", notification);
    NSLog(@"[CDVXinge] callback ids: %@", self.callbackIdArray);
    [self.callbackIdArray enumerateObjectsUsingBlock:^(id callbackId, NSUInteger idx, BOOL *stop) {
        NSLog(@"[CDVXinge] callbackId: %@", callbackId);
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:notification.object];
        [result setKeepCallback:[NSNumber numberWithBool:YES]];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    }];
}

@end


