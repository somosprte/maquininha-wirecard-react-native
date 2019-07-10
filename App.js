import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  NativeModules,
  Alert,
  TouchableOpacity,
  PermissionsAndroid,
  AsyncStorage,
} from 'react-native';

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

const { WireCard } = NativeModules;

class App extends Component {
  state = {
    maquininhaConnected: false,
    SDKInitializated: false,
  };

  async componentDidMount() {
    const permissions = [
      PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE
    ];

    const granted = await PermissionsAndroid.requestMultiple(permissions);

    AsyncStorage.setItem('@App:LOCATION_PERMISSION', JSON.parse(granted["android.permission.ACCESS_FINE_LOCATION"] === PermissionsAndroid.RESULTS.GRANTED));
    AsyncStorage.setItem('@App:PHONE_STATE_PERMISSION', JSON.parse(granted["android.permission.READ_PHONE_STATE"] === PermissionsAndroid.RESULTS.GRANTED));
    AsyncStorage.setItem('@App:EXTERNAL_STORAGE_PERMISSION', JSON.parse(granted["android.permission.WRITE_EXTERNAL_STORAGE"] === PermissionsAndroid.RESULTS.GRANTED));

    WireCard.checkMaquininhaStatus(checkMaquininhaStatusResponse => {
      WireCard.getMaquininhaStatus(maquininhaConnected => {
        this.setState({ maquininhaConnected });
      });
    });

    WireCard.init(initSDKResponse => {
      WireCard.getSDKStatus(SDKInitializated => {
        this.setState({ SDKInitializated });
      });
    });
  }

  charge = () => {
    const { WireCard } = NativeModules;

    const item = {
      description: 'Compra de produto', // Descrição do produto.
      quantity: 2, // Quantidade de produtos comprados.
      value: 100, // Valor unitário do produto, deve ser enviado um valor inteiro para o SDK. 100 = R$ 1,00.
      details: 'Detalhes da compra do produto', // Detalhes da compra.
      installment: 1, // Número das parcelas, utilizado apenas para compras com cartão de crédito.
      type: 1, // Flag utilizada para determinar se a compra deve ser realizada no crédito ou no débito.
    };

    WireCard.charge(item, (payment) => {
      const response = JSON.parse(payment);
      Alert.alert(
        'Pagamento',
        response.status ? this.getPaymentStatus(response.status) : response.description,
        [
          { text: 'OK', onPress: () => { } },
        ],
        { cancelable: false },
      );
    });
  }

  handleChangeInput = (name, value) => {
    const { data } = this.state;

    this.setState({ data: { ...data, [name]: value } });
  }

  getPaymentStatus = status => {
    switch (status) {
      case 'CREATED':
        return 'Criado';
      case 'WAITING':
        return 'Aguardando pagamento';
      case 'IN_ANALYSIS':
        return 'Em análise';
      case 'PRE_AUTHORIZED':
        return 'Pré-autorizado';
      case 'AUTHORIZED':
        return 'Autorizado';
      case 'CANCELLED':
        return 'Cancelado';
      case 'REFUNDED':
        return 'Eeembolsado';
      case 'REVERSED':
        return 'Estornado';
      case 'SETTLED':
        return 'Concluído';
    }
  }

  render() {
    const { SDKInitializated, maquininhaConnected } = this.state;

    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.charge} disabled={!SDKInitializated && !maquininhaConnected}>
          <Text style={styles.instructions}>Realizar pagamento</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

export default App;
