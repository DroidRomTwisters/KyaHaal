package com.example.kyahaal.Utils;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetTimeAgo2 extends MultiDexApplication {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        Calendar time1=Calendar.getInstance ();
        time1.setTimeInMillis ( time );

        Calendar now=Calendar.getInstance ();

        final String timeFormatString= "hh:mm aa";
        final String dateFormatString="EEEE, MMMM d, hh:mm aa";
        final long HOURS=60*60*60;

        if (now.get ( Calendar.YEAR)==time1.get ( Calendar.YEAR)){
            if (now.get ( Calendar.DATE )==time1.get ( Calendar.DATE )){
                return "TODAY";
            }else if (now.get ( Calendar.DATE )-time1.get ( Calendar.DATE )==1){
                return "YESTERDAY";
            }else {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM YYYY");
                return simpleDateFormat.format(time);
            }
        }else {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM YYYY");
            return simpleDateFormat.format(time);
        }

    }
}
