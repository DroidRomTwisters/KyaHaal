package com.example.kyahaal.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyahaal.R;
import com.example.kyahaal.Utils.GetTimeAgo2;
import com.example.kyahaal.database.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private Context context;
    private ArrayList<MessageModal> storemsg;
    private DataBaseHelper myDb;
    private String mchatuid;
    private String mcurruid;
    private int tp;
    public MessageAdapter(Context context,ArrayList<MessageModal> msgstore,String mchatuid,String mcurruid){
        this.context=context;
        this.storemsg=msgstore;
        this.mchatuid=mchatuid;
        this.mcurruid=mcurruid;
        myDb=new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=null;
        if (viewType==2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_right, parent, false);
        }else if (viewType==0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_left, parent, false);
        }else if (viewType==3){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_left_with_dayinfo, parent, false);
        }else if (viewType==4){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_right_with_dayinfo, parent, false);
        }else if (viewType==5 ){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_left, parent, false);
        }else if (viewType==6){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_right, parent, false);
        }
        if (view!=null){
        return new MessageViewHolder(view,viewType);
        }else {
        return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
                MessageModal messageModal=storemsg.get(position);
                holder.msg.setText(messageModal.getData());
                long timestamp=messageModal.getRead_timestamp();
                String ts=timestamptotime(timestamp);
                holder.time.setText(ts);
                if (messageModal.getIsSent()!=0){
                    if (holder.isSent!=null) {
                        holder.isSent.setImageResource(R.drawable.ic_done_blue);
                    }
                }
                if (holder.day!=null){
                    String day=GetTimeAgo2.getTimeAgo ( messageModal.getRead_timestamp (),context );
                    holder.day.setText ( day );
                }
    }
    @Override
    public int getItemCount() {
        return storemsg.size();
    }
    @Override
    public int getItemViewType(int position) {
        MessageModal messageModal=storemsg.get(position);
        MessageModal messageModal1=null;
        String time1=null;
        String time2=null;
        if (position!=0) {
            messageModal1 = storemsg.get ( position - 1 );
        }
        time1 = GetTimeAgo2.getTimeAgo ( messageModal.getRead_timestamp (),context );
        if (messageModal1!=null) {
            time2 = GetTimeAgo2.getTimeAgo ( messageModal1.getRead_timestamp (), context );
        }
        if (messageModal.getFrom_me()==0){
            if (position==0){
                return 3;
            }else {
                if (time1 != null && !(time1.equals ( time2 ))) {
                    return 3;
                } else {
                    if (messageModal1!=null && messageModal1.getFrom_me ()==messageModal.getFrom_me ()){
                        return 5;
                    }else {
                        return 0;
                    }
                }
            }
        }else {
            if (position==0){
                return 4;
            }else {
                if (time1 != null && !(time1.equals ( time2 ))) {
                    return 4;
                } else {if (messageModal1!=null && messageModal1.getFrom_me ()==messageModal.getFrom_me ()){
                    return 6;
                }else {
                    return 2;
                }
                }
            }
        }

    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    String timestamptotime(long timestamp){
        try {
            Date time=new Date(timestamp);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
            String tp= simpleDateFormat.format(time);
            return tp;

        }catch (Exception e){

        }
        return "";
    }


}
