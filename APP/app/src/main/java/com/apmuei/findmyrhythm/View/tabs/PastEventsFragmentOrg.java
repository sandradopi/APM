package com.apmuei.findmyrhythm.View.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.PersistentOrganizerInfo;
import com.apmuei.findmyrhythm.R;
import com.apmuei.findmyrhythm.View.FinishedEventInfoActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PastEventsFragmentOrg extends Fragment {

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_past_events, container, false);
        Date date;
        ListView mListView;
        Date actualDate = new Date();
        mListView = (ListView) view.findViewById(R.id.eventlist);
        PersistentOrganizerInfo persistentOrganicerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
        final ArrayList<Event> pastEvents= persistentOrganicerInfo.getEvents();
        final ArrayList<Event> pastEventsFiltered= new ArrayList<Event>();
        final ArrayList<String> rates= new ArrayList<String>();


        for (Event event : pastEvents) {
            if(event.getEventDate().compareTo(actualDate) < 0  ) {
                pastEventsFiltered.add(event);
                rates.add(null);
            }
        }
        Comparator c = Collections.reverseOrder();
        Collections.sort(pastEventsFiltered,c);

        mListView.setAdapter(new ListAdapterPast(this.requireContext(), pastEventsFiltered, rates));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), FinishedEventInfoActivity.class);
                //String eventJson = (new Gson()).toJson(pastEvents.get((int) id));
                intent.putExtra("EVENT", pastEventsFiltered.get((int)id).getId());
                intent.putExtra("RECOMMENDED", false);
                getActivity().startActivity(intent);


            }
        });
        return view;
    }
}
