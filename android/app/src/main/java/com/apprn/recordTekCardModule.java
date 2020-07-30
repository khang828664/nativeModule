package com.apprn;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.BroadcastReceiver;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.dolphinsolutionsvn.dualiutils.Interface.CardInteractionInterface;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.apprn.EMVCard;
import com.dolphinsolutionsvn.dualiutils.DualCardUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.LifecycleEventListener;

import javax.annotation.RegEx;


public class recordTekCardModule extends ReactContextBaseJavaModule implements  CardInteractionInterface {

    private static ReactContext reactContext;
    private static ReactApplicationContext reactApplicationContext;

    //Const Value
    public static final String DEVICE_ID = "deviceid";
    public static final String MOCK_ID = "792001000479";
    private static final String BANK_CARD_TYPE = "2";
    private static final String TEKMEDI_CARD_TYPE = "1";
    public static final String FIRST_USE = "firstuse";

    // constant MIFARE_KEY_A
    private static final String MIFARE_KEY_A = "6C6564616E67";
    private static final String DEFAULT_MIFARE_KEY_A = "FFFFFFFFFFFF";
    private static final String DEFAULT_MIFARE_KEY_B = "FFFFFFFFFFFF";
    private static final String ACCESS_BIT = "FF078069";
    private static final int DATA_BLOCK = 14;
    private static final int DATA_SECTOR_TRAILER_BLOCK = 15;
    public static String ACTION_REFRESH = "refresh";
    public static boolean NOT_USING_READER = false;
    DualCardUtils dualCardUtils;
    Promise promise;
    public static BroadcastReceiver mBoardCastReceiver;

    recordTekCardModule(ReactApplicationContext context) {

        super(context);
        reactApplicationContext = context;
        reactContext = context;
        dualCardUtils = new DualCardUtils();
        dualCardUtils.initialize(reactContext);
        dualCardUtils.connectDevice();
    }

    @NonNull
    @Override
    public String getName() {
        return "recordTek";
    }


    @ReactMethod
    public  void  startDetect () {
        if (dualCardUtils.isConnected())
        {
            dualCardUtils.startCardDetect(this);
        }
        else
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("NOT_CONNECT",false);
    }
    //Check Listening Native module event
    @ReactMethod
    public  void test () {
        for (int index = 0 ; index < 100 ; index++  ) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("test",index);
        }
    }



     public String getTekmediCard () {
        dualCardUtils.anticol();
        if (dualCardUtils.authMifare(MIFARE_KEY_A, DATA_BLOCK, DualCardUtils.KeyType.TYPE_A)) {
            String data = dualCardUtils.readMifare(DATA_BLOCK);
            if (!data.isEmpty()) {
                if (NOT_USING_READER) {
                    return MOCK_ID;
                }
                return data.substring(55, 67);
            }
            if (NOT_USING_READER) {
                return MOCK_ID;
            }

        }
        return "";
    }
    @Override
    public void onCardRecognized() {
        String cardNumber = getTekmediCard();
        Log.d("CARD_NUMBER",cardNumber);
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("RECORD", cardNumber);
        stopDetect();

//        if(!cardNumber.isEmpty()) {
//            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
//                    .emit("RECORD", cardNumber);
//            stopDetect();
//
//        } else  {
//            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
//                    .emit("RECORD", "CARD RONG");
//            stopDetect();
//        }

    }
    @ReactMethod
    public  void detetchCard (Promise promise) {
        String  card = getTekmediCard();
        promise.resolve(card);
    }

    protected void stopDetect () {
        dualCardUtils.stopCardDetect();
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("STOP_DETECT", true);
    }
    @ReactMethod
    public void  stop () {
        dualCardUtils.stopCardDetect();
    }
}




