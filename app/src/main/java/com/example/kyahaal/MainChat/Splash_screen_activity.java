package com.example.kyahaal.MainChat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.kyahaal.R;
import com.example.kyahaal.contacts.ContactsActivity2;
import com.example.kyahaal.login.LoginActivity;
import com.example.kyahaal.login.UserDataActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_screen_activity extends AppCompatActivity {
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_activity);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null){
            i=1;
        }else {
            SharedPreferences preferences = getSharedPreferences ( "UserData", 0 );
            String complete = preferences.getString ( "complete", null );
            if (complete!=null &&complete.length ()>4){
                i=2;
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i==0) {
                    Intent intent1 = new Intent ( Splash_screen_activity.this, MainActivity.class );
                    startActivity ( intent1 );
                    finish ();
                    overridePendingTransition ( R.anim.slide_from_right, R.anim.slide_to_left );
                }else if (i==1){
                    Intent intent = new Intent(Splash_screen_activity.this, LoginActivity.class);
                    startActivity(intent);
                    finish ();
                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                }else {
                    Intent intent=new Intent(Splash_screen_activity.this, UserDataActivity.class);
                    startActivity(intent);
                    finish ();
                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                }
            }
        },1500);
    }
}
