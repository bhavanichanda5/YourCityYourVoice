package com.example.yourcityyourvoice;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class home_ extends Fragment {

    private TextView greet;
    LinearLayout one,two,three,four;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home_, container, false);

        one = rootView.findViewById(R.id.one);
        two = rootView.findViewById(R.id.two);
        three = rootView.findViewById(R.id.three);
        four = rootView.findViewById(R.id.four);
        greet = rootView.findViewById(R.id.greeting);

        setGreetingMessage();


        one.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), post_a_complaint.class);
            startActivity(intent);
            requireActivity().finish();
        });
        two.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(),RateToilet.class);
            startActivity(intent);
            requireActivity().finish();
        });

        three.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(),Location.class);
            startActivity(intent);
            requireActivity().finish();
        });
        four.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(),app_rating.class);
            startActivity(intent);
            requireActivity().finish();
        });





        return rootView;
    }

    private void setGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 0 && hour < 12) {
            greeting = "Good Morning,";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon,";
        } else if (hour >= 17 && hour < 21) {
            greeting = "Good Evening,";
        } else {
            greeting = "Good Night,";
        }

        greet.setText(greeting + " Welcome Active Citizen");
    }
}
