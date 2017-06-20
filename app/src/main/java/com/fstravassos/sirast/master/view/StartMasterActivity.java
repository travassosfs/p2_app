package com.fstravassos.sirast.master.view;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fstravassos.sirast.LoginActivity;
import com.fstravassos.sirast.R;
import com.fstravassos.sirast.SirastUtils;
import com.fstravassos.sirast.master.CreateAccountActivity;
import com.fstravassos.sirast.master.Session;
import com.fstravassos.sirast.master.models.Slavery;
import com.fstravassos.sirast.master.database.MasterDataBase;
import com.fstravassos.sirast.smsmodule.IListenerReceiver;
import com.fstravassos.sirast.smsmodule.Message;
import com.fstravassos.sirast.smsmodule.Sms;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartMasterActivity extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, IListenerReceiver {

    Sms sms = new Sms();
    String number;
    String name;
    double lat = 0;
    double lng = 0;
    String speed = "";
    String user = "None";
    MasterDataBase db;
    FragmentTransaction ft;
    DialogFragment dialogFragment;
    SupportMapFragment mapFragment;
    TextView mTvAdding;

    private void setPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    private void setToolbarAndFloatButton() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartFragmentTransaction("buscaslave");

                dialogFragment = SearchSlaveDialog.newInstance();
                dialogFragment.show(ft, "buscaslave");
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void restartFragmentTransaction(String tag) {
        ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_master);
        sms.setmListener(this);
        db = new MasterDataBase(this);
        mTvAdding = (TextView) findViewById(R.id.tv_adding);
        ft = getFragmentManager().beginTransaction();

        setPermissions();
        setToolbarAndFloatButton();
        loadSlavers();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void parseSlavers(String result) {

        try {
            JSONArray jsonObj = new JSONArray(result);

            for (int i = 0; i < jsonObj.length(); i++) {
                JSONObject c = jsonObj.getJSONObject(i);

                String name = c.getString("usuario");
                String number = c.getString("numero");

                Slavery slave = new Slavery();
                slave.setName(name);
                slave.setNumber(number);
                db.addSlaver(slave);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadSlavers() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = SirastUtils.mUrl + "/user/slavers?userId="+Session.mId;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseSlavers(response.toString());
                    }
                }
                ,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.getLocalizedMessage() != null ? error.networkResponse.statusCode +"" : "Erro de serviço ao adicionar veículo";
                        Log.e("Error.Response", msg);
                    }
                }
        );

        queue.add(getRequest);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        LatLng location = new LatLng(lat, lng);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

        map.addMarker(new MarkerOptions()
                .title(user)
                .snippet(number)
                .snippet("Velocidade: " + speed)
                .position(location));
    }

    public void getLocation(String number) {
        dialogFragment.dismiss();
        sms.sendMsg(number, "SIRAST get location&speed");
    }

    public void addUser(String name, String number) {
        mTvAdding.setVisibility(View.VISIBLE);
        this.number = number;
        this.name = name;

        sms.sendMsg(number, "register sirast slave");
    }

    @Override
    public void receiveSms(Message msg) {
        String[] latlng = msg.getmText().toString().split(";");
        if (latlng.length == 3) {
            lat = Double.parseDouble(latlng[0]);
            lng = Double.parseDouble(latlng[1]);
            speed = latlng[2];
//            user = db.getSlaver(msg.getmNumber().toString()).getName();

            if (mapFragment == null)
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else if (number != null && msg.getmNumber().contains(number) && msg.getmText().equals("SIRAST register ok")) {
            mTvAdding.setVisibility(View.GONE);
            dialogFragment.dismiss();
            Slavery slave = new Slavery();
            slave.setName(name);
            slave.setNumber(number);
            db.addSlaver(slave);
            addSlaverService(slave);
        }
    }

    private void addSlaverService(final Slavery slave) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest putRequest = new StringRequest(Request.Method.POST, SirastUtils.mUrl + "/user/slavers",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        //startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                        //finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.getLocalizedMessage() != null ? error.networkResponse.statusCode +"" : "Erro de serviço ao adicionar veículo";
                        Log.e("Error.Response", msg);
                    }
                }
        ){

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("number", slave.getNumber());
                params.put("userName", slave.getName());
                params.put("idMaster", Session.mId);

                return params;
            }
        };

        queue.add(putRequest);
    }

    @Override
    public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_master, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "SIRAST - app de rastreio via SMS: https://play.google.com/store");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        } else if (id == R.id.action_contact) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)}); // recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[SIRAST APP]");
            startActivity(Intent.createChooser(emailIntent, "Enviar Email"));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_add) {
            restartFragmentTransaction("addslave");
            dialogFragment = new AddSlaveDialog();
            dialogFragment.show(ft, "addslave");
        } else if (id == R.id.nav_remove) {
            restartFragmentTransaction("removeslave");
            dialogFragment = new RemoveSlaveDialog();
            dialogFragment.show(ft, "removeslave");
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "SIRAST - app de rastreio via SMS: play.google.com");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_contact) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)}); // recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[SIRAST APP]");
            startActivity(Intent.createChooser(emailIntent, "Enviar Email"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
