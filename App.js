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
    maquininhaIsConnected: false,
    initialized: false,
  };

  authenticate = () => {
    const { WireCard } = NativeModules;
    
    WireCard.authenticate();
  }

  checkMaquininhaStatus = () => {
    const { WireCard } = NativeModules;

    WireCard.checkMaquininhaStatus(callback => {
      Alert.alert(
        'Alert Title',
        callback,
        [
          {text: 'Ask me later', onPress: () => console.log('Ask me later pressed')},
          {
            text: 'Cancel',
            onPress: () => console.log('Cancel Pressed'),
            style: 'cancel',
          },
          {text: 'OK', onPress: () => console.log('OK Pressed')},
        ],
        {cancelable: false},
      );
    });
  }

  init = () => {
    const { WireCard } = NativeModules;

    WireCard.init(callback => {
      Alert.alert(
        'Alert Title',
        callback,
        [
          {text: 'Ask me later', onPress: () => console.log('Ask me later pressed')},
          {
            text: 'Cancel',
            onPress: () => console.log('Cancel Pressed'),
            style: 'cancel',
          },
          {text: 'OK', onPress: () => console.log('OK Pressed')},
        ],
        {cancelable: false},
      );
    });
  }

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.init}>
          <Text style={styles.instructions}>Iniciar</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={this.checkMaquininhaStatus}>
          <Text style={styles.instructions}>Testar conexão</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

export default App;
