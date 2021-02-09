package com.allansantosh.mytrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class AddTracker extends AppCompatActivity {

    EditText garage_name, serial_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tracker);

        garage_name = findViewById(R.id.garage_name);
        serial_number = findViewById(R.id.serial_number);

        Button backto_garage_view_button = findViewById(R.id.back_to_garage_view_button);
        backto_garage_view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddTracker.this, TrackerView.class);
                intent.putExtra("ID", getIntent().getStringExtra("ID"));
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });

        Button register_garage_button = findViewById(R.id.register_garage_door_button);
        register_garage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (garage_name.getText().toString().equalsIgnoreCase("")) {

                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(AddTracker.this, "Enter a name for the Tracker!");

                } else {

                    if (serial_number.getText().toString().equalsIgnoreCase("")) {

                        ViewDialog alert = new ViewDialog();
                        alert.showDialog(AddTracker.this, "Enter the device serial number!");
                    } else {

                        background_for_addgarage bg = new background_for_addgarage(AddTracker.this, AddTracker.this);
                        bg.execute(getIntent().getStringExtra("ID"), garage_name.getText().toString(), serial_number.getText().toString());

                    }
                }
            }
        });



    }

    class background_for_addgarage extends AsyncTask<String, Void, String> {

        AlertDialog dialog;
        Context context;
        Activity activity;
        public Boolean login = false;

        public background_for_addgarage(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            if (s.contains("success")) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "Your tracker registration was successful!");
                garage_name.setText("");
                serial_number.setText("");

            } else if (s.contains("device already exists")) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "This device has already been registered!");

            } else {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, s);
            }

        }

        @Override
        protected String doInBackground(String... voids) {
            String result = "";
            String id = voids[0];
            String name = voids[1];
            String serial_number = voids[2];

            String connstr = "https://allansantosh.com/DistributedProject/device_register.php";

            try {
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                        + "&&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                        + "&&" + URLEncoder.encode("serial_no", "UTF-8") + "=" + URLEncoder.encode(serial_number, "UTF-8");

                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();

                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
                ips.close();
                http.disconnect();
                return result;

            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }


            return result;
        }
    }

    public void onBackPressed(){
        // Do Nothing
    }
}
