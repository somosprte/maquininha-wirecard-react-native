package com.wireCard;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import com.facebook.react.modules.core.PermissionListener;

import android.app.Application;
import android.app.Activity;
import android.widget.Toast;

import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;

import br.com.moip.authentication.Authentication;
import br.com.moip.authentication.BasicAuth;

import br.com.moip.request.ItemRequest;
import br.com.moip.request.MposRequest;
import br.com.moip.request.ReceiverRequest;
import br.com.moip.request.AmountRequest;

import br.com.moip.mpos.MoipMpos;
import br.com.moip.mpos.MposError;
import br.com.moip.mpos.MposAction;

import br.com.moip.mpos.callback.PinpadCallback;
import br.com.moip.mpos.callback.InitCallback;
import br.com.moip.mpos.callback.MposCallback;

import br.com.moip.mpos.model.request.MposPaymentRequest;
import br.com.moip.mpos.model.response.MposPaymentResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class WireCardModule extends ReactContextBaseJavaModule implements PermissionListener {

    private final String TOKEN = "AI6P4DIYJVFPARN1JM81T9TW5XWJAA2N";
    private final String PASSWORD = "2UILDC1B7UI8VCVXADT0TDPB5GSM0EXGKBI0QA2A";
    private final String[] PERMISSIONS = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE
    };
    private final int PERMISSION_CODE_REQUEST = 1;

    private Activity activity;
    private ReactApplicationContext reactContext;
    private Boolean SDKInitializated;
    private Boolean maquininhaConnected;
    private String status;
    private Boolean statusCleared;
    private int locationPermission;
    private int storagePermission;
    private int readPhoneStatePermission;
    private Boolean locationPermissionState;
    private Boolean storagePermissionState;
    private Boolean readPhoneStatePermissionState;

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

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_CODE_REQUEST) {
            HashMap<String, Integer> permissionsResult = new HashMap<String, Integer>();
            int deniedcount = 0;

            for (int index = 0; index < grantResults.length; index++) {
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    permissionsResult.put(permissions[index], grantResults[index]);
                    deniedcount++;
                }
            }

            if (deniedcount == 0) {
                this.setLocationPermissionState(true);
                this.setReadPhoneStatePermissionState(true);
                this.setStoragePermissionState(true);

                return true;
            }
        }
        return false;
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
            if (this.hasPermissions(PERMISSIONS)) {
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
                            callback.invoke(e.toString());
                        }
                    });
                }       
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
                String id = String.valueOf(object.get("id"));
                int quantity = (Integer) object.get("quantity");
                int value = (Integer) object.get("value");
                int type = (Integer) object.get("type");
                int installments = (Integer) object.get("installments");

                ItemRequest itemRequest = new ItemRequest(description, quantity, details, value);

                List items = Arrays.asList(itemRequest);

                MposPaymentRequest.Type transactionType = type == 1 ? MposPaymentRequest.Type.DEBIT
                        : MposPaymentRequest.Type.CREDIT;

                MposPaymentRequest mposPaymentRequest = new MposPaymentRequest().installment(installments).ownId(id)
                        .type(transactionType).items(items);

                MoipMpos.charge(this.activity, mposPaymentRequest, new MposCallback() {
                    @Override
                    public void onActionChanged(MposAction action) {
                        setStatus(action.toString());
                        setStatusCleared(false);
                    }

                    @Override
                    public void onSuccess(MposPaymentResponse mposPaymentResponse) {
                        callback.invoke(mposPaymentResponse.toString());
                        setStatusCleared(false);
                    }

                    public void onError(MposError e) {
                        callback.invoke(e.toString());
                        setStatusCleared(false);
                    }
                });
            }
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
                } else if (System.currentTimeMillis() - startTime > timeout) {
                    callback.invoke("timeout");
                    timer.cancel();
                }
            }
        }, delay, interval);
    }

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
            // case Map:
            // object.put(key, convertMapToJson(readableMap.getMap(key)));
            // break;
            // case Array:
            // object.put(key, convertArrayToJson(readableMap.getArray(key)));
            // break;
            }
        }
        return object;
    }

    private boolean hasPermissions(String... permissions) {
        List<String> permissionsNeeded = new ArrayList<String>();

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this.getActivity(), permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PERMISSION_CODE_REQUEST);
            return false;
        }

        return true;
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

    public void setStoragePermissionState(Boolean storagePermissionState) {
        this.storagePermissionState = storagePermissionState;
    }

    public Boolean getStoragePermissionState() {
        return this.storagePermissionState;
    }

    public void setLocationPermissionState(Boolean locationPermissionState) {
        this.locationPermissionState = locationPermissionState;
    }

    public Boolean getLocationPermissionState() {
        return this.locationPermissionState;
    }

    public void setReadPhoneStatePermissionState(Boolean readPhoneStatePermissionState) {
        this.readPhoneStatePermissionState = readPhoneStatePermissionState;
    }

    public Boolean getReadPhoneStatePermissionState() {
        return this.readPhoneStatePermissionState;
    }

    public int getLocationPermition() {
        return this.locationPermission;
    }

    public int getStoragePermission() {
        return this.storagePermission;
    }

    public int getReadPhoneStatePermission() {
        return this.readPhoneStatePermission;
    }

}