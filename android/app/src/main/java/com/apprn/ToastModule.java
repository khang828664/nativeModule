package com.apprn;
import android.widget.Toast;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import java.util.Map;
import java.util.HashMap;

public class ToastModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String DURATION_LONG_KEY = "LONG";

  ToastModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
      }
  
  @Override 
  public String getName () {
      return "ToastExample";
  }

  @Override 
  public Map<String, Object> getConstants () {
      final Map<String , Object> constants = new HashMap<> () ;
      constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
      constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);

    return  constants;
  }
  @ReactMethod 
  public void show(String message , int duration) {
      Toast.makeText(getReactApplicationContext() , message, duration).show();
  }
  @ReactMethod 
  public void  minus  ( final int result , Promise promise ) {
    promise.resolve(result + 1); 
 }
}