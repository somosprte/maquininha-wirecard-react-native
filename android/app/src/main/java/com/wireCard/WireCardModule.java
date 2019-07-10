package com.wireCard;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import android.app.Application;
import android.app.Activity;

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

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class WireCardModule extends ReactContextBaseJavaModule {

  private ReactApplicationContext reactContext;
  private Boolean SDKInitializated;
  private Boolean maquininhaConnected;
  private String status;
  private Boolean statusCleared;
  private Callback callback;

  public WireCardModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.status = null;
  }

  @Override
  public String getName() {
    return "WireCard";
  }

  @ReactMethod
  public void getSDKStatus(Callback callback) {
    callback.invoke(this.SDKInitializated);
  }

  @ReactMethod
  public void getMaquininhaStatus(Callback callback) {
    callback.invoke(this.maquininhaConnected);
  }

  @ReactMethod
  public void init(Callback callback) {
    this.callback = callback;
    MoipMpos.init(getCurrentActivity(), MoipMpos.Enviroment.SANDBOX, new BasicAuth(TOKEN, PASSWORD),
        new InitCallback() {
          public void onSuccess() {
            SDKInitializated = true;
            callback.invoke("SDK inicializado");
          }

          public void onError(MposError e) {
            SDKInitializated = false;
            callback.invoke(e.toString());
          }
        });
  }

  @ReactMethod
  public void checkMaquininhaStatus(Callback callback) {
    this.callback = callback;

    MoipMpos.isPinpadConnected(getCurrentActivity(), new PinpadCallback() {
      public void onSuccess() {
        maquininhaConnected = true;
        callback.invoke("Maquininha conectada");
      }

      public void onError(MposError e) {
        maquininhaConnected = false;
        callback.invoke(e.toString());
      }

      @Override
      public void onActionChanged(MposAction action) {
        status = action.toString();
      }
    });
  }

  @ReactMethod
  public void charge(ReadableMap item, Callback callback) throws JSONException {
    final Callback c = callback;

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

      MposPaymentRequest mposPaymentRequest = new MposPaymentRequest().installment(installment).type(transactionType)
          .items(items);

      MoipMpos.charge(getCurrentActivity(), mposPaymentRequest, new MposCallback() {
        @Override
        public void onActionChanged(MposAction action) {
          status = action.toString();
          statusCleared = false;
        }

        @Override
        public void onSuccess(MposPaymentResponse mposPaymentResponse) {
          Gson gson = new Gson();
          c.invoke(gson.toJson(mposPaymentResponse));
          statusCleared = false;
        }

        public void onError(MposError e) {
          Gson gson = new Gson();
          c.invoke(gson.toJson(e));
          statusCleared = false;
        }
      });
    }
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
      }
    }
    return object;
  }

}
