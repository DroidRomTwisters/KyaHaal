package com.example.kyahaal.MainChat;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class ChatListModelCallback extends DiffUtil.Callback {

    ArrayList <ChatListModel> oldchatlist;
    ArrayList <ChatListModel> newchatlist;

    public ChatListModelCallback(ArrayList <ChatListModel> oldchatlist, ArrayList <ChatListModel> newchatlist) {
        this.oldchatlist = oldchatlist;
        this.newchatlist = newchatlist;
    }

    @Override
    public int getOldListSize() {
        return oldchatlist!=null? oldchatlist.size ():0;
    }

    @Override
    public int getNewListSize() {
        return newchatlist!=null? newchatlist.size ():0;
    }

    @Override
    public boolean areItemsTheSame(int oldpos, int newpos) {
        return newchatlist.get ( newpos ).hashCode ()== oldchatlist.get ( oldpos ).hashCode () ;
    }

    @Override
    public boolean areContentsTheSame(int oldpos, int newpos) {
        int result=0;
        if (oldchatlist.get ( oldpos)!=null && newchatlist.get ( newpos)!=null) {
            result = newchatlist.get ( newpos).compareTo ( oldchatlist.get ( oldpos) );
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
        ChatListModel oldchatlist1=oldchatlist.get ( oldItemPosition );
        ChatListModel newchatlist1=newchatlist.get ( newItemPosition );

        Bundle diff=new Bundle (  );
        if (oldchatlist1.name!=null && newchatlist1.name!=null){
            if (!oldchatlist1.name.equals ( newchatlist1.name )){
                diff.putString ( "name",newchatlist1.name );
            }
        }

        if (oldchatlist1.last_unseenmsg!=null && newchatlist1.last_unseenmsg!=null){
            if (!oldchatlist1.last_unseenmsg.equals ( newchatlist1.last_unseenmsg )){
                diff.putString ( "lastmsg",newchatlist1.last_unseenmsg );
            }
        }

        if (!(oldchatlist1.unread_count ==newchatlist1.unread_count)){
            diff.putInt ( "uc",newchatlist1.unread_count );
        }

        if (!(oldchatlist1.media_url.equals ( newchatlist1.media_url ))){
            diff.putString ( "murl",newchatlist1.media_url );
        }

        if (!(oldchatlist1.isChecked()==newchatlist1.isChecked())){
            diff.putBoolean ( "ic",newchatlist1.isChecked() );
        }

        if (!(oldchatlist1.lastmsg_time.equals ( newchatlist1.lastmsg_time ))){
            diff.putString ( "lt",newchatlist1.lastmsg_time );
        }

        if (diff.size ()==0){
            return null;
        }
        return diff;
    }
}
