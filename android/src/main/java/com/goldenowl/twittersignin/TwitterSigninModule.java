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
import android.app.Activity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactMethod;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Map;
import java.util.Set;


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
    public void logIn(String consumerKey, String consumerSecret,  final Callback callback) {
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(consumerKey, consumerSecret);
        //Fabric.with(getReactApplicationContext(), new Twitter(authConfig));
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
                callback.invoke(null, map);
                /*
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
                */
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("failure", exception.toString());
                callback.invoke(exception.toString(), null);
            }
        });
    }

    @ReactMethod
    public void logOut(boolean forceClearCookies, final Callback callback) {

      // Desperately wnating to logout from
      // fabric's twitter session..


      try {
        TwitterSession ts = TwitterCore
          .getInstance()
          .getSessionManager()
          .getActiveSession();

        if(forceClearCookies || ts != null) {
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
          } else {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(getReactApplicationContext());
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
          }
        }

      } catch(Exception e) {

      }

      try {
          Map<Long, TwitterSession> sessions = Twitter.getSessionManager().getSessionMap();
          System.out.println("TWITTER SEESIONS " + +sessions.size());
          Set<Long> sessids = sessions.keySet();
          for (Long sessid : sessids) {
              System.out.println("TWITTER SESSION CLEARING " + sessid);
              Twitter.getSessionManager().clearSession(sessid);
          }
      } catch(Exception e) {
          System.out.println("TWITTER: logout # clear active session");
          e.printStackTrace();
      }

      try {
          System.out.println("TWITTER CLEARING ACTIVE SESSION");
        Twitter
          .getSessionManager()
          .clearActiveSession();
      } catch (Exception e) {
          System.out.println("TWITTER: logout # clear active session");
          e.printStackTrace();
      }

      try {
          System.out.println("TWITTER LOGGING OUT");
        Twitter.logOut();
      } catch (Exception e) {
        System.out.println("TWITTER: logout # logout");
        e.printStackTrace();
      }

      callback.invoke(null, true);
    }

    @Override
    public void onActivityResult(Activity activity, final int requestCode, final int resultCode, final Intent data) {
        if(twitterAuthClient != null && twitterAuthClient.getRequestCode()==requestCode) {
            boolean twitterLoginWasCanceled = (resultCode == RESULT_CANCELED);
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {}
}
