import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  NativeModules,
  Alert,
  TouchableOpacity,
  PermissionsAndroid,
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
        'Maquininha',
        callback,
        [
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
      description: 'Compra de produto', // Descrição do produto.
      quantity: 2, // Quantidade de produtos comprados.
      value: 100, // Valor unitário do produto, deve ser enviado um valor inteiro para o SDK. 100 = R$ 1,00.
      details: 'Detalhes da compra do produto', // Detalhes da compra.
      installment: 1, // Número das parcelas, utilizado apenas para compras com cartão de crédito.
      type: 1, // Flag utilizada para determinar se a compra deve ser realizada no crédito ou no débito.
    };

    WireCard.charge(item, (payment) => {
      Alert.alert(
        'Pagamento',
        this.getPaymentStatus(JSON.parse(payment)),
        [
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

  getPaymentStatus = payment => {
    switch(payment.status) {
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
    const {
      SDKInitializated,
      maquininhaConnected,
      locationPermission,
      readPhoneStatePermission,
      externalStoragePermission,
    } = this.state;

    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.init} disabled={!locationPermission && !readPhoneStatePermission && !externalStoragePermission}>
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
