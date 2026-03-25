package com.example.yourcityyourvoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import android.widget.BaseAdapter;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private List<NotificationModel> notificationList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        }

        NotificationModel notification = notificationList.get(position);

        TextView title = convertView.findViewById(R.id.notification_title);
        TextView time = convertView.findViewById(R.id.notification_time);
        TextView description = convertView.findViewById(R.id.description);

        title.setText(notification.getTitle());
        time.setText(notification.getFormattedTime());  // Set formatted time
        description.setText(notification.getDescription());

        return convertView;
    }

}
