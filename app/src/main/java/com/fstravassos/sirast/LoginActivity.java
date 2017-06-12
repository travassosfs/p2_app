package com.fstravassos.sirast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fstravassos.sirast.master.CreateAccountActivity;
import com.fstravassos.sirast.master.Session;
import com.fstravassos.sirast.master.view.StartMasterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText mEtUser;
    EditText mEtPassword;
    ImageView mIvLogin;
    TextView mTvCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mEtUser = (EditText) findViewById(R.id.et_user);
        mEtPassword = (EditText) findViewById(R.id.et_pass);
        mIvLogin = (ImageView) findViewById(R.id.bt_login);
        mTvCreateAccount = (TextView) findViewById(R.id.tv_create_account);

        mIvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mEtUser.getText().toString();
                String pass = mEtPassword.getText().toString();
                loginService("http://162.243.85.202:3030/sirast/login", user, pass);
            }
        });

        mTvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });
    }

    private void loginService(String url, final String user, final String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        parserLogin(response);
                        startActivity(new Intent(LoginActivity.this, StartMasterActivity.class));
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.getLocalizedMessage() != null ? error.networkResponse.statusCode +"" : "Erro de serviço";
                        Log.e("Error.Response", msg);
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", user);
                params.put("pass", password);

                return params;
            }
        };

        queue.add(putRequest);
    }

    private void parserLogin(String result) {
        try {
            JSONArray jsonObj = new JSONArray(result);

            for (int i = 0; i < jsonObj.length(); i++) {
                JSONObject c = jsonObj.getJSONObject(i);

                int id = c.getInt("id");
                String number = c.getString("numero");
                String user = c.getString("usuario");

                Session.login(user, number, id+"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
