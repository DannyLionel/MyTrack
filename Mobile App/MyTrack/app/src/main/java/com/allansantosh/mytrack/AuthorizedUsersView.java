package com.allansantosh.mytrack;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.Date;

public class AuthorizedUsersView extends AppCompatActivity {

    ArrayList<AuthorizedUser> AuthorizedUserArrayList;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_users_view);

        AuthorizedUserArrayList = new ArrayList();
        lv = (ListView) findViewById(R.id.list);

        Button back_button = findViewById(R.id.back_to_manage_tracker_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AuthorizedUsersView.this, ManageTracker.class);
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

        Button add_auth_user_button = findViewById(R.id.add_authorized_user_button);
        add_auth_user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AuthorizedUsersView.this, AddAuthorizedUser.class);
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

        background_for_auth_user bg = new background_for_auth_user(AuthorizedUsersView.this,AuthorizedUsersView.this);
        bg.execute(getIntent().getStringExtra("tracker_id"));

    }


    public class background_for_auth_user extends AsyncTask<String, Void,String> {

        Context context;
        Activity activity;

        public Boolean login = false;

        public background_for_auth_user(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            if (s.contains("error")) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "No authorized users! Add a new authorized user!");
            }

            else  {

                try {

                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("the_array");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject user_object = jsonArray.getJSONObject(i);
                        AuthorizedUserArrayList.add(new AuthorizedUser(
                                user_object.getString("id"),
                                user_object.getString("name")
                        ));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               AuthorizedUsersView.CustomListAdapter adapter = new AuthorizedUsersView.CustomListAdapter(AuthorizedUsersView.this,  AuthorizedUserArrayList);
                lv.setAdapter(adapter);

            }
        }

        @Override
        protected String doInBackground(String... voids) {
            String result = "";
            String id = voids[0];

            String connstr = "https://allansantosh.com/DistributedProject/num_auth_user_check_json.php";

            try {
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                String data = URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

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

    class CustomListAdapter extends BaseAdapter {
        private ArrayList<AuthorizedUser> searchArrayList;
        private LayoutInflater mInflater;
        Context context;

        public CustomListAdapter(Context context, ArrayList<AuthorizedUser> results) {
            searchArrayList = results;
            mInflater = LayoutInflater.from(context);
            this.context = context;
        }

        public int getCount() {
            return searchArrayList.size();
        }

        public Object getItem(int position) {
            return searchArrayList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            AuthorizedUsersView.CustomListAdapter.ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.authorized_users_search_list_layout, null);
                holder = new AuthorizedUsersView.CustomListAdapter.ViewHolder();
                holder.garage_name = (TextView) convertView.findViewById(R.id.garage_name);
                holder.view_garage = (Button) convertView.findViewById(R.id.view_garage_button);

                convertView.setTag(holder);
            } else {
                holder = (AuthorizedUsersView.CustomListAdapter.ViewHolder) convertView.getTag();
            }

            holder.garage_name.setText(searchArrayList.get(position).getName());
            holder.view_garage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    background_for_auth_user2 bg = new background_for_auth_user2(AuthorizedUsersView.this,AuthorizedUsersView.this);
                    Log.d(searchArrayList.get(position).getId(),getIntent().getStringExtra("tracker_id"));
                    bg.execute(searchArrayList.get(position).getId(),getIntent().getStringExtra("tracker_id"));
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView garage_name;
            Button view_garage;
        }


    }


    public class background_for_auth_user2 extends AsyncTask<String, Void,String> {

        Context context;
        Activity activity;

        public Boolean login = false;

        public background_for_auth_user2(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            if (s.contains("error")) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "Unable to remove user! Please try again!");
            }

            else  {

                AuthorizedUserArrayList.clear();

                try {

                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("the_array");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject user_object = jsonArray.getJSONObject(i);
                        AuthorizedUserArrayList.add(new AuthorizedUser(
                                user_object.getString("id"),
                                user_object.getString("name")
                        ));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AuthorizedUsersView.CustomListAdapter adapter = new AuthorizedUsersView.CustomListAdapter(AuthorizedUsersView.this,  AuthorizedUserArrayList);
                lv.setAdapter(adapter);

            }
        }

        @Override
        protected String doInBackground(String... voids) {
            String result = "";
            String id = voids[0];
            String tracker_id = voids[1];

            String connstr = "https://allansantosh.com/DistributedProject/num_auth_user_remove_and_check_json.php";

            try {
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                String current_time =  new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS").format(new Date());

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                String data = URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(tracker_id, "UTF-8")
                        + "&&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                + "&&" + URLEncoder.encode("current_time", "UTF-8") + "=" + URLEncoder.encode(current_time.toString(), "UTF-8");

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