package com.example.kyahaal.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.kyahaal.MainChat.Splash_screen_activity;
import com.example.kyahaal.R;
import com.example.kyahaal.essentials.User;
import com.example.kyahaal.settings.StatusActivity;
import com.example.kyahaal.ui.ProfilePicActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.net.ConnectivityManager.NetworkCallback;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class UserDataActivity extends AppCompatActivity {

  private static final int GALLERY_REQUEST_CODE =1;
  private CircleImageView profile_pic;
  private FloatingActionButton done_btn2;
  private EditText user_name,ustatus,uphone;
  private String mobile_no,dp_link;
  private StorageReference mStorageRef;
  FirebaseAuth mAuth;
  private ProgressBar dppb;
  FirebaseUser currentFirebaseUser;
  File mediatorage,imgfile;
  Toolbar toolbar;
  ActionBar actionBar;
  boolean isConnected=false;
  Timer timer,t;
  NetworkCallback networkCallback;
  ConnectivityManager connectivityManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_data);
    toolbar=findViewById ( R.id.action_bar );
    setSupportActionBar ( toolbar );
    actionBar=getSupportActionBar ();
    if (actionBar!=null){
      actionBar.setTitle ( "Profile" );
    }
    profile_pic=findViewById(R.id.profile_pic);
    profile_pic.setImageBitmap(null);
    user_name=findViewById(R.id.username);
    done_btn2=findViewById(R.id.donebtn2);
    ustatus=findViewById ( R.id.ustatus );
    uphone=findViewById ( R.id.umobile );
    dppb=findViewById(R.id.dppb);
    mStorageRef = FirebaseStorage.getInstance().getReference();
    currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    mediatorage = new File(Environment.getExternalStorageDirectory(),"kyahaal/me");
    imgfile = new File(mediatorage.getPath()+ File.separator + "me.jpg");
    settitle ();

  }

  @Override
  protected void onStart() {
    super.onStart ();

  }

  private void pickFromGallery() {
    Intent intent_1 = new Intent(Intent.ACTION_PICK);
    intent_1.setType("image/*");
    String[] mimeTypes = {"image/jpeg", "image/png"};
    intent_1.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
    startActivityForResult(intent_1, GALLERY_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    CropImage.ActivityResult result = CropImage.getActivityResult(data);
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      switch (requestCode) {
        case GALLERY_REQUEST_CODE:
          assert data != null;
          Uri selectedImage = data.getData();
          CropImage.activity(selectedImage).setAspectRatio(1,1).start(this);
          break;

        case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
          File picturefile=save_profile_pic(MEDIA_TYPE_IMAGE);
          if (picturefile == null) {
            Log.d("Tag", "Error creating media file");
            return;
          }
          try {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),result.getUri());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream);
            byte[] image= stream.toByteArray();
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
                Task<Uri> downloadUri=taskSnapshot.getStorage().getDownloadUrl();
                while (!downloadUri.isComplete());
                Uri downloadUrl=downloadUri.getResult();
                String downloadUrlstring=downloadUrl.toString();
                try {
                  final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                  String Uid = null;
                  if (currentFirebaseUser != null) {
                    Uid = currentFirebaseUser.getUid();
                  }
                  final FirebaseDatabase database = FirebaseDatabase.getInstance();
                  DatabaseReference comref = database.getReference().child("User").child(Uid);
                  Map<String, Object> map = new HashMap<>();
                  map.put("profilepic",downloadUrlstring);
                  comref.updateChildren(map);
                } catch (Exception e) {
                  e.printStackTrace();
                }

              }
            });
          } catch (FileNotFoundException e) {
            Log.d("Tag", "File not found" + e.getMessage());
          } catch (IOException e) {
            Log.d("Tag", "Error accessing file" + e.getMessage());
          }
          break;
        case 10:
          if (data!=null){
            String status1=data.getStringExtra("status");
            ustatus.setText(status1);
          }
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    new profilepic().execute (  );
    connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService( Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null) {
      if (networkCallback!=null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          connectivityManager.registerDefaultNetworkCallback ( networkCallback );
        } else {
          NetworkRequest request = new NetworkRequest.Builder ().addCapability ( NetworkCapabilities.NET_CAPABILITY_INTERNET ).build ();
          connectivityManager.registerNetworkCallback ( request, networkCallback );
        }
      }
    }
    settitle ();
    String Uid= null;
    if (currentFirebaseUser != null) {
      Uid = currentFirebaseUser.getUid();
    }
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    if (Uid != null) {
      DatabaseReference ref = database.getReference().child("User").child(Uid);
      ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          User user = dataSnapshot.getValue(User.class);
          if (user!=null && user.getMobile_no ()!=null) {
            mobile_no = user.getMobile_no ();
            uphone.setText ( mobile_no );
            if (user.getName ()!=null){
              user_name.setText ( user.getName () );
            }
            if (user.getStatus ()!=null){
              ustatus.setText ( user.getStatus () );
            }
          }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });

      ustatus.setOnClickListener ( new View.OnClickListener () {
        @Override
        public void onClick(View v) {
          Intent intent=new Intent( UserDataActivity.this, StatusActivity.class);
          intent.putExtra("status",ustatus.getText ().toString ());
          startActivityForResult(intent,10);
          overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
        }
      } );

      profile_pic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) { String[] options = {"View Image", "Remove image", "Choose Image from gallery"};

          AlertDialog.Builder builder = new AlertDialog.Builder(UserDataActivity.this);
          builder.setTitle("Choose an option");
          builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if (which == 0) {
                Intent intent = new Intent(UserDataActivity.this, ProfilePicActivity.class);
                startActivity(intent);
              } else if (which == 1) {
                profile_pic.setImageResource(R.drawable.ic_default_dp1);
                File mediatorage = new File(Environment.getExternalStorageDirectory(), "kyahaal/me");
                File imgfile = new File(mediatorage.getPath() + File.separator + "me.jpg");
                if (imgfile.exists()) {
                  imgfile.delete();
                }
                StorageReference fref = FirebaseStorage.getInstance().getReference("Users_profile_pics");
                fref.child(mobile_no + "_profilepic" + ".jpg").delete();

                final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String Uid = null;
                if (currentFirebaseUser != null) {
                  Uid = currentFirebaseUser.getUid();
                }
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference comref = database.getReference().child("User").child(Uid);
                Map<String, Object> map = new HashMap<>();
                map.put("profilepic", null);
                comref.updateChildren(map);
              } else if (which == 2) {
                if (ContextCompat.checkSelfPermission (UserDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                  if (ActivityCompat.shouldShowRequestPermissionRationale (UserDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )){
                    AlertDialog.Builder alert=new AlertDialog.Builder ( UserDataActivity.this );
                    alert.setCancelable ( true );
                    alert.setTitle ( "Storage Permission Required" );
                    alert.setMessage ( "KyaHaal needs permission to access your storage and files to store some user related information." );
                    alert.setPositiveButton ( "Continue", new DialogInterface.OnClickListener () {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions ( UserDataActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE },1 );
                      }
                    } );

                    AlertDialog alertDialog=alert.create ();
                    alertDialog.show ();
                  }else {
                    ActivityCompat.requestPermissions ( UserDataActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE },1 );
                  }
                }else {
                  pickFromGallery();
                }

              }
            }
          });
          builder.show();

        }
      });
      done_btn2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String username = user_name.getText().toString();
          if (username.length() > 0) {
            try {
              String Uid = null;
              if (currentFirebaseUser != null) {
                Uid = currentFirebaseUser.getUid();
              }
              final FirebaseDatabase database = FirebaseDatabase.getInstance();
              if (Uid!=null) {
                DatabaseReference comref = database.getReference ().child ( "User" ).child ( Uid );
                Map <String, Object> map = new HashMap <> ();
                map.put ( "name", username );
                comref.updateChildren ( map );
              }
            } catch (Exception e) {
              Toast.makeText(UserDataActivity.this, "Something went wrong!!! we are looking at it", Toast.LENGTH_LONG).show();
              e.printStackTrace();
            } finally {
              Intent intent = new Intent(UserDataActivity.this, finsihingActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
              overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
          } else {
            user_name.setError("please enter a valid username");
            user_name.requestFocus();
          }
        }
      });
    }else {
      Intent intent = new Intent(UserDataActivity.this, LoginActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish ();
      overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
    switch (requestCode){
      case 1:
        if (grantResults .length>0  && grantResults[0]==PackageManager.PERMISSION_GRANTED ) {
          pickFromGallery ();
        }
      case 2:
        
    }
  }

  private class profilepic extends AsyncTask<String,String,String> {
    @Override
    protected void onPreExecute() {
      if (t!=null){
        t.cancel ();
        t.purge ();
      }
      dppb.setVisibility(View.VISIBLE);
      super.onPreExecute ();
    }

    @Override
    protected String doInBackground(final String... strings) {
      final FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference comref = database.getReference().child("User").child(currentFirebaseUser.getUid ());
      comref.addValueEventListener ( new ValueEventListener () {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          User user= dataSnapshot.getValue (User.class) ;
          if (user!=null && user.getProfilepic ()!=null) {
            dp_link = user.getProfilepic ();
          }else {
            dp_link = null;
          }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
      return null;
    }

    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute ( s );
      if (dp_link!=null){
        Glide.with ( UserDataActivity.this ).load ( dp_link ).placeholder ( R.drawable.ic_default_dp1 ).into ( profile_pic );
        dppb.setVisibility ( View.GONE );
      }else {
        t=new Timer (  );
        t.schedule ( new TimerTask () {
          @Override
          public void run() {
            runOnUiThread ( new Runnable () {
              @Override
              public void run() {
                new profilepic ().execute ();
              }
            } );

          }
        } ,1500);
        Glide.with ( UserDataActivity.this ).load ( R.drawable.ic_default_dp1 ).into ( profile_pic );
      }

    }
  }


  private File save_profile_pic(int mediatype) {

    try {
      if (!mediatorage.exists()) {
        if (!mediatorage.mkdirs()) {
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
      mediaFile = new File(mediatorage.getPath() + File.separator + "me.jpg");
    }else {
      return null;
    }
    return mediaFile;
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

  public void settitle() {
    if (timer!=null){
      timer.cancel();
      timer.purge();
    }
    networkCallback = new NetworkCallback() {
      @Override
      public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        isConnected = true;
      }

      @Override
      public void onLost(@NonNull Network network) {
        super.onLost(network);
        isConnected = false;

      }
    };

    if (isConnected){
      actionBar.setTitle("Profile");
    }else {
      actionBar.setTitle("Connecting...");
    }
    timer = new Timer ();
    timer.schedule(new TimerTask () {
      @Override
      public void run() {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            settitle();
          }
        });

      }
    },900);
  }

  

}
