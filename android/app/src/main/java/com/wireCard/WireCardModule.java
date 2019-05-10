package com.wireCard;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import android.app.Application;
import android.app.Activity;

import android.widget.Toast;

import br.com.moip.authentication.Authentication;
import br.com.moip.authentication.BasicAuth;

import br.com.moip.request.ItemRequest;
import br.com.moip.request.MposRequest;

import br.com.moip.mpos.MoipMpos;
import br.com.moip.mpos.MposError;
import br.com.moip.mpos.MposAction;

import br.com.moip.mpos.callback.PinpadCallback;
import br.com.moip.mpos.callback.InitCallback;
import br.com.moip.mpos.callback.MposCallback;

import br.com.moip.mpos.model.request.MposPaymentRequest;
import br.com.moip.mpos.model.response.MposPaymentResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.util.List;

import java.lang.Exception;

import com.google.gson.Gson;

public class WireCardModule extends ReactContextBaseJavaModule {

    private final String TOKEN = "*** TOKEN ***";
    private final String PASSWORD = "*** PASSWORD ***";

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
    public void init(Callback callback) throws Exception {
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
                    }

                    public void onError(MposError e) {
                        setSDKInitializated(false);
                        Gson gson = new Gson();
                        callback.invoke(gson.toJson(e));
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

    @ReactMethod
    public void charge(ReadableMap item, Callback callback) throws JSONException {
        this.setActivity(getCurrentActivity());

        if (this.getActivity() == null) {
            Toast.makeText(getReactApplicationContext(), "Erro de activity", Toast.LENGTH_LONG).show();
        } else {
            JSONObject object = convertMapToJson(item);

            if (object != null) {
                String description = String.valueOf(object.get("description"));
                String details = String.valueOf(object.get("details"));
                int quantity = (Integer) object.get("quantity");
                int value = (Integer) object.get("value");
                int type = (Integer) object.get("type");
                int installment = (Integer) object.get("installment");

                ItemRequest itemRequest = new ItemRequest(description, quantity, details, value);

                List items = Arrays.asList(itemRequest);

                MposPaymentRequest.Type transactionType = type == 1 ? MposPaymentRequest.Type.CREDIT
                        : MposPaymentRequest.Type.DEBIT;

                MposPaymentRequest mposPaymentRequest = new MposPaymentRequest().installment(installment)
                        .type(transactionType).items(items);

                MoipMpos.charge(this.activity, mposPaymentRequest, new MposCallback() {
                    @Override
                    public void onActionChanged(MposAction action) {
                        setStatus(action.toString());
                        setStatusCleared(false);
                    }

                    @Override
                    public void onSuccess(MposPaymentResponse mposPaymentResponse) {
                        Gson gson = new Gson();
                        callback.invoke(gson.toJson(mposPaymentResponse));
                        setStatusCleared(false);
                    }

                    public void onError(MposError e) {
                        Gson gson = new Gson();
                        callback.invoke(gson.toJson(e));
                        setStatusCleared(false);
                    }
                });
            }
        }
    }

    /**
     * Método utilizado para ficar "escutando" as alterações de estado do SDK e/ou da conexão com a maquininha.
     *
     * @param callback
     * @author Lucas Gabriel
     * @since 26/04/2019
     */
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
                } else if (System.currentTimeMillis() - startTime > timeout) {
                    callback.invoke("timeout");
                    timer.cancel();
                }
            }
        }, delay, interval);
    }

    /**
     * Método utilizado para extrair os dados recebidos do Javascript e transformar em um objeto de onde
     * possam ser extraídos os valores necessários para as funcionalidades.
     *
     * @param map
     * @return JSONObject
     * @throws JSONException
     */
    private static JSONObject convertMapToJson(ReadableMap map) throws JSONException {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = map.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (map.getType(key)) {
            case Null:
                object.put(key, JSONObject.NULL);
                break;
            case Boolean:
                object.put(key, map.getBoolean(key));
                break;
            case Number:
                object.put(key, map.getInt(key));
                break;
            case String:
                object.put(key, map.getString(key));
                break;
            }
        }
        return object;
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
