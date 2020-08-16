package com.example.kyahaal.MainChat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyahaal.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatViewHolder extends RecyclerView.ViewHolder{
    CircleImageView profilepic;
    TextView uname,lastmsg;
    ImageView isSeen,statechecked;
    TextView lastmsg_tie,unread_count;
    private ChatListAdapter.OnChatClickListener chatClickListener;

    public ChatViewHolder(@NonNull View itemView, final ChatListAdapter.OnChatClickListener chatClickListener){
        super(itemView);
        this.chatClickListener=chatClickListener;
        profilepic=itemView.findViewById(R.id.profileimage);
        profilepic.setDrawingCacheEnabled(true);
        uname=itemView.findViewById(R.id.username);
        lastmsg=itemView.findViewById(R.id.textView10);
        isSeen=itemView.findViewById(R.id.imageView9);
        lastmsg_tie=itemView.findViewById(R.id.last_msg_time);
        unread_count=itemView.findViewById(R.id.unread_count);
        statechecked=itemView.findViewById(R.id.imageView8);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatClickListener.OnProfilePicClick(getAdapterPosition(),profilepic);
            }
        });
        itemView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                chatClickListener.OnchatClickListener ( getAdapterPosition () );
            }
        } );

        itemView.setOnLongClickListener ( new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                chatClickListener.OnChatLongClickListener ( getAdapterPosition () );
                return true;
            }
        } );
    }

}
