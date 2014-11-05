//
//  StaticVariables.m
//  斯凯移动OA
//
//  Created by 柳杨 on 14-8-26.
//
//

#import "StaticVariables.h"

@implementation StaticVariables

@synthesize deviceToken;
@synthesize account;
@synthesize deviceTokenStr;

static StaticVariables* _instance;

+(StaticVariables *) staticInstance{
    if (_instance == nil){
        _instance = [[StaticVariables alloc] init];
    }
    return _instance;
}

@end
