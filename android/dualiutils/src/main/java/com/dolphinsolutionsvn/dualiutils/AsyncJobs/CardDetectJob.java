package com.dolphinsolutionsvn.dualiutils.AsyncJobs;

import android.os.AsyncTask;
import android.util.Log;

import com.dolphinsolutionsvn.dualiutils.DualCardUtils;
import com.dolphinsolutionsvn.dualiutils.Hex;
import com.dolphinsolutionsvn.dualiutils.Interface.CardInteractionInterface;
import com.duali.dualcard.jni.DualCardJni;
import com.duali.dualcard.jni.DualCardResponse;
import com.duali.dualcard.jni.ResponseCode;

public class CardDetectJob extends AsyncTask<Byte, Integer, byte[]> {

    private boolean looping;
    private int port;
    private CardInteractionInterface cardInteraction;

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public CardDetectJob(int port, CardInteractionInterface cardInteraction) {
        this.port = port;
        looping = true;
        this.cardInteraction = cardInteraction;
    }

    @Override
    protected byte[] doInBackground(Byte... params) {
        byte[] data = null;
        while (looping) {
            try {
                if (isCancelled()) {
                    looping = false;
                    break;
                }
                byte baud = (byte) DualCardUtils.BAUD;
                byte cid = (byte) DualCardUtils.CID;
                byte nad = (byte) DualCardUtils.NAD;
                byte option = 0x00;

                Log.d("detectCard", "doInBackground: ." + cardInteraction.getClass().getSimpleName());
                DualCardResponse res = DualCardJni.getInstance().DE_FindCard(port, baud, cid, nad, option);
                if (res.getResponseCode() == ResponseCode.DE_OK) {
                    Log.d("A recive", "doInBackground: " + Hex.bytesToHexString(data));
                    looping = false;
                    Log.d("detectCard: ", "<= 00" + Hex.bytesToHexString(res.getResponseData()));
                } else {
                    int err = res.getResponseCode();
                    Log.d("detectCard: ", "<= " + Hex.byteToHexString((byte) err) + " " + DualCardJni.getInstance().GetErrMsg(err));
                    Thread.sleep(300);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    private void detectCard() {

    }

    @Override
    protected void onPostExecute(byte[] data) {
        super.onPostExecute(data);
        cardInteraction.onCardRecognized();
    }
}
