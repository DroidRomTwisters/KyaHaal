package com.example.kyahaal.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kyahaal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class Settings extends AppCompatActivity {
    RelativeLayout user_info;
    File imgfile;
    String username;
    String userstatus;
    Bitmap bmp;
    FirebaseAuth mAuth;
    String link;
    private CircleImageView profile_pic;
    private TextView uname,status;
    private RecyclerView recyclerView;
    private List<String> list;
    DatabaseReference mRootref;
    DatabaseReference comref;
    private List<String> list1;
    private int[] images={R.drawable.ic_key,R.drawable.ic_chat,R.drawable.ic_bell,R.drawable.ic_data,R.drawable.ic_help,R.drawable.ic_rename};
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.action_bar);
        user_info=findViewById(R.id.user_info);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Settings");
        mAuth = FirebaseAuth.getInstance();
        uname=findViewById(R.id.textView3);
        status=findViewById(R.id.textView4);
        profile_pic=findViewById(R.id.profile_image1);
        recyclerView=findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mRootref= FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        list= Arrays.asList(getResources().getStringArray(R.array.Settings_Option));
        list1= Arrays.asList(getResources().getStringArray(R.array.Settings_Option_Description));
        adapter=new RecyclerAdapter(images,list,list1);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setOnline();
        new profilepicthread ().execute (  );
        user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(Settings.this, Pair.create((View)profile_pic,profile_pic.getTransitionName()));
                Intent intent=new Intent(Settings.this, UserProfileActivity.class);
                startActivity(intent,activityOptionsCompat.toBundle());
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
    }

    private class profilepicthread extends AsyncTask<String,String,String >{
        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences preferences= getSharedPreferences("UserData",0);
            link=preferences.getString ( "dp_link" ,null);
            username = preferences.getString ( "name" ,null);
            userstatus=preferences.getString ( "status" ,null);


            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            uname.setText(username);
            if (userstatus!=null)
            {
                status.setText(userstatus);
            }else {
                status.setText(R.string.default_about);
            }
            Glide.with( Settings.this).load(link).placeholder(R.drawable.ic_default_dp1).into(profile_pic);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        setTimestamp();
    }


    public void setOnline(){
        mRootref.child("Online").setValue(true);
    }

    public void setTimestamp(){
        mRootref.child("Online").setValue(false);
        mRootref.child("seen").setValue(TIMESTAMP);
    }
}
