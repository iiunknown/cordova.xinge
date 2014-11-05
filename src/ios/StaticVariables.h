//
//  StaticVariables.h
//  斯凯移动OA
//
//  Created by 柳杨 on 14-8-26.
//
//

#import <Foundation/Foundation.h>
//用于存储信鸽推送所需的公共变量信息。
@interface StaticVariables : NSObject

//设备识别码对象
@property(strong, nonatomic) NSData * deviceToken;
//设备识别码对象经过信鸽处理过返回的字符串。
@property(strong, nonatomic) NSString* deviceTokenStr;
//登录账号
@property(strong, nonatomic) NSString* account;
//静态实例
+(StaticVariables*)staticInstance;

@end