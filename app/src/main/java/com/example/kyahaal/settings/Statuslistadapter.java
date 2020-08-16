package com.example.kyahaal.settings;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyahaal.R;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Statuslistadapter extends BaseAdapter {
    Activity activity;
    List<StatusModelclass> status;
    LayoutInflater inflater;

    public Statuslistadapter(Activity activity, List<StatusModelclass> status) {
        this.activity = activity;
        this.status = status;
    }

    public Statuslistadapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return status.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;
       ViewHolder viewHolder=null;
        if (convertView==null){
            LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater1 != null;
            view=inflater1.inflate(R.layout.mylist,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.statustext=view.findViewById(R.id.text4);
            //viewHolder.corn=view.findViewById(R.id.imageView);
            view.setTag(viewHolder);
        }else
        {
            viewHolder= (ViewHolder) view.getTag();
        }

        StatusModelclass model=status.get(position);
        viewHolder.statustext.setText(model.getStatus());

        if (model.isSelected()){
            viewHolder.corn.setImageResource(R.drawable.ic_chat);

        }else {
            viewHolder.corn.setImageResource(R.drawable.ic_bell);
        }
        return view;
    }


    public void updateRecords(List<StatusModelclass> status){
        this.status=status;
        notifyDataSetChanged();
    }

    class ViewHolder{
        TextView statustext;
        ImageView corn;}

}
