package com.example.kyahaal;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class kyahaal extends MultiDexApplication {
    private DatabaseReference mUsersDatabase,mydb;
    FirebaseAuth mAuth;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext ( base );
        MultiDex.install ( this );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth=FirebaseAuth.getInstance ();
        if (mAuth.getCurrentUser ()!=null) {
            mydb = FirebaseDatabase.getInstance ().getReference ( "User" ).child ( mAuth.getCurrentUser ().getUid () );
            mydb.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mydb.child ( "Online" ).onDisconnect ().setValue ( false );
                    mydb.child ( "seen" ).onDisconnect ().setValue ( TIMESTAMP );
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            } );

        }

    }

    /*public void askstoragepermissiopn(){
        if (ContextCompat.checkSelfPermission (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )){
                AlertDialog.Builder alert=new AlertDialog.Builder ( MainActivity.this );
                alert.setCancelable ( true );
                alert.setTitle ( "Storage Permission Required" );
                alert.setMessage ( "KyaHaal needs permission to access your storage and files to store some user related information." );
                alert.setPositiveButton ( "Continue", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions ( MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE },2 );
                    }
                } );

                AlertDialog alertDialog=alert.create ();
                alertDialog.show ();
            }else {
                ActivityCompat.requestPermissions ( MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE },2 );
            }
        }
    }*/

}
