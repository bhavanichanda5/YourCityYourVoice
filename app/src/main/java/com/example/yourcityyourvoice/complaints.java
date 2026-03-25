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

public class complaints extends Fragment {

    LinearLayout posted,voted,nearBy,city,yourActivity,search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_complaints, container, false);

        posted = view.findViewById(R.id.posted);
        voted = view.findViewById(R.id.voted);
        nearBy = view.findViewById(R.id.nearby);
        city = view.findViewById(R.id.city);
        yourActivity = view.findViewById(R.id.your_activity);
        search = view.findViewById(R.id.search);

        posted.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), posted.class);
            startActivity(intent);
        });
        voted.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), voted.class);
            startActivity(intent);
        });
        nearBy.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), nearby.class);
            startActivity(intent);
        });
        city.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), city.class);
            startActivity(intent);
        });
        yourActivity.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), yourAcitivity.class);
            startActivity(intent);
        });
        search.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), search.class);
            startActivity(intent);
        });


        return view;
    }
}