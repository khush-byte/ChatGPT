package com.khush.chatgpt3;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.khush.chatgpt3.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    TextToSpeech textToSpeech;
    ActivityMainBinding binding;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, List<MyData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = mInflater.inflate(R.layout.chat_bot, parent, false);
        View view2 = mInflater.inflate(R.layout.chat_user, parent, false);
        switch (viewType) {
            case 1: return new ViewHolder1(view1);
            case 2: return new ViewHolder2(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String animal = mData.get(position).message;

//        textToSpeech = new TextToSpeech(mInflater.getContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int i) {
//                // if No error is found then only it will run
//                if(i!=TextToSpeech.ERROR){
//                    // To Choose language of speech
//                    textToSpeech.setLanguage(Locale.US);
//                }
//            }
//        });
//        textToSpeech.setSpeechRate(0.9f);

        switch (holder.getItemViewType()) {
            case 1:
                ViewHolder1 viewHolder1 = (ViewHolder1)holder;
                viewHolder1.chatText.setText(animal);
                break;

            case 2:
                ViewHolder2 viewHolder2 = (ViewHolder2)holder;
                viewHolder2.chatText.setText(animal);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder{// implements View.OnClickListener {
        TextView chatText;
        ViewHolder1(View itemView) {
            super(itemView);
            chatText = itemView.findViewById(R.id.chatText);
            //itemView.setOnClickListener(this);
            chatText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //chatText.setTextIsSelectable(true);
                    //Log.d("MyTag", "Comp");
                    textToSpeak(chatText.getText().toString());
                }
            });
            chatText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyText(itemView.getContext(), chatText.getText().toString());
                    //Log.d("MyTag", "Comp");
                    Toast.makeText(itemView.getContext(), "Text copied to clipboard", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }

//        @Override
//        public void onClick(View view) {
//            if (mClickListener != null)
//            {
//                mClickListener.onItemClick(view, getAdapterPosition());
//                chatText.setTextIsSelectable(true);
//                Log.d("MyTag", "Comp");
//            }
//        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {//implements View.OnClickListener {
        TextView chatText;

        ViewHolder2(View itemView) {
            super(itemView);
            chatText = itemView.findViewById(R.id.chatText);
            //itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            if (mClickListener != null) {
//                mClickListener.onItemClick(view, getAdapterPosition());
//                chatText.setTextIsSelectable(true);
//                Log.d("MyTag", "Perosn");
//            }
//        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private void textToSpeak(String text) {
        if(textToSpeech.isSpeaking()){
            textToSpeech.stop();
            binding.cancelTalk.setVisibility(View.GONE);
        }else{
            textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
            binding.cancelTalk.setVisibility(View.VISIBLE);
        }
    }

    private void copyText(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }
}