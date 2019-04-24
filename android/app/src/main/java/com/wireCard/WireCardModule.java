package com.wireCard;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.app.Application;
import android.app.Activity;
import android.widget.Toast;

import br.com.moip.authentication.Authentication;
import br.com.moip.authentication.BasicAuth;
import br.com.moip.mpos.MoipMpos;
import br.com.moip.mpos.MposError;
// import br.com.moip.models.PinpadCallback;
import br.com.moip.mpos.callback.InitCallback;

public class WireCardModule extends ReactContextBaseJavaModule {

    private final String TOKEN = "AI6P4DIYJVFPARN1JM81T9TW5XWJAA2N";
    private final String PASSWORD = "2UILDC1B7UI8VCVXADT0TDPB5GSM0EXGKBI0QA2A";

    private Activity activity;
    private ReactApplicationContext reactContext;
    private Callback callback;
    private Boolean maquininhaIsConnected;
    private Boolean initialized;
    private Boolean authenticated;

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
        successCallback.invoke(null, initialized);
    }

    @ReactMethod
    public void start() {
        Authentication authentication = new BasicAuth(TOKEN, PASSWORD);

        if (authentication == null) {
            Toast.makeText(getReactApplicationContext(), "Falha de autenticação", Toast.LENGTH_LONG).show();
        } else {
            MoipMpos.init(activity, MoipMpos.Enviroment.SANDBOX, authentication, new InitCallback() {
                public void onSuccess() {
                    Toast.makeText(getReactApplicationContext(), "SDK iniciado", Toast.LENGTH_LONG).show();
                }
                public void onError(MposError e) {
                    Toast.makeText(getReactApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

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

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Boolean getAuthenticated() {
        return this.authenticated;
    }

    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }

    public Boolean getInitialized() {
        return this.initialized;
    }

}