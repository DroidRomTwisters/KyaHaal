package com.example.kyahaal.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.example.kyahaal.R;
import com.example.kyahaal.WorkManagers.ContactsWorkManager;
import com.example.kyahaal.database.ContactsDBhelper;
import com.example.kyahaal.message.MessageActivity;
import com.example.kyahaal.ui.ContactProfileImageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class ContactsActivity2 extends AppCompatActivity implements ContactsRecyclerAdapter2.OnContactClickListener {
  private DatabaseReference mydb;
  static ContactsDBhelper myDb;
  static ContactsRecyclerAdapter2 adapter;
  static ArrayList<ContactModelDiffUtils> allchats,modelDiffUtils;
  static RecyclerView mContactslist;
  Toolbar toolbar;
  ActionBar actionBar;
  OneTimeWorkRequest request;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contacts);
    toolbar = findViewById(R.id.action_bar);
    setSupportActionBar(toolbar);
    actionBar = getSupportActionBar();
    mContactslist=findViewById(R.id.contactsrecycle);
    LinearLayoutManager mLayoutManager = new LinearLayoutManager ( this );
    mContactslist.setLayoutManager ( mLayoutManager );
    mContactslist.setHasFixedSize ( true );
    myDb=new ContactsDBhelper(this);
    allchats=myDb.contactArrayList();
    adapter=new ContactsRecyclerAdapter2(this,allchats,this);
    adapter.setHasStableIds(true);
    mContactslist.setAdapter(adapter);
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        startwork ();
        return true;
      }
    });
    if (actionBar!=null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
      actionBar.setTitle("Contacts");
    }
    mydb= FirebaseDatabase.getInstance().getReference("User").child( Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser()).getUid());
  }

  @Override
  protected void onStart() {
    super.onStart ();
  }

  @Override
  protected void onResume() {
    super.onResume();
    setOnline();
  }

  @Override
  protected void onPause() {
    super.onPause();
    setTimestamp();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.contacts_menu,menu);
    return true;
  }

  @Override
  public void OnContactClick(int position) {
    final ContactModelDiffUtils contactModal = allchats.get(position);
    Intent intent = new Intent(this, MessageActivity.class);
    intent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
    intent.putExtra("username", contactModal.getNAME());
    intent.putExtra("number", contactModal.getNUMBER());
    intent.putExtra("uid", contactModal.getUID());
    intent.putExtra("link",contactModal.getMEDIA_URL());
    startActivity(intent);
  }

  @Override
  public void OnProfilePicClick(int position, ImageView view) {
    final ContactModelDiffUtils contactModal = allchats.get(position);
    Intent intent1=new Intent(this, ContactProfileImageActivity.class);
    intent1.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
    intent1.putExtra("username",contactModal.getNAME());
    intent1.putExtra("number",contactModal.getNUMBER());
    intent1.putExtra("uid", contactModal.getUID());
    intent1.putExtra("link",contactModal.getMEDIA_URL());
    startActivity(intent1);
  }


  public static void refresh(){
    modelDiffUtils= myDb.contactArrayList ();
    adapter.setStorecontacts ( modelDiffUtils );
  }

  public void setOnline(){
    Thread thread=new Thread ( new Runnable () {
      @Override
      public void run() {
        mydb.child("Online").setValue(true);
      }
    } );
    thread.start ();
  }

  public void setTimestamp(){
    Thread thread=new Thread ( new Runnable () {
      @Override
      public void run() {
        mydb.child("Online").setValue(false);
        mydb.child("seen").setValue(TIMESTAMP);
      }
    } );
    thread.start ();
  }

  public void startwork(){
    Thread thread=new Thread ( new Runnable () {
      @Override
      public void run() {
        request = new OneTimeWorkRequest .Builder ( ContactsWorkManager.class ).build ();
        WorkManager.getInstance ( ContactsActivity2.this ).enqueue ( request );
      }
    });
    thread.start ();
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed ();
    return super.onSupportNavigateUp ();
  }
}
