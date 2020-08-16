package com.example.kyahaal.MainChat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;

import com.bumptech.glide.Glide;
import com.example.kyahaal.R;
import com.example.kyahaal.contacts.ContactsActivity2;
import com.example.kyahaal.database.ContactsDBhelper;
import com.example.kyahaal.database.DataBaseHelper;
import com.example.kyahaal.essentials.User;
import com.example.kyahaal.login.UserDataActivity;
import com.example.kyahaal.message.MessageActivity;
import com.example.kyahaal.message.MessageonlineModal;
import com.example.kyahaal.settings.Settings;
import com.example.kyahaal.ui.ContactProfileImageActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class MainActivity extends AppCompatActivity implements ChatListAdapter.OnChatClickListener {
    Context  context;
    static RecyclerView mchatlist;
    ChatListAdapter adapter;
    static DataBaseHelper myDb;
    static ContactsDBhelper condb;
    DrawerLayout drawer;
    static FirebaseAuth mAuth;
    CircleImageView profile_pic;
    StorageReference mStorageRef;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    static ArrayList<ChatListModel> allchats,newallchats;
    private LinearLayoutManager mLayoutManager;
    DatabaseReference mRootref,mChatref;
    FloatingActionButton con_btn;
    Toolbar toolbar;
    String link;
    MessageonlineModal messageonlineModal;
    Activity activity;
    ChildEventListener childEventListener;
    TextView custtitle;
    Boolean multiselect=false;
    ArrayList<ChatListModel> selecteditems;
    ArrayList<Integer> pos;
    int type=0;
    boolean shouldrefresh=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null) {
            activity=MainActivity.this;
            toolbar = findViewById(R.id.action_bar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                navigationView = findViewById(R.id.nav_view);
                View iview = navigationView.getHeaderView(0);
                mchatlist = findViewById(R.id.mchatlist);
                profile_pic = iview.findViewById(R.id.profile_pic);
            }
            selecteditems=new ArrayList <> (  );
            pos=new ArrayList <> (  );
            mLayoutManager = new LinearLayoutManager(this);
            mchatlist.setLayoutManager(mLayoutManager);
            mchatlist.setHasFixedSize(true);
            con_btn = findViewById(R.id.contacts_fab);
            condb = new ContactsDBhelper(this);
            mRootref = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

            //populating view
            myDb = new DataBaseHelper(this);
            shouldrefresh=false;
            type=0;
            startcontactactivity ();
            custtitle=findViewById ( R.id.tvtitle1 );
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (multiselect){
            if (selecteditems.size ()>0){
                selecteditems.clear ();
                adapter.removeselection ();
                setupToolbar2 ();
            }
        }
        childEventListener=new ChildEventListener () {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                messageonlineModal = dataSnapshot.getValue(MessageonlineModal.class);
                if (messageonlineModal != null) {
                    long timesent = messageonlineModal.getTimesent();
                    final String push_id = messageonlineModal.getPush_id();
                    boolean done=myDb.insertMessage(messageonlineModal.getMessage(), messageonlineModal.getFrom(), mAuth.getCurrentUser().getUid(), 0, timesent, 1, push_id);
                    if (done) {
                        myDb.incrementuc ( messageonlineModal.getFrom (), MainActivity.this );
                    }
                    mRootref.child("messages").child(push_id).setValue ( null ).addOnSuccessListener(new OnSuccessListener <Void> () {
                        @Override
                        public void onSuccess(Void aVoid) {
                            myDb.updatechatlist(messageonlineModal.getFrom(),push_id,0);
                            refresh();
                        }
                    });
                    final String[] chatuserphno = new String[1];

                    //add new chat if exists in database
                    final Thread thread = new Thread( new Runnable() {
                        @Override
                        public void run() {
                            mChatref = FirebaseDatabase.getInstance().getReference("User").child(messageonlineModal.getFrom());
                            mChatref.addValueEventListener(new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user!=null) {
                                        chatuserphno[0] = user.getMobile_no();
                                        String cname = getContactsname(MainActivity.this, chatuserphno[0]);
                                        if (cname!=null) {
                                            boolean done=myDb.insertdata(messageonlineModal.getFrom(), chatuserphno[0], cname, messageonlineModal.getPush_id(), null, 0,1,user.getProfilepic (),timestamptotime ( messageonlineModal.getTimesent () ));
                                            if (!done){
                                                myDb.updatelatestmsgtime ( messageonlineModal.getFrom (),timestamptotime ( messageonlineModal.getTimesent () ) );
                                                myDb.updateprofilepic(messageonlineModal.getFrom(),cname);
                                            }
                                        }else{
                                            boolean done=myDb.insertdata(messageonlineModal.getFrom(), chatuserphno[0], chatuserphno[0], messageonlineModal.getPush_id(), null, 0,1,user.getProfilepic (),timestamptotime ( messageonlineModal.getTimesent () ));
                                            if (!done){
                                                myDb.updatelatestmsgtime ( messageonlineModal.getFrom (),timestamptotime ( messageonlineModal.getTimesent () ) );
                                                myDb.updateprofilepic(messageonlineModal.getFrom(),chatuserphno[0]);
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                }
                            });
                        }
                    });
                    thread.start ();


                }else {
                    refresh ();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        if (mAuth.getCurrentUser()!=null && !activity.isFinishing () && !activity.isDestroyed ()) {
            new profilepicthread ().execute (  );
            populateview();
            setOnline();
            mRootref.child ( "messages" ).addChildEventListener ( childEventListener );
            refresh ();

        }
    }

    @Override
    protected void onStart() {
        super.onStart ();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuth.getCurrentUser()!=null) {
            myDb.close ();
        }
    }


    @Override
    public void OnchatClickListener(int position) {
        final ChatListModel chatListModel=allchats.get(position);
        if (multiselect){
            custtitle.setText ( String.valueOf ( selecteditems.size ()+1) );
            changeSelecteditems ( chatListModel,position );
        }else {
            stop ();
            Intent intent = new Intent ( context, MessageActivity.class );
            intent.putExtra ( "username", chatListModel.getName () );
            intent.putExtra ( "number", chatListModel.getPhnum () );
            intent.putExtra ( "uid", chatListModel.getUid () );
            intent.putExtra ( "link", chatListModel.getMedia_url () );
            startActivity ( intent );
        }

    }

    @Override
    public void OnProfilePicClick(int position, ImageView view) {
        final ChatListModel chatListModel=allchats.get(position);
        Intent intent1=new Intent(context, ContactProfileImageActivity.class);
        intent1.putExtra("username",chatListModel.getName());
        intent1.putExtra("number",chatListModel.getPhnum());
        intent1.putExtra("uid",chatListModel.getUid());
        intent1.putExtra ( "link",chatListModel.getMedia_url ());
        startActivity(intent1);
    }

    @Override
    public void OnChatLongClickListener(int position) {
        final ChatListModel chatListModel=allchats.get(position);
        if(!multiselect) {
            multiselect = true;
            setupToolbar ();
            custtitle.setVisibility ( View.VISIBLE );
            custtitle.setText ( String.valueOf ( selecteditems.size ()+1) );
            changeSelecteditems ( chatListModel ,position);
        }else {
            custtitle.setText ( String.valueOf ( selecteditems.size ()+1) );
            changeSelecteditems ( chatListModel,position );
        }

    }
    private class profilepicthread extends AsyncTask<String,String,String >{
        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences preferences= context.getSharedPreferences("UserData",0);
            link=preferences.getString ( "dp_link" ,null);
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            if (!activity.isFinishing () && !activity.isDestroyed ()) {
                Glide.with ( MainActivity.this ).load ( link ).placeholder ( R.drawable.ic_default_dp1 ).into ( profile_pic );
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (multiselect){
            selecteditems.clear ();
            setupToolbar2 ();
            adapter.removeselection ();
        }else {
            super.onBackPressed ();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        selecteditems.clear ();
        setupToolbar2 ();
        adapter.removeselection ();
        return true;
    }

    public void refresh(){
        if (mAuth.getCurrentUser()!=null) {
            newallchats=myDb.chatListModels();
            adapter.setListchats ( newallchats );
        }
    }

    public void populateview(){

        //drawer initialisation and related code...
        drawer = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        con_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=1;
                startcontactactivity ();
            }
        });

        //setting actions for options in drawer!!!
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_settings:
                        Intent intent=new Intent(MainActivity.this, Settings.class);
                        intent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;

                    case R.id.nav_contacts:
                        type=1;
                        startcontactactivity ();
                        break;

                    default:
                        drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    //actionbar drawer toggling
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }else {
            if (item.getItemId () == R.id.action_delete) {
                for (int i = 0; i < selecteditems.size (); i++) {
                    ChatListModel chatListModel = selecteditems.get ( i );
                    myDb.deletechatrecord ( chatListModel.getUid (), Objects.requireNonNull ( mAuth.getCurrentUser () ).getUid (), chatListModel.getUid () );
                }
                selecteditems.clear ();
                setupToolbar2 ();
                adapter.notifyDataSetChanged ();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //setting whether user is online or offline
    public void setOnline(){
        mRootref.child("Online").setValue(true);
    }

    public void setTimestamp(){
        mRootref.child("Online").setValue(false);
        mRootref.child("seen").setValue(TIMESTAMP);
    }

    public void setupToolbar(){
        con_btn.setClickable ( false );
        profile_pic.setClickable ( false );
        toolbar.getMenu().clear();
        toolbar.setTitle ( null );
        custtitle.setText ( String.valueOf ( selecteditems.size ()) );
        toolbar.inflateMenu(R.menu.main_cab);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    public void setupToolbar2(){
        con_btn.setClickable ( true );
        multiselect=false;
        profile_pic.setClickable ( true );
        custtitle.setVisibility ( View.GONE);
        toolbar.getMenu().clear();
        toolbar.setTitle(R.string.app_name);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            refresh();
        }
    }

    public static String getContactsname(Context context,String mobile_no){
        ContentResolver cr=context.getContentResolver();
        Uri uri=Uri.withAppendedPath( ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(mobile_no));
        Cursor cursor=cr.query(uri,new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);
        if (cursor==null){
            return null;
        }
        String contactName=null;
        if (cursor.moveToFirst()){
            contactName=cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (!cursor.isClosed()){
            cursor.close();
        }
        return contactName;
    }

    public void stop(){
        mRootref.child ( "messages" ).removeEventListener ( childEventListener );
    }

    public void changeSelecteditems(ChatListModel chatListModel,int position){
        if (!(selecteditems.contains ( chatListModel ))){
            selecteditems.add ( chatListModel );
            adapter.changeselection(selecteditems,position);
        }else {
            selecteditems.remove ( chatListModel );
            adapter.changeselection(selecteditems,position);
            if (selecteditems.size ()==0){
                setupToolbar2 ();
            }
        }
    }

    public void startcontactactivity(){
        if (ContextCompat.checkSelfPermission (MainActivity.this,  Manifest.permission.READ_CONTACTS )!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale ( MainActivity.this, Manifest.permission.READ_CONTACTS )) {
                AlertDialog.Builder alert = new AlertDialog.Builder ( MainActivity.this );
                alert.setCancelable ( true );
                alert.setTitle ( "Storage Permission Required" );
                alert.setMessage ( "KyaHaal needs permission to access your contacts to display which of your contacts use this app" );
                alert.setPositiveButton ( "Continue", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions ( MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1 );
                    }
                } );
                AlertDialog alertDialog = alert.create ();
                alertDialog.show ();
            } else {
                ActivityCompat.requestPermissions ( MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1 );
            }
        }else {
            if (type==0){
                allchats = myDb.chatListModels();
                adapter = new ChatListAdapter(this, allchats, this, mAuth.getCurrentUser().getUid(), MainActivity.this, MainActivity.this);
                adapter.setHasStableIds(true);
                mchatlist.setAdapter(adapter);
            }else {
                Intent intent1=new Intent(MainActivity.this, ContactsActivity2.class);
                intent1.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
        switch (requestCode){
            case 1:
                if (type==0) {
                    if (grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED) {
                        shouldrefresh=true;
                        allchats = myDb.chatListModels ();
                        adapter = new ChatListAdapter ( this, allchats, this, mAuth.getCurrentUser ().getUid (), MainActivity.this, MainActivity.this );
                        adapter.setHasStableIds ( true );
                        mchatlist.setAdapter ( adapter );
                    } else {
                        Toast.makeText ( context, "permission denied", Toast.LENGTH_SHORT ).show ();
                        finish ();
                    }
                }else {
                    if (grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent1 = new Intent ( MainActivity.this, ContactsActivity2.class );
                        intent1.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity ( intent1 );
                        overridePendingTransition ( R.anim.slide_from_right, R.anim.slide_to_left );
                    }else {
                        Toast.makeText ( context, "permission denied", Toast.LENGTH_SHORT ).show ();
                    }
                }
                break;
        }
    }

    String timestamptotime(long timestamp){
        try {
            Date time=new Date(timestamp);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat ( "yyyy-MM-dd hh:mm:ss a" );
            String tp= simpleDateFormat.format(time);
            return tp;

        }catch (Exception e){

        }
        return "";
    }
}
