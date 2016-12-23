//
//  TwitterSignin.h
//  TwitterSignin
//
//  Created by Justin Nguyen on 22/5/16.
//  Copyright © 2016 Golden Owl. All rights reserved.
//

#import "RCTBridge.h"
#import <Foundation/Foundation.h>

@interface TwitterSignin : NSObject <RCTBridgeModule> {
    RCTResponseSenderBlock _callback;
}

@end
