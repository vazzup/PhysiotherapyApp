package edu.somaiya.physiodevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String server_ip_address = "192.168.43.178:5000";
    public static Integer global_rep = 1;
    Button login;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.login = findViewById(R.id.login_button);
        this.username = findViewById(R.id.username_edit_text);
        this.password = findViewById(R.id.password_edit_text);
        login.setOnClickListener((view) -> {
            String username_text = username.getText().toString().trim();
            String password_text = password.getText().toString().trim();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Intent intent = new Intent(this, ProfilesActivity.class);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + server_ip_address + "/loginverify", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("OK")) {
                        startActivity(intent);
                    } else {
                        Log.d("LOGINRESP", response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("email", username_text); // Send email
                    MyData.put("password", password_text); // Send email
                    return MyData;
                }
            };
            requestQueue.add(stringRequest);
        });
    }
}
