//
//  TwitterSigninModule.java
//  TwitterSignin
//
//  Created by Lukasz Holc on 11/08/16.
//  Copyright © 2016 Lukasz Holc. All rights reserved.
//

package com.luzgan.twittersignin;

import android.content.Intent;
import android.util.Log;
import android.app.Activity;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactMethod;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import io.fabric.sdk.android.Fabric;


public class TwitterSigninModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private Boolean errorOnEmptyEmail;
    private final int RESULT_CANCELED = 0;
    TwitterAuthClient twitterAuthClient;
    private Callback callback = null;
    //112 is the average ascii value for every letter in 'twitter'
    private static final int REQUEST_CODE = 112112;
    private ReactApplicationContext reactContext;

    public TwitterSigninModule(ReactApplicationContext reactContext, Boolean errorOnEmptyEmail) {
        super(reactContext);
        this.errorOnEmptyEmail = errorOnEmptyEmail;
        reactContext.addActivityEventListener(this);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "TwitterSignin";
    }

    @ReactMethod
    public void logIn(String consumerKey, String consumerSecret,  final Promise promise) {
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
                twitterAuthClient.requestEmail(session, new com.twitter.sdk.android.core.Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        map.putString("email", result.data);
                        promise.resolve(map);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // invoke callback with no email key
                        if (errorOnEmptyEmail) {
                            promise.reject(exception);
                        } else {
                            promise.resolve(map);
                        }
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                promise.reject(exception);
            }
        });
    }

    @ReactMethod
    public void composeTweet(ReadableMap options, final Callback callback) {
        try {
            this.callback = callback;

            String body = options.hasKey("body") ? options.getString("body") : "";

            TweetComposer.Builder builder = new TweetComposer.Builder(reactContext).text(body);
            final Intent intent = builder.createIntent();
            reactContext.startActivityForResult(intent, REQUEST_CODE, intent.getExtras());

        } catch (Exception e) {
            //error!
            sendCallback(false, false, true);
            throw e;
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if(twitterAuthClient != null && twitterAuthClient.getRequestCode()==requestCode) {
            boolean twitterLoginWasCanceled = (resultCode == RESULT_CANCELED);
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                sendCallback(true, false, false);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                sendCallback(false, true, false);
            }
        }
    }
    
    public void onNewIntent(Intent intent) { }

    public void sendCallback(Boolean completed, Boolean cancelled, Boolean error) {
        if (callback != null) {
            callback.invoke(completed, cancelled, error);
            callback = null;
        }
    }

}
