package com.khush.chatgpt3;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class MainActivity extends AppCompatActivity {
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException |
                 NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        //doRequest();

        ArrayList<MyData> animalNames = new ArrayList<>();
        MyData line1 = new MyData();
        line1.type = 1;
        line1.message = "Hi. How can I assist you today?";
        animalNames.add(line1);


        RecyclerView recyclerView = findViewById(R.id.chatField);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, animalNames);
        recyclerView.setAdapter(adapter);
    }

    public void doRequest() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.openai.com/v1/chat/completions");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "Bearer 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");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    JSONArray jsonArray = new JSONArray();
                    JSONObject message = new JSONObject();
                    message.put("role", "user");
                    message.put("content", "What ia your name?");
                    jsonArray.put(message);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("temperature", 1);
                    jsonParam.put("frequency_penalty", 0);
                    jsonParam.put("model", "gpt-3.5-turbo");
                    jsonParam.put("messages", jsonArray);

                    //Log.i("MyTag", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    //Log.i("MyTag", String.valueOf(conn.getResponseCode()));

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    conn.disconnect();
                    String response = sb.toString();
                    //Log.i("MyTag", response);

                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray choices = json.getJSONArray("choices");
                        JSONObject text = (JSONObject) choices.getJSONObject(0).get("message");
                        String answer = text.getString("content");
                        setAnswer(answer);

                    } catch (Throwable t) {
                        Log.e("MyTag", "Could not parse malformed JSON");
                        setAnswer(t.getMessage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    setAnswer(e.getMessage());
                }
            }
        });
        thread.start();
    }

    public void setAnswer(String text) {
        Log.e("MyTag", text);
    }
}