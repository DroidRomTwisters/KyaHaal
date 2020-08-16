package com.example.kyahaal.login;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionPlan;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.kyahaal.MainChat.MainActivity;
import com.example.kyahaal.MainChat.Splash_screen_activity;
import com.example.kyahaal.R;
import com.example.kyahaal.essentials.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class finsihingActivity extends AppCompatActivity {
    FirebaseUser currentuser;
    DatabaseReference dbref;
    FirebaseAuth mAuth;
    String name,number,token_id,dp_link,complete,status;
    Timer timer;
    boolean isConnected;
    ConnectivityManager connectivityManager;
    NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_finsihing );
        mAuth=FirebaseAuth.getInstance ();
        currentuser=mAuth.getCurrentUser ();
        dbref= FirebaseDatabase.getInstance ().getReference ("User").child (currentuser.getUid ());
        addinfo ();
    }

    @Override
    protected void onResume() {
        super.onResume ();
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
        addinfo ();
    }

    @Override
    protected void onStart() {
        super.onStart ();

    }

    public void addinfo(){
        if (isConnected) {
            if (timer!=null){
                timer.cancel ();
                timer.purge ();
            }
            dbref.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue (User.class);
                    if (user!=null) {
                        name = user.getName ();
                        status = user.getStatus ();
                        token_id = user.getToken_id ();
                        dp_link = user.getProfilepic ();
                        number = user.getMobile_no ();
                        new dboperations ().execute (  );
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }else {
            settitle ();
        }
    }

    public class dboperations extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
        }

        @Override
        protected String doInBackground(String... strings) {
            dbref.child ( "isconfiguringcomplete" ).setValue ( "true" );
            complete="true";
            SharedPreferences.Editor editor=getSharedPreferences ( "UserData" ,0).edit ();
            editor.putString ( "name",name );
            editor.putString ( "status",status );
            editor.putString ( "complete",complete );
            editor.putString ( "token_id",token_id );
            editor.putString ( "dp_link",dp_link );
            editor.putString ( "number",number );
            editor.apply ();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute ( s );
            Intent intent1 = new Intent ( finsihingActivity.this, MainActivity.class );
            startActivity ( intent1 );
            finish ();
            overridePendingTransition ( R.anim.slide_from_right, R.anim.slide_to_left );
        }
    }

    public void settitle() {
        if (timer!=null){
            timer.cancel();
            timer.purge();
        }

        networkCallback = new NetworkCallback () {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                isConnected = true;
                addinfo ();
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
                        settitle ();
                    }
                });

            }
        },900);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
    }
}
