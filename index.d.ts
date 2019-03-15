import * as React from "react";
import { StyleProp, ViewProps, ViewStyle } from "react-native";

export interface ConfigureParams {
  TWITTER_COMSUMER_KEY: string;
  TWITTER_CONSUMER_SECRET: string;
}

export interface User {
  authToken?: string;
  authTokenSecret?: string;
  name?: string;
  userID?: string;
  userName?: string;
  email?: string;
}

export namespace TwitterLogin {
  function init(params: ConfigureParams): Promise<object>;
  function logIn(): Promise<User>;
  function logOut(): void;
}
