package com.example.kyahaal.contacts;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class ContactDiffUtilCallback extends DiffUtil.Callback {
    ArrayList <ContactModelDiffUtils> newstorechats;
    ArrayList <ContactModelDiffUtils> storechats;

    public ContactDiffUtilCallback(ArrayList <ContactModelDiffUtils> newstorechats, ArrayList <ContactModelDiffUtils> storechats) {
        this.newstorechats = newstorechats;
        this.storechats = storechats;
    }

    @Override
    public int getOldListSize() {
        return storechats!=null? storechats.size ():0;
    }

    @Override
    public int getNewListSize() {
        return newstorechats!=null? newstorechats.size ():0;
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return newstorechats.get ( i1 ).UID.equals ( storechats.get ( i ).UID );
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        int result=0;
        if (storechats.get ( i )!=null && newstorechats.get ( i1 )!=null) {
            result = newstorechats.get ( i1 ).compareTo ( storechats.get ( i ) );
        }
        if (result==0){
            return true;
        }else {
            return false;
        }
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        ContactModelDiffUtils contactModelDiffUtils=newstorechats.get ( newItemPosition );
        ContactModelDiffUtils contactModelDiffUtils1=storechats.get ( oldItemPosition );

        Bundle diff=new Bundle (  );
        if (contactModelDiffUtils.MEDIA_URL!=null && contactModelDiffUtils1.MEDIA_URL!=null) {
            if (!contactModelDiffUtils.MEDIA_URL.equals ( contactModelDiffUtils1.MEDIA_URL )) {
                diff.putString ( "MEDIA_URL", contactModelDiffUtils.MEDIA_URL );
            }
        }
        if (contactModelDiffUtils.STATUS!=null && contactModelDiffUtils1.STATUS!=null) {
            if (!contactModelDiffUtils.STATUS.equals ( contactModelDiffUtils1.STATUS )) {
                diff.putString ( "STATUS", contactModelDiffUtils.STATUS );
            }
        }
        if (contactModelDiffUtils.NAME!=null && contactModelDiffUtils1.NAME!=null) {
            if (!contactModelDiffUtils.NAME.equals ( contactModelDiffUtils1.NAME )) {
                diff.putString ( "NAME", contactModelDiffUtils.NAME );
            }
        }
        if (diff.size ()==0){
            return null;
        }
        return diff;
    }
}
