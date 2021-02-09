package com.allansantosh.mytrack;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.io.UnsupportedEncodingException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ManageTracker extends AppCompatActivity {

    static private GoogleMap mMap;
    MqttAndroidClient client;
    myCallback callback;
    Button back_to_garage_list, authorized_users_button;
    TextView device_name;
    static TextView device_status, gps_textview, speed_textview;
    static SupportMapFragment mapFragment;
    static String trackername, serial_no;
    static StatusVar gps_error, device_status_var;
    static Button mystats;
    static boolean stats_avaliable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tracker);

        serial_no = getIntent().getStringExtra("serial_no");
        trackername = getIntent().getStringExtra("tracker_name");
        device_name = findViewById(R.id.manage_device_name);
        gps_textview = findViewById(R.id.textView3);
        speed_textview = findViewById(R.id.textView4);
        device_name.setText(trackername);
        device_status = findViewById(R.id.manage_device_status);
        device_status.setText("Offline");
        device_status.setTextColor(Color.RED);
        back_to_garage_list = findViewById(R.id.back_to_garage_list_button);
        authorized_users_button = findViewById(R.id.authrized_users_button);
        mystats = findViewById(R.id.mystats_button);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://144.217.242.17:1883",
                clientId);
        callback = new myCallback();
        client.setCallback(callback);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("TAG", "onSuccess");
                    sub("dis_sys_project/"+getIntent().getStringExtra("serial_no")+"/connection",1);
                    sub("dis_sys_project/"+getIntent().getStringExtra("serial_no")+"/gps",1);
                    sub("dis_sys_project/main_server",1);
                    pub("dis_sys_project/"+getIntent().getStringExtra("serial_no")+"/check_connection","check connection now");
                    pub("dis_sys_project/main_check_connection","check main server connection now");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("TAG", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("TAG", "failed");
        }

        mystats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stats_avaliable) {
                    pub("dis_sys_project/" + getIntent().getStringExtra("serial_no") + "/get_stats", "get stats now");
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(ManageTracker.this, "Email with your driving stats will be sent out soon!");
                }
                else {
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(ManageTracker.this, "Stats not avaliable at this time. Server maybe offline!");

                }
            }
        });

        back_to_garage_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    IMqttToken disconToken = client.disconnect();
                    disconToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // we are now successfully disconnected
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // something went wrong, but probably we are disconnected anyway
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(ManageTracker.this, TrackerView.class);
                intent.putExtra("ID", getIntent().getStringExtra("ID"));
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });

        authorized_users_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getStringExtra("owner_type").equalsIgnoreCase("2")) {
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(ManageTracker.this, "You are only an authorized user for this device. You cannot add additional authorized users!");
                } else {

                    try {
                        IMqttToken disconToken = client.disconnect();
                        disconToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // we are now successfully disconnected
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // something went wrong, but probably we are disconnected anyway
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }


                    Intent intent = new Intent(ManageTracker.this, AuthorizedUsersView.class);
                    intent.putExtra("ID", getIntent().getStringExtra("ID"));
                    intent.putExtra("username", getIntent().getStringExtra("username"));
                    intent.putExtra("name", getIntent().getStringExtra("name"));
                    intent.putExtra("email", getIntent().getStringExtra("email"));
                    intent.putExtra("tracker_id", getIntent().getStringExtra("tracker_id"));
                    intent.putExtra("serial_no", getIntent().getStringExtra("serial_no"));
                    intent.putExtra("tracker_name", getIntent().getStringExtra("tracker_name"));
                    intent.putExtra("owner_type", getIntent().getStringExtra("owner_type"));
                    startActivity(intent);
                }
            }
        });
        gps_error = new StatusVar();
        gps_error.setListener(new StatusVar.ChangeListener() {
            @Override
            public void onChange() {
                if (gps_error.status == true) {
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(ManageTracker.this, "GPS has malfunctioned! Please check device!");
                }
            }
        });

        device_status_var = new StatusVar();
        device_status_var.setListener(new StatusVar.ChangeListener() {
            @Override
            public void onChange() {
                if (device_status_var.status == true) {
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(ManageTracker.this, "Tracker has gone Offline! Please check internet connectivity of the device!");
                }
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getView().setVisibility(View.GONE);

    }

    static public OnMapReadyCallback onMapReadyCallback(float lat, float lng){
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.clear();
                LatLng lat_long = new LatLng(lat, lng);

                mMap.addMarker(new
                        MarkerOptions().position(lat_long).title(trackername)).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_long, 16));
            }
        };
    }


    public void sub(String topic, int qos) {
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void pub(String topic, String payload) {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();

        }
    }

    public void onBackPressed(){
        // Do Nothing
    }

    public static class manage_garage extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String s) {
            String[] splited = s.split("\\s+");
            if (splited[1].equalsIgnoreCase("Online")) {
                device_status_var.setStatus(false);
                device_status.setText("Online");
                device_status.setTextColor(Color.GREEN);
                mapFragment.getView().setVisibility(View.VISIBLE);

                }
            else if (splited[1].equalsIgnoreCase("Offline")) {
                device_status_var.setStatus(true);
                device_status.setText("Offline");
                device_status.setTextColor(Color.RED);
                mapFragment.getView().setVisibility(View.GONE);
                gps_textview.setText("GPS : --");
                speed_textview.setText("Speed : --");
            }
            else if (splited[0].equalsIgnoreCase("dis_sys_project/"+serial_no+"/gps")) {
                if (splited[1].equalsIgnoreCase("GPS_Error")) {
                    mapFragment.getView().setVisibility(View.GONE);
                    gps_textview.setText("GPS : Error");
                    speed_textview.setText("Speed : Error");
                    gps_error.setStatus(true);


                } else {
                    gps_error.setStatus(false);
                    mapFragment.getMapAsync(onMapReadyCallback(Float.parseFloat(splited[1]), Float.parseFloat(splited[2])));
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    gps_textview.setText("GPS : " + splited[1] + ", " + splited[2]);
                    speed_textview.setText("Speed : " + splited[3] + " km/h");
                }
            }
            else if (splited[1].equalsIgnoreCase("Server_Offline")) {
                stats_avaliable = false;

            }
            else if (splited[1].equalsIgnoreCase("Server_Online")) {
                stats_avaliable = true;

            }
        }

        @Override
        protected String doInBackground(String... voids) {
            return voids[0] + " " + voids[1];
        }
    }

    public static class StatusVar {
        private boolean status = false;
        private ChangeListener listener;

        public void setStatus(boolean status) {
            this.status = status;
            if (listener != null) listener.onChange();
        }

        public ChangeListener getListener() {
            return listener;
        }

        public void setListener(ChangeListener listener) {
            this.listener = listener;
        }

        public interface ChangeListener {
            void onChange();
        }
    }

}


