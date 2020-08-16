package com.example.kyahaal.Utils;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTimeAgo extends MultiDexApplication {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            Date time1=new Date(time);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
            String tp= simpleDateFormat.format(time1);
            return "last seen today at "+tp;
        } else if (diff < 90 * MINUTE_MILLIS) {
            Date time1=new Date(time);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
            String tp= simpleDateFormat.format(time1);
            return "last seen today at "+tp;
        } else if (diff < 24 * HOUR_MILLIS) {
            Date time1=new Date(time);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
            String tp= simpleDateFormat.format(time1);
            return "last seen today at "+tp;
        } else if (diff < 48 * HOUR_MILLIS && diff > 24 * HOUR_MILLIS) {
            Date time1=new Date(time);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
            String tp= simpleDateFormat.format(time1);
            return "last seen yesterday at "+tp;
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
