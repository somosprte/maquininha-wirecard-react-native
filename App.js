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
    isOn: false,
  };

  turnOn = () => {
    NativeModules.WireCard.turnOn();
    this.updateStatus()
  }
  turnOff = () => {
    NativeModules.WireCard.turnOff();
    this.updateStatus()
  }
  updateStatus = () => {
    NativeModules.WireCard.getStatus( (error, isOn)=>{
    this.setState({ isOn: isOn});
  })
  }

  render() {
    const { isOn } = this.state;

    return (
      <View style={styles.container}>
        <Text style={styles.instructions}>Bulb is {isOn ? "ON": "OFF"}</Text>

        <TouchableOpacity onPress={isOn ? () => this.turnOff() : () => this.turnOn()}>
          <Text style={styles.instructions}>Testar conexão</Text>
          </TouchableOpacity>
      </View>
    );
  }
}

export default App;
