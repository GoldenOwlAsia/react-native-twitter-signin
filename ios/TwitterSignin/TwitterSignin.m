//
//  TwitterSignin.m
//  TwitterSignin
//
//  Created by Justin Nguyen on 22/5/16.
//  Copyright © 2016 Golden Owl. All rights reserved.
//
#import <Fabric/Fabric.h>
#import <TwitterKit/TwitterKit.h>
#import "RCTEventDispatcher.h"
#import "TwitterSignin.h"

@implementation TwitterSignin

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();


RCT_EXPORT_METHOD(logIn: (NSString *)consumerKey consumerSecret:(NSString *)consumerSecret resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    [[Twitter sharedInstance] startWithConsumerKey:consumerKey consumerSecret:consumerSecret];
    [Fabric with:@[[Twitter class]]];
    
    [[Twitter sharedInstance] logInWithMethods:TWTRLoginMethodWebBased completion:^(TWTRSession *session, NSError *error) {
        if (error) {
            NSDictionary *body = @{
                                   @"domain":error.domain,
                                   @"code":@(error.code),
                                   @"userInfo":[error.userInfo description]
                                   };
            reject(@"Error", @"Twitter signin error", body);
        } else {
            TWTRAPIClient *client = [TWTRAPIClient clientWithCurrentUser];
            NSURLRequest *request = [client URLRequestWithMethod:@"GET"
                                                             URL:@"https://api.twitter.com/1.1/account/verify_credentials.json"
                                                      parameters:@{@"include_email": @"true", @"skip_status": @"true"}
                                                           error:nil];
            [client sendTwitterRequest:request completion:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
                NSError *jsonError;
                NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
                NSString *email = @"";
                if (json[@"email"]) {
                    email = json[@"email"];
                }
                NSLog(@"email here");
                NSLog(email);
                NSDictionary *body = @{@"authToken": session.authToken,
                                       @"authTokenSecret": session.authTokenSecret,
                                       @"userID":session.userID,
                                       @"email": email,
                                       @"userName":session.userName};
              resolve(@[[NSNull null], body]);
            }];
        }
    }];
}

@end
