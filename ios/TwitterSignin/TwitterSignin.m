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
#import <RCTConvert.h>
#import <RCTUtils.h>

@implementation TwitterSignin

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(logIn:(NSString *)consumerKey consumerSecret:(NSString *)consumerSecret callback:(RCTResponseSenderBlock)callback)
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
            callback(@[body, [NSNull null]]);
        } else {
            TWTRAPIClient *client = [TWTRAPIClient clientWithCurrentUser];
            NSURLRequest *request = [client URLRequestWithMethod:@"GET"
                                                             URL:@"https://api.twitter.com/1.1/account/verify_credentials.json"
                                                      parameters:@{@"include_email": @"true", @"skip_status": @"true"}
                                                           error:nil];
            [client sendTwitterRequest:request completion:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
                NSError *jsonError;
                NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
                NSString *email = json[@"email"] ?: @"";
                NSDictionary *body = @{@"authToken": session.authToken,
                                       @"authTokenSecret": session.authTokenSecret,
                                       @"userID":session.userID,
                                       @"email": email,
                                       @"userName":session.userName};
                callback(@[[NSNull null], body]);
            }];
        }
    }];
}


RCT_EXPORT_METHOD(showTweetComposerWithSharingContent:(id)json callback:(RCTResponseSenderBlock)jsCallback) {
    NSLog(@"RCTVkSdkShare#show");
    
    TWTRComposer *composer = [TWTRComposer new];
    
    NSDictionary *contentData = [RCTConvert NSDictionary:json];
    NSError *error;
    [self fillTweet:composer withContent:contentData error:&error];
    
    if (error) {
        jsCallback(@[[self _NSError2JS:(error)], [NSNull null]]);
    }
    else {
        UIWindow *keyWindow = RCTSharedApplication().keyWindow;
        UIViewController *rootViewController = keyWindow.rootViewController;
        
        [composer showFromViewController:rootViewController completion:^(TWTRComposerResult result) {
            NSLog(@"RCTVkSdkShare#show-presented");
        }];
    }
};

RCT_EXPORT_METHOD(getFriendsListWithCallback:(RCTResponseSenderBlock)jsCallback) {
    NSLog(@"RCTTwitterFriendsList#get");
    if (!jsCallback) {
        NSLog(@"RCTTwitterFriendsList#get-nocallback");
        return;
    }
    
    TWTRAPIClient *client = [TWTRAPIClient clientWithCurrentUser];
    NSURLRequest *request = [client URLRequestWithMethod:@"GET"
                                                     URL:@"https://api.twitter.com/1.1/friends/list.json"
                                              parameters:@{@"count": @"200", @"cursor": @"-1"}
                                                   error:nil];
    [client sendTwitterRequest:request completion:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
        if (connectionError) {
            NSLog(@"RCTTwitterFriendsList#get-connectionerror:%@", connectionError);
            jsCallback(@[[self _NSError2JS:connectionError], [NSNull null]]);
        }
        else if (!data) {
            NSLog(@"RCTTwitterFriendsList#get-nodata");
            jsCallback(@[@"nodata", [NSNull null]]);
        }
        else {
            NSError *jsonError;
            NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
            if (jsonError) {
                NSLog(@"RCTTwitterFriendsList#get-JSONerror:%@", jsonError);
                jsCallback(@[[self _NSError2JS:jsonError], [NSNull null]]);
            }
            else {
                NSLog(@"RCTTwitterFriendsList#get-success");
                jsCallback(@[[NSNull null], json[@"users"]]);
            }
        }
    }];
};

RCT_EXPORT_METHOD(getFollowersListWithCallback:(RCTResponseSenderBlock)jsCallback) {
    NSLog(@"RCTTwitterFollowersList#get");
    if (!jsCallback) {
        NSLog(@"RCTTwitterFollowersList#get-nocallback");
        return;
    }
    
    TWTRAPIClient *client = [TWTRAPIClient clientWithCurrentUser];
    NSURLRequest *request = [client URLRequestWithMethod:@"GET"
                                                     URL:@"https://api.twitter.com/1.1/followers/list.json"
                                              parameters:@{@"count": @"200", @"cursor": @"-1"}
                                                   error:nil];
    [client sendTwitterRequest:request completion:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
        if (connectionError) {
            NSLog(@"RCTTwitterFollowersList#get-connectionerror:%@", connectionError);
            jsCallback(@[[self _NSError2JS:connectionError], [NSNull null]]);
        }
        else if (!data) {
            NSLog(@"RCTTwitterFollowersList#get-nodata");
            jsCallback(@[@"nodata", [NSNull null]]);
        }
        else {
            NSError *jsonError;
            NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
            if (jsonError) {
                NSLog(@"RCTTwitterFollowersList#get-JSONerror:%@", jsonError);
                jsCallback(@[[self _NSError2JS:jsonError], [NSNull null]]);
            }
            else {
                NSLog(@"RCTTwitterFollowersList#get-success");
                jsCallback(@[[NSNull null], json[@"users"]]);
            }
        }
    }];
};

#pragma mark - helpers

- (NSDictionary *)_NSError2JS:(NSError *)error {
    NSDictionary *jsError = @{
                              @"code" : [NSNumber numberWithLong:error.code],
                              @"domain" : error.domain,
                              @"description" : error.localizedDescription
                              };
    
    return jsError;
}

- (void)fillTweet:(TWTRComposer *)tweetComposer withContent:(NSDictionary *)content error:(NSError **)error {
    NSString *text = [RCTConvert NSString:content[@"text"]];
    if (text) {
        [tweetComposer setText:text];
    }
    
    NSURL *linkURL = [RCTConvert NSURL:content[@"linkURL"]];
    if (linkURL) {
        [tweetComposer setURL:linkURL];
    }
    
    NSURL *imageURL = [RCTConvert NSURL:content[@"imageURL"]];
    if (imageURL) {
        NSLog(@"RCTTweeterTweet#downloadimage:%@", imageURL);
        
        NSError* downloadError;
        NSMutableURLRequest* urlRequest = [NSMutableURLRequest requestWithURL:imageURL cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:15];
        NSData *imageData = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:nil error:&downloadError];
        
        if (downloadError) {
            NSLog(@"RCTTweeterTweet#downloadimage-failed:%@", downloadError);
            *error = downloadError;
        }
        else {
            NSLog(@"RCTTweeterTweet#downloadimage-downloaded");
            [tweetComposer setImage:[UIImage imageWithData:imageData]];
        }
    }
}
@end