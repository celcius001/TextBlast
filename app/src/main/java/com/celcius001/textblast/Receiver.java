package com.celcius001.textblast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    private static  MessageListener messageListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle data = intent.getExtras();
            Object[] pdus = (Object[]) data.get("pdus");
            for(int i=0; i<pdus.length; i++){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String message = smsMessage.getMessageBody() + " --- " + smsMessage.getDisplayOriginatingAddress();
                Log.d("message", message);
                messageListener.messageReceived(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("error", String.valueOf(e));
        }
    }

    public static void bindListener(MessageListener listener) {
        messageListener = listener;
    }
}
