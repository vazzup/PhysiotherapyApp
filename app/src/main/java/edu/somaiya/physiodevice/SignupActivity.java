package edu.somaiya.physiodevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, password;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        signupButton.setOnClickListener((view) -> {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + MainActivity.server_ip_address + "/doctorsignup", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("email", email.getText().toString().trim()); // Send email
                    MyData.put("password", password.getText().toString().trim()); // Send email
                    MyData.put("name", name.getText().toString().trim()); // Send email
                    return MyData;
                }
            };
            VolleySingleton.getInstance().getRequestQueue().add(stringRequest);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}
