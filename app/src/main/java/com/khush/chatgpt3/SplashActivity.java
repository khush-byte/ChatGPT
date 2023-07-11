package com.khush.chatgpt3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView text = (TextView)findViewById(R.id.info_text);
        text.setVisibility(View.GONE);

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException |
                 NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        if (NetworkManager.isNetworkAvailable(getApplicationContext())) {
            parseWebPage();
            //updateGPTKey();
        }
        else
        {
            Handler handler = new Handler();
            Runnable refresh = new Runnable() {
                @Override
                public void run() {
                    if (!NetworkManager.isNetworkAvailable(getApplicationContext())) {
                        text.setVisibility(View.VISIBLE);
                        handler.postDelayed(this, 1000);
                    } else {
                        text.setVisibility(View.GONE);
                        parseWebPage();
                    }
                }
            };
            handler.postDelayed(refresh, 1000);
        }
    }

//    private void updateGPTKey(){
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "https://64a28739b45881cc0ae54a79.mockapi.io/api/v1/key";
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        //Log.i("MyTag", response);
//                        parseKey(response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("MyTag", String.valueOf(error));
//            }
//        });
//        queue.add(stringRequest);
//    }
//
//    private void parseKey(String response){
//        try {
//            JSONArray json = new JSONArray(response);
//            JSONObject key = (JSONObject) json.getJSONObject(0);
//            String APIkey = "Bearer "+key.getString("key");
//            //Log.i("MyTag", APIkey);
//
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.putExtra("key", APIkey);
//            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(
//                    getApplicationContext(),
//                    android.R.anim.fade_in, android.R.anim.fade_out
//            ).toBundle();
//            startActivity(intent, bundle);
//            finish();
//        } catch (Throwable t) {
//            Log.i("MyTag", Objects.requireNonNull(t.getMessage()));
//        }
//    }

    private void parseWebPage(){
        String url = "https://talkai.info/chat/";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Document doc = Jsoup.connect(url).get();
                    Element section = doc.selectFirst("section");
                    assert section != null;
                    String[] lines = section.toString().split("\n");
                    String[] objects = lines[0].split(" ");
                    String[] keyLine = objects[objects.length-1].split("\"");
                    String APIkey = "Bearer " + keyLine[1];

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    //intent.putExtra("key", APIkey);
                    SharedPreferences sharedPreferences = getSharedPreferences("MyData",MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                    prefEditor.putString("key", APIkey).apply();

                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(
                            getApplicationContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out
                    ).toBundle();
                    startActivity(intent, bundle);
                    finish();

                    //Log.d("MyTag", APIkey);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.i("MyTag", Objects.requireNonNull(e.getMessage()));
                }
            }
        });
        thread.start();
    }
}