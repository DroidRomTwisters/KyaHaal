package com.example.kyahaal.message;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyahaal.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView msg;
    TextView time,day;
    ImageView isSent,tri;

    public MessageViewHolder(@NonNull View itemView,int viewtype) {
        super(itemView);
        msg=itemView.findViewById(R.id.text);
        time=itemView.findViewById(R.id.msg_time);
        isSent=itemView.findViewById(R.id.is_Read_iv);
        tri=itemView.findViewById ( R.id. test_arrow);
        tri.setVisibility ( View.VISIBLE );
        if (viewtype==3 || viewtype==4){
            day=itemView.findViewById ( R.id.day);
        }else if (viewtype==5 || viewtype==6){
            tri.setVisibility ( View.INVISIBLE );
        }
    }
}
