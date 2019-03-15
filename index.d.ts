import * as React from "react";
import { StyleProp, ViewProps, ViewStyle } from "react-native";

export interface User {
  authToken?: string;
  authTokenSecret?: string;
  name?: string;
  userID?: string;
  userName?: string;
  email?: string;
}

export namespace TwitterLogin {
  function init(TWITTER_COMSUMER_KEY: string, TWITTER_CONSUMER_SECRET: string): Promise<object>;
  function logIn(): Promise<User>;
  function logOut(): void;
}
