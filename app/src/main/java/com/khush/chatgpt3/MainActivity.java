package com.khush.chatgpt3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.khush.chatgpt3.databinding.ActivityMainBinding;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class MainActivity extends AppCompatActivity {
    RecyclerViewAdapter adapter;
    ArrayList<MyData> database;
    ActivityMainBinding binding;
    private String newMessage = "";
    RecyclerView recyclerView;
    private String APIkey = "";

    MediaPlayer mSound;
    TextToSpeech textToSpeech;
    Boolean speechMode = true;
    Boolean menuMode = false;
    SharedPreferences.Editor prefEditor;
    private static final int REQUEST_CODE = 100;
    Boolean popupState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Intent intent = getIntent();
        //APIkey = intent.getStringExtra("key");

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException |
                 NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        binding.loadingAnim.setVisibility(View.GONE);
        binding.menuField.setVisibility(View.GONE);
        binding.cancelTalk.setVisibility(View.GONE);
        //binding.speechModeSwitch.setChecked(true);

        mSound = MediaPlayer.create(this, R.raw.chin3);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        prefEditor = sharedPreferences.edit();

        APIkey = sharedPreferences.getString("key", "");

        if (sharedPreferences.getBoolean("speechMode", true)) {
            speechMode = true;
            //binding.speechModeSwitch.setChecked(true);
        } else {
            speechMode = false;
            //binding.speechModeSwitch.setChecked(false);
        }

//        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int i) {
//                // if No error is found then only it will run
//                if(i!=TextToSpeech.ERROR){
//                    // To Choose language of speech
//                    textToSpeech.setLanguage(Locale.US);
//                    textToSpeech.setSpeechRate(0.9f);
//                }
//            }
//        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            //Log.i("MyTag","On Start");
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            //Log.i("MyTag","On Done");
                            binding.cancelTalk.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(String utteranceId) {
                            //Log.i("MyTag","On Error");
                            binding.cancelTalk.setVisibility(View.GONE);
                        }
                    });

                } else {
                    //Log.i("MyTag","Initialization Failed");
                    binding.cancelTalk.setVisibility(View.GONE);
                }
            }
        });


        database = new ArrayList<>();
        MyData line = new MyData();
        line.type = 1;
        line.message = "Hi. How can I assist you today?";
        database.add(line);
        //mSound.start();

        recyclerView = findViewById(R.id.chatField);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, database);
        adapter.textToSpeech = textToSpeech;
        adapter.binding = binding;
        recyclerView.setAdapter(adapter);
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.messageField.getText().toString();
                binding.menuField.setVisibility(View.GONE);

                if (NetworkManager.isNetworkAvailable(getApplicationContext())) {
                    if (text.length() > 0) {
                        MyData line2 = new MyData();
                        line2.type = 2;
                        line2.message = text;
                        database.add(line2);

                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        adapter.notifyDataSetChanged();

                        newMessage = text;
                        doRequest();
                    } else {
                        Toast.makeText(getApplicationContext(), "The message field is empty!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You don't have an internet connection!", Toast.LENGTH_SHORT).show();
                }
                binding.messageField.getText().clear();
            }
        });

        binding.menuBtn.isSelected();
//        binding.menuBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!menuMode) {
//                    binding.menuField.setVisibility(View.VISIBLE);
//                    menuMode = true;
//                }
//                else {
//                    binding.menuField.setVisibility(View.GONE);
//                    menuMode = false;
//                }
//            }
//        });

//        binding.speechModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(binding.speechModeSwitch.isChecked()){
//                    speechMode = true;
//                }else{
//                    speechMode = false;
//                }
//
//                initAppSetting();
//                prefEditor.putBoolean("speechMode", speechMode).commit();
//            }
//        });

        binding.cancelTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                binding.cancelTalk.setVisibility(View.GONE);
            }
        });

        binding.sendBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (speechMode) {
                    voiceEnter();
                    return true;
                } else {
                    return false;
                }
            }
        });

        initAppSetting();

        binding.messageField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (speechMode) {
                    if (binding.messageField.getText().length() == 0) {
                        binding.sendBtn.setText("hold");
                    } else {
                        binding.sendBtn.setText("send");
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    public void doRequest() {
        binding.sendBtn.setEnabled(false);
        binding.loadingAnim.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.openai.com/v1/chat/completions");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", APIkey);
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    JSONArray jsonArray = new JSONArray();
                    JSONObject message = new JSONObject();
                    message.put("role", "user");
                    message.put("content", URLEncoder.encode(newMessage, "UTF-8"));
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
                    int responseCode = conn.getResponseCode();
                    //Log.i("MyTag", "Error code: "+ responseCode);

                    if (responseCode == 200) {
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
                            //Log.i("MyTag", t.getMessage().toString()+ "Could not parse malformed JSON");
                            setAnswer("There was an error, I can't answer now!\nPlease restart the app");
                        }
                    } else {
                        setAnswer("There was an error, I can't answer now!\nPlease restart the app");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.i("MyTag", e.toString());
                    setAnswer("There was an error, I can't answer now!\nPlease restart the app");
                }
            }
        });
        thread.start();
    }

    public void setAnswer(String text) {
        //Log.e("MyTag", text);
        MyData line = new MyData();
        line.type = 1;
        line.message = text;
        database.add(line);
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapter.textToSpeech = textToSpeech;
                adapter.binding = binding;
                binding.sendBtn.setEnabled(true);
                binding.loadingAnim.setVisibility(View.GONE);
                binding.sendBtn.setEnabled(true);
            }
        });

        if (speechMode) {
            mSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                    binding.cancelTalk.setVisibility(View.VISIBLE);
                }
            });
            mSound.start();
        }
    }

    private void voiceEnter() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "voice recognition ...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            binding.messageField.getText().clear();
            //binding.messageField.setText(matches.get(0));
            if (matches != null) {
                String msg = matches.get(0);
                if (msg.length() > 0) {
                    MyData line2 = new MyData();
                    line2.type = 2;
                    line2.message = msg;
                    database.add(line2);

                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    adapter.notifyDataSetChanged();
                    newMessage = msg;
                    doRequest();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initAppSetting() {
        if (speechMode) {
            if (binding.messageField.getText().length() > 0) {
                binding.sendBtn.setText("send");
            } else {
                binding.sendBtn.setText("hold");
                binding.messageField.clearFocus();
            }
        } else {
            binding.sendBtn.setText("send");
        }
    }

    public void onSettingsButtonClick(View view) {
        popupState = true;
        closeKeyboard();
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.settings_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button closeBtn = popupView.findViewById(R.id.popup_btn_close);
        SwitchMaterial switchBtn = popupView.findViewById(R.id.popupSpeechModeSwitch);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAppSetting();
                popupState = false;
                popupWindow.dismiss();
            }
        });

        if (speechMode) {
            switchBtn.setChecked(true);
        } else {
            switchBtn.setChecked(false);
        }
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchBtn.isChecked()) {
                    speechMode = true;
                    binding.messageField.getText().clear();
                } else {
                    speechMode = false;
                }
                prefEditor.putBoolean("speechMode", speechMode).commit();
            }
        });

        // dismiss the popup window when touched
//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                popupWindow.dismiss();
//                return true;
//            }
//        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (popupState) {
            popupState = false;
            binding.messageField.clearFocus();
        }
    }
}