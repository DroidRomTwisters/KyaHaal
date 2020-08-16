package com.example.kyahaal.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class ImageCompressor {
    File imgfile;
    File mediatorage;
    File thumnailfile;
    StorageReference mStorageRef;
    File mediatorage1;
    Boolean done=false;
    Boolean isMade=true;
    public boolean ImageCompressor(final String mobile, final int picture_type, final int delete) {
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference dpref = mStorageRef.child("Users_profile_pics/" + mobile + "_profilepic" + ".jpg");
            if (picture_type == 2) {
                mediatorage = new File(Environment.getExternalStorageDirectory(), "kyahaal/.profilepictures");
                if (!mediatorage.exists()) {
                    mediatorage.mkdirs();
                }
                imgfile = new File(mediatorage.getPath() + File.separator + mobile + "_profilepic" + ".jpg");
                if (imgfile.exists()) {
                    if (delete == 1) {
                        imgfile.delete();
                    }
                }
            }else if (picture_type==1){
                mediatorage = new File(Environment.getExternalStorageDirectory(),"kyahaal/me");
                if (!mediatorage.exists()){
                    mediatorage.mkdirs();
                }
                imgfile = new File(mediatorage.getPath()+ File.separator + "me.jpg");
            }
            if (!imgfile.exists()) {
                dpref.getFile(imgfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        boolean ismade=makethumbnail(mobile, picture_type,delete);
                        done=ismade;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                return done;
            } else {
                boolean ismade = makethumbnail(mobile, picture_type,delete);
                done = ismade;
                return done;
            }
    }

    public boolean makethumbnail(final String mobile, final int picture_type, final int delete){
        if (imgfile.length() / 1024 > 0) {
            Thread thumthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imgfile.getAbsolutePath(), bitmapOptions);

                    int desiredWidth = 256;
                    int desiredHeight = 256;
                    float widthScale = (float) bitmapOptions.outWidth / desiredWidth;
                    float heightScale = (float) bitmapOptions.outHeight / desiredHeight;
                    float scale = Math.min(widthScale, heightScale);
                    int samplesize = 1;
                    while (samplesize < scale) {
                        samplesize *= 2;
                    }

                    bitmapOptions.inSampleSize = samplesize;
                    bitmapOptions.inJustDecodeBounds = false;

                    if (picture_type==2) {
                        mediatorage1 = new File(Environment.getExternalStorageDirectory(), "kyahaal/.profilepictures_thumb");
                        if (!mediatorage1.exists()) {
                            mediatorage1.mkdirs();
                        }
                        thumnailfile = new File(mediatorage1.getPath() + File.separator + mobile + "_thumb_profilepic" + ".jpg");
                    }else {
                        mediatorage1 = new File(Environment.getExternalStorageDirectory(), "kyahaal/me");
                        if (!mediatorage1.exists()) {
                            mediatorage1.mkdirs();
                        }
                        thumnailfile = new File(mediatorage1.getPath() + File.separator + "me_thumbnail.jpg");
                    }
                    Bitmap thumbnail = BitmapFactory.decodeFile(imgfile.getAbsolutePath(), bitmapOptions);
                    if (thumbnail!=null) {
                            if (!thumnailfile.exists()) {
                                try {
                                    FileOutputStream fos = new FileOutputStream(thumnailfile);
                                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                } catch (IOException e) {
                                    isMade=false;
                                    e.printStackTrace();
                                }
                            }else if (thumnailfile.exists() && delete!=0){
                                boolean done=thumnailfile.delete();
                                if (done){
                                    try {
                                        FileOutputStream fos = new FileOutputStream(thumnailfile);
                                        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                        fos.flush();
                                        fos.close();
                                    } catch (IOException e) {
                                        isMade=false;
                                        e.printStackTrace();
                                    }
                                }
                            }
                    }
                }
            });
            thumthread.start();
        }else {
            boolean result=imgfile.delete();
            if (result){
                ImageCompressor(mobile,picture_type,delete);
            }
        }
        return isMade;
    }

}

