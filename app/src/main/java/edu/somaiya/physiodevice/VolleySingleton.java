package edu.somaiya.physiodevice;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by vazzup on 6/19/18.
 */

public class VolleySingleton extends Application {

    private static VolleySingleton volleyInstance;

    private RequestQueue requestQueue;

    static synchronized VolleySingleton getInstance() {
        return volleyInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        volleyInstance = this;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

}
