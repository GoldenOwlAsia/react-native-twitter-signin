//
//  TwitterSigninModule.java
//  TwitterSignin
//
//  Created by Justin Nguyen on 22/5/16.
//  Copyright © 2016 Golden Owl. All rights reserved.
//

package com.goldenowl.twittersignin;

import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactMethod;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import javax.annotation.Nullable;

import io.fabric.sdk.android.Fabric;


public class TwitterSigninModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private final int RESULT_CANCELED = 0;
    TwitterAuthClient twitterAuthClient;

    public TwitterSigninModule(ReactApplicationContext reactContext) {
        super(reactContext);

        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "TwitterSignin";
    }

    @ReactMethod
    public void logIn(ReadableMap options, final Callback callback) {
        final boolean getEmail = options.isNull("requestEmail") ? options.getBoolean("requestEmail") : false;
        String consumerKey = options.getString("consumerKey");
        String consumerSecret = options.getString("consumerSecret");
        TwitterAuthConfig authConfig = new TwitterAuthConfig(consumerKey, consumerSecret);
        Fabric.with(getReactApplicationContext(), new Twitter(authConfig));
        twitterAuthClient = new TwitterAuthClient();

        Twitter.logIn(getCurrentActivity(), new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                final TwitterSession session = result.data;
                TwitterAuthToken twitterAuthToken = session.getAuthToken();
                final WritableMap map = Arguments.createMap();
                map.putString("authToken", twitterAuthToken.token);
                map.putString("authTokenSecret", twitterAuthToken.secret);
                map.putString("name", session.getUserName());
                map.putString("userID", Long.toString(session.getUserId()));
                map.putString("userName", session.getUserName());
                if (getEmail) {
                    twitterAuthClient.requestEmail(session, new com.twitter.sdk.android.core.Callback<String>() {
                        @Override
                        public void success(Result<String> result) {
                            map.putString("email", result.data);
                            callback.invoke(null, map);
                        }

                        @Override
                        public void failure(TwitterException exception) {
                            // invoke callback with no email key
                            callback.invoke(null, map);
                        }
                    });
                } else {
                    callback.invoke(null, map);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("failure", exception.toString());
                callback.invoke(exception, null);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(twitterAuthClient != null && twitterAuthClient.getRequestCode()==requestCode) {
            boolean twitterLoginWasCanceled = (resultCode == RESULT_CANCELED);
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
    }
}
