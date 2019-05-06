import React, { Component } from 'react';
import { StyleSheet, Text, View, NativeModules, Alert, TouchableOpacity, PermissionsAndroid } from 'react-native';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

class App extends Component {
  state = {
    maquininhaConnected: false,
    SDKInitializated: false,
    locationPermission: false,
    externalStoragePermission: false,
    readPhoneStatePermission: false,
  };

  checkMaquininhaStatus = () => {
    const { WireCard } = NativeModules;

    WireCard.checkMaquininhaStatus(callback => {
      this.updateMaquininhaStatus();

      Alert.alert(
        'Alert Title',
        callback,
        [
          { text: 'Cancelar', onPress: () => { }, style: 'cancel' },
          { text: 'OK', onPress: () => { } },
        ],
        { cancelable: false },
      );
    });
  }

  init = async () => {
    const { WireCard } = NativeModules;

    try {
      const permissions = await PermissionsAndroid.requestMultiple([
        PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
      ]);
      await console.log(permissions.android.permission.ACCESS_FINE_LOCATION);
      await this.setState({
        locationPermission: permissions.android.permission.ACCESS_FINE_LOCATION === 'granted',
        readPhoneStatePermission: permissions.android.permission.READ_PHONE_STATE === 'granted',
        externalStoragePermission: permissions.android.permission.WRITE_EXTERNAL_STORAGE = 'granted',
      });
    } catch (err) {
      console.warn(err);
    }

    // WireCard.init(callback => {
    //   this.updateSDKStatus();
    // });
  }

  updateSDKStatus = () => {
    const { WireCard } = NativeModules;

    WireCard.getSDKStatus(SDKInitializated => {
      this.setState({ SDKInitializated });
    });
  }

  updateMaquininhaStatus = () => {
    const { WireCard } = NativeModules;

    WireCard.getMaquininhaStatus(maquininhaConnected => {
      this.setState({ maquininhaConnected });
    });
  }

  charge = () => {
    const { WireCard } = NativeModules;

    const item = {
      description: 'Produto/Serviço',
      quantity: 1,
      value: 1,
      details: 'Teste',
      installments: 2,
      id: 'TESTE TESTE',
      type: 1,
      secondary: '',
      amount: 2,
    };

    WireCard.charge(item, (callback) => {
      Alert.alert(
        'Alert Title',
        callback,
        [
          { text: 'Cancelar', onPress: () => { }, style: 'cancel' },
          { text: 'OK', onPress: () => { } },
        ],
        { cancelable: false },
      );
    });
  }

  render() {
    const { SDKInitializated, maquininhaConnected } = this.state;

    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.init}>
          <Text style={styles.instructions}>Iniciar SDK</Text>
        </TouchableOpacity>

        <Text>SDK {SDKInitializated ? 'inicializado' : 'não inicializado'}</Text>

        <TouchableOpacity onPress={this.checkMaquininhaStatus} disabled={!SDKInitializated}>
          <Text style={styles.instructions}>Testar conexão com a maquininha</Text>
        </TouchableOpacity>

        <Text>Maquininha {maquininhaConnected ? 'conectada' : 'desconectada'}</Text>

        {SDKInitializated && maquininhaConnected && (
          <TouchableOpacity onPress={this.charge}>
            <Text style={styles.instructions}>Realizar pagamento</Text>
          </TouchableOpacity>
        )}
      </View>
    );
  }
}

export default App;
