package com.apprn;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.BroadcastReceiver;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.dolphinsolutionsvn.dualiutils.Interface.CardInteractionInterface;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.apprn.EMVCard;
import com.dolphinsolutionsvn.dualiutils.DualCardUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;


public class recordTekCardModule extends ReactContextBaseJavaModule  implements  CardInteractionInterface {

    private static  ReactApplicationContext reactContext;

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
    Promise promise ;
    DualCardUtils dualCardUtils ;
    public String  card = "";

    recordTekCardModule(ReactApplicationContext context) {

        super (context);
               reactContext = context;
        dualCardUtils = new DualCardUtils();
        dualCardUtils.initialize(reactContext);
        dualCardUtils.connectDevice();
        promise = new Promise() {
            @Override
            public void resolve(@Nullable Object value) {

            }

            @Override
            public void reject(String code, String message) {

            }

            @Override
            public void reject(String code, Throwable throwable) {

            }

            @Override
            public void reject(String code, String message, Throwable throwable) {

            }

            @Override
            public void reject(Throwable throwable) {

            }

            @Override
            public void reject(Throwable throwable, WritableMap userInfo) {

            }

            @Override
            public void reject(String code, @NonNull WritableMap userInfo) {

            }

            @Override
            public void reject(String code, Throwable throwable, WritableMap userInfo) {

            }

            @Override
            public void reject(String code, String message, @NonNull WritableMap userInfo) {

            }

            @Override
            public void reject(String code, String message, Throwable throwable, WritableMap userInfo) {

            }

            @Override
            public void reject(String message) {

            }
        };

    }

    @NonNull
    @Override
    public String getName() {

        return "recordTek";
    }

    public String getTekmediCard ()  {
            String tekmediCard = tekmediCard();
            if (!tekmediCard.isEmpty()) {
               return tekmediCard ;
            }
            return "";
    }
    @ReactMethod
    public void  getCard (Promise promise) {

        promise.resolve(card);
    }
    @ReactMethod
    public void startDetect () {
        dualCardUtils.startCardDetect(this);
    }
    public String tekmediCard () {
        dualCardUtils.anticol();
        if (dualCardUtils.authMifare( MIFARE_KEY_A, DATA_BLOCK, DualCardUtils.KeyType.TYPE_A)) {

            String data = dualCardUtils.readMifare(DATA_BLOCK);
            if (!data.isEmpty()) {
                if(NOT_USING_READER) {
                    return MOCK_ID;
                }
                return data.substring(55,67);
            }
            if (NOT_USING_READER) {
                return MOCK_ID ;
            }
        }
        return "" ;
    }

    @Override
    public void onCardRecognized() {
        getTekmediCard();
    }
}
