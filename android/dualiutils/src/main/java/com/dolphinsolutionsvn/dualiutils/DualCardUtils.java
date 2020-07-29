package com.dolphinsolutionsvn.dualiutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Log;

import com.dolphinsolutionsvn.dualiutils.AsyncJobs.CardDetectJob;
import com.dolphinsolutionsvn.dualiutils.Interface.CardInteractionInterface;
import com.duali.dualcard.jni.DualCardJni;

import com.duali.dualcard.jni.DualCardResponse;
import com.duali.dualcard.jni.ResponseCode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class DualCardUtils {

    public enum DataMode {PLAIN, MACED, ENCRYPTED}

    private static final String TAG = "DualCardUtils";

    public static final int BAUD = 115200;
    public static final int CID = 0;
    public static final int NAD = 1;

    private static final int USBFS = 100;
    private static final int DRAGON_PRD_ID = 1540;
    private static final int CUBE_PRD_ID = 1551;
    private static final int VEN_ID = 7602;


    public enum KeyType { TYPE_A, TYPE_B}
    private DualCardJni dualCardJni;
    private boolean connected;
    private int port;
    private Context context;

    private UsbManager usbManager;
    private UsbDevice usbDevice;

    CardDetectJob job;
    ArrayList<String> devices;
    int dragonIndex = -1;
    int cubeIndex = -1;

    private boolean looping = false;
    private boolean isUsing = false;

    public DualCardJni getDualCardJni() {
        return dualCardJni;
    }

    public void initialize(Context context) {
        dualCardJni = DualCardJni.getInstance();
        this.context = context;
        //autoGrandPermission();
    }

    public void autoGrandPermission() {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (device.getVendorId() == VEN_ID && device.getProductId() == DRAGON_PRD_ID) {
                grantAutomaticPermission(device);
            }
        }
    }

    public boolean grantAutomaticPermission(UsbDevice usbDevice)
    {
        try
        {
            Context context= this.context;
            PackageManager pkgManager=context.getPackageManager();
            ApplicationInfo appInfo=pkgManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            Class serviceManagerClass=Class.forName("android.os.ServiceManager");
            Method getServiceMethod=serviceManagerClass.getDeclaredMethod("getService",String.class);
            getServiceMethod.setAccessible(true);
            android.os.IBinder binder=(android.os.IBinder)getServiceMethod.invoke(null, Context.USB_SERVICE);

            Class iUsbManagerClass=Class.forName("android.hardware.usb.IUsbManager");
            Class stubClass=Class.forName("android.hardware.usb.IUsbManager$Stub");
            Method asInterfaceMethod=stubClass.getDeclaredMethod("asInterface", android.os.IBinder.class);
            asInterfaceMethod.setAccessible(true);
            Object iUsbManager=asInterfaceMethod.invoke(null, binder);


            System.out.println("UID : " + appInfo.uid + " " + appInfo.processName + " " + appInfo.permission);
            @SuppressLint("SoonBlockedPrivateApi") final Method grantDevicePermissionMethod = iUsbManagerClass.getDeclaredMethod("grantDevicePermission", UsbDevice.class,int.class);
            grantDevicePermissionMethod.setAccessible(true);
            grantDevicePermissionMethod.invoke(iUsbManager, usbDevice,appInfo.uid);


            System.out.println("Method OK : " + binder + "  " + iUsbManager);
            return true;
        }
        catch(Exception e)
        {
            System.err.println("Error trying to assing automatic usb permission : ");
            e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    public boolean isLooping() {
        return looping;
    }

    public void getConnectedDevice() {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        ArrayList<UsbDevice> usbDevices = new ArrayList<>(deviceList.values());
        for (int i = 0; i < usbDevices.size(); i++) {

            UsbDevice device = usbDevices.get(i);
            Log.d(TAG, "getConnectedDevice: " + device.getDeviceName());
            if (device.getVendorId() == VEN_ID && device.getProductId() == DRAGON_PRD_ID) {
                dragonIndex = i;
            }

            if (device.getVendorId() == VEN_ID && device.getProductId() == CUBE_PRD_ID) {
                cubeIndex = i;
            }
        }

        devices = dualCardJni.DE_FindUsbDevice(context.getApplicationContext());
        if (devices.isEmpty()) {
            //Toast.makeText(activity.getApplicationContext(), "Please connect reader", Toast.LENGTH_SHORT).show();
        }
    }



    public boolean connectDevice() {
        Log.d(TAG, "connectDevice: ");
        if (connected) {
            Log.d(TAG, "connectDevice: already connected");
            return false;
        }

        getConnectedDevice();
        Log.d(TAG, "connectDevice: Dragon index" + dragonIndex);
        for (String string: devices
             ) {
            Log.d(TAG, "connectDevice: " + string);

        }
        if (devices.isEmpty() || dragonIndex == -1) {
            Log.d(TAG, "No device");
            return false;
        }

        for (int index = 0; index < devices.size(); index++) {
            if (devices.get(index).equalsIgnoreCase("Dragon NFC Reader")) {
                if (cubeIndex != -1) {
                    if (dragonIndex > cubeIndex) {
                        port = 101;
                    } else {
                        port = 100;
                    }
                } else {
                    port = 100;
                }

                Log.d("Port", "Port : " + port);
                int response = dualCardJni.DE_InitPort(port, 0);

                if (response == port) {
                    connected = true;
                    Log.d(TAG, "connectDevice: Connected");
                    return true;
                } else {
                    port = -1;
                    Log.d(TAG, "connectDevice: FAIL");
                    return false;
                }
            }
        }

        return false;

    }

    public void dispose() {
        disConnect();
    }

    private void disConnect() {
        if (!connected) {
            Log.d(TAG, "connectDevice: not connect yet");
            return;
        }

        dualCardJni.DE_BuzzerOn(port);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dualCardJni.DE_BuzzerOff(port);
        dualCardJni.DE_ClosePort(port);
        port = -1;
        looping = false;
        connected = false;
    }


    public void startCardDetect(CardInteractionInterface cardInteraction) {
        if (!connected) {
            Log.d(TAG, "loopTestType: device not connected");
            return;
        }

        do {
            if (job != null && job.isLooping()) {
                job.cancel(true);
            }
        } while (job != null && job.isLooping());


        job = new CardDetectJob(port, cardInteraction);
        job.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        looping = true;
    }

    public void stopCardDetect() {
        if (!connected) {
            Log.d(TAG, "loopTestType: device not connected");
            return;
        }

        do {
            if (job != null && job.isLooping()) {
                job.cancel(true);
            }
        } while (job != null && job.isLooping());

        looping = false;
    }

    public String readData(int fileno, int offset, int len) {
        DualCardResponse dualCardResponse;
        String file =                 Hex.intToHexString(fileno).substring(6, 8);


        dualCardResponse = dualCardJni.DE_DESFire_ReadFile_Batch(
            port,
            Hex.hexStringToByte(computeFlag(false, false, true, false, true)),
            Hex.hexStringToByte(Hex.intToHexString(fileno).substring(6, 8)),
            offset,
            len
        );

        return Hex.bytesToASCIIString(dualCardResponse.getResponseData());
    }

    public boolean writeData(int fileNo, int offset, int len, String hexData) {
        DualCardResponse dualCardResponse;
        dualCardResponse = dualCardJni.DE_DESFire_WrietFile_Batch(
                port,
                Hex.hexStringToByte(computeFlag(false, false, true, false, true)),
                Hex.hexStringToByte(Hex.intToHexString(fileNo).substring(6, 8)),
                offset,
                len,
                Hex.hexStringToBytes(hexData)
        );

        return dualCardResponse.getResponseCode() == DualCardJni.DE_OK;
    }

    public boolean loadKey(String aid, String key, int keyNo, int keySize) {
        DualCardResponse dualCardResponse;
        dualCardResponse = dualCardJni.DE_DESFire_SetConfig_Batch(
                port,
                Hex.hexStringToBytes(aid),
                Hex.hexStringToBytes(key),
                keySize,
                keyNo
        );

        return dualCardResponse.getResponseCode() == DualCardJni.DE_OK;
    }

    public String computeFlag(boolean mac, boolean af, boolean crc32, boolean crc16, boolean encipher) {
        String binFlag = "000";

        if (mac) binFlag += "1";
        else binFlag += "0";
        if (af) binFlag += "1";
        else binFlag += "0";
        if (crc32) binFlag += "1";
        else binFlag += "0";
        if (crc16) binFlag += "1";
        else binFlag += "0";
        if (encipher) binFlag += "1";
        else binFlag += "0";

        int decimal = Integer.parseInt(binFlag, 2);
        return Integer.toString(decimal, 16);
    }

    public String APDU(String command) {
        byte [] commandByte = Hex.hexStringToBytes(command);
        DualCardResponse response = dualCardJni.DE_APDU(port, commandByte.length, commandByte);
        if (response.getResponseCode() == DualCardJni.DE_OK) {
            return Hex.bytesToHexString(response.getResponseData());
        } else {
            return "";
        }
    }

    public void anticol()
    {
        try {
            DualCardResponse a = dualCardJni.DEA_Idle_Req(port);
            DualCardResponse b = dualCardJni.DEA_AntiSelLevel(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Boolean authMifare(String key, int block, KeyType keyType)
    {
        byte[] keyInBytes = Hex.hexStringToBytes(key);
        byte keyTypeByte = keyType == KeyType.TYPE_A ? Integer.valueOf(0x00).byteValue() : Integer.valueOf(0x04).byteValue();
        byte blockInByte = Integer.valueOf(block).byteValue();
        DualCardResponse dualCardResponse = dualCardJni.DEA_Authkey(port, keyTypeByte, keyInBytes, blockInByte);
        return dualCardResponse.getResponseCode() == ResponseCode.DE_OK;
    }

    public String readMifare(int block)
    {
        DualCardResponse response = dualCardJni.DEA_Read(port, (Integer.valueOf(block)).byteValue());
        if (response.getResponseCode() == ResponseCode.DE_OK)
        {
            return Hex.bytesToPrettyString(response.getResponseData());
        }

        return "";
    }
}
