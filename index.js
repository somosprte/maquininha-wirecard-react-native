/**
 * @format
 */

import { AppRegistry, AsyncStorage, PermissionsAndroid } from 'react-native';
import App from './App';
import { name as appName } from './app.json';

const requestPermissions = async () => {
  const permissions = [
    PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
    PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
    PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE
  ];

  const granted = await PermissionsAndroid.requestMultiple(permissions);

  AsyncStorage.setItem('@App:LOCATION_PERMISSION', JSON.parse(granted["android.permission.ACCESS_FINE_LOCATION"] === PermissionsAndroid.RESULTS.GRANTED));
  AsyncStorage.setItem('@App:PHONE_STATE_PERMISSION', JSON.parse(granted["android.permission.READ_PHONE_STATE"] === PermissionsAndroid.RESULTS.GRANTED));
  AsyncStorage.setItem('@App:EXTERNAL_STORAGE_PERMISSION', JSON.parse(granted["android.permission.WRITE_EXTERNAL_STORAGE"] === PermissionsAndroid.RESULTS.GRANTED));
}

requestPermissions();

AppRegistry.registerComponent(appName, () => App);
