package com.example.kyahaal.MainChat;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kyahaal.R;
import com.example.kyahaal.database.DataBaseHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private static Context context;
    private ArrayList <ChatListModel> listchats;
    private File mediatorage;
    long last_msg_time;
    private DataBaseHelper myDb, contdb;
    OnChatClickListener chatClickListener;
    String curruid;
    AppCompatActivity activity;
    MainActivity mactivity;
    ChatListModel chatListModel;
    ArrayList<ChatListModel> selecteditems=new ArrayList <> (  );

    public ChatListAdapter(Context ctx, ArrayList <ChatListModel> listchats, OnChatClickListener chatClickListener, String curruid, AppCompatActivity activity, MainActivity mactivity) {
        context = ctx;
        this.listchats = listchats;
        myDb = new DataBaseHelper ( context );
        this.chatClickListener = chatClickListener;
        this.curruid = curruid;
        this.activity = activity;
        this.mactivity = mactivity;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext () ).inflate ( R.layout.chats_single_layout, parent, false );
        return new ChatViewHolder ( view, chatClickListener );
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {
        chatListModel = listchats.get ( position );
        if (selecteditems.size ()!=0){
            if (selecteditems.contains ( chatListModel )){
                holder.statechecked.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected_bg));
            }else {
                holder.statechecked.setVisibility(View.GONE);
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }else {
            holder.statechecked.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        if (chatListModel.getUnread_count () != 0) {
            holder.unread_count.setVisibility ( View.VISIBLE );
            if (chatListModel.getUnread_count () > 99) {
                holder.unread_count.setTextSize ( 11 );
                holder.unread_count.setText ( "99+" );
            } else {
                holder.unread_count.setText ( String.valueOf ( chatListModel.getUnread_count () ) );
            }
        } else {
            holder.unread_count.setVisibility ( View.GONE );
        }

        Glide.with ( context ).load ( chatListModel.getMedia_url () ).placeholder ( R.drawable.ic_default_dp1 ).into ( holder.profilepic );

        String username = getContactsname ( context, chatListModel.getPhnum () );
        if (username == null) {
            if (chatListModel.getPhnum ().equals ( chatListModel.getName () )) {
                holder.uname.setText ( chatListModel.getName () );
            } else {
                boolean done = myDb.updateprofilepic ( chatListModel.getUid (), chatListModel.getPhnum () );
                if (done) {
                    holder.uname.setText ( chatListModel.getPhnum () );
                }
            }
        } else {
            if (chatListModel.getName ().equals ( username )) {
                holder.uname.setText ( chatListModel.getName () );
            } else {
                boolean done = myDb.updateprofilepic ( chatListModel.getUid (), username );
                if (done) {
                    holder.uname.setText ( chatListModel.getPhnum () );
                }
            }
        }
        String lastseen;
        Cursor res = myDb.getlastmsg ( chatListModel.getLast_unseenmsg () );
        if (res.moveToLast ()) {
            lastseen = res.getString ( 0 );
            if (chatListModel.getFrom_me () == 0) {
                holder.isSeen.setVisibility ( View.GONE );
                holder.lastmsg.setPaddingRelative ( 0, 0, 0, 0 );
                holder.lastmsg.setText ( lastseen );
            } else {
                holder.lastmsg.setText ( lastseen );
            }
        }
        Cursor result = myDb.getLastmsgtime ( chatListModel.getLast_unseenmsg () );
        if (result.moveToLast ()) {
            last_msg_time = result.getLong ( 0 );
            String tp = timestamptotime ( last_msg_time );
            holder.lastmsg_tie.setText ( tp );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, List <Object> payloads) {
        ChatListModel chatListModel = listchats.get ( position );
        if (payloads.isEmpty ()) {
            super.onBindViewHolder ( holder, position, payloads );
        } else {
            Bundle o = (Bundle) payloads.get ( 0 );
            for (String key : o.keySet ()) {
                switch (key) {
                    case "name":
                        String username = getContactsname ( context, chatListModel.getPhnum () );
                        if (username == null) {
                            if (chatListModel.getPhnum ().equals ( chatListModel.getName () )) {
                                holder.uname.setText ( chatListModel.getName () );
                            } else {
                                boolean done = myDb.updateprofilepic ( chatListModel.getUid (), chatListModel.getPhnum () );
                                if (done) {
                                    holder.uname.setText ( chatListModel.getPhnum () );
                                }
                            }
                        } else {
                            if (chatListModel.getName ().equals ( username )) {
                                holder.uname.setText ( chatListModel.getName () );
                            } else {
                                boolean done = myDb.updateprofilepic ( chatListModel.getUid (), username );
                                if (done) {
                                    holder.uname.setText ( chatListModel.getPhnum () );
                                }
                            }
                        }

                    case "lastmsg":
                        String lastseen;
                        Cursor res = myDb.getlastmsg ( chatListModel.getLast_unseenmsg () );
                        if (res.moveToLast ()) {
                            lastseen = res.getString ( 0 );
                            if (chatListModel.getFrom_me () == 0) {
                                holder.isSeen.setVisibility ( View.GONE );
                                holder.lastmsg.setPaddingRelative ( 0, 0, 0, 0 );
                                holder.lastmsg.setText ( lastseen );
                            } else {
                                holder.lastmsg.setText ( lastseen );
                            }
                        }
                        Cursor result = myDb.getLastmsgtime ( chatListModel.getLast_unseenmsg () );
                        if (result.moveToLast ()) {
                            last_msg_time = result.getLong ( 0 );
                            String tp = timestamptotime ( last_msg_time );
                            holder.lastmsg_tie.setText ( tp );
                        }

                    case "uc":
                        if (chatListModel.getUnread_count () != 0) {
                            holder.unread_count.setVisibility ( View.VISIBLE );
                            if (chatListModel.getUnread_count () > 99) {
                                holder.unread_count.setTextSize ( 11 );
                                holder.unread_count.setText ( "99+" );
                            } else {
                                holder.unread_count.setText ( String.valueOf ( chatListModel.getUnread_count () ) );
                            }
                        } else {
                            holder.unread_count.setVisibility ( View.GONE );
                        }

                    case "murl":
                        Glide.with ( context ).load ( chatListModel.getMedia_url () ).placeholder ( R.drawable.ic_default_dp1 ).into ( holder.profilepic );

                    case "lt":
                        if (!(position==0)){
                            notifyItemMoved ( position,0 );
                        }
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return listchats.size ();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType ( position );
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }


    String timestamptotime(long timestamp) {
        try {
            Date time = new Date ( timestamp );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "hh:mm a" );
            return simpleDateFormat.format ( time );
        } catch (Exception e) {

        }
        return "";
    }

    public static String getContactsname(Context context, String mobile_no) {
        ContentResolver cr = context.getContentResolver ();
        Uri uri = Uri.withAppendedPath ( ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode ( mobile_no ) );
        Cursor cursor = cr.query ( uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null );
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst ()) {
            contactName = cursor.getString ( cursor.getColumnIndex ( ContactsContract.PhoneLookup.DISPLAY_NAME ) );
        }
        if (!cursor.isClosed ()) {
            cursor.close ();
        }
        return contactName;
    }

    public interface OnChatClickListener {
        void OnchatClickListener(int position);

        void OnProfilePicClick(int position, ImageView view);

        void OnChatLongClickListener(int position);
    }



    private ArrayList <ChatListModel> getListchats() {
        return listchats;
    }


    void setListchats(ArrayList <ChatListModel> newlistchats) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff ( new ChatListModelCallback ( newlistchats, getListchats () ) );
        listchats.clear ();
        this.listchats.addAll ( newlistchats );
        result.dispatchUpdatesTo ( this );
    }

    void changeselection(ArrayList<ChatListModel> selecteditems,int position){
            this.selecteditems=selecteditems;
            notifyItemChanged ( position );
    }

    void removeselection(){
        selecteditems.clear ();
        notifyDataSetChanged ();
    }

}


