package com.example.kyahaal.message;

public class MessageModal {
    String data;
    String from_uid,push_id;
    String to_uid;
    int from_me,isSent;
    Long read_timestamp;

    public MessageModal() {

    }

    public MessageModal(String data, String from_uid, String to_uid,int from_me, Long read_timestamp,int isSent,String push_id) {
        this.data = data;
        this.from_uid = from_uid;
        this.from_me = from_me;
        this.to_uid=to_uid;
        this.isSent=isSent;
        this.push_id=push_id;
        this.read_timestamp = read_timestamp;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public String getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(String to_uid) {
        this.to_uid = to_uid;
    }

    public String getData() {
        return data;
    }

    public int getIsSent() {
        return isSent;
    }

    public void setIsSent(int isSent) {
        this.isSent = isSent;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFrom_uid() {
        return from_uid;
    }

    public void setFrom_uid(String from_uid) {
        this.from_uid = from_uid;
    }

    public int getFrom_me() {
        return from_me;
    }

    public void setFrom_me(int from_me) {
        this.from_me = from_me;
    }

    public Long getRead_timestamp() {
        return read_timestamp;
    }

    public void setRead_timestamp(Long read_timestamp) {
        this.read_timestamp = read_timestamp;
    }

}
