package com.example.kyahaal.contacts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kyahaal.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContactsRecyclerAdapter2 extends RecyclerView.Adapter<ContactsViewHolder> {
    String user;
    private File mediatorage;
    private Context ctx;
    ArrayList<ContactModelDiffUtils> storecontacts;
    private OnContactClickListener contactClickListener;
    ContactModelDiffUtils contactModal;


    public ContactsRecyclerAdapter2() {
    }

    public ContactsRecyclerAdapter2(Context ctx, ArrayList<ContactModelDiffUtils> storecontacts,OnContactClickListener contactClickListener) {
        this.ctx = ctx;
        this.storecontacts = storecontacts;
        this.contactClickListener=contactClickListener;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_single_layout,parent,false);
        return new ContactsViewHolder(view,contactClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        contactModal = storecontacts.get ( position );
        if (contactModal.getMEDIA_URL () != null) {
            Glide.with ( ctx ).load ( contactModal.getMEDIA_URL () ).placeholder ( R.drawable.ic_default_dp1 ).into ( holder.profilepic );
        }
        holder.uname.setText ( contactModal.getNAME () );
        if (contactModal.getSTATUS () == null) {
            holder.ustatus.setVisibility ( View.GONE );
        } else {
            holder.ustatus.setVisibility ( View.VISIBLE );
            holder.ustatus.setText ( contactModal.getSTATUS () );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, List <Object> payloads) {
        if (payloads.isEmpty ()){
            super.onBindViewHolder ( holder, position, payloads );
        }else {
            final ContactModelDiffUtils contactModal = storecontacts.get ( position );
            Bundle o=(Bundle)payloads.get (0);
            for (String key:o.keySet ()){
                switch (key) {
                    case "MEDIA_URL":
                        if (contactModal.getMEDIA_URL () != null) {
                            Glide.with ( ctx ).load ( contactModal.getMEDIA_URL () ).placeholder ( R.drawable.ic_default_dp1 ).into ( holder.profilepic );
                        }
                        break;
                    case "STATUS":
                        if (contactModal.getSTATUS () == null) {
                            holder.ustatus.setVisibility ( View.GONE );
                        } else {
                            holder.ustatus.setVisibility ( View.VISIBLE );
                            holder.ustatus.setText ( contactModal.getSTATUS () );
                        }
                        break;
                    case "NAME":
                        holder.uname.setText ( contactModal.getNAME () );
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return storecontacts.size();
    }

    @Override
    public long getItemId(int position) {
        return storecontacts.get(position).getUID().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public interface OnContactClickListener{
        void OnContactClick(int position);
        void OnProfilePicClick(int position,ImageView view);
    }

    public ArrayList <ContactModelDiffUtils> getStorecontacts() {
        return storecontacts;
    }

    public void setStorecontacts(ArrayList <ContactModelDiffUtils> newstorecontacts) {
        DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff ( new ContactDiffUtilCallback ( newstorecontacts,getStorecontacts ()) );
        storecontacts.clear ();
        this.storecontacts.addAll ( newstorecontacts );
        diffResult.dispatchUpdatesTo ( this );

    }
}
