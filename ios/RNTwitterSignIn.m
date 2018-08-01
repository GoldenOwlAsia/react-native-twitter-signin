//
//  TwitterSignin.m
//  TwitterSignin
//
//  Created by Justin Nguyen on 22/5/16.
//  Copyright © 2016 Golden Owl. All rights reserved.
//

#import <TwitterKit/TWTRKit.h>
#import <React/RCTConvert.h>
#import <React/RCTUtils.h>
#import "RNTwitterSignIn.h"

@implementation RNTwitterSignIn

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(init: (NSString *)consumerKey consumerSecret:(NSString *)consumerSecret resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    [[Twitter sharedInstance] startWithConsumerKey:consumerKey consumerSecret:consumerSecret];
}
RCT_EXPORT_METHOD(logIn: (RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    [[Twitter sharedInstance] logInWithCompletion:^(TWTRSession * _Nullable session, NSError * _Nullable error) {
        if (session) {
            TWTRAPIClient *client = [TWTRAPIClient clientWithCurrentUser];

            [client requestEmailForCurrentUser:^(NSString *email, NSError *error) {
                NSString *requestedEmail = (email) ? email : @"";
                NSDictionary *body = @{@"authToken": session.authToken,
                                       @"authTokenSecret": session.authTokenSecret,
                                       @"userID":session.userID,
                                       @"email": requestedEmail,
                                       @"userName":session.userName};
                resolve(body);
            }];
        } else {
            reject(@"Error", @"Twitter signin error", error);
        }
    }];
}

RCT_EXPORT_METHOD(logOut)
{
    TWTRSessionStore *store = [[Twitter sharedInstance] sessionStore];
    NSString *userID = store.session.userID;
    [store logOutUserID:userID];
}
@end
