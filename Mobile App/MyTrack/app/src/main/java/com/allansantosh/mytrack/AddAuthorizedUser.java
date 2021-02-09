package com.allansantosh.mytrack;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddAuthorizedUser extends AppCompatActivity {

    EditText auth_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_authorized_user);

        auth_user = findViewById(R.id.garage_name);

        Button backto_authorized_view_button = findViewById(R.id.back_to_garage_view_button);
        backto_authorized_view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddAuthorizedUser.this, AuthorizedUsersView.class);
                intent.putExtra("ID", getIntent().getStringExtra("ID"));
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("tracker_id",  getIntent().getStringExtra("tracker_id"));
                intent.putExtra("serial_no",  getIntent().getStringExtra("serial_no"));
                intent.putExtra("tracker_name", getIntent().getStringExtra("tracker_name"));
                intent.putExtra("owner_type", getIntent().getStringExtra("owner_type"));
                startActivity(intent);
            }
        });

        Button register_garage_button = findViewById(R.id.register_garage_door_button);
        register_garage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (auth_user.getText().toString().equalsIgnoreCase("")) {

                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(AddAuthorizedUser.this, "Enter the username of the person you wish to add!");

                } else if (auth_user.getText().toString().equalsIgnoreCase(getIntent().getStringExtra("username")) ) {

                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(AddAuthorizedUser.this, "You cannot add yourself as authorized user. You own the device!");

                } else {

                    AddAuthorizedUser.background_for_add_auth_user bg = new AddAuthorizedUser.background_for_add_auth_user(AddAuthorizedUser.this, AddAuthorizedUser.this);
                    bg.execute(getIntent().getStringExtra("tracker_id"), auth_user.getText().toString());

                }
            }
        });

    }

    class background_for_add_auth_user extends AsyncTask<String, Void, String> {

        Context context;
        Activity activity;

        public background_for_add_auth_user(Context context, Activity activity) {
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
                alert.showDialog(activity, "Authorized user registration was successful!");
                auth_user.setText("");

            } else if (s.contains("user already exists")) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "This user has already been registered as authorized user!");

            }

            else if (s.contains("invalid user")) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "This user does not exist in the system!");

            }

            else if (s.contains("error")) {

            ViewDialog alert = new ViewDialog();
            alert.showDialog(activity, "Error adding the authorized user!");

            }
            else {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, s);
            }
        }

        @Override
        protected String doInBackground(String... voids) {
            String result = "";
            String tracker_id = voids[0];
            String username = voids[1];

            String connstr = "https://allansantosh.com/DistributedProject/add_auth_user.php";

            try {
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                String current_time =  new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS").format(new Date());
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                String data = URLEncoder.encode("tracker_id", "UTF-8") + "=" + URLEncoder.encode(tracker_id, "UTF-8")
                        + "&&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                + "&&" + URLEncoder.encode("current_time", "UTF-8") + "=" + URLEncoder.encode(current_time, "UTF-8");

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
