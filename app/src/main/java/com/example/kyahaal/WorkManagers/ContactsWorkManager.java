package com.example.kyahaal.WorkManagers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.kyahaal.contacts.ContactsActivity2;
import com.example.kyahaal.database.ContactsDBhelper;
import com.example.kyahaal.essentials.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactsWorkManager extends Worker{
  FirebaseAuth mAuth;
  ContactsDBhelper myDb;
  String[] aNumberFromContacts;
  String mobile_no;
  String mobile;
  private DatabaseReference mUsersDatabase;
  Context context;
  String[] uid;

  public ContactsWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
    super ( context, workerParams );
    this.context=context;
  }

  @NonNull
  @Override
  public Result doWork() {
    mAuth=FirebaseAuth.getInstance ();
    if (mAuth.getCurrentUser()!=null) {
      myDb = new ContactsDBhelper(context);
      Cursor contacts;
      contacts = context.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
      assert contacts != null;
      aNumberFromContacts = new String[contacts.getCount()];
      int i = 0;
      while (contacts.moveToNext()) {
        String number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        String s=number.replaceAll("[()\\s-]+", "");
        if (s.length()==11){
          String newstring=s.substring(1,10);
          aNumberFromContacts[i]=newstring;
        }else {
          aNumberFromContacts[i]=s;
        }
        i++;
      }
      contacts.close();
      uid = new String[]{null};
      final SharedPreferences preferences= context.getSharedPreferences("UserData",0);
      mobile_no=preferences.getString ( "number" ,null);
      mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User");
      mUsersDatabase.addChildEventListener(new ChildEventListener () {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
          if (dataSnapshot.exists()) {
            User user = dataSnapshot.getValue(User.class);
            if (user != null) {
              mobile = user.getMobile_no();
              if (dataSnapshot.getKey() != null) {
                uid[0] = dataSnapshot.getKey();
              }
              for (String aNumberFromContact : aNumberFromContacts) {
                if (mobile != null && mobile.equals ( "+91" + aNumberFromContact ) && !mobile.equals ( mobile_no ) || mobile != null && mobile.equals ( aNumberFromContact ) && !mobile.equals ( mobile_no )) {
                  String cname = getContactsname ( context, aNumberFromContact );
                  boolean done = myDb.insertcontact ( uid[ 0 ], mobile, cname, user.getProfilepic (), null, null, user.getStatus () );
                  if (!done) {
                    myDb.updatproflink ( uid[ 0 ], user.getProfilepic () );
                    myDb.updatename ( uid[ 0 ], cname );
                  }else {
                    ContactsActivity2.refresh ();
                  }
                }
              }
            }
          }

        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
          User user=dataSnapshot.getValue(User.class);
          if (user!=null) {
            if (user.getMobile_no() != null) {
              myDb.deletecontact(user.getMobile_no());
            }
          }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
      });
    }
    myDb.close ();
    return Result.success ();
  }
  public static String getContactsname(Context context, String mobile_no){
    ContentResolver cr=context.getContentResolver();
    Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(mobile_no));
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
}