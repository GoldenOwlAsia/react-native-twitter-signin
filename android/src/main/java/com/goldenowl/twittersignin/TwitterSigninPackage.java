//
//  TwitterSigninPackage.java
//  TwitterSignin
//
//  Created by Justin Nguyen on 22/5/16.
//  Updated at 12/17 by Maurilio Campos.
//

package com.goldenowl.twittersignin;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TwitterSigninPackage implements ReactPackage {

    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();

        modules.add(new TwitterSigninModule(reactContext));
        return modules;
    }

    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList();
    }
}

