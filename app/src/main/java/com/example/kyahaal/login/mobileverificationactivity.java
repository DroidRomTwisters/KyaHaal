package com.example.kyahaal.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kyahaal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class mobileverificationactivity extends AppCompatActivity implements TextWatcher {

    private String mVerificationId, mobile_no;
    private FirebaseAuth mAuth;
    EditText code1, code2, code3, code4, code5, code6;
    TextView uphone, resendcode;
    String code;
    ProgressBar progressBar;
    Timer timer;
    boolean isConnected;
    ActionBar actionBar;
    Toolbar toolbar;
    PhoneAuthProvider.ForceResendingToken token;
    ConnectivityManager connectivityManager;
    ConnectivityManager.NetworkCallback networkCallback;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_mobile_verification );
        toolbar = findViewById ( R.id.action_bar );
        setSupportActionBar ( toolbar );
        actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.setTitle ( "OTP Verification" );
            actionBar.setDisplayHomeAsUpEnabled ( true );
        }
        mAuth = FirebaseAuth.getInstance ();
        Intent intent = getIntent ();
        mobile_no = intent.getStringExtra ( "mobile_no" );
        sendverificationcode ( mobile_no );
        code1 = findViewById ( R.id.code1 );
        code2 = findViewById ( R.id.code2 );
        code3 = findViewById ( R.id.code3 );
        code4 = findViewById ( R.id.code4 );
        code5 = findViewById ( R.id.code5 );
        code6 = findViewById ( R.id.code6 );
        progressBar = findViewById ( R.id.login_progress );
        uphone = findViewById ( R.id.userphone_no );
        uphone.setText ( "+91" + mobile_no );
        resendcode = findViewById ( R.id.resend_code );
        code1.requestFocus ();
        settitle ();
    }

    @Override
    protected void onStart() {
        super.onStart ();

    }

    @Override
    public boolean onNavigateUp() {
        new AlertDialog.Builder( mobileverificationactivity.this).setTitle("Exit Verification").setMessage("Are you sure you want to Stop OTP validation?").setPositiveButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (timer!=null){
                    timer.cancel ();
                    timer.purge ();
                }
                endactivitywhenfailed ();
            }
        }).setNegativeButton ( "Continue", null).show();
        return super.onNavigateUp ();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        new AlertDialog.Builder( mobileverificationactivity.this).setTitle("Exit Verification").setMessage("Are you sure you want to Stop OTP validation?").setPositiveButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (timer!=null){
                    timer.cancel ();
                    timer.purge ();
                }
                endactivitywhenfailed ();
            }
        }).setNegativeButton ( "Continue", null).show();
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
        settitle ();
        code1.addTextChangedListener ( this );
        code2.addTextChangedListener ( this );
        code3.addTextChangedListener ( this );
        code4.addTextChangedListener ( this );
        code5.addTextChangedListener ( this );
        code6.addTextChangedListener ( this );

        resendcode.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                resendverificationcode ( mobile_no );
            }
        } );

        code1.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    code2.requestFocus ();
                } else {
                    if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_BACK))) {
                        code1.setText ( null );
                    }
                }
                return false;
            }
        } );

        code2.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    code3.requestFocus ();
                } else {
                    if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_BACK))) {
                        code2.setText ( null );
                        code1.requestFocus ();
                    }
                }
                return false;
            }
        } );

        code3.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    code4.requestFocus ();
                } else {
                    if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_BACK))) {
                        code3.setText ( null );
                        code2.requestFocus ();
                    }
                }
                return false;
            }
        } );

        code4.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    code5.requestFocus ();
                } else {
                    if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_BACK))) {
                        code5.setText ( null );
                        code4.requestFocus ();
                    }
                }
                return false;
            }
        } );

        code5.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    code6.requestFocus ();
                } else {
                    if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_BACK))) {
                        code6.setText ( null );
                        code5.requestFocus ();
                    }
                }
                return false;
            }
        } );

        code6.setOnEditorActionListener ( new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    verifyVerificationcode ();
                } else {
                    if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_BACK))) {
                        code1.setText ( null );
                    }
                }
                return false;
            }
        } );

    }

    private void sendverificationcode(String mobile_no) {
        PhoneAuthProvider.getInstance ().verifyPhoneNumber (
                "+91" + mobile_no,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
        Timer timer1 = new Timer ();
        timer1.schedule ( new TimerTask () {
            @Override
            public void run() {
                resendcode.setVisibility ( View.VISIBLE );
            }
        }, 60000 );
    }

    private void resendverificationcode(String mobile_no) {
        PhoneAuthProvider.getInstance ().verifyPhoneNumber (
                "+91" + mobile_no,
                30,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks,
                token
        );
        Timer timer1 = new Timer ();
        timer1.schedule ( new TimerTask () {
            @Override
            public void run() {
                resendcode.setVisibility ( View.VISIBLE );
            }
        }, 30000 );
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode ();
            if (code != null) {
                String[] digits = new String[ code.length () ];
                int j = 0;
                for (char ch : code.toCharArray ()) {
                    digits[ j++ ] = Character.toString ( ch );
                }
                for (int i = 1; i <= digits.length; i++) {

                    if (i == 1) {
                        code1.setText ( digits[ 0 ] );
                    } else if (i == 2) {
                        code2.setText ( digits[ 1 ] );
                    } else if (i == 3) {
                        code3.setText ( digits[ 2 ] );
                    } else if (i == 4) {
                        code4.setText ( digits[ 3 ] );
                    } else if (i == 5) {
                        code5.setText ( digits[ 4 ] );
                    } else if (i == 6) {
                        code6.setText ( digits[ 5 ] );
                    }
                }
                Timer timer2 = new Timer ();
                timer2.schedule ( new TimerTask () {
                    @Override
                    public void run() {
                        verifyVerificationcode ();
                    }
                }, 2000 );

            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText ( mobileverificationactivity.this, e.getMessage (), Toast.LENGTH_LONG ).show ();
            endactivitywhenfailed ();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent ( s, forceResendingToken );
            Toast.makeText ( mobileverificationactivity.this, "Code Sent", Toast.LENGTH_SHORT ).show ();
            token = forceResendingToken;
            mVerificationId = s;
        }
    };

    private void verifyVerificationcode() {
        code = code1.getText ().toString () + code2.getText ().toString () + code3.getText ().toString () + code4.getText ().toString () + code5.getText ().toString () + code6.getText ().toString ();
        if (code.length () == 6) {
            progressBar.setVisibility ( View.VISIBLE );
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential ( mVerificationId, code );
            signInWithPhoneAuthCredential ( credential );
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential ( credential ).addOnCompleteListener ( mobileverificationactivity.this, new OnCompleteListener <AuthResult> () {
            @Override
            public void onComplete(@NonNull Task <AuthResult> task) {
                if (task.isSuccessful ()) {
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
                    String Uid = null;
                    if (currentFirebaseUser != null) {
                        Uid = currentFirebaseUser.getUid ();
                    }
                    final FirebaseDatabase database = FirebaseDatabase.getInstance ();
                    String token_id = FirebaseInstanceId.getInstance ().getToken ();
                    if (Uid != null) {
                        DatabaseReference mUserRef = database.getReference ().child ( "User" ).child ( Uid );
                        Map <String, Object> map1 = new HashMap <> ();
                        map1.put ( "mobile_no", "+91" + mobile_no );
                        map1.put ( "isconfiguringcomplete", "false" );
                        map1.put ( "token_id", token_id );
                        mUserRef.updateChildren ( map1 );
                        Intent intent1 = new Intent ( mobileverificationactivity.this, UserDataActivity.class );
                        startActivity ( intent1 );
                        finish ();
                        overridePendingTransition ( R.anim.slide_from_right, R.anim.slide_to_left );
                    } else {
                        Toast.makeText ( mobileverificationactivity.this, "Something went wrong,we are looking at it.", Toast.LENGTH_SHORT ).show ();
                        endactivitywhenfailed ();
                    }

                } else {
                    Toast.makeText ( mobileverificationactivity.this, "Sorry!!! we encountered some problem, We will fix it soon.", Toast.LENGTH_LONG ).show ();
                    endactivitywhenfailed ();
                }
            }
        } );
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (getCurrentFocus () != null) {
            if (getCurrentFocus () == code1) {
                if (code1.getText ().length () == 1) {
                    code2.requestFocus ();
                    resendcode.setVisibility ( View.VISIBLE );
                } else if (code1.length () == 0) {
                    code1.requestFocus ();
                }
            } else if (getCurrentFocus () == code2) {
                if (code2.getText ().length () == 1) {
                    code3.requestFocus ();
                } else if (code1.length () == 0) {
                    code2.requestFocus ();
                }
            } else if (getCurrentFocus () == code3) {
                if (code3.getText ().length () == 1) {
                    code4.requestFocus ();
                } else if (code1.length () == 0) {
                    code3.requestFocus ();
                }
            } else if (getCurrentFocus () == code4) {
                if (code4.getText ().length () == 1) {
                    code5.requestFocus ();
                } else if (code1.length () == 0) {
                    code4.requestFocus ();
                }
            } else if (getCurrentFocus () == code5) {
                if (code5.getText ().length () == 1) {
                    code6.requestFocus ();
                } else if (code1.length () == 0) {
                    code5.requestFocus ();
                }
            } else if (getCurrentFocus () == code6) {
                if (code6.getText ().length () == 1) {
                    verifyVerificationcode ();
                } else if (code1.length () == 0) {
                    code6.requestFocus ();
                }
            }
        }
    }

    public void settitle() {
        if (timer != null) {
            timer.cancel ();
            timer.purge ();
        }

        networkCallback = new ConnectivityManager.NetworkCallback () {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable ( network );
                isConnected = true;
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost ( network );
                isConnected = false;

            }
        };

        if (isConnected) {
            actionBar.setTitle ( "OTP Verification" );
        } else {
            actionBar.setTitle ( "Connecting..." );
        }
        timer = new Timer ();
        timer.schedule ( new TimerTask () {
            @Override
            public void run() {
                runOnUiThread ( new Runnable () {
                    @Override
                    public void run() {
                        settitle ();
                    }
                } );

            }
        }, 900 );
    }

    public void endactivitywhenfailed(){
        Intent intent = new Intent( mobileverificationactivity.this,LoginActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        intent.putExtra("mobile_no", mobile_no );
        startActivity ( intent );
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish ();
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
