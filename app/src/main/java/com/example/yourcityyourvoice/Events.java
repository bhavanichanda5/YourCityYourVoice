package com.example.yourcityyourvoice;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Events extends Fragment {

    LinearLayout yours,upcoming,popular,bookmarked,all;
    FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);

        yours = view.findViewById(R.id.yours);
        upcoming = view.findViewById(R.id.upcoming);
        popular = view.findViewById(R.id.popular);
        bookmarked = view.findViewById(R.id.bookmarked);
        all = view.findViewById(R.id.all);
        fab = view.findViewById(R.id.fab);

        yours.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), yours.class);
            startActivity(intent);
        });
        upcoming.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), upcoming.class);
            startActivity(intent);
        });

        popular.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), popular.class);
            startActivity(intent);
        });
        bookmarked.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), bookmarked.class);
            startActivity(intent);
        });

        all.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), all.class);
            startActivity(intent);
        });
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), fab.class);
            startActivity(intent);
        });

        return view;
    }
}