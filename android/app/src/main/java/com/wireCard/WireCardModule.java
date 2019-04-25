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
import br.com.moip.mpos.MposAction;
import br.com.moip.mpos.callback.PinpadCallback;
import br.com.moip.mpos.callback.InitCallback;

import java.util.Timer;
import java.util.TimerTask;

public class WireCardModule extends ReactContextBaseJavaModule {

    private final String TOKEN = "AI6P4DIYJVFPARN1JM81T9TW5XWJAA2N";
    private final String PASSWORD = "2UILDC1B7UI8VCVXADT0TDPB5GSM0EXGKBI0QA2A";

    private Activity activity;
    private ReactApplicationContext reactContext;
    private Boolean SDKInitializated;
    private Boolean maquininhaConnected;
    private String status;
    private Boolean statusCleared;

    public WireCardModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.status = null;
        this.statusCleared = true;
    }

    @Override
    public String getName() {
        return "WireCard";
    }

    @ReactMethod
    public void getSDKStatus(Callback callback) {
        callback.invoke(this.getSDKInitializated());
    }

    @ReactMethod
    public void getMaquininhaStatus(Callback callback) {
        callback.invoke(this.getMaquininhaConnected());
    }

    @ReactMethod
    public void init(Callback callback) {
        this.setActivity(getCurrentActivity());

        if (this.getActivity() == null) {
            Toast.makeText(getReactApplicationContext(), "Erro de activity", Toast.LENGTH_LONG).show();
        } else {
            Authentication authentication = new BasicAuth(TOKEN, PASSWORD);

            if (authentication == null) {
                Toast.makeText(getReactApplicationContext(), "Falha na autenticação", Toast.LENGTH_LONG).show();
            } else {
                setStatusCleared(false);
                MoipMpos.init(activity, MoipMpos.Enviroment.SANDBOX, authentication, new InitCallback() {
                    public void onSuccess() {
                        setSDKInitializated(true);
                        checkStatus(callback);
                        // callback.invoke("SDK inicializado");
                    }

                    public void onError(MposError e) {
                        setSDKInitializated(false);
                        callback.invoke(e.toString());
                    }
                });
            }
        }
    }

    @ReactMethod
    public void checkMaquininhaStatus(Callback callback) {
        this.setActivity(getCurrentActivity());

        if (this.getActivity() == null) {
            Toast.makeText(getReactApplicationContext(), "Erro de activity", Toast.LENGTH_LONG).show();
        } else {
            MoipMpos.isPinpadConnected(activity, new PinpadCallback() {
                public void onSuccess() {
                    setMaquininhaConnected(true);
                    callback.invoke("Maquininha conectada");
                }
    
                public void onError(MposError e) {
                    setMaquininhaConnected(false);
                    callback.invoke(e.toString());
                }
    
                @Override
                public void onActionChanged(MposAction action) {
                    setStatus(action.toString());
                    setStatusCleared(false);
                }
            });
        }
    }

    private void checkStatus(Callback callback) {
		int delay = 0;
		int interval = 100;
		Timer timer = new Timer();
		int timeout = 60000; // 60 seconds
		long startTime = System.currentTimeMillis();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (!getStatusCleared()) {
					setStatusCleared(true);
					callback.invoke(getStatus());
					timer.cancel();
				} else if (System.currentTimeMillis()-startTime > timeout) {
					callback.invoke("timeout");
					timer.cancel();
				}
			}
		}, delay, interval);
	}

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public void setReactcontext(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
    }

    public ReactApplicationContext getReactContext() {
        return this.reactContext;
    }

    public void setSDKInitializated(Boolean SDKInitializated) {
        this.SDKInitializated = SDKInitializated;
    }

    public Boolean getSDKInitializated() {
        return this.SDKInitializated;
    }

    public void setMaquininhaConnected(Boolean maquininhaConnected) {
        this.maquininhaConnected = maquininhaConnected;
    }

    public Boolean getMaquininhaConnected() {
        return this.maquininhaConnected;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatusCleared(Boolean statusCleared) {
        this.statusCleared = statusCleared;
    }

    public Boolean getStatusCleared() {
        return this.statusCleared;
    }

}