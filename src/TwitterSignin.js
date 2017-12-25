
import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  NativeModules,
  TouchableOpacity,
} from 'react-native';
import PropTypes from 'prop-types';
import Icon from 'react-native-vector-icons/Ionicons';

const { RNTwitterSignIn } = NativeModules;

export class TwitterButton extends Component {
  static propTypes = {
    isLoggedIn: PropTypes.bool,
    successLogin: PropTypes.func.isRequired,
    errorLogin: PropTypes.func.isRequired,
    logOut: PropTypes.func.isRequired,
    textButton: PropTypes.string,
    textLogOut: PropTypes.string,
    size: PropTypes.number,
    style: PropTypes.any,
  };

  static defaultProps = {
    textButton: 'Login with Twitter',
    isLoggedIn: false,
    textLogout: 'Log out',
    size: 32,
  };

  _twitterSignIn = () => {
    const { successLogin, errorLogin } = this.props;

    TwitterSignInSingleton.logIn()
      .then((loginData) => {
        const { authToken, authTokenSecret } = loginData;
        if (authToken && authTokenSecret) {
          successLogin(loginData);
        }
      }).catch((error) => {
        errorLogin(error);
      });
  }

  handleLogout = () => {
    TwitterSignInSingleton.logOut()
    then(() => {
      this.props.logOut();
    });
  }

  render() {
    const { isLoggedIn, textButton, textLogout, style, size } = this.props;
    return (
      <View style={{ flex: 1 }}>
        {
          isLoggedIn
            ?
            <TouchableOpacity
              onPress={this.handleLogout}
            >
              <Text>{textLogout}</Text>
            </TouchableOpacity>
            :
            <Icon.Button name='logo-twitter' size={size} color='white' style={[styles.icon, style]} onPress={this._twitterSignIn}>
              {textButton}
            </Icon.Button>
        }
      </View>
    );
  }
};

const styles = StyleSheet.create({
  icon: {
    width: 200,
    height: 50,
  }
});

class TwitterSignIn {

  constructor() {
    this._user = null;
    this.signinIsInProcess = false;
  }

  init(ComsumerKey, ComsumerSecret) {
    if (!ComsumerKey) {
      throw new Error('TwitterSignIn - Missing ComsumerKey');
    }

    if (!ComsumerSecret) {
      throw new Error('TwitterSignIn - Missing ComsumerSecret');
    }


    RNTwitterSignIn.init(ComsumerKey, ComsumerKey);
    return Promise.resolve(true);
  }

  logIn() {
    return RNTwitterSignIn.logIn();
  }

  logOut() {
    return new Promise((resolve, reject) => {
      RNTwitterSignIn.logOut();
      resolve();
    });
  }

}

const TwitterSignInSingleton = new TwitterSignIn();

export default TwitterSignInSingleton;
