package com.example.kyahaal.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.example.kyahaal.R;
import com.example.kyahaal.Utils.ImageCompressor;
import com.example.kyahaal.ui.ChangeUserName;
import com.example.kyahaal.ui.ProfilePicActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class UserProfileActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private static final int GALLERY_REQUEST_CODE =2;
    LinearLayout user_profile_ll,user_status;
    TextView user_name,phone_no,status;
    String mobile_no,username,userstatus,link;
    File imgfile;
    CircleImageView profile_pic,chngdpbtn;
    ProgressBar dppb;
    FirebaseAuth mAuth;
    DatabaseReference mRootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(120));
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Profile");
        mAuth=FirebaseAuth.getInstance();
        mRootref=FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        user_name=findViewById(R.id.user_name);
        user_status=findViewById(R.id.linearLayout2);
        user_profile_ll=findViewById(R.id.linearLayout);
        status=findViewById(R.id.textView8);
        profile_pic=findViewById(R.id.profile_pic);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        chngdpbtn=findViewById(R.id.changedpbtn);
        phone_no=findViewById(R.id.user_phone_no);
        dppb=findViewById(R.id.dp_pb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOnline();
        new profilepic ().execute (  );
        user_profile_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserProfileActivity.this, ChangeUserName.class);
                intent.putExtra("name",user_name.getText());
                startActivity(intent);

            }
        });
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(UserProfileActivity.this,profile_pic,"profileimagetransition");
                Intent intent=new Intent(UserProfileActivity.this, ProfilePicActivity.class);
                startActivity(intent,activityOptionsCompat.toBundle());
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });

        chngdpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        user_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserProfileActivity.this,StatusActivity.class);
                intent.putExtra("status",status.getText());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final CropImage.ActivityResult result = CropImage.getActivityResult(data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    assert data != null;
                    Uri selectedImage = data.getData();
                    CropImage.activity(selectedImage).setAspectRatio(1,1).start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    Thread dpthread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dppb.setVisibility(View.VISIBLE);
                                }
                            });
                            File picturefile=save_profile_pic(MEDIA_TYPE_IMAGE);
                            if (picturefile == null) {
                                Log.d("Tag", "Error creating media file");
                                return;
                            }
                            try {
                                assert result != null;
                                Bitmap bitmap= MediaStore.Images.Media.getBitmap(UserProfileActivity.this.getContentResolver(),result.getUri());
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream);
                                final byte[] image= stream.toByteArray();
                                FileOutputStream fos = new FileOutputStream(picturefile);
                                fos.write(image);
                                fos.close();
                                File mediatorage = new File(Environment.getExternalStorageDirectory(),"kyahaal/me");
                                File imgfile=new File(mediatorage.getPath()+ File.separator + "me.jpg");
                                Uri file=Uri.fromFile(imgfile);
                                StorageReference dpref=mStorageRef.child("Users_profile_pics/"+ mobile_no + "_profilepic" + ".jpg");
                                dpref.putFile(file)
                                        .addOnCanceledListener(new OnCanceledListener() {
                                            @Override
                                            public void onCanceled() {
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        ImageCompressor imageCompressor=new ImageCompressor();
                                        boolean done=imageCompressor.ImageCompressor(mobile_no,1,1);
                                        if(done) {
                                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!downloadUri.isComplete()) ;
                                            Uri downloadUrl = downloadUri.getResult();
                                            if (downloadUrl!=null) {
                                                final String downloadUrlstring = downloadUrl.toString ();
                                                try {
                                                    final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
                                                    String Uid = null;
                                                    if (currentFirebaseUser != null) {
                                                        Uid = currentFirebaseUser.getUid ();
                                                    }
                                                    final FirebaseDatabase database = FirebaseDatabase.getInstance ();
                                                    assert Uid != null;
                                                    DatabaseReference comref = database.getReference ().child ( "User" ).child ( Uid );
                                                    Map <String, Object> map = new HashMap <> ();
                                                    map.put ( "profilepic", downloadUrlstring );

                                                    comref.updateChildren ( map ).addOnSuccessListener ( new OnSuccessListener <Void> () {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            SharedPreferences.Editor editor=getSharedPreferences ( "UserData" ,0).edit ();
                                                            editor.putString ( "dp_link",downloadUrlstring);
                                                            editor.apply ();
                                                            Activity activity = UserProfileActivity.this;
                                                            if (!activity.isFinishing () && !activity.isDestroyed ()) {
                                                                new profilepic ().execute (  );

                                                            }
                                                            Toast.makeText ( UserProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT ).show ();
                                                        }
                                                    } );
                                                } catch (Exception e) {
                                                    e.printStackTrace ();
                                                }
                                            }
                                        }

                                    }
                                });
                            } catch (FileNotFoundException e) {
                                Log.d("Tag", "File not found" + e.getMessage());
                            } catch (IOException e) {
                                Log.d("Tag", "Error accessing file" + e.getMessage());
                            }
                        }
                    });
                    dpthread.start();
                    break;
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class profilepic extends AsyncTask<String,String ,String> {

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences preferences= getSharedPreferences("UserData",0);
            link=preferences.getString ( "dp_link" ,null);
            username = preferences.getString ( "name" ,null);
            userstatus=preferences.getString ( "status" ,null);
            mobile_no=preferences.getString ( "number",null );
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            user_name.setText ( username  );
            status.setText ( userstatus );
            phone_no.setText ( mobile_no );
            Glide.with(UserProfileActivity.this).load(link).placeholder(R.drawable.ic_default_dp1).into(profile_pic);
            dppb.setVisibility ( View.GONE );
            super.onPostExecute(s);
        }
    }
    private void pickFromGallery() {
        Intent intent_1 = new Intent(Intent.ACTION_PICK);
        intent_1.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent_1.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent_1, GALLERY_REQUEST_CODE);
    }
    private File save_profile_pic(int mediatype) {
        File mediaStoragedir = new File(Environment.getExternalStorageDirectory(), "kyahaal/me");
        try {
            if (!mediaStoragedir.exists()) {
                if (!mediaStoragedir.mkdirs()) {
                    Log.d("kyahaal", "failed to create directory");
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        File mediaFile;
        if (mediatype==MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStoragedir.getPath() + File.separator + "me.jpg");
        }else {
            return null;
        }
        return mediaFile;
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
