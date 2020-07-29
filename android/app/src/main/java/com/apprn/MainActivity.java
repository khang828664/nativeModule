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
        import com.facebook.react.ReactInstanceManager;
        import com.facebook.react.ReactPackage;
        import com.facebook.react.bridge.ActivityEventListener;
        import com.facebook.react.bridge.ReactContext;
        import com.facebook.react.bridge.ReactApplicationContext;
        import com.facebook.react.bridge.ReactContextBaseJavaModule;
        import com.facebook.react.bridge.Promise;
        import com.facebook.react.bridge.Callback;
        import com.facebook.react.modules.core.DeviceEventManagerModule;
        import com.facebook.react.bridge.LifecycleEventListener;


        // import package Duali
        import com.dolphinsolutionsvn.dualiutils.DualCardUtils;
        import com.dolphinsolutionsvn.dualiutils.Interface.CardInteractionInterface;

        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;


public class MainActivity extends ReactActivity implements CardInteractionInterface {
    DualCardUtils dualCardUtils;
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
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (dualCardUtils.connectDevice()) {
                Log.d("DEBUG", "ham kiem tra vua chay");
                getReactInstanceManager().getCurrentReactContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("CONNECT", "true");
                Log.d("Debug", "startCardDetectorBoard");
                dualCardUtils.startCardDetect(MainActivity.this);
            } else
                getReactInstanceManager().getCurrentReactContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("NOT_CONNECT", "false");
        }
    };


    @Override
    protected String getMainComponentName() {
        return "appRN";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dualCardUtils = new DualCardUtils();
        dualCardUtils.initialize(this);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "Chạy con cãx");
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        dualCardUtils.connectDevice();
        if (dualCardUtils.isConnected()) {
            dualCardUtils.startCardDetect(MainActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dualCardUtils.stopCardDetect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //onRecognize method
    @Override
    public void onCardRecognized() {
        Log.d("error", "da chay ham xac minh");
        String bankSequence = getBankSequence();
        if (!bankSequence.isEmpty()) {
            EMVCard emvCard = EMVCard.parse(bankSequence);
            if (emvCard != null) {
                String emvSequence = emvCard.toString();
                Log.d("success", "is Run ");
            } else {
                Log.d("error", "Đã xảy ra lỗi");
            }
            return;
        }
        String tekmediNumber = getTekmediNumber();
        if (tekmediNumber == null || tekmediNumber.isEmpty()) {
            Log.d("error", "Không nhận được thẻ ");
            return;
        }
    }

    public String getBankSequence() {
        dualCardUtils.anticol();
        String respone = dualCardUtils.APDU("00A404000E325041592E5359532E444446303100");
        if (respone.isEmpty()) {
            return respone;
        }
        respone = dualCardUtils.APDU("00A4040007A000000727101000");
        if (respone.isEmpty()) {
            return respone;
        }
        respone = dualCardUtils.APDU("80A800000F830D0000000000000000000001084000");
        if (respone.isEmpty()) {
            return respone;
        }
        respone = dualCardUtils.APDU("00B2011400");
        if (respone.isEmpty()) {
            return respone;
        }

        return respone.substring(0, respone.length() - 4);
    }

    public String getTekmediNumber() {
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
}



