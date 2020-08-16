package com.example.kyahaal.message;

public class MessageonlineModal {
    String message,from,mediaUrl,push_id;
    long timesent;

    public long getTimesent() {
        return timesent;
    }

    public void setTimesent(long timesent) {
        this.timesent = timesent;
    }

    public MessageonlineModal() {
    }

    public MessageonlineModal(String message, String from, String mediaUrl, String push_id, long timesent) {
        this.message = message;
        this.from = from;
        this.mediaUrl = mediaUrl;
        this.push_id = push_id;
        this.timesent = timesent;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }




}
