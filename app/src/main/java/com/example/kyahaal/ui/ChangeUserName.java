package com.example.kyahaal.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kyahaal.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChangeUserName extends AppCompatActivity {

    EditText name;
    TextView cancel,save;
    DatabaseReference mUserdb;
    FirebaseDatabase Database=FirebaseDatabase.getInstance();
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    boolean isConnected=false;
    Timer timer;
    ActionBar actionBar;
    ConnectivityManager connectivityManager;
    ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        name=findViewById(R.id.editText);
        Intent intent=getIntent();
        String uname=intent.getStringExtra("name");
        name.setText(uname);
        name.requestFocus();
        cancel=findViewById(R.id.textView11);
        save=findViewById(R.id.textView12);
        assert user!=null;
        String uid=user.getUid();
        mUserdb = Database.getReference("User").child(uid);
    }

    @Override
    protected void onStart() {
        super.onStart();
        settitle ();
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService( Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (networkCallback!=null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    connectivityManager.registerDefaultNetworkCallback ( networkCallback );
                } else {
                    NetworkRequest request = new NetworkRequest.Builder ().addCapability ( NetworkCapabilities.NET_CAPABILITY_INTERNET ).build ();
                    connectivityManager.registerNetworkCallback ( request, networkCallback );
                }
            }
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name=name.getText().toString();

                Thread tp =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, Object> map1=new HashMap<>();
                        map1.put("name",user_name);
                        mUserdb.updateChildren(map1).addOnSuccessListener ( new OnSuccessListener <Void> () {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SharedPreferences.Editor editor=getSharedPreferences ( "UserData" ,0).edit ();
                                editor.putString ( "name",user_name);
                                editor.apply ();

                                runOnUiThread ( new Runnable () {
                                    @Override
                                    public void run() {
                                        finish ();
                                    }
                                } );
                            }
                        } );
                    }
                });
                if (isConnected) {
                    tp.start ();
                }else {
                    Toast.makeText ( ChangeUserName.this, "Please Connect to Internet to use this functionality", Toast.LENGTH_SHORT ).show ();
                    finish ();
                }
            }
        });
    }

    public void settitle() {
        if (timer!=null){
            timer.cancel();
            timer.purge();
        }

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                isConnected = true;
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                isConnected = false;

            }
        };

        timer = new Timer ();
        timer.schedule(new TimerTask () {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        settitle();
                    }
                });

            }
        },500);
    }

}
