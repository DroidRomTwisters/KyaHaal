package com.example.kyahaal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.kyahaal.contacts.ContactModelDiffUtils;
import com.example.kyahaal.contacts.ContactModelDiffUtils;

import java.util.ArrayList;

public class ContactsDBhelper extends SQLiteOpenHelper {
  public static String DATABASE_NAME = "contacts.db";
  public static String TABLE_NAME1 = "contacts";
  public static String query1;

  public ContactsDBhelper(@Nullable Context context) {
    super(context, DATABASE_NAME, null, 2);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {

    query1 = "create table contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT,UID TEXT,NUMBER TEXT,NAME TEXT,MEDIA_URL TEXT,PROFILE_LOC TEXT,THUMB_LOC TEXT,STATUS TEXT,unique(UID,NUMBER))";
    db.execSQL(query1);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("drop table if exists contacts");
    onCreate(db);
  }

  public boolean insertcontact(String uid, String number, String name, String media_url, String profile_loc, String thumb_loc, String status) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("UID", uid);
    contentValues.put("NUMBER", number);
    contentValues.put("NAME", name);
    contentValues.put("MEDIA_URL", media_url);
    contentValues.put("PROFILE_LOC", profile_loc);
    contentValues.put("THUMB_LOC", thumb_loc);
    contentValues.put("STATUS", status);
    long result = db.insert("contacts", null, contentValues);
    return result != -1;
  }

  public boolean deletecontact(String mobile) {
    SQLiteDatabase db = this.getWritableDatabase();
    long res = db.delete("contacts", "NUMBER=?", new String[]{mobile});
    return res != -1;
  }

  public ArrayList<ContactModelDiffUtils> contactArrayList() {
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<ContactModelDiffUtils> storecontacts = new ArrayList<>();
    Cursor cursor = db.rawQuery("select * from contacts order by NAME asc", null);
    if (cursor.moveToFirst()) {
      do {
        String conuid = cursor.getString(1);
        String connum = cursor.getString(2);
        String conname = cursor.getString(3);
        String conmediaurl = cursor.getString(4);
        String conprofloc = cursor.getString(5);
        String conthumbloc = cursor.getString(6);
        String constatus = cursor.getString(7);
        if (!storecontacts.contains(new ContactModelDiffUtils(conuid, connum, conname, conmediaurl, conprofloc, conthumbloc, constatus))) {
          storecontacts.add(new ContactModelDiffUtils(conuid, connum, conname, conmediaurl, conprofloc, conthumbloc, constatus));
        }
      } while (cursor.moveToNext());
    }
    cursor.close();
    return storecontacts;
  }

  public Cursor getproflink(String mobile) {
    SQLiteDatabase db = this.getWritableDatabase();
    String cmobile = '\'' + mobile + '\'';
    return db.rawQuery("select media_url from contacts WHERE number LIKE " + cmobile, null);
  }

  public boolean deletetable() {
    SQLiteDatabase db = this.getWritableDatabase();
    long res = db.delete("contacts", null, null);
    return res != -1;
  }

  public boolean updatename(String uid, @Nullable String name) {
    SQLiteDatabase db = this.getWritableDatabase();
    String ouid = '\'' + uid + '\'';
    String oname = '\"' + name + '\"';
    db.execSQL("UPDATE contacts SET name=" + oname + " WHERE uid LIKE " + ouid);
    return true;
  }

  public boolean updatproflink(String uid, @Nullable String prof) {
    SQLiteDatabase db = this.getWritableDatabase();
    String ouid = '\'' + uid + '\'';
    String oprof = '\"' + prof + '\"';
    db.execSQL("UPDATE contacts SET MEDIA_URL=" + oprof + " WHERE uid LIKE " + ouid);
    return true;
  }

  public ArrayList<ContactModelDiffUtils> getcontactdetails(String uid){
    String ouid = '\'' + uid + '\'';
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<ContactModelDiffUtils> storecontacts = new ArrayList<>();
    Cursor cursor = db.rawQuery("select * from contacts WHERE uid LIKE "+ouid, null);
    if (cursor.moveToFirst()) {
      do {
        String conuid = cursor.getString(1);
        String connum = cursor.getString(2);
        String conname = cursor.getString(3);
        String conmediaurl = cursor.getString(4);
        String conprofloc = cursor.getString(5);
        String conthumbloc = cursor.getString(6);
        String constatus = cursor.getString(7);
        if (!storecontacts.contains(new ContactModelDiffUtils(conuid, connum, conname, conmediaurl, conprofloc, conthumbloc, constatus))) {
          storecontacts.add(new ContactModelDiffUtils(conuid, connum, conname, conmediaurl, conprofloc, conthumbloc, constatus));
        }
      } while (cursor.moveToNext());
    }
    cursor.close();
    return storecontacts;
  }
}

