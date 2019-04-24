import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, NativeModules, NativeEventEmitter, TouchableOpacity } from 'react-native';

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
    maquininhaIsConnected: false,
    authenticated: '',
  };

  authenticate = () => {
    const { WireCard } = NativeModules;
    
    WireCard.authenticate();
    this.updateStatus();
  }

  testConnection = () => {
    const { WireCard } = NativeModules;

    WireCard.checkMaquininhaStatus();
    this.updateStatus();
  }

  start = () => {
    const { WireCard } = NativeModules;

    WireCard.start();
  }

  updateStatus = () => {
    const { WireCard } = NativeModules;

    WireCard.getStatus((error, authenticated) => {
      this.setState({ authenticated });
    })
  }

  render() {
    const { maquininhaIsConnected, authenticated } = this.state;

    return (
      <View style={styles.container}>
        <Text style={styles.instructions}>{`Autenticação ${authenticated}`}</Text>

        <TouchableOpacity onPress={this.authenticate}>
          <Text style={styles.instructions}>Autenticar</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={this.testConnection}>
          <Text style={styles.instructions}>Testar conexão</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

export default App;
