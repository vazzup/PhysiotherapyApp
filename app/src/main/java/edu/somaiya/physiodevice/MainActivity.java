package edu.somaiya.physiodevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String server_ip_address = "192.168.43.178:5000";
    Button login;
    EditText username, password;
    TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.login = findViewById(R.id.login_button);
        this.username = findViewById(R.id.username_edit_text);
        this.password = findViewById(R.id.password_edit_text);
        this.signupLink = findViewById(R.id.signup_link);
        login.setOnClickListener((view) -> {
            String username_text = username.getText().toString().trim();
            String password_text = password.getText().toString().trim();
            Intent intent = new Intent(this, DoctorActivity.class);
            Log.d("LOGIN", "button clicked!!");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + server_ip_address + "/loginverify", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("OK")) {
                            intent.putExtra("doctorid", jsonObject.getInt("doctorid"));
                            intent.putExtra("doctorname", jsonObject.getString("doctorname"));
                            startActivity(intent);
                        } else {
                            Log.d("LOGINRESP", response);
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            }, error -> error.printStackTrace()) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("email", username_text); // Send email
                    MyData.put("password", password_text); // Send email
                    return MyData;
                }
            };
            VolleySingleton.getInstance().getRequestQueue().add(stringRequest);
        });
        signupLink.setOnClickListener((view) -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }
}
