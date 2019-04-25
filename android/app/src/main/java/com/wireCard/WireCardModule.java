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
import br.com.moip.mpos.MposError;
import br.com.moip.mpos.MposAction;
import br.com.moip.mpos.callback.PinpadCallback;
import br.com.moip.mpos.callback.InitCallback;

public class WireCardModule extends ReactContextBaseJavaModule {

    private final String TOKEN = "AI6P4DIYJVFPARN1JM81T9TW5XWJAA2N";
    private final String PASSWORD = "2UILDC1B7UI8VCVXADT0TDPB5GSM0EXGKBI0QA2A";

    private Activity activity;
    private ReactApplicationContext reactContext;

    public WireCardModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "WireCard";
    }

    @ReactMethod
    public void init(Callback callback) {
        this.setActivity(getCurrentActivity());

        if (this.getActivity() == null) {
            callback.invoke("Erro de activity");
        } else {
            Authentication authentication = new BasicAuth(TOKEN, PASSWORD);

            if (authentication == null) {
                callback.invoke("Falha de autenticação");
            } else {
                MoipMpos.init(activity, MoipMpos.Enviroment.SANDBOX, authentication, new InitCallback() {
                    public void onSuccess() {
                        callback.invoke("SDK iniciado");
                    }

                    public void onError(MposError e) {
                        callback.invoke(e.toString());
                    }
                });
            }
        }
    }

    @ReactMethod
    public void checkMaquininhaStatus(Callback callback) {
        MoipMpos.isPinpadConnected(activity, new PinpadCallback() {
            public void onSuccess() {
                callback.invoke("Maquininha conectada");
            }

            public void onError(MposError e) {
                callback.invoke(e.toString());
            }

            @Override
            public void onActionChanged(MposAction action) {
                callback.invoke(action.getMessage());
            }
        });
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return this.activity;
    }

}