package edu.somaiya.physiodevice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PatientActivity extends AppCompatActivity {

    Context context;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        context = this;

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getIntent());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = findViewById(R.id.patienttabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class AboutFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public AboutFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static AboutFragment newInstance(int sectionNumber, Intent intent) {
            String patientName = intent.getStringExtra("patientname");
            int patientAge = intent.getIntExtra("patientage", 0);
            String patientSex = intent.getStringExtra("patientsex");
            int patientID = intent.getIntExtra("patientid", 1);
            String patientDescription = intent.getStringExtra("patientdescription");
            int doctorID = intent.getIntExtra("doctorid", 1);
            AboutFragment fragment = new AboutFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("patientname", patientName);
            args.putInt("patientage", patientAge);
            args.putString("patientsex", patientSex);
            args.putInt("patientid", patientID);
            args.putString("patientdescription", patientDescription);
            args.putInt("doctorid", doctorID);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            View rootView = inflater.inflate(R.layout.fragment_patient, container, false);
            TextView patientName = rootView.findViewById(R.id.patientinfo_name);
            TextView patientDescription = rootView.findViewById(R.id.patientinfo_desc);
            patientName.setText(args.getString("patientname") + ", " + args.getString("patientsex") + ", " + args.getInt("patientage"));
            patientDescription.setText(args.getString("patientdescription"));
            LinearLayout trainingsList = rootView.findViewById(R.id.traininginfolist);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + MainActivity.server_ip_address + "/gettrainings/" + args.getInt("patientid"), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray result = jsonObject.getJSONArray("result");
                        int length = result.length();
                        trainingsList.removeAllViewsInLayout();
                        for (int i = 0; i < length; i++) {
                            View view = inflater.inflate(R.layout.training_list_layout, null, false);
                            ((TextView) view.findViewById(R.id.traininginfodesc)).setText(result.getJSONObject(i).getString("description"));
                            ((TextView) view.findViewById(R.id.traininginfodname)).setText(result.getJSONObject(i).getString("doctorname"));
                            ((TextView) view.findViewById(R.id.traininginforeps)).setText(result.getJSONObject(i).getString("repetitions") + " reps");
                            ((TextView) view.findViewById(R.id.traininginfotimestamp)).setText(result.getJSONObject(i).getString("timestamp"));
                            trainingsList.addView(view);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            VolleySingleton.getInstance().getRequestQueue().add(stringRequest);

            FloatingActionButton fab = rootView.findViewById(R.id.refreshhistorybutton);
            fab.setOnClickListener((view) -> {
                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "http://" + MainActivity.server_ip_address + "/gettrainings/" + args.getInt("patientid"), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");
                            int length = result.length();
                            trainingsList.removeAllViewsInLayout();
                            for (int i = 0; i < length; i++) {
                                View view = inflater.inflate(R.layout.training_list_layout, null, false);
                                ((TextView) view.findViewById(R.id.traininginfodesc)).setText(result.getJSONObject(i).getString("description").replaceAll("%20", " "));
                                ((TextView) view.findViewById(R.id.traininginfodname)).setText(result.getJSONObject(i).getString("doctorname").replaceAll("%20", " "));
                                ((TextView) view.findViewById(R.id.traininginforeps)).setText(result.getJSONObject(i).getInt("repetitions") + " reps");
                                ((TextView) view.findViewById(R.id.traininginfotimestamp)).setText(result.getJSONObject(i).getString("timestamp").replaceAll("%20", " "));
                                trainingsList.addView(view);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                VolleySingleton.getInstance().getRequestQueue().add(stringRequest2);
            });
            return rootView;
        }
    }

    public static class TrainingFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public TrainingFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TrainingFragment newInstance(int sectionNumber, Intent intent) {
            String patientName = intent.getStringExtra("patientname");
            int patientAge = intent.getIntExtra("patientage", 0);
            String patientSex = intent.getStringExtra("patientsex");
            int patientID = intent.getIntExtra("patientid", 1);
            String patientDescription = intent.getStringExtra("patientdescription");
            int doctorID = intent.getIntExtra("doctorid", 1);
            TrainingFragment fragment = new TrainingFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("patientname", patientName);
            args.putInt("patientage", patientAge);
            args.putString("patientsex", patientSex);
            args.putInt("patientid", patientID);
            args.putString("patientdescription", patientDescription);
            args.putInt("doctorid", doctorID);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_training, container, false);

            Bundle args = getArguments();
            LinearLayout eProfilesList = rootView.findViewById(R.id.eprofilelist);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + MainActivity.server_ip_address + "/get_profiles/" + args.getInt("patientid"), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d("Training Response", response);
                        JSONArray result = jsonObject.getJSONArray("profiles");
                        int length = result.length();
                        for (int i = 0; i < length; i++) {
                            View view = inflater.inflate(R.layout.eprofile_list_layout, null, false);
                            ((TextView) view.findViewById(R.id.eprofiledesc)).setText(result.getJSONObject(i).getInt("profileid") + ". " + result.getJSONObject(i).getString("description").replaceAll("%20", " "));
                            ((TextView) view.findViewById(R.id.eprofiledoctor)).setText(result.getJSONObject(i).getString("doctorname").replaceAll("%20", " "));
                            if (!(result.getJSONObject(i).getString("lastused").replaceAll("%20", " ").equalsIgnoreCase("null"))) {
                                ((TextView) view.findViewById(R.id.eprofiletimestamp)).setText(result.getJSONObject(i).getString("lastused").replaceAll("%20", " "));
                            } else {
                                ((TextView) view.findViewById(R.id.eprofiletimestamp)).setText("");
                            }
                            view.setOnClickListener((innerView) -> {
                                final EditText taskEditText = new EditText(getContext());
                                AlertDialog dialog = new AlertDialog.Builder(getContext())
                                        .setTitle("Repetitions")
                                        .setMessage("How many times do you want to conduct the exercise?")
                                        .setView(taskEditText)
                                        .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String descText = ((TextView) innerView.findViewById(R.id.eprofiledesc)).getText().toString();
                                                String number = "";
                                                for (int i = 0; i < descText.length(); i++) {
                                                    if (descText.charAt(i) <= '9' && descText.charAt(i) >= '0') {
                                                        number += descText.charAt(i);
                                                    } else {
                                                        break;
                                                    }
                                                }
                                                int profileid = Integer.parseInt(number.trim());
                                                int reps = Integer.parseInt(taskEditText.getText().toString().trim());
                                                StringRequest stringRequest1 = new StringRequest(Request.Method.GET,
                                                        "http://" + MainActivity.server_ip_address + "/start_training/" + args.getInt("doctorid") + "/" + args.getInt("patientid") + "/" + profileid + "/" + reps, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        error.printStackTrace();
                                                    }
                                                });
                                                VolleySingleton.getInstance().getRequestQueue().add(stringRequest1);
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .create();
                                dialog.show();
                            });
                            eProfilesList.addView(view);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            VolleySingleton.getInstance().getRequestQueue().add(stringRequest);

            FloatingActionButton fab = rootView.findViewById(R.id.addeprofilebutton);
            fab.setOnClickListener((view) -> {
//                LinearLayout linearLayout = new LinearLayout(getContext());
//                linearLayout.setOrientation(LinearLayout.VERTICAL);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                linearLayout.setLayoutParams(layoutParams);
//                EditText descriptionText = new EditText(getContext());
//                descriptionText.setHint("Description");
//                descriptionText.setLayoutParams(new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                descriptionText.setPadding(2, 0, 2, 2);
//                linearLayout.addView(descriptionText);
//
//                AlertDialog dialog = new AlertDialog.Builder(getContext())
//                        .setTitle("Repetitions")
//                        .setMessage("How many times do you want to conduct the exercise?")
//                        .setView(linearLayout)
//                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setNegativeButton("Cancel", null)
//                        .create();

                final EditText taskEditText = new EditText(getContext());
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Description")
                        .setMessage("Describe the profile you are training")
                        .setView(taskEditText)
                        .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String description = taskEditText.getText().toString().trim();
                                description.replaceAll(" ", "%20");
                                StringRequest stringRequest1 = new StringRequest(Request.Method.GET,
                                        "http://" + MainActivity.server_ip_address + "/start_calibration/" + args.getInt("doctorid") + "/" + args.getInt("patientid") + "/" + description, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                });
                                VolleySingleton.getInstance().getRequestQueue().add(stringRequest1);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            });

            FloatingActionButton fab2 = rootView.findViewById(R.id.refresheprofilebutton);
            fab2.setOnClickListener((view) -> {
                StringRequest stringRequest3 = new StringRequest(Request.Method.GET, "http://" + MainActivity.server_ip_address + "/get_profiles/" + args.getInt("patientid"), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            eProfilesList.removeAllViewsInLayout();
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("Training Response", response);
                            JSONArray result = jsonObject.getJSONArray("profiles");
                            int length = result.length();
                            for (int i = 0; i < length; i++) {
                                View view = inflater.inflate(R.layout.eprofile_list_layout, null, false);
                                ((TextView) view.findViewById(R.id.eprofiledesc)).setText(result.getJSONObject(i).getInt("profileid") + ". " + result.getJSONObject(i).getString("description").replaceAll("%20", " "));
                                ((TextView) view.findViewById(R.id.eprofiledoctor)).setText(result.getJSONObject(i).getString("doctorname").replaceAll("%20", " "));
                                if (!(result.getJSONObject(i).getString("lastused").replaceAll("%20", " ").equalsIgnoreCase("null"))) {
                                    ((TextView) view.findViewById(R.id.eprofiletimestamp)).setText(result.getJSONObject(i).getString("lastused").replaceAll("%20", " "));
                                } else {
                                    ((TextView) view.findViewById(R.id.eprofiletimestamp)).setText("");
                                }
                                view.setOnClickListener((innerView) -> {
                                    final EditText taskEditText = new EditText(getContext());
                                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                                            .setTitle("Repetitions")
                                            .setMessage("How many times do you want to conduct the exercise?")
                                            .setView(taskEditText)
                                            .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String descText = ((TextView) innerView.findViewById(R.id.eprofiledesc)).getText().toString();
                                                    String number = "";
                                                    for (int i = 0; i < descText.length(); i++) {
                                                        if (descText.charAt(i) <= '9' && descText.charAt(i) >= '0') {
                                                            number += descText.charAt(i);
                                                        } else {
                                                            break;
                                                        }
                                                    }
                                                    int profileid = Integer.parseInt(number.trim());
                                                    int reps = Integer.parseInt(taskEditText.getText().toString().trim());
                                                    StringRequest stringRequest1 = new StringRequest(Request.Method.GET,
                                                            "http://" + MainActivity.server_ip_address + "/start_training/" + args.getInt("doctorid") + "/" + args.getInt("patientid") + "/" + profileid + "/" + reps, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            error.printStackTrace();
                                                        }
                                                    });
                                                    VolleySingleton.getInstance().getRequestQueue().add(stringRequest1);
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .create();
                                    dialog.show();
                                });
                                eProfilesList.addView(view);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                VolleySingleton.getInstance().getRequestQueue().add(stringRequest3);
            });
            return rootView;
        }
    }

    public static class VisualisationFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public VisualisationFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static VisualisationFragment newInstance(int sectionNumber, Intent intent) {
            String patientName = intent.getStringExtra("patientname");
            int patientAge = intent.getIntExtra("patientage", 0);
            String patientSex = intent.getStringExtra("patientsex");
            int patientID = intent.getIntExtra("patientid", 1);
            String patientDescription = intent.getStringExtra("patientdescription");
            int doctorID = intent.getIntExtra("doctorid", 1);
            VisualisationFragment fragment = new VisualisationFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("patientname", patientName);
            args.putInt("patientage", patientAge);
            args.putString("patientsex", patientSex);
            args.putInt("patientid", patientID);
            args.putString("patientdescription", patientDescription);
            args.putInt("doctorid", doctorID);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_visualisation, container, false);

            StringRequest repRequest = new StringRequest(Request.Method.GET, "http://" + MainActivity.server_ip_address + "/getrepdata/" + getArguments().getInt("patientid"),
                    (response) -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");
                            int length = result.length();
                            TreeMap<String, Integer> map = new TreeMap<String, Integer>();
                            for (int i = 0; i < length; i++) {
                                JSONObject item = result.getJSONObject(i);
                                int reps = item.getInt("repetitions");
                                String timestamp = item.getString("timestamp").trim();
                                if (!map.containsKey(timestamp)) {
                                    map.put(timestamp, reps);
                                } else {
                                    map.put(timestamp, map.get(timestamp) + reps);
                                }
                            }
                            LineChart repsChart = rootView.findViewById(R.id.dailyrepschart);
                            List<Entry> entries = new ArrayList<Entry>();
                            int i = 0;
                            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                                String key = entry.getKey();
                                Integer value = entry.getValue();
                                entries.add(new Entry(++i, value));
                            }

                            LineDataSet dataSet = new LineDataSet(entries, "Reps v/s session no.");
                            LineData lineData = new LineData(dataSet);
                            repsChart.setData(lineData);
                            repsChart.invalidate();
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }, (error) -> {

            });

            StringRequest distanceRequest = new StringRequest(Request.Method.GET, "http://" + MainActivity.server_ip_address + "/getrangedata/" + getArguments().getInt("patientid"),
                    (response) -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");
                            int length = result.length();
                            TreeMap<String, Integer> map = new TreeMap<String, Integer>();
                            for (int i = 0; i < length; i++) {
                                JSONObject item = result.getJSONObject(i);
                                String profile = item.getString("profile");
                                String[] vals = profile.split(",");
                                int[] vals_i = new int[vals.length];
                                int mx = -100000000;
                                int mn = 100000000;
                                for (int j = 0; j < vals.length; j++) {
                                    vals_i[j] = Integer.parseInt(vals[j]);
                                    mn = Math.min(mn, vals_i[j]);
                                    mx = Math.max(mx, vals_i[j]);
                                }
                                String timestamp = item.getString("timestamp").trim();
                                if (!map.containsKey(timestamp)) {
                                    map.put(timestamp, mx - mn);
                                } else {
                                    map.put(timestamp, Math.max(map.get(timestamp), mx - mn));
                                }
                            }
                            LineChart repsChart = rootView.findViewById(R.id.dailyrepschart);
                            List<Entry> entries = new ArrayList<Entry>();
                            int i = 0;
                            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                                String key = entry.getKey();
                                Integer value = entry.getValue();
                                entries.add(new Entry(++i, value));
                            }

                            LineDataSet dataSet = new LineDataSet(entries, "Reps v/s session no.");
                            LineData lineData = new LineData(dataSet);
                            repsChart.setData(lineData);
                            repsChart.invalidate();
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }, (error) -> {

            });

            VolleySingleton.getInstance().getRequestQueue().add(repRequest);

            RadioButton rvrb = rootView.findViewById(R.id.rv_rb);
            RadioButton dvrb = rootView.findViewById(R.id.dv_rb);

            rvrb.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
                if (b) {
                    VolleySingleton.getInstance().getRequestQueue().add(repRequest);
                }
            });

            dvrb.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
                if (b) {
                    VolleySingleton.getInstance().getRequestQueue().add(distanceRequest);
                }
            });

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Intent intent;

        public SectionsPagerAdapter(FragmentManager fm, Intent intent) {
            super(fm);
            this.intent = intent;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "History";
                case 1:
                    return "Train";
                default:
                    return "Visualise";
            }

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return AboutFragment.newInstance(position + 1, intent);
                case 1:
                    return TrainingFragment.newInstance(position + 1, intent);
                case 2:
                    return VisualisationFragment.newInstance(position + 1, intent);
                default:
                    return AboutFragment.newInstance(position + 1, intent);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
