package com.example.kyahaal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.kyahaal.MainChat.ChatListModel;
import com.example.kyahaal.message.MessageModal;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
  public static String DATABASE_NAME="chat.db";
  public static String TABLE_NAME1,TABLE_NAME2;

  public DataBaseHelper(@Nullable Context context) {
    super(context,DATABASE_NAME,null,2);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    TABLE_NAME1="create table chatlist (UID TEXT PRIMARY KEY, PH_NUMBER TEXT ,NAME TEXT,last_unseenmsg TEXT,last_seenmsg TEXT,from_me INTEGER,unread_count INTEGER,media_url TEXT,lastmsg_time TEXT,unique(PH_NUMBER,NAME,last_unseenmsg,last_seenmsg,media_url))";
    TABLE_NAME2="create table messages (_id INTEGER PRIMARY KEY AUTOINCREMENT,data TEXT,from_uid TEXT,to_uid TEXT,from_me INTEGER,read_timestamp LONG,isSent INTEGER,push_id TEXT,unique(push_id))";
    db.execSQL(TABLE_NAME1);
    db.execSQL(TABLE_NAME2);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("drop table if exists chatlist");
    onCreate(db);
  }


  public ArrayList<MessageModal> messageModalArrayList(String curruid, String chatuid){
    SQLiteDatabase db= this.getWritableDatabase();
    ArrayList<MessageModal> storemsg=new ArrayList<>();
    Cursor cursor=db.rawQuery("select * from messages order by read_timestamp asc",null);
    if (cursor.moveToFirst()){
      do {
        String data=cursor.getString(1);
        String from_uid=cursor.getString(2);
        String to_uid=cursor.getString(3);
        int from_me=cursor.getInt(4);
        long read_timestamp=cursor.getLong(5);
        int isSent=cursor.getInt(6);
        String push_id=cursor.getString(7);
        if (from_uid.equals(curruid) && to_uid.equals(chatuid) || from_uid.equals(chatuid) && to_uid.equals(curruid))
        {  if (!storemsg.contains(new MessageModal(data,from_uid,to_uid,from_me,read_timestamp,isSent,push_id))) {
          storemsg.add(new MessageModal(data, from_uid, to_uid, from_me, read_timestamp, isSent, push_id));
        }
        }
      }while (cursor.moveToNext());
    }
    cursor.close();
    return storemsg;
  }

  public boolean insertdata(String uid, String phnum, String name, String last_unseenmsg , String last_seenmsg, int from_me,int unread_count,String media_url,String lastmsg_time){
    SQLiteDatabase db= this.getWritableDatabase();
    ContentValues contentValues=new ContentValues();
    contentValues.put("UID",uid);
    contentValues.put("PH_NUMBER",phnum);
    contentValues.put("NAME",name);
    contentValues.put("last_unseenmsg",last_unseenmsg);
    contentValues.put("last_seenmsg",last_seenmsg);
    contentValues.put("from_me",from_me);
    contentValues.put("unread_count",unread_count);
    contentValues.put ( "media_url",media_url );
    contentValues.put ( "lastmsg_time",lastmsg_time );
    long result = db.insert("chatlist",null,contentValues);
    return result != -1;
  }


  public boolean insertMessage(String data, String from_uid,String to_uid, int from_me, long time,int isSent,String push_id){
    SQLiteDatabase db= this.getWritableDatabase();
    ContentValues contentValues=new ContentValues();
    contentValues.put("data",data);
    contentValues.put("from_uid",from_uid);
    contentValues.put("to_uid",to_uid);
    contentValues.put("from_me",from_me);
    contentValues.put("read_timestamp",time);
    contentValues.put("isSent",isSent);
    contentValues.put("push_id",push_id);
    long result = db.insert("messages",null,contentValues);
    return result != -1;
  }
  public Cursor getData(){
    SQLiteDatabase db= this.getWritableDatabase();
    try (Cursor res = db.rawQuery("select * from chatlist", null)) {
      return res;
    }
  }

  public Cursor getMessages(){
    SQLiteDatabase db= this.getWritableDatabase();
    try (Cursor res = db.rawQuery("select * from messages order by read_timestamp asc", null)) {
      return res;
    }
  }

  public Cursor getlastmsg(String push_id){
    SQLiteDatabase db= this.getWritableDatabase();
    String opush_id='\''+push_id+'\'';
    return db.rawQuery("select data from messages WHERE push_id LIKE "+opush_id,null);

  }

  public Cursor getLastmsgtime(String push_id){
    SQLiteDatabase db= this.getWritableDatabase();
    String opush_id='\''+push_id+'\'';
    return db.rawQuery("select read_timestamp from messages WHERE push_id LIKE "+opush_id,null);
  }

  public boolean updatemsg(String push_id,int isSent){
    SQLiteDatabase db= this.getWritableDatabase();
    String opush_id='\''+push_id+'\'';
    db.execSQL("UPDATE messages SET isSent="+isSent+" WHERE push_id LIKE "+opush_id);
    return false;
  }

  public boolean updatechatlist(String uid,String push_id,int from_me){
    SQLiteDatabase db= this.getWritableDatabase();
    String ouid='\''+uid+'\'';
    String olm='\''+push_id+'\'';
    db.execSQL("UPDATE chatlist SET last_unseenmsg="+olm+" , from_me="+from_me+" WHERE UID LIKE "+ouid);
    return true;
  }

  public boolean updateprofilepic(String uid, @Nullable String name){
    SQLiteDatabase db= this.getWritableDatabase();
    String ouid='\''+uid+'\'';
    String oname='\"'+name+'\"';
    db.execSQL("UPDATE chatlist SET NAME="+oname+ " WHERE UID LIKE " + ouid);

    return true;
  }

  public void incrementuc(String uid,Context ctx){
    SQLiteDatabase db= this.getWritableDatabase();
    String ouid='\''+uid+'\'';
    db.execSQL("UPDATE chatlist SET unread_count=unread_count+1 WHERE uid LIKE " + ouid);
  }

  public void unreadcounttozero(String uid,Context ctx){
    SQLiteDatabase db= this.getWritableDatabase();
    String ouid='\''+uid+'\'';
    db.execSQL("UPDATE chatlist SET unread_count=0 WHERE uid LIKE " + ouid);
  }
  public ArrayList<ChatListModel> chatListModels(){
    SQLiteDatabase db= this.getWritableDatabase();
    ArrayList<ChatListModel> storechat=new ArrayList<>();
    Cursor cursor=db.rawQuery("select * from chatlist order by lastmsg_time desc",null);
    if (cursor.moveToFirst()){
      do {
        String cuid=cursor.getString(0);
        String cnumber=cursor.getString(1);
        String cname=cursor.getString(2);
        String last_unseenmsg=cursor.getString(3);
        String last_seenmsg=cursor.getString(4);
        int from_me=cursor.getInt(5);
        int unread_count=cursor.getInt ( 6 );
        String media_url=cursor.getString ( 7 );
        String lastmsg_time=cursor.getString ( 8 );
        storechat.add(new ChatListModel(cuid,cnumber,cname,last_unseenmsg,last_seenmsg,from_me,unread_count,media_url,lastmsg_time));
      }while (cursor.moveToNext());
    }
    cursor.close();
    return storechat;
  }

  public boolean updatemediaurl(String uid, @Nullable String media_url){
    SQLiteDatabase db= this.getWritableDatabase();
    String ouid='\''+uid+'\'';
    String omediaurl='\"'+media_url+'\"';
    db.execSQL("UPDATE chatlist SET media_url="+omediaurl+ " WHERE UID LIKE " + ouid);
    return true;
  }

  public boolean updatelatestmsgtime(String uid, @NotNull String time){
    SQLiteDatabase db= this.getWritableDatabase();
    String ouid='\''+uid+'\'';
    String otime='\''+time+'\'';
    db.execSQL("UPDATE chatlist SET lastmsg_time="+otime+ " WHERE UID LIKE " + ouid);
    return true;
  }
  public Cursor getunread_count(String uid){
    String ouid='\''+uid+'\'';
    SQLiteDatabase db= this.getWritableDatabase();
    Cursor res=db.rawQuery("select unread_count from chatlist WHERE UID LIKE "+ouid,null);
    return res;
  }

  public boolean deletechatrecord(String uid,String curruid, String chatuid){
    String ouid='\''+uid+'\'';
    String ocurruid='\''+curruid+'\'';
    String ochatuid='\''+chatuid+'\'';
    SQLiteDatabase db= this.getWritableDatabase();
    long done=db.delete("chatlist","UID=?",new String[]{uid});
    long done1 = 0;
    long done2=0;
    if (done!=-1){
      done1=db.delete("messages","from_uid=? AND to_uid=?",new String[]{chatuid,curruid});
    }
    if (done1!=-1){
      done2=db.delete("messages","from_uid=? AND to_uid=?",new String[]{curruid,chatuid});
    }
    if (done2!=-1){
      return true;
    }else {
      return false;
    }
  }
}
