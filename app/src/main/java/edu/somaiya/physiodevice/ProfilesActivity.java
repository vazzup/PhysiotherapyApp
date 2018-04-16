package edu.somaiya.physiodevice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ProfilesActivity extends AppCompatActivity {

    Button refreshButton, calibrateButton;
    ListView profilesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.refreshButton = (Button) findViewById(R.id.refresh_button);
        this.calibrateButton = (Button) findViewById(R.id.calibrate_button);
        this.profilesList = (ListView) findViewById(R.id.profiles_list);

        profilesList.setOnItemClickListener(((adapterView, view, i, l) -> {
            Log.d("ITEM", "Item Clicked!");
            new AsyncStartTrainingTask().execute(Integer.parseInt(((TextView) adapterView.getItemAtPosition(i)).getText().toString().trim()));
        }));

        this.profilesList.setAdapter(new ArrayAdapter<String>(this, R.layout.profile_list_layout, new ArrayList<String>()));

        calibrateButton.setOnClickListener((view) -> {
            new AsyncCalibrateTask().execute();
        });

        refreshButton.setOnClickListener((view) -> {
                new AsyncRefreshTask().execute();
        });
    }

    private class AsyncCalibrateTask extends AsyncTask<Void, Void, Void> {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = "http://" + MainActivity.server_ip_address + "/start_calibration";
            Log.d("CALIBURL", urlString);
            String result;
            try {
                URL myUrl = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                connection.connect();

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;

                while ((inputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                bufferedReader.close();
                inputStreamReader.close();

                result = stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }); */
            return null;
        }
    }

    private class AsyncRefreshTask extends AsyncTask<Void, Void, Void> {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        public JSONObject jsonObject;
        ArrayAdapter arrayAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrayAdapter = (ArrayAdapter) profilesList.getAdapter();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                String[] profileNames = jsonObject.getJSONArray("profile_nos").join(",").split(",");
                Log.d("PostExecute", profileNames[0] + "x" + profileNames[1] + "x" + profileNames[2]);
                for(String profileName : profileNames) {
                    arrayAdapter.add(profileName);
                }
                arrayAdapter.notifyDataSetChanged();
                Log.d("PostExecute", "NOTIFY CHANGE");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = "http://" + MainActivity.server_ip_address + "/get_profiles";
            Log.d("CALIBURL", urlString);
            String result = "{\"profile_nos\": [1, 2, 3, 4]}";
            try {
                URL myUrl = new URL(urlString);

                HttpURLConnection connection =  (HttpURLConnection) myUrl.openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                connection.connect();

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;

                while((inputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                bufferedReader.close();
                inputStreamReader.close();

                result = stringBuilder.toString();
                Log.d("REFRESH", result);
                try {
                    jsonObject = new JSONObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class AsyncStartTrainingTask extends AsyncTask<Integer, Void, Void> {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected Void doInBackground(Integer... params) {
            String urlString = "http://" + MainActivity.server_ip_address + "/start_training/" + params[0];
            Log.d("TRAINURL", urlString);
            String result = "";
            try {
                URL myUrl = new URL(urlString);

                HttpURLConnection connection =  (HttpURLConnection) myUrl.openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                connection.connect();

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;

                while((inputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                bufferedReader.close();
                inputStreamReader.close();

                result = stringBuilder.toString();
                Log.d("TRAINRES", result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
