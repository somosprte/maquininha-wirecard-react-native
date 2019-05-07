import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  NativeModules,
  Alert,
  TouchableOpacity,
  PermissionsAndroid,
  TextInput,
  ScrollView,
} from 'react-native';
import { TextInputMask } from 'react-native-masked-text';

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
    data: {},
    options: {
      precision: 2,
      separator: ',',
      delimiter: '.',
      unit: 'R$',
      suffixUnit: ''
    },
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
    const { data } = this.state;

    const item = {
      description: data.description,
      quantity: Number(data.quantity),
      value: this.moneyField.getRawValue(),
      details: data.details,
      installment: Number(data.installment),
      type: 1,
    };

    console.log(item);

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

  handleChangeInput = (name, value) => {
    const { data } = this.state;

    this.setState({ data: { ...data, [name]: value }});
  }

  render() {
    const {
      SDKInitializated,
      maquininhaConnected,
      locationPermission,
      readPhoneStatePermission,
      externalStoragePermission,
      data,
      options,
    } = this.state;

    return (
      <ScrollView contentContainerStyle={styles.container}>
        <TouchableOpacity onPress={this.init} disabled={!locationPermission && !readPhoneStatePermission && !externalStoragePermission}>
          <Text style={styles.instructions}>Iniciar SDK</Text>
        </TouchableOpacity>

        <Text>SDK {SDKInitializated ? 'inicializado' : 'não inicializado'}</Text>

        <TouchableOpacity onPress={this.checkMaquininhaStatus} disabled={!SDKInitializated}>
          <Text style={styles.instructions}>Testar conexão com a maquininha</Text>
        </TouchableOpacity>

        <Text>Maquininha {maquininhaConnected ? 'conectada' : 'desconectada'}</Text>

        <TextInput
          underlineColorAndroid="transparent"
          placeholder="Descrição"
          value={data.description}
          onChangeText={value => this.handleChangeInput('description', value)}
        />

        <TextInput
          underlineColorAndroid="transparent"
          placeholder="Detalhes"
          value={data.details}
          onChangeText={value => this.handleChangeInput('details', value)}
        />

        <TextInput
          underlineColorAndroid="transparent"
          placeholder="Quantidade"
          keyboardType="numeric"
          value={data.quantity}
          onChangeText={value => this.handleChangeInput('quantity', value)}
        />

        <TextInputMask
          type="money"
          underlineColorAndroid="transparent"
          placeholder="Valor (R$)"
          keyboardType="numeric"
          value={data.value}
          onChangeText={value => this.handleChangeInput('value', value)}
          ref={(ref) => this.moneyField = ref}
        />

        <TextInput
          underlineColorAndroid="transparent"
          placeholder="Parcelas"
          keyboardType="numeric"
          value={data.installment}
          onChangeText={value => this.handleChangeInput('installment', value)}
        />


        {SDKInitializated && maquininhaConnected && (
          <TouchableOpacity onPress={this.charge}>
            <Text style={styles.instructions}>Realizar pagamento</Text>
          </TouchableOpacity>
        )}
      </ScrollView>
    );
  }
}

export default App;
