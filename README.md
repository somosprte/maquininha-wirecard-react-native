# wirecard-react-native-bridge

Implementação de bridge para integração do SDK do moip à sua aplicação Android, desenvolvida em react-native.

# Guia de uso

Para utilizar a bridge em seu projeto clone o repositório

```
$ git clone github.com/somosprte/maquininha-wirecard-react-native
```

Adicionar as linhas abaixo no arquivo android/build.gradle

```java
    repositories {
        maven { url "https://packagecloud.io/stone/sdk-android/maven2/" }
        maven { url "https://packagecloud.io/stone/sdk-android-snapshot/maven2" }
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    }
```

E estas linhas no arquivo android/app/build.gradle

```java

    defaultConfig {
      ...
      multiDexEnabled true
    }

    packagingOptions {
        exclude 'META-INF/DEPENDENCIES'
    }

    dependencies {
      ...
      implementation 'br.com.moip.mpos:mpos-android-sdk:5.0.1'
      implementation 'com.google.code.gson:gson:2.8.5'
    }
```

Adicione as seguintes permissões ao AndroidManifest

```xml

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

Além das permissões acima, as permissões abaixo são necessárias para que o SDK seja executado, suas solicitações devem ser feitas em tempo de execução da aplicação, e é recomendado que seja feito ao início da aplicação como um todo. Juntamente com as demais permissões de execução da aplicação.

```java

    android.permission.ACCESS_FINE_LOCATION
    android.permission.READ_PHONE_STATE
    android.permission.WRITE_EXTERNAL_STORAGE
```

Os arquivos da bridge se encontram no pacote java.com.wireCard, copie-os na mesma estrutura, para a sua aplicação. e altere as informações referentes às suas credenciais do wirecard e o ambiente de execução do SDK. Lembrando que, esta implementação utiliza o método BasicAuth de autenticação.

```java

    private final String TOKEN = "SEU_TOKEN";
    private final String PASSWORD = "SEU_PASSWORD";

    MoipMpos.Enviroment.SANDBOX // Ambiente de desenvolvimento
    MoipMpos.Enviroment.PRODUCTION // Ambiente de produção
```

Para utilizar as funcionalidades do SDK, o arquivo App.js contém exemplos das funcionalidades de iniciar o SDK, testar conexão com a maquininha, realizar pagamento, e da requisição das permissões necessárias para execução das funcionalidades.
