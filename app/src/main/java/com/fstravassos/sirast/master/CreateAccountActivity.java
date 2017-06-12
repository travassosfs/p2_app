package com.fstravassos.sirast.master;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fstravassos.sirast.LoginActivity;
import com.fstravassos.sirast.R;
import com.fstravassos.sirast.master.view.StartMasterActivity;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    EditText mUserName;
    EditText mNumber;
    EditText mPassword;
    Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mUserName = (EditText) findViewById(R.id.et_email);
        mNumber = (EditText) findViewById(R.id.et_number);
        mPassword = (EditText) findViewById(R.id.et_pass);
        mSave = (Button) findViewById(R.id.bt_save);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mUserName.getText().toString();
                String number = mNumber.getText().toString();
                String pass = mPassword.getText().toString();
                registerService("http://162.243.85.202:3030/sirast/user", user, number, pass);
            }
        });
    }

    private void registerService(String url, final String user, final String number, final String pass) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest putRequest = new StringRequest(Request.Method.POST    , url,
            new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                    finish();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String msg = error.getLocalizedMessage() != null ? error.networkResponse.statusCode +"" : "Erro de serviço";
                    Log.e("Error.Response", msg);
                }
            }
        ){

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("number", number);
                params.put("userName", user);
                params.put("pass", pass);

                return params;
            }
        };

        queue.add(putRequest);
    }
}
