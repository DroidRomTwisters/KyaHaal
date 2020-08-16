package com.example.kyahaal.settings;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyahaal.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder>{
    @NonNull
    private List<String> list;
    Bitmap bmp;
    private List<String> list1;
    private int[] images;
    public RecyclerAdapter(int[] images,List<String> list,List<String> list1 ){
        this.images=images;
        this.list=list;
        this.list1=list1;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_layout,parent,false);
        ImageViewHolder imageViewHolder=new ImageViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        final int image_id=images[position];
        holder.Settings_Icon.setImageResource(image_id);
        holder.Settings_Option.setText(list.get(position));
        holder.Settings_Option_Description.setText(list1.get(position));
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView Settings_Icon;
        TextView Settings_Option;
        TextView Settings_Option_Description;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Settings_Icon=itemView.findViewById(R.id.settings_icon);
            Settings_Option=itemView.findViewById(R.id.settings_option);
            Settings_Option_Description=itemView.findViewById(R.id.settings_option_description);
        }
    }
}
