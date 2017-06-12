package com.fstravassos.sirast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fstravassos.sirast.smsmodule.*;

public class MainActivity extends AppCompatActivity implements IListenerReceiver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Sms sms = new Sms();
        sms.setmListener(this);
        sms.sendMsg("83999753722", "teste");
    }

    @Override
    public void receiveSms(Message msg) {
        Toast.makeText(this, msg.getmText(), Toast.LENGTH_LONG).show();
    }
}
