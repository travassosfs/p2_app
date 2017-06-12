package com.fstravassos.sirast.slaver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.fstravassos.sirast.R;
import com.fstravassos.sirast.slaver.database.SlaverDataBase;
import com.fstravassos.sirast.slaver.models.Master;
import com.fstravassos.sirast.smsmodule.IListenerReceiver;
import com.fstravassos.sirast.smsmodule.Message;
import com.fstravassos.sirast.smsmodule.Sms;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class StartMenuActivity extends AppCompatActivity implements IListenerReceiver, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    Sms sms = new Sms();
    SlaverDataBase db;
    String number;
    TextView mMasternumber;

    double lat;
    double lng;
    float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu2);
        db = new SlaverDataBase(this);
        sms.setmListener(this);

        mMasternumber = (TextView) findViewById(R.id.master_number);

        List<Master> items = db.getAllMasters();
        if(items.size() > 0) {
            mMasternumber.setText(items.get(0).getNumber());
        }

        ActivityCompat.requestPermissions(StartMenuActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                             Manifest.permission.SEND_SMS},
                1);
        callConnection();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient != null) {
            stopLocationUpdate();
        }
    }

    private synchronized void callConnection() {
        Log.i("LOG", "callConnection()");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {
        initLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, StartMenuActivity.this);
    }

    private void stopLocationUpdate() {
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, StartMenuActivity.this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (l != null) {
            Log.i("LOG", "latitude: " + l.getLatitude());
            Log.i("LOG", "longitude: " + l.getLongitude());
        }

        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed(" + connectionResult + ")");
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        speed = location.getSpeed();

//        tvCoordinate.setText(Html.fromHtml("Location: "+location.getLatitude()+"<br />"+
//                "Longitude: "+location.getLongitude()+"<br />"+
//                "Bearing: "+location.getBearing()+"<br />"+
//                "Altitude: "+location.getAltitude()+"<br />"+
//                "Speed: "+location.getSpeed()+"<br />"+
//                "Provider: "+location.getProvider()+"<br />"+
//                "Accuracy: "+location.getAccuracy()+"<br />"+
//                "Speed: "+ DateFormat.getTimeInstance().format(new Date())+"<br />"));
    }

    @Override
    public void receiveSms(final Message msg) {
        number = msg.getmNumber().toString();
        if (msg.getmText().equals("register sirast slave")) {
            //region add master
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            try {
                                if (db.getAllMasters().size() != 0) {
                                    db.deleteAll();
                                }
                            }catch (Exception e) {
                                Log.e("Error DB (Rmv master): ", e.getMessage());
                            }
                            sms.sendMsg(number, "SIRAST register ok");
                            Master master = new Master();
                            master.setName("master");
                            master.setNumber(number);
                            db.addMaster(master);
                            mMasternumber.setText(number);
                            //myLocation.getLocation(getApplicationContext(), locationResult);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            sms.sendMsg(msg.getmNumber().toString(), "SIRAST registro recusado");
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Pedido de cadastramento de " + msg.getmNumber()).setPositiveButton("Aceitar", dialogClickListener)
                    .setNegativeButton("Recusar", dialogClickListener).show();
            //endregion
        } else if (msg.getmText().equals("SIRAST get location")) {
            if (db.getMaster(number) != null) {
                sms.sendMsg(number, lat + ";" + lng);
            } else
                sms.sendMsg(number, "SIRAST - aparelho não registrado como master");
        } else if (msg.getmText().equals("SIRAST get speed")) {
            if (db.getMaster(number) != null) {
                sms.sendMsg(number, (speed*3600/1000) + "");
            } else
                sms.sendMsg(number, "SIRAST - aparelho não registrado como master");
        } else if (msg.getmText().equals("SIRAST get location&speed")) {
            if (db.getMaster(number) != null) {
                sms.sendMsg(number, lat + ";" + lng + ";" + (speed*3600/1000));
            } else
                sms.sendMsg(number, "SIRAST - aparelho não registrado como master");
        }
    }
}