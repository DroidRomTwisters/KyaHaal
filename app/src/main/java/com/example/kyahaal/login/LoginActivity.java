package com.example.kyahaal.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kyahaal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends AppCompatActivity implements TextWatcher {
    private EditText mobile;
    private FloatingActionButton continuebtn;
    private ProgressBar loginprogress;
    boolean isConnected=false;
    Toolbar toolbar;
    Timer timer;
    ActionBar actionBar;
    ConnectivityManager connectivityManager;
    ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mobile=findViewById(R.id.verification_code);
        mobile.requestFocus();
        continuebtn=findViewById(R.id.donebtn2 );
        loginprogress=findViewById(R.id.login_progress);
        toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setTitle("Your Phone");
        }
        settitle ();
    }

    @Override
    protected void onStart() {
        super.onStart ();

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        settitle();
        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobile_no = mobile.getText().toString().trim();
                if (mobile_no.length() != 10) {
                    mobile.setError("please enter a valid mobile number");
                    mobile.requestFocus();
                    return;
                } else {
                    continuebtn.setClickable(false);
                    continuebtn.setImageDrawable(null);
                    loginprogress.setVisibility(View.VISIBLE);
                    Timer timer=new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (isConnected) {
                                Intent intent = new Intent(LoginActivity.this, mobileverificationactivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("mobile_no", mobile_no);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginprogress.setVisibility(View.GONE);
                                    }
                                });
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                finish();
                            }
                        }
                    },1000);

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



        if (isConnected){
            actionBar.setTitle("Your Phone");
        }else {
            actionBar.setTitle("Waiting for Network...");
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        settitle();
                    }
                });

            }
        },900);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }

    @Override
    protected void onPause() {
        super.onPause ();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();

    }
}