package com.example.kyahaal.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.example.kyahaal.R;

public class ProfilePicActivity extends AppCompatActivity {

    String link;
    ImageView profile_pic;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        setContentView(R.layout.activity_profile_pic);
        profile_pic=findViewById(R.id.profile_pic);
        profile_pic.setOnTouchListener(new ImageMatrixTouchHandler(getApplicationContext()));
        profilepic();

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Profile Photo");
    }
    private void profilepic() {
        SharedPreferences preferences= getSharedPreferences("UserData",0);
        link=preferences.getString ( "dp_link" ,null);
        Glide.with(this).load(link).placeholder (R.drawable.ic_default_dp1).into(profile_pic);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
