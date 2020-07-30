package com.apprn;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.content.BroadcastReceiver;
        import android.util.Log;
        import android.view.Window;
        import android.view.WindowManager;

        // import some package connect from JAVA to JS from react-native
        import androidx.annotation.NonNull;

        import com.facebook.react.ReactActivity;

        import com.facebook.react.bridge.ReactApplicationContext;


        // import package Duali

        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;


public class MainActivity extends ReactActivity  {
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

    // New static property
    private static Boolean IS_CONNECTED_USB = true;
    private static Boolean IS_NOT_CONNECTED_USB = false;

    // Event static Name
    private static final String RECORD_TEKMEDI_CARD = "RECORD_TEKMEDI_CARD";
    private static final String RECORD_BANK_CARD = "RECORD_BANK_CARD";
    private static final String CONNECT_USB = "CONNECT";
    private static final String NOT_CONNECT_USB = "NOT_CONNECT";
    private static ReactApplicationContext reactContext;

    // Important  property


    Timer resetTimer;
    TimerTask resetTimerTask;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final String INSURANCE_TYPE = "1";
    private static final String SERVICE_TYPE = "6";

    // Check event usb connect

    @Override
    protected String getMainComponentName() {
        return "appRN";
    }
}


