package com.example.kyahaal.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kyahaal.R;
import com.example.kyahaal.Utils.GetTimeAgo;
import com.example.kyahaal.Utils.GetTimeAgo2;
import com.example.kyahaal.database.DataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class MessageActivity extends AppCompatActivity {
    Context context;
    FloatingActionButton send_msg;
    ActionBar actionBar;
    String uname;
    static String mChatUserid;
    static String mCurrentUserid;
    String profilepic,link;
    CircleImageView profile_pic;
    MessageAdapter adapter;
    EditText msg;
    LinearLayoutManager mLayoutManager;
    static ArrayList<MessageModal> storemsg;
    static DataBaseHelper myDb;
    DatabaseReference mRootref,mChatref;
    static RecyclerView mMsglist;
    TextView username,last_seen,day;
    boolean Online=false;
    boolean isTyping=false;
    Long seen=null;
    TextView typing;
    Timer timer,timer1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        msg=findViewById(R.id.send_msg);
        send_msg=findViewById(R.id.msg_btn);
        actionBar = getSupportActionBar();
        profile_pic=findViewById(R.id.profile_pic);
        username=findViewById(R.id.textView13);
        day=findViewById ( R.id.day );
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Intent intent = getIntent();
        mCurrentUserid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        uname = intent.getStringExtra("username");
        username.setText(uname);
        profilepic=intent.getStringExtra("number");
        mChatUserid=intent.getStringExtra("uid");
        link=intent.getStringExtra("link");
        myDb=new DataBaseHelper(MessageActivity.this);
        mRootref= FirebaseDatabase.getInstance().getReference("User").child(mCurrentUserid);
        mChatref=FirebaseDatabase.getInstance().getReference("User").child(mChatUserid);
        mMsglist=findViewById(R.id.messagelist);
        mLayoutManager=new LinearLayoutManager(this);
        mMsglist.setHasFixedSize(true);
        mMsglist.setItemViewCacheSize(20);
        mMsglist.setLayoutManager(mLayoutManager);
        storemsg=myDb.messageModalArrayList(mCurrentUserid,mChatUserid);
        last_seen=findViewById(R.id.last_seen);
        if (storemsg.size()>0){
           adapter=new MessageAdapter(this,storemsg,mChatUserid,mCurrentUserid);
           adapter.setHasStableIds ( true );
           mMsglist.scrollToPosition(storemsg.size()-1);
           mMsglist.setAdapter(adapter);
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        mMsglist.addOnScrollListener ( new RecyclerView.OnScrollListener () {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (timer1!=null){
                    timer1.cancel ();
                    timer1.purge ();
                }
                int pos = 0;
                if (mMsglist.getLayoutManager () instanceof LinearLayoutManager){
                    pos=((LinearLayoutManager) mMsglist.getLayoutManager ()).findFirstCompletelyVisibleItemPosition ();
                }if (pos>=0){
                    MessageModal messageModal=storemsg.get ( pos );
                    String time= GetTimeAgo2.getTimeAgo ( messageModal.getRead_timestamp (),MessageActivity.this);
                    if (time.equals ( "TODAY" )){
                        day.setVisibility ( View.GONE);
                    }else {
                        day.setVisibility ( View.VISIBLE );
                        day.setText ( time );
                    }
                }
                super.onScrollStateChanged ( recyclerView, newState );
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                timer1=new Timer ();
                timer1.schedule ( new TimerTask () {
                    @Override
                    public void run() {
                        runOnUiThread ( new Runnable () {
                            @Override
                            public void run() {
                                day.setVisibility ( View.GONE);
                            }
                        } );

                    }
                },5000 );
                super.onScrolled ( recyclerView, dx, dy );
            }
        } );

        myDb.unreadcounttozero(mChatUserid,MessageActivity.this);
        myDb.close ();
        getCurrentStatus();
        setOnline();
        actionBar.setTitle(null);
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChatref.child(mCurrentUserid).child("istyping").setValue(true);
                if (timer!=null){
                    timer.cancel();
                    timer.purge();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mChatref.child(mCurrentUserid).child("istyping").setValue(false);
                    }
                },1000);
            }
        });

        if (link!=null){
            Glide.with(this).load(link).placeholder(R.drawable.ic_default_dp1).into(profile_pic);
        }else {
            Glide.with(this).load(R.drawable.ic_default_dp1).into(profile_pic);
        }

        mRootref.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageonlineModal messageonlineModal=dataSnapshot.getValue(MessageonlineModal.class);
                if (messageonlineModal!=null) {
                    long timesent = messageonlineModal.getTimesent();
                    myDb.insertMessage(messageonlineModal.getMessage(), messageonlineModal.getFrom(), mCurrentUserid, 0, timesent, 1, messageonlineModal.getPush_id());
                    myDb.updatelatestmsgtime ( mChatUserid,timestamptotime ( messageonlineModal.getTimesent () ) );
                    myDb.close ();
                    String push_id=messageonlineModal.getPush_id();
                    mRootref.child("messages").child(push_id).removeValue();
                }
                refresh();
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
        });



        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message=msg.getText().toString().trim();
                if (!TextUtils.isEmpty(message)){
                    DatabaseReference user_message_push = mChatref.child("messages").push();
                    final String push_id=user_message_push.getKey();
                    assert push_id != null;
                    myDb.insertMessage(message,mCurrentUserid,mChatUserid,1,new Date().getTime(),0,push_id);
                    final Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("message",message);
                    messageMap.put("from",mCurrentUserid);
                    messageMap.put("mediaUrl",null);
                    messageMap.put("timesent", ServerValue.TIMESTAMP);
                    messageMap.put("seen",true);
                    messageMap.put("push_id",push_id);
                    mChatref.child ( "messages" ).child ( push_id ).updateChildren ( messageMap, new DatabaseReference.CompletionListener () {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            boolean result = myDb.insertdata ( mChatUserid, profilepic, uname, push_id, null, 1, 0, link, String.valueOf ( new SimpleDateFormat ( "yyyy-MM-dd hh:mm:ss a" ) ));
                            if (!result) {
                                myDb.updatechatlist ( mChatUserid, push_id, 1 );
                                myDb.updatelatestmsgtime ( mChatUserid,timestamptotime2 () );
                            }
                            myDb.updatemsg ( push_id, 1 );
                            refresh ();
                        }
                    } );
                    msg.setText(null);
                    refresh();
                }
            }
        });
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

    public void refresh(){
        storemsg=myDb.messageModalArrayList(mCurrentUserid,mChatUserid);
        if (storemsg.size()>0){
            adapter=new MessageAdapter(context,storemsg,mChatUserid,mCurrentUserid);
            adapter.setHasStableIds ( true );
            mMsglist.scrollToPosition(storemsg.size()-1);
            mMsglist.setAdapter(adapter);
        }

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


    public void getCurrentStatus(){
        typing = findViewById(R.id.typing);
        mRootref.child(mChatUserid).child("istyping").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    isTyping= (boolean) dataSnapshot.getValue();
                    typing.setVisibility(View.VISIBLE);
                    last_seen.setVisibility(View.GONE);
                    if (!isTyping){
                        typing.setVisibility(View.GONE);
                        last_seen.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mChatref.child("Online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    Online= (boolean) dataSnapshot.getValue();
                    if (Online) {
                        last_seen.setText("Online");
                    } else {
                        mChatref.child("seen").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()!=null){
                                    seen= (Long) dataSnapshot.getValue();
                                    String last = GetTimeAgo.getTimeAgo(seen, MessageActivity.this);
                                    last_seen.setText(last);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }else {
                    last_seen.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    String timestamptotime2(){
        try {
            Date time=new Date();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat ( "yyyy-MM-dd hh:mm:ss a" );
            String tp= simpleDateFormat.format(time);
            return tp;

        }catch (Exception e){

        }
        return "";
    }

}
