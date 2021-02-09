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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPage extends AppCompatActivity {

    EditText full_name, email, username, password, retype_password;
    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);


        full_name = findViewById(R.id.full_name_field);
        username = findViewById(R.id.username_field);
        password = findViewById(R.id.password_field);
        retype_password = findViewById(R.id.retype_password_field);
        email = findViewById(R.id.emailaddress_field);

        Button backtologin_button = findViewById(R.id.back_to_home_button);
        backtologin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                startActivity(intent);
            }
        });

        Button register_button = findViewById(R.id.rent_my_car_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (full_name.getText().toString().equalsIgnoreCase("")) {

                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(RegisterPage.this, "Enter your full name!");

                } else {

                    if (username.getText().toString().equalsIgnoreCase("")) {

                        ViewDialog alert = new ViewDialog();
                        alert.showDialog(RegisterPage.this, "Enter your username!");
                    } else {

                        if (password.getText().toString().equalsIgnoreCase("")) {

                            ViewDialog alert = new ViewDialog();
                            alert.showDialog(RegisterPage.this, "Enter your password!");
                        } else {

                            if (retype_password.getText().toString().equalsIgnoreCase("")) {

                                ViewDialog alert = new ViewDialog();
                                alert.showDialog(RegisterPage.this, "Retype your password!");
                            } else {


                                if (!password.getText().toString().equals(retype_password.getText().toString())) {

                                    ViewDialog alert = new ViewDialog();
                                    alert.showDialog(RegisterPage.this, "Your passwords don't match!");
                                } else {

                                    if (email.getText().toString().equalsIgnoreCase("")) {

                                        ViewDialog alert = new ViewDialog();
                                        alert.showDialog(RegisterPage.this, "Enter your email!");
                                    } else {

                                        Pattern pattern = Pattern.compile(regex);
                                        Matcher matcher = pattern.matcher(email.getText().toString());
                                        if (matcher.matches() == false) {


                                            ViewDialog alert = new ViewDialog();
                                            alert.showDialog(RegisterPage.this, "Enter valid email!");

                                        } else {

                                            background_for_register bg = new background_for_register(RegisterPage.this, RegisterPage.this);
                                            bg.execute(full_name.getText().toString(), username.getText().toString(), password.getText().toString(),
                                                    email.getText().toString());

                                        }

                                    }

                                }

                            }

                        }

                    }

                }


            }
        });


    }

    class background_for_register extends AsyncTask<String, Void, String> {

        AlertDialog dialog;
        Context context;
        Activity activity;
        public Boolean login = false;

        public background_for_register(Context context, Activity activity) {
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
                alert.showDialog(activity, "Your registration was successful!");
                full_name.setText("");
                username.setText("");
                password.setText("");
                retype_password.setText("");
                email.setText("");

            } else if (s.contains("email already exists")) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "This email already exists!");

            } else if (s.contains("username already exists")) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "This username already exists!");

            } else {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "Unable to register!");
            }

        }

        @Override
        protected String doInBackground(String... voids) {
            String result = "";
            String full_name = voids[0];
            String username = voids[1];
            String password = voids[2];
            String email = voids[3];

            String connstr = "https://allansantosh.com/DistributedProject/register.php";

            try {
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                String data = URLEncoder.encode("fullname", "UTF-8") + "=" + URLEncoder.encode(full_name, "UTF-8")
                        + "&&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                        + "&&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
                        + "&&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

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
