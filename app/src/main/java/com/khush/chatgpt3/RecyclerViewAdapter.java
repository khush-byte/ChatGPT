package com.khush.chatgpt3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyData> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, List<MyData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.chat_bot, parent, false);
//        return new ViewHolder(view);
//    }

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


    // binds the data to the TextView in each row
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        String animal = mData.get(position);
//        holder.chatText.setText(animal);
//    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String animal = mData.get(position).message;
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

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return mData.get(position).type;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder1 extends RecyclerView.ViewHolder {//implements View.OnClickListener {
        TextView chatText;

        ViewHolder1(View itemView) {
            super(itemView);
            chatText = itemView.findViewById(R.id.chatText);
            //itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            if (mClickListener != null) //mClickListener.onItemClick(view, getAdapterPosition());
//            {
//                chatText.setTextIsSelectable(true);
//            }
//        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {//} implements View.OnClickListener {
        TextView chatText;

        ViewHolder2(View itemView) {
            super(itemView);
            chatText = itemView.findViewById(R.id.chatText);
            //itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            if (mClickListener != null) { //mClickListener.onItemClick(view, getAdapterPosition());
//                chatText.setTextIsSelectable(true);
//            }
//        }
    }

    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData.get(id);
//    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}