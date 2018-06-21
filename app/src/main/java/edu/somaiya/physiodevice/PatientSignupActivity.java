package edu.somaiya.physiodevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PatientSignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_signup);
        Button signup = findViewById(R.id.signup_pbutton);
        signup.setOnClickListener((view) -> {
            final String name = ((TextView) findViewById(R.id.signup_pname)).getText().toString().trim().replaceAll(" ", "%20");
            final int age = Integer.parseInt(((TextView) findViewById(R.id.signup_page)).getText().toString().trim());
            final String sex = ((TextView) findViewById(R.id.signup_psex)).getText().toString().trim().replaceAll(" ", "%20");
            final String description = ((TextView) findViewById(R.id.signup_pdescription)).getText().toString().trim().replaceAll(" ", "%20");
            final int doctorid = getIntent().getIntExtra("doctorid", 1);
            String doctorname = getIntent().getStringExtra("doctorname");
            Intent intent = new Intent(this, DoctorActivity.class);
            intent.putExtra("doctorname", doctorname);
            intent.putExtra("doctorid", doctorid);
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    "http://" + MainActivity.server_ip_address + "/patientsignup",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            startActivity(intent);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("doctorid", "" + doctorid);
                    MyData.put("name", name);
                    MyData.put("age", "" + age);
                    MyData.put("sex", sex);
                    MyData.put("description", description);
                    return MyData;
                }
            };
            VolleySingleton.getInstance().getRequestQueue().add(stringRequest);
        });
    }
}
