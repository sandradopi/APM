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
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.OrganizerEventInfoActivity;
import com.google.gson.Gson;

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

        PersistentOrganizerInfo persistentOrganicerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
        final ArrayList<Event> nextEvents= persistentOrganicerInfo.getEvents();
        final String[] events = new String[nextEvents.size()];
        final String[] dates = new String[nextEvents.size()];
        final String[] prices = new String[nextEvents.size()];

        int i = 0;
        for (Event event : nextEvents) {
            events[i] = event.getName();
            dates[i] = "fecha";
            prices[i] = String.valueOf(event.getPrice()).concat("€");
            i++;
        }

        System.out.println("SUECIA"+nextEvents.get(0).getName());
        mListView.setAdapter(new ListAdapterNext(this.requireContext(), events, dates, prices));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), OrganizerEventInfoActivity.class);
                String eventJson = (new Gson()).toJson(nextEvents.get((int) id));
                intent.putExtra("EVENT", eventJson);
                getActivity().startActivity(intent);


            }
        });

        return view;
    }

}