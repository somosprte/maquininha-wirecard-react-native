package com.wireCard;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.app.Activity;
import android.widget.Toast;
import android.util.Log;

// import br.com.moip.auth.Authentication;
// import br.com.moip.auth.BasicAuth;
// import br.com.moip.MoipMpos;

public class WireCardModule extends ReactContextBaseJavaModule {
    private Activity mActivity;
    private ReactApplicationContext reactContext;
    private Callback callback;
    private Boolean isOn = false;

    private final String TOKEN = "AI6P4DIYJVFPARN1JM81T9TW5XWJAA2N";
    private final String PASSWORD = "2UILDC1B7UI8VCVXADT0TDPB5GSM0EXGKBI0QA2A";

    public WireCardModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "WireCard";
    }

    @ReactMethod
    public void getStatus(Callback successCallback) {
        successCallback.invoke(null, isOn);
    }

    @ReactMethod
    public void turnOn() {
        isOn = true;
    }
    @ReactMethod
    public void turnOff() {
        isOn = false;
    }

    // @ReactMethod
    // public void payment(Callback callback) {
    //    this.callback = callback;

    //    Authentication authentication = new BasicAuth(TOKEN, PASSWORD);

    //    MoipMpos.init(activity, MoipMpos.Enviroment.SANBOX, authentication, new InitCallback() {
    //        public void onSuccess() {
    //            Toast.makeText(getApplicationContext(), "Successo", Toast.LENGTH_LONG);
    //        }
    //        public void onError(MposError e) {
    //            Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG);
    //        }
    //    });

    //    MoipMpos.isPinpadConnected(activity, new PinpadCallback() {
    //        @Override
    //        public void onSuccess() {
    //             Toast.makeText(getApplicationContext(), "Maquininha conectada", Toast.LENGTH_LONG);
    //        }
       
    //        public void onError(MposError e) {
    //             Toast.makeText(getApplicationContext(), "Maquininha não conectada", Toast.LENGTH_LONG);
    //        }
    //    });
    // }

    // @Override
    // public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    // }

    // @Override
    // public void onNewIntent(Intent intent) {

    // }

}