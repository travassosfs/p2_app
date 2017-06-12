package com.fstravassos.sirast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.fstravassos.sirast.master.view.StartMasterActivity;

public class SelectUserActivity extends AppCompatActivity {

    //region UI references.

    LinearLayout mMaster;
    LinearLayout mSlave;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadComponents();
        loadActions();
    }

    private void loadActions() {
        mMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(SelectUserActivity.this, StartMasterActivity.class));
                startActivity(new Intent(SelectUserActivity.this, LoginActivity.class));
            }
        });
        mSlave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectUserActivity.this, com.fstravassos.sirast.slaver.StartMenuActivity.class));
            }
        });
    }

    private void loadComponents() {
        mMaster = (LinearLayout) findViewById(R.id.llmaster);
        mSlave = (LinearLayout) findViewById(R.id.llslave);
    }

}

