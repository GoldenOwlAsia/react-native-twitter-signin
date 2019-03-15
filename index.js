import { NativeModules } from "react-native";

const { RNTwitterSignIn } = NativeModules;

if (__DEV__ && !RNTwitterSignIn) {
  console.error(
    "RN TwitterLogin native module is not correctly linked. Please read the readme, setup and troubleshooting instructions carefully or try manual linking."
  );
}

export { RNTwitterSignIn as TwitterLogin };
