package com.example.yourcityyourvoice;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ComplaintAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, Object>> data;
    private LayoutInflater inflater;

    public ComplaintAdapter(Context context, ArrayList<HashMap<String, Object>> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_list_item, null);
            holder = new ViewHolder();
            holder.iconImageView = convertView.findViewById(R.id.iconImageView);
            holder.titleTextView = convertView.findViewById(R.id.titleTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, Object> item = data.get(position);
        holder.titleTextView.setText(item.get("title").toString());
        holder.iconImageView.setImageResource((int) item.get("icon"));

        return convertView;
    }

    static class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
    }
}

