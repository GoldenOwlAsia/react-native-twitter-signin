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
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.WritableNativeMap;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


public class TwitterSigninModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    public TwitterLoginButton loginButton;

    public TwitterSigninModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "TwitterSignin";
    }

    @ReactMethod
    public void logIn(String consumerKey, String consumerSecret,  final Callback callback) {
        Log.i(">>>", "Login ");
        loginButton = new TwitterLoginButton(getCurrentActivity());
        loginButton.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> sessionResult) {
                Log.d(">>>", "callback result");
                WritableMap result = new WritableNativeMap();
                result.putString("authToken", sessionResult.data.getAuthToken().token);
                result.putString("authTokenSecret",sessionResult.data.getAuthToken().secret);
                result.putString("userID", sessionResult.data.getUserId()+"");
                result.putString("userName", sessionResult.data.getUserName());
                callback.invoke(null, result);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(">>>", "callback error");
                callback.invoke(exception.toString(), null);
            }
        });
        loginButton.performClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (loginButton != null) {
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }
}
