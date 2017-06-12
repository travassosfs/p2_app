package com.fstravassos.sirast.master.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fstravassos.sirast.R;
import com.fstravassos.sirast.master.models.Slavery;
import com.fstravassos.sirast.master.database.MasterDataBase;
import com.fstravassos.sirast.smsmodule.IListenerReceiver;
import com.fstravassos.sirast.smsmodule.Message;
import com.fstravassos.sirast.smsmodule.Sms;

public class AddSlaveDialog extends DialogFragment implements IListenerReceiver {

    EditText mEtApelido;
    EditText mEtNumero;
    Button mBtAdd;
    ProgressBar bar;
    LinearLayout mLlBg;
    MasterDataBase db;
    Sms sms = new Sms();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_add_slave, container, false);

        db = new MasterDataBase(getActivity());
        loadComponents(v);

        mBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEtNumero.getText().toString().length() == 11 && db.getSlaver(mEtNumero.getText().toString()) == null) {
                    String name = mEtApelido.getText().toString();
                    String number = mEtNumero.getText().toString();
                    mEtApelido.setText("");
                    mEtNumero.setText("");
                    ((StartMasterActivity) getActivity()).addUser(name, number);
                }
//                    sms.sendMsg(mEtNumero.getText().toString(), "register sirast slave");
            }
        });

        return v;
    }

    @Override
    public void receiveSms(Message msg) {
        String name = mEtApelido.getText().toString();
        String number = mEtNumero.getText().toString();

        Toast.makeText(getActivity(), msg.getmNumber() + " - " + msg.getmText(), Toast.LENGTH_LONG).show();

        if(msg.getmNumber().contains(number) && msg.getmText().equals("SIRAST register ok")) {
            Slavery slave = new Slavery();
            slave.setName(name);
            slave.setNumber(number);
            db.addSlaver(slave);
        }

        mEtApelido.setText("");
        mEtNumero.setText("");


    }

    private void loadComponents(View view) {
        mEtApelido = (EditText) view.findViewById(R.id.et_apelido);
        mEtNumero = (EditText) view.findViewById(R.id.numero);
        mBtAdd = (Button) view.findViewById(R.id.bt_add);
        bar = (ProgressBar) view.findViewById(R.id.progressBar);
        mLlBg = (LinearLayout) view.findViewById(R.id.ll_bg);
    }
}
