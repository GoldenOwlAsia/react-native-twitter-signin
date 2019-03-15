import { NativeModules } from "react-native";

const { TwitterLogin } = NativeModules;

if (__DEV__ && !TwitterLogin) {
  console.error(
    "RN TwitterLogin native module is not correctly linked. Please read the readme, setup and troubleshooting instructions carefully or try manual linking."
  );
}

export { TwitterLogin };
