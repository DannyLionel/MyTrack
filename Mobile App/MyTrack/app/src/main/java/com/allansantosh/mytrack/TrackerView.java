package com.allansantosh.mytrack;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.ArrayList;

public class TrackerView extends AppCompatActivity {

    TextView nameandemail;
    ArrayList<Tracker> TrackerArrayList;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_view);

        TrackerArrayList = new ArrayList();
        lv = (ListView) findViewById(R.id.list);
        nameandemail = findViewById(R.id.nameandemail);
        nameandemail.setText("Name: " + getIntent().getStringExtra("name") + "\nEmail: " + getIntent().getStringExtra("email"));

        Button register_button = findViewById(R.id.add_new_garage_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TrackerView.this, AddTracker.class);
                intent.putExtra("ID", getIntent().getStringExtra("ID"));
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                startActivity(intent);
            }
        });

        background_for_garage_view bg = new background_for_garage_view(TrackerView.this,TrackerView.this);
        bg.execute(getIntent().getStringExtra("ID"));

    }


    public class background_for_garage_view extends AsyncTask<String, Void,String> {

        Context context;
        Activity activity;

        public Boolean login = false;

        public background_for_garage_view(Context context, Activity activity) {
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
                alert.showDialog(activity, "No tracker! Add a new tracker!");
            }

            else  {

                try {

                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("the_array");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject user_object = jsonArray.getJSONObject(i);
                        TrackerArrayList.add(new Tracker(
                                user_object.getString("id"),
                                user_object.getString("device_id"),
                                user_object.getString("user_id"),
                                user_object.getString("owner_type"),
                                user_object.getString("serial_no"),
                                user_object.getString("name")
                        ));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CustomListAdapter adapter = new CustomListAdapter(TrackerView.this,  TrackerArrayList);
                lv.setAdapter(adapter);

            }
        }

        @Override
        protected String doInBackground(String... voids) {
            String result = "";
            String id = voids[0];

            String connstr = "https://allansantosh.com/DistributedProject/num_tracker_check_json.php";

            try {
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

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
        private ArrayList<Tracker> searchArrayList;
        private LayoutInflater mInflater;
        Context context;

        public CustomListAdapter(Context context, ArrayList<Tracker> results) {
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
            CustomListAdapter.ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.tracker_search_list_layout, null);
                holder = new CustomListAdapter.ViewHolder();
                holder.garage_name = (TextView) convertView.findViewById(R.id.garage_name);
                holder.view_garage = (Button) convertView.findViewById(R.id.view_garage_button);

                convertView.setTag(holder);
            } else {
                holder = (CustomListAdapter.ViewHolder) convertView.getTag();
            }

            holder.garage_name.setText(searchArrayList.get(position).getName());
            holder.view_garage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(TrackerView.this, ManageTracker.class);
                    intent.putExtra("ID", getIntent().getStringExtra("ID"));
                    intent.putExtra("username", getIntent().getStringExtra("username"));
                    intent.putExtra("name", getIntent().getStringExtra("name"));
                    intent.putExtra("email", getIntent().getStringExtra("email"));
                    intent.putExtra("owner_type", searchArrayList.get(position).getOwner_type());
                    intent.putExtra("tracker_id",  searchArrayList.get(position).getDevice_id());
                    intent.putExtra("serial_no",  searchArrayList.get(position).getSerial_no());
                    intent.putExtra("tracker_name", searchArrayList.get(position).getName());
                    startActivity(intent);
                }
            });

            return convertView;
        }
        class ViewHolder {
            TextView garage_name;
            Button view_garage;
        }
    }

    public void onBackPressed(){
        // Do Nothing
    }
}
