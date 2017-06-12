package com.fstravassos.sirast.smsmodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/**
 * Created by felip_000 on 05/12/2016.
 */

public class Broadcast extends BroadcastReceiver {

    public static IListenerReceiver mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Sms sms = new Sms();
        SmsMessage msg = sms.receiveSms(intent);
        Message message = new Message();
        message.setmNumber(msg.getDisplayOriginatingAddress());
        message.setmText(msg.getDisplayMessageBody());

        if(mListener != null) {
            mListener.receiveSms(message);
        }
    }
}
