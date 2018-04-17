package edu.somaiya.physiodevice;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    Button refreshButton, calibrateButton, trainButton;
    RadioGroup profilesRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.refreshButton = (Button) findViewById(R.id.refresh_button);
        this.calibrateButton = (Button) findViewById(R.id.calibrate_button);
        this.profilesRadioGroup = (RadioGroup) findViewById(R.id.profile_radio_group);
        this.trainButton = (Button) findViewById(R.id.train_button);

        /* profilesList.setOnItemClickListener(((adapterView, view, i, l) -> {
            Log.d("ITEM", "Item Clicked!");
            new AsyncStartTrainingTask().execute(Integer.parseInt(((TextView) adapterView.getItemAtPosition(i)).getText().toString().trim()));
        })); */

        calibrateButton.setOnClickListener((view) -> {
            new AsyncCalibrateTask().execute();
        });

        refreshButton.setOnClickListener((view) -> {
                new AsyncRefreshTask().execute(this);
        });

        trainButton.setOnClickListener((view) -> {
            if(profilesRadioGroup.getCheckedRadioButtonId() == -1)
            {
            } else {
                RadioButton radioButton = (RadioButton) findViewById(profilesRadioGroup.getCheckedRadioButtonId());
                new AsyncStartTrainingTask().execute(Integer.parseInt(radioButton.getText().toString().trim()), MainActivity.global_rep);
            }
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
            return null;
        }
    }

    private class AsyncRefreshTask extends AsyncTask<Context, Void, Void> {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        public JSONObject jsonObject;
        public Context context;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                profilesRadioGroup.removeAllViews();
                String[] profileNames = jsonObject.getJSONArray("profile_nos").join(",").split(",");
                int i = 0;
                int len = profileNames.length;
                for(int j = len - 1; j>=0; j--) {
                    RadioButton radioButton = new RadioButton(this.context);
                    radioButton.setText(profileNames[j]);
                    radioButton.setId(12345 + i++);
                    profilesRadioGroup.addView(radioButton);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            this.context = contexts[0];
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
            String urlString = "http://" + MainActivity.server_ip_address + "/start_training/" + params[0] + "/" + params[1];
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

    class ProfileAdapter extends BaseAdapter implements ListAdapter {

        Context context;
        ArrayList<String> arrayList;

        ProfileAdapter(Context context, ArrayList<String> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        public void add(String s) {
            arrayList.add(s);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.profile_list_layout, null);
            }
            if(i >= arrayList.size()) {
                return view;
            }
            Button button = (Button) findViewById(R.id.profile_button);
            if(button == null) return view;
            button.setText((String)arrayList.get(i));
            button.setOnClickListener((view1) -> {
                new AsyncStartTrainingTask().execute(Integer.parseInt(button.getText().toString().trim()), MainActivity.global_rep);
            });

            return view;
        }
    }
}
