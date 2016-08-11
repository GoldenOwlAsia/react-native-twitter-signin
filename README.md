[![npm version](https://badge.fury.io/js/react-native-luzgan-twitter-signin.svg)](https://badge.fury.io/js/react-native-luzgan-twitter-signin)

Fork of https://github.com/GoldenOwlAsia/react-native-twitter-signin

Forked to add promise support and exceptions support. As well as making it working for RN0.29+.

Also - in opposition to GoldenOwl twitter signin, this plugin have an option to fail if email is not available to you. Note that this is not standard, you can login without email - I've made it this way to support our app.

## Table of contents
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Usage](#usage)
- [Copyright and license](#copyright-and-license)

## Prerequisites

Since Twitter Kit can only be used via [Fabric](https://twitter.com/fabric), make sure you install Fabric properly:

 - *iOS* install Fabric according to this guide: https://docs.fabric.io/ios/fabric/getting-started.html
 - *Android* install Fabric according to this guide: https://docs.fabric.io/android/fabric/migration/android-studio.html

Fabric will use your Twitter accounts to generate API keys and tokens therefore you have to redo this every time you change your Twitter credentials.


## Setup

Firstly, install the npm package:

    npm install react-native-luzgan-twitter-signin --save

#### iOS

It is pretty simple to install it for iOS:

  - Open you Xcode IDE
  - In `Libraries` choose `Add files...` and add the 2 files `ios/TwitterSignin/TwitterSignin.h` and `ios/TwitterSignin/TwitterSignin.m` in your `node_modules/react-native-twitter-signin` folder
  - Rebuild your project to make sure no build error

#### Android

On Android, it will use `Gradle` so all you need to do is to point to the correct project location:

  - In your `${project_dir}/android/settings.gradle` add this:

        include ':react-native-luzgan-twitter-signin'
        project(':react-native-luzgan-twitter-signin').projectDir = new File(rootProject.projectDir,'../node_modules/react-native-luzgan-twitter-signin/android')
  - In your `${project_dir}/android/app/build.gradle` add this:

        depedencies {
           ...
           compile project(':react-native-luzgan-twitter-signin')
           ...
        }
  - RN0.29+ In you `MainApplication.java` makes use of the package as following:

```java
        [...]
        //add in the imports
        import com.luzgan.twittersignin.TwitterSigninPackage;
        [...]
        /**
        * A list of packages used by the app. If the app uses additional views
        * or modules besides the default ones, add more packages here.
        */
        @Override
        protected List<ReactPackage> getPackages() {
           return Arrays.<ReactPackage>asList(
             new MainReactPackage(),
             new TwitterSigninPackage(false) //Add that. If you want to see an error on empty email do new TwitterSigninPackage(true)
           );
         }
```

Keeps in mind that all the configure is for your build tools to recognise the files. So open your Xcode and Android Studio to try making builds and make sure they pass.

## Usage

 - Import your native module as follow:

```javascript

        import React from 'react-native';
        var {
          NativeModules
          } = React;
        const { TwitterSignin } = NativeModules;
        class TwitterButton extends SocialButton {
          _twitterSignIn() {
            TwitterSignin.logIn({TWITTER_COMSUMER_KEY}, {TWITTER_CONSUMER_SECRET})
            .then((data) => {
             //use data
            }).catch((err) => {
             //handle err
            });
          }
          render() {
            return (
              <Icon name='social-twitter' size={32} color='white' style={styles.icon} onPress={this._twitterSignIn.bind(this)}/>
            );
          }
        };
```


## Copyright and license

Code and documentation copyright 2016 Lukasz Holc. Code released under [the MIT license](https://github.com/Luzgan/react-native-twitter-signin/blob/master/LICENSE).
