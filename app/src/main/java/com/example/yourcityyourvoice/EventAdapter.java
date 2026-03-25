package com.example.yourcityyourvoice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends BaseAdapter {

    private Context context;
    private List<EventModel> eventList;
    private LayoutInflater inflater;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private BookmarkUpdateListener bookmarkUpdateListener; // Interface for updates

    public EventAdapter(Context context, List<EventModel> eventList, BookmarkUpdateListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.inflater = LayoutInflater.from(context);
        this.sharedPreferences = context.getSharedPreferences("bookmarked_events", Context.MODE_PRIVATE);
        this.bookmarkUpdateListener = listener;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.event_items, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EventModel event = eventList.get(position);

        holder.eventTitle.setText(event.getTitle());
        holder.eventTime.setText(event.getTime());
        holder.eventTimestamp.setText(formatTimestamp(event.getTimestamp()));
        holder.eventDescription.setText(event.getDescription());
        holder.organizerName.setText(event.getOrganizer());
        holder.eventLocation.setText(event.getLocation());

        // Load image using Glide
        Glide.with(context)
                .load(event.getImageUrl())
                .placeholder(R.drawable.img_6)
                .into(holder.eventImage);

        // Update bookmark button color initially
        updateBookmarkIcon(holder.btnBookmark, isBookmarked(event));

        // Share Button Click Listener
        holder.btnShare.setOnClickListener(v -> shareEvent(event));

        // Bookmark Button Click Listener
        holder.btnBookmark.setOnClickListener(v -> {
            boolean isBookmarked = toggleBookmark(event);
            updateBookmarkIcon(holder.btnBookmark, isBookmarked);

            // Notify the activity to update the list if needed
            if (bookmarkUpdateListener != null) {
                bookmarkUpdateListener.onBookmarkUpdated();
            }
        });

        return convertView;
    }

    // Method to format timestamp
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Method to Share Event
    private void shareEvent(EventModel event) {
        String shareText = "Check out this event: " + event.getTitle() +
                "\nOrganizer: " + event.getOrganizer() +
                "\nLocation: " + event.getLocation() +
                "\nDescription: " + event.getDescription();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // Toggle Bookmark and return new state
    private boolean toggleBookmark(EventModel event) {
        List<EventModel> bookmarkedEvents = getBookmarkedEvents();
        boolean isBookmarked = isBookmarked(event);

        if (isBookmarked) {
            bookmarkedEvents.removeIf(e -> e.getTitle().equals(event.getTitle()));
            Toast.makeText(context, "Removed from Bookmarks", Toast.LENGTH_SHORT).show();
        } else {
            bookmarkedEvents.add(event);
            Toast.makeText(context, "Added to Bookmarks", Toast.LENGTH_SHORT).show();
        }

        saveBookmarkedEvents(bookmarkedEvents);
        notifyDataSetChanged();
        return !isBookmarked;
    }

    // Check if Event is Bookmarked
    private boolean isBookmarked(EventModel event) {
        List<EventModel> bookmarkedEvents = getBookmarkedEvents();
        for (EventModel e : bookmarkedEvents) {
            if (e.getTitle().equals(event.getTitle())) {
                return true;
            }
        }
        return false;
    }

    // Get Bookmarked Events
    private List<EventModel> getBookmarkedEvents() {
        String json = sharedPreferences.getString("bookmarked_list", "[]");
        List<EventModel> bookmarkedEvents = gson.fromJson(json, new TypeToken<List<EventModel>>() {}.getType());
        return (bookmarkedEvents != null) ? bookmarkedEvents : new ArrayList<>();
    }

    // Save Bookmarked Events
    private void saveBookmarkedEvents(List<EventModel> bookmarkedEvents) {
        String json = gson.toJson(bookmarkedEvents);
        sharedPreferences.edit().putString("bookmarked_list", json).apply();
    }

    // Update Bookmark Icon Color
    private void updateBookmarkIcon(ImageButton btnBookmark, boolean isBookmarked) {
        if (isBookmarked) {
            btnBookmark.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.bookmark));
        } else {
            btnBookmark.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.baseline_bookmark_border_24));
        }
    }

    // ViewHolder pattern
    private static class ViewHolder {
        TextView eventTitle, eventTime, eventDescription, organizerName, eventLocation, eventTimestamp;
        ImageView eventImage;
        ImageButton btnShare, btnBookmark;

        ViewHolder(View view) {
            eventTitle = view.findViewById(R.id.event_title);
            eventTime = view.findViewById(R.id.eve_time);
            eventTimestamp = view.findViewById(R.id.event_time);
            eventDescription = view.findViewById(R.id.event_description);
            organizerName = view.findViewById(R.id.organizer_name);
            eventLocation = view.findViewById(R.id.event_location);
            eventImage = view.findViewById(R.id.event_img);
            btnShare = view.findViewById(R.id.btn_share);
            btnBookmark = view.findViewById(R.id.btn_bookmark);
        }
    }
}
