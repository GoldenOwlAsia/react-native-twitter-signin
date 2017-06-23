[![npm version](https://badge.fury.io/js/react-native-twitter-signin.svg)](https://badge.fury.io/js/react-native-twitter-signin)

# React Native : Twitter Signin
This package provides necessary code to get your social sign in using Twitter works with least pain possible.

## Table of contents
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Usage](#usage)
- [Todo](#todo)
- [Copyright and license](#copyright-and-license)
- [Inspiration](#inspiration)

## Prerequisites

Go to [Twitter Apps](https://apps.twitter.com/) to create your app so that you can obtain API key and secret, note:

 - Remember to set a Callback Url, whatever will work
 - By default, Twitter won't allow you to grab user's email, so you have to apply for a permission for your app to retrieve user's email

Since Twitter Kit can only be used via [Fabric](https://twitter.com/fabric), make sure you install Fabric properly:

 - *iOS* install Fabric according to this guide: https://docs.fabric.io/ios/fabric/getting-started.html
 - *Android* install Fabric according to this guide: https://docs.fabric.io/android/fabric/migration/android-studio.html

Fabric will use your Twitter accounts to generate API keys and tokens therefore you have to redo this every time you change your Twitter credentials.


## Setup

Firstly, install the npm package:

    npm install react-native-twitter-signin --save

#### iOS

It is pretty simple to install it for iOS:

  - Open you Xcode IDE
  - In `Libraries` choose `Add files...` and add the 2 files `ios/TwitterSignin/TwitterSignin.h` and `ios/TwitterSignin/TwitterSignin.m` in your `node_modules/react-native-twitter-signin` folder
  - Rebuild your project to make sure no build error

#### Android

On Android, it will use `Gradle` so all you need to do is to point to the correct project location:

  - In your `${project_dir}/android/settings.gradle` add this:

        include ':react-native-twitter-signin'
        project(':react-native-twitter-signin').projectDir = new File(rootProject.projectDir,'../node_modules/react-native-twitter-signin/android')
  - In your `${project_dir}/android/app/build.gradle` add this:

        depedencies {
           ...
           compile project(':react-native-twitter-signin')
           ...
        }
  - In you `MainApplication.java` makes use of the package as following:

```java
        
        /**
        * A list of packages used by the app. If the app uses additional views
        * or modules besides the default ones, add more packages here.
        */
        @Override
        protected List<ReactPackage> getPackages() {
           return Arrays.<ReactPackage>asList(
             new MainReactPackage(),
             new FacebookLoginPackage(),
             new TwitterSigninPackage(),
             new RNGoogleSigninPackage(this),
             new VectorIconsPackage(),
             new RNSvgPackage()
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
            TwitterSignin.logIn(Constants.TWITTER_COMSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET, (error, loginData) => {
              if (!error) {
              } else {
                Alert.alert('Invalid login', 'Unable to login');
              }
            });
          }
          render() {
            return (
              <Icon name='social-twitter' size={32} color='white' style={styles.icon} onPress={this._twitterSignIn.bind(this)}/>
            );
          }
        };
```

## Todo
The code was extracted from my project so it satisfies my current need, if you need extra functions, feel free to submit to issue list or fork it.

## Copyright and license

Code and documentation copyright 2016 Justin Nguyen. Code released under [the MIT license](https://github.com/GoldenOwlAsia/react-native-twitter-signin/blob/master/LICENSE).

## Inspiration

[react-native](http://facebook.github.io/react-native/)
[react-native-facebook-login](https://github.com/magus/react-native-facebook-login)


[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=EFCCM8QYU4ZR2&lc=VN&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)

