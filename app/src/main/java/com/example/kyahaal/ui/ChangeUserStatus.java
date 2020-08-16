package com.example.kyahaal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kyahaal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChangeUserStatus extends AppCompatActivity {
    EditText status;
    TextView cancel,save;
    DatabaseReference mUserdb;
    FirebaseDatabase Database=FirebaseDatabase.getInstance();
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_status);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        status=findViewById(R.id.editText);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=getIntent();
        String ustatus=intent.getStringExtra("status");
        status.setText(ustatus);
        status.requestFocus();
        cancel=findViewById(R.id.textView11);
        save=findViewById(R.id.textView12);
        assert user!=null;
        String uid=user.getUid();
        mUserdb = Database.getReference("User").child(uid);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_status=status.getText().toString();
                Intent intent=new Intent();
                intent.putExtra("status",user_status);
                setResult(1,intent);

                Thread tp =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, Object> map1=new HashMap<>();
                        map1.put("status",user_status);
                        mUserdb.updateChildren(map1);
                    }
                });
                tp.start();
                finish();
            }
        });
    }
}
