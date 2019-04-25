import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, NativeModules, Alert, TouchableOpacity } from 'react-native';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

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

  init = () => {
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
      </View>
    );
  }
}

export default App;
