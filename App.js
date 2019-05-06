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

  async componentDidMount() {
    try {
      const permissions = [
        PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE
      ];

      const granted = await PermissionsAndroid.requestMultiple(permissions);

      this.setState({
        locationPermission: granted["android.permission.ACCESS_FINE_LOCATION"] === PermissionsAndroid.RESULTS.GRANTED,
        readPhoneStatePermission: granted["android.permission.READ_PHONE_STATE"] === PermissionsAndroid.RESULTS.GRANTED,
        externalStoragePermission: granted["android.permission.WRITE_EXTERNAL_STORAGE"] === PermissionsAndroid.RESULTS.GRANTED,
      });
    } catch (error) {
      console.log(error);
    }
  }

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

    WireCard.init(callback => {
      this.updateSDKStatus();
    });
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
      value: 100,
      details: 'Teste',
      installment: 2,
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
