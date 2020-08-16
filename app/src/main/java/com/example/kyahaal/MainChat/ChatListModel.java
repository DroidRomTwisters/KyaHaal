package com.example.kyahaal.MainChat;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ChatListModel implements Comparable,Cloneable, Serializable {
    String uid,phnum,name,last_unseenmsg,last_seenmsg,media_url,lastmsg_time;
    int from_me,unread_count;
    boolean isChecked;




    public ChatListModel(String uid, String phnum, String name, String last_unseenmsg, String last_seenmsg, int from_me) {
        this.uid = uid;
        this.phnum = phnum;
        this.name = name;
        this.last_unseenmsg = last_unseenmsg;
        this.last_seenmsg = last_seenmsg;
        this.from_me=from_me;
    }

    public ChatListModel(String uid, String phnum, String name, String last_unseenmsg, String last_seenmsg, int from_me, int unread_count, String media_url, String lastmsg_time) {
        this.uid = uid;
        this.phnum = phnum;
        this.name = name;
        this.last_unseenmsg = last_unseenmsg;
        this.last_seenmsg = last_seenmsg;
        this.from_me=from_me;
        this.unread_count=unread_count;
        this.media_url=media_url;
        this.lastmsg_time=lastmsg_time;
    }

    public String getLastmsg_time() {
        return lastmsg_time;
    }

    public void setLastmsg_time(String lastmsg_time) {
        this.lastmsg_time = lastmsg_time;
    }

    public ChatListModel() {

    }
    public String getMedia_url() {
        return media_url;
    }
    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }
    public String getUid() {
        return uid;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhnum() {
        return phnum;
    }

    public void setPhnum(String phnum) {
        this.phnum = phnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_seenmsg() {
        return last_seenmsg;
    }

    public void setLast_seenmsg(String last_seenmsg) {
        this.last_seenmsg = last_seenmsg;
    }

    public String getLast_unseenmsg() {
        return last_unseenmsg;
    }

    public void setLast_unseenmsg(String last_unseenmsg) {
        this.last_unseenmsg = last_unseenmsg;
    }

    public int getFrom_me() {
        return from_me;
    }

    public void setFrom_me(int from_me) {
        this.from_me = from_me;
    }


    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public int compareTo(Object o) {
        ChatListModel compare=(ChatListModel) o;
        if (compare.uid.equals ( this.uid ) && compare.last_unseenmsg.equals ( this.last_unseenmsg ) && compare.unread_count==this.unread_count && compare.from_me==this.from_me && compare.name.equals ( this.name ) && compare.phnum.equals ( this.phnum ) && compare.isChecked==this.isChecked && compare.lastmsg_time.equals ( this.lastmsg_time )){
            return 0;
        }
        return 1;
    }

    @NonNull
    @Override
    protected ChatListModel clone() throws CloneNotSupportedException {
        ChatListModel clone;
        clone=(ChatListModel) super.clone ();
        return clone;
    }

}
