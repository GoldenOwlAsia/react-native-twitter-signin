
import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Alert,
  NativeModules,
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';

const { RNTwitterSignIn } = NativeModules;

const Constants = {
    //Dev Parse keys
    TWITTER_COMSUMER_KEY: 'Mp0taY9OcO8UhvacuTPU73Xbp',
    TWITTER_CONSUMER_SECRET: 'HWAhDOOUFYsuL4H4w445eEta2lzpxRBN07zxuFZCo5UwbD9RqG',
};

export default class TwitterButton extends Component {
  _twitterSignIn() {
      RNTwitterSignIn.init(Constants.TWITTER_COMSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);
      RNTwitterSignIn.logIn()
        .then((loginData)=>{
          console.log(loginData);
        }).catch((error)=>{
          console.log(error);
        });
  }
  render() {
    return (
      <Icon.Button name='logo-twitter' size={32} color='white' style={styles.icon} onPress={this._twitterSignIn.bind(this)}>
        Login with Twitter
      </Icon.Button>
    );
  }
};

const styles = StyleSheet.create({
  icon: {
    width: 200,
    height: 50,
  }
});