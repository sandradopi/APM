package com.example.findmyrhythm.View.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findmyrhythm.View.ListAdapter;
import com.example.findmyrhythm.R;


public class NextEventsFragment extends Fragment {


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.next_events_fragment, container, false);

        ListView mListView;
        mListView = (ListView) view.findViewById(R.id.eventlist);
        String[] events = new String[] {"Viva Suecia", "Dani Fernández", "Antonio José"};
        String[] dates = new String[] { "Sab, 28 Agosto | 22:30", "Viernes, 6 Julio | 23:30", "Domingo, 4 Abril | 22:00" };
        String[] prices = new String[] {"20€", "20€", "10€"};
        String[] rates = new String[] {};
        mListView.setAdapter(new ListAdapter(this.requireContext(), events, dates, prices, rates));

        return view;
    }

}