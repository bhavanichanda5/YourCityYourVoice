package com.example.yourcityyourvoice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class bookmarked extends AppCompatActivity implements BookmarkUpdateListener {

    private ListView bookmarkListView;
    private ImageView emptyImageView;
    private EventAdapter eventAdapter;
    private List<EventModel> bookmarkedEvents;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked);

        setupToolbar();
        initializeComponents();
        loadBookmarkedEvents();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bookmarked Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initializeComponents() {
        bookmarkListView = findViewById(R.id.list_view_bookmarks);
        emptyImageView = findViewById(R.id.emptyImageView); // Reference to empty image
        sharedPreferences = getSharedPreferences("bookmarked_events", MODE_PRIVATE);
        bookmarkedEvents = new ArrayList<>();
        eventAdapter = new EventAdapter(this, bookmarkedEvents, this);
        bookmarkListView.setAdapter(eventAdapter);
    }

    private void loadBookmarkedEvents() {
        bookmarkedEvents.clear();
        bookmarkedEvents.addAll(getBookmarkedEvents());

        if (bookmarkedEvents.isEmpty()) {
            bookmarkListView.setVisibility(View.GONE);
            emptyImageView.setVisibility(View.VISIBLE);
        } else {
            bookmarkListView.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(View.GONE);
            eventAdapter.notifyDataSetChanged();
        }
    }

    private List<EventModel> getBookmarkedEvents() {
        String json = sharedPreferences.getString("bookmarked_list", "[]");
        List<EventModel> bookmarkedEvents = gson.fromJson(json, new TypeToken<List<EventModel>>() {}.getType());
        return (bookmarkedEvents != null) ? bookmarkedEvents : new ArrayList<>();
    }

    @Override
    public void onBookmarkUpdated() {
        loadBookmarkedEvents();
    }
}
