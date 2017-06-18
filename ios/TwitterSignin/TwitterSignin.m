//
//  TwitterSignin.m
//  TwitterSignin
//
//  Created by Justin Nguyen on 22/5/16.
//  Copyright © 2016 Golden Owl. All rights reserved.
//

#import <TwitterKit/TwitterKit.h>
#import <React/RCTEventDispatcher.h>
#import "TwitterSignin.h"

@implementation TwitterSignin

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(logIn:(NSString *)consumerKey consumerSecret:(NSString *)consumerSecret callback:(RCTResponseSenderBlock)callback)
{

  [[Twitter sharedInstance] logInWithCompletion:^(TWTRSession * _Nullable session, NSError * _Nullable error) {
    if (error) {
      NSDictionary *body = @{
                             @"domain":error.domain,
                             @"code":@(error.code),
                             @"userInfo":[error.userInfo description]
                             };
      callback(@[body, [NSNull null]]);
    } else {
      TWTRAPIClient *client = [TWTRAPIClient clientWithCurrentUser];
      NSURLRequest *request = [client URLRequestWithMethod:@"GET"
                                                       URL:@"https://api.twitter.com/1.1/account/verify_credentials.json"
                                                parameters:@{@"skip_status": @"true"}
                                                     error:nil];
      [client sendTwitterRequest:request completion:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
        NSError *jsonError;
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
        NSString *email = @"";
        if (json[@"email"]) {
          email = json[@"email"];
        }
        NSLog(@"email here: %@", email);
        NSDictionary *body = @{@"authToken": session.authToken,
                               @"authTokenSecret": session.authTokenSecret,
                               @"userID":session.userID,
                               @"email": email,
                               @"userName":session.userName};
        NSLog(@"body: %@", body);
        callback(@[[NSNull null], body]);
      }];
    }
  }];
}

@end
