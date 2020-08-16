package com.example.kyahaal.contacts;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyahaal.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profilepic;
    TextView uname,ustatus;
    ContactsRecyclerAdapter2.OnContactClickListener contactClickListener;

    public ContactsViewHolder(@NonNull View itemView, final ContactsRecyclerAdapter2.OnContactClickListener contactClickListener) {
        super(itemView);
        profilepic=itemView.findViewById(R.id.profileimage);
        profilepic.setDrawingCacheEnabled(true);
        uname=itemView.findViewById(R.id.username);
        ustatus=itemView.findViewById(R.id.user_status);
        ustatus.setTextColor(Color.GRAY);
        this.contactClickListener=contactClickListener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactClickListener.OnContactClick(getAdapterPosition());
            }
        });
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactClickListener.OnProfilePicClick(getAdapterPosition(),profilepic);
            }
        });
    }


}
