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
import com.example.findmyrhythm.View.FinishedEventInfoActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PastEventsFragmentOrg extends Fragment {

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_past_events, container, false);

        ListView mListView;
        mListView = (ListView) view.findViewById(R.id.eventlist);
        PersistentOrganizerInfo persistentOrganicerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
        final ArrayList<Event> pastEvents= persistentOrganicerInfo.getEvents();

        String[] events = new String[pastEvents.size()];
        String[] dates = new String[pastEvents.size()];
        String[] rates = new String[pastEvents.size()];

        int i = 0;
        for (Event event : pastEvents) {
            events[i] = event.getName();
            dates[i] = "fecha";
            rates[i] = null;
            i++;
        }
        mListView.setAdapter(new ListAdapterPast(this.requireContext(), events, dates, rates));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), FinishedEventInfoActivity.class);
                //String eventJson = (new Gson()).toJson(pastEvents.get((int) id));
                intent.putExtra("EVENT", pastEvents.get((int) id).getId());
                getActivity().startActivity(intent);


            }
        });
        return view;
    }
}
