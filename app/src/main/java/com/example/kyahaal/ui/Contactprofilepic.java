package com.example.kyahaal.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.transition.ChangeBounds;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.example.kyahaal.R;

import java.io.File;

public class Contactprofilepic extends AppCompatActivity {
    ImageView pp;
    String link,uname;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(200));
        setContentView(R.layout.activity_contactprofilepic);
        Intent intent=getIntent();
        link=intent.getStringExtra("link");
        uname=intent.getStringExtra("username");
        pp=findViewById(R.id.friend_profile_pic);
        pp.setOnTouchListener(new ImageMatrixTouchHandler(getApplicationContext()));
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(uname);
        Glide.with(this).load(link).placeholder(R.drawable.ic_default_dp1).into(pp);


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
