package edu.somaiya.physiodevice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static String server_ip_address = "0.0.0.0";
    Button login;
    EditText username, password, ip_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        server_ip_address = "0.0.0.0";
        this.login = (Button) findViewById(R.id.login_button);
        this.username = (EditText) findViewById(R.id.username_edit_text);
        this.password =  (EditText) findViewById(R.id.password_edit_text);
        this.ip_address =  (EditText) findViewById(R.id.ip_add_edit_text);
        login.setOnClickListener((view) -> {
            String ip_text = ip_address.getText().toString().trim();
            String username_text = username.getText().toString().trim();
            String password_text = password.getText().toString().trim();
            server_ip_address = ip_text;
            Intent intent = new Intent(this, ProfilesActivity.class);
            startActivity(intent);
        });
    }
}
