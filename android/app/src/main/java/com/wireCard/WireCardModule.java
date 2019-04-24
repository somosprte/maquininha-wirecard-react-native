package com.wireCard;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.app.Application;
import android.app.Activity;

import br.com.moip.authentication.Authentication;
import br.com.moip.authentication.BasicAuth;
import br.com.moip.mpos.MoipMpos;
// import br.com.moip.models.PinpadCallback;
// import br.com.moip.models.InitCallback;

public class WireCardModule extends ReactContextBaseJavaModule {

    private final String TOKEN = "AI6P4DIYJVFPARN1JM81T9TW5XWJAA2N";
    private final String PASSWORD = "2UILDC1B7UI8VCVXADT0TDPB5GSM0EXGKBI0QA2A";

    private Activity activity;
    private ReactApplicationContext reactContext;
    private Callback callback;
    private Boolean maquininhaIsConnected = false;
    private String authenticated;
    private Authentication authentication;

    public WireCardModule(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);
        this.reactContext = reactContext;
        this.activity = activity;
    }

    @Override
    public String getName() {
        return "WireCard";
    }

    @ReactMethod
    public void getStatus(Callback successCallback) {
        successCallback.invoke(null, authenticated);
    }

    @ReactMethod
    public void authenticate() {
        authentication = new BasicAuth(TOKEN, PASSWORD);
        this.setAuthenticated(authentication.toString());
    }

    // @ReactMethod
    // public void start() {
    //     MoipMpos.init(activity, MoipMpos.Enviroment.SANDBOX, authentication, new InitCallback() {
    //         public void onSuccess() {
    //             Callback.invoke("SDK iniciado");
    //         }
    //         public void onError(MposError e) {
    //             Callback.invoke("Erro ao iniciar SDK " + e.toString());
    //         }
    //     });
    // }

    // @ReactMethod
    // public void checkMaquininhaStatus() {
    //     MoipMpos.isPinpadConnected(activity, new PinpadCallback() {
    //         @Override
    //         public void onSuccess() {
    //             this.setMaquininhaIsConnected(true);
    //             Callback.invoke("A maquininha está conectada");
    //         }
        
    //         public void onError(MposError e) {
    //             this.setMaquininhaIsConnected(false);
    //             Callback.invoke("A maquininha não está conectada");
    //         }
    //     });
    // }
    
    public void setMaquininhaIsConnected(Boolean maquininhaIsCoonnected) {
        this.maquininhaIsConnected = maquininhaIsCoonnected;
    }

    public Boolean getMaquininhaIsConnected() {
        return this.maquininhaIsConnected;
    }

    public void setAuthenticated(String authenticated) {
        this.authenticated = authenticated;
    }

    public String getAuthenticated() {
        return this.authenticated;
    }

}