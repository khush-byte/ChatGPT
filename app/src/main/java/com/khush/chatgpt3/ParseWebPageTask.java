package com.khush.chatgpt3;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.util.Objects;

public class ParseWebPageTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
        String key = "";
        try{
            Document doc = Jsoup.connect(url).get();
            //Log.d("MyTag", doc.getElementsByClass("chat").toString());

            Element root = doc.select("section").get(0);
            key = root.toString();
            return key;

        }catch (IOException e){
            Log.d("MyTag", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }
}
