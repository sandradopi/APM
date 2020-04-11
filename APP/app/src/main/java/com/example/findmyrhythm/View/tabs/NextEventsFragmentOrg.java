package com.example.findmyrhythm.View.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.OrganizerEventInfoActivity;

import java.util.ArrayList;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NextEventsFragmentOrg extends Fragment {


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_next_events, container, false);

        ListView mListView;
        mListView = (ListView) view.findViewById(R.id.eventlist);

        PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        ArrayList<Event> nextEvents= persistentUserInfo.getEvents();

        String[] events = new String[nextEvents.size()];
        String[] dates = new String[nextEvents.size()];
        String[] prices = new String[nextEvents.size()];

        int i = 0;
        for (Event event : nextEvents) {
            events[i] = event.getName();
            dates[i] = event.getDate().toString();
            prices[i] = String.valueOf(event.getPrice());
            i++;
        }

        mListView.setAdapter(new ListAdapterNext(this.requireContext(), events, dates, prices));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), OrganizerEventInfoActivity.class);
                getActivity().startActivity(intent);


            }
        });

        return view;
    }

}