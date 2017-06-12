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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fstravassos.sirast.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_master);
        sms.setmListener(this);
        db = new MasterDataBase(this);
        mTvAdding = (TextView) findViewById(R.id.tv_adding);
        ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

//        ActivityCompat.requestPermissions(this,new String[]{
//                Manifest.permission.SEND_SMS},1);
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
        }
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
