package com.wireCard;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.app.Application;
import android.app.Activity;
import android.widget.Toast;
import android.util.Log;

import br.com.moip.mpos.Authentication;
import br.com.moip.mpos.BasicAuth;
import br.com.moip.mpos.MoipMpos;

public class WireCardModule extends ReactContextBaseJavaModule {

    private Activity activity;
    private ReactApplicationContext reactContext;
    private Callback callback;
    private Boolean maquininhaIsConnected = false;

    private final String TOKEN = "AI6P4DIYJVFPARN1JM81T9TW5XWJAA2N";
    private final String PASSWORD = "2UILDC1B7UI8VCVXADT0TDPB5GSM0EXGKBI0QA2A";

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
        successCallback.invoke(null, maquininhaIsConnected);
    }

    // @ReactMethod
    // public void start() {
    //     MoipMpos.init(activity, MoipMpos.Enviroment.SANBOX, authentication, new InitCallback() {
    //         public void onSuccess() {
    //             Toast.makeText(getApplicationContext(), "Successo", Toast.LENGTH_LONG);
    //         }
    //         public void onError(MposError e) {
    //             Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG);
    //         }
    //     });
    // }

    @ReactMethod
    public void checkMaquininhaStatus() {
        MoipMpos.isPinpadConnected(activity, new PinpadCallback() {
            @Override
            public void onSuccess() {
                this.setMaquininhaIsConnected(true);
                Callback.invoke("A maquininha está conectada");
            }
        
            public void onError(MposError e) {
                this.setMaquininhaIsConnected(false);
                Callback.invoke("A maquininha não está conectada");
            }
        });
    }
    
    @ReactMethod
    public void authenticate() {
       Authentication authentication = new BasicAuth(TOKEN, PASSWORD);
    }

    public void setMaquininhaIsConnected(Boolean maquininhaIsCoonnected) {
        this.maquininhaIsConnected = maquininhaIsCoonnected;
    }

    public Boolean getMaquininhaIsConnected() {
        return this.maquininhaIsConnected;
    }

}