package com.fstravassos.sirast.smsmodule;

import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

/**
 * Created by felip_000 on 28/11/2016.
 */

public class Sms {

    private SmsManager smsManager = SmsManager.getDefault();

    public void sendMsg(String number, String message) {
        smsManager.sendTextMessage(number, null, message, null, null);
    }

    public SmsMessage receiveSms(Intent intent) {
        SmsMessage[] msg = getMessagesFromIntent(intent);
        if(msg != null) {
            return msg[0];
        }
        return null;
    }

    public void setmListener(IListenerReceiver mListener) {
        Broadcast.mListener = mListener;
    }
}
