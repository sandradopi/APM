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
import com.apmuei.findmyrhythm.View.OrganizerEventInfoActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NextEventsFragmentOrg extends Fragment {


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_next_events, container, false);

        ListView mListView;
        mListView = (ListView) view.findViewById(R.id.eventlist);
        Date actualDate = new Date();

        PersistentOrganizerInfo persistentOrganicerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
        final ArrayList<Event> nextEvents= persistentOrganicerInfo.getEvents();
        final ArrayList<Event> nextEventsFiltered= new ArrayList<Event>();
        final ArrayList<String> prices= new ArrayList<String>();




        for (Event event : nextEvents) {
            if(event.getEventDate().compareTo(actualDate) > 0  ) {
                nextEventsFiltered.add(event);

            }
        }
        Comparator c = Collections.reverseOrder();
        Collections.sort(nextEventsFiltered,c);


        for (Event event : nextEventsFiltered) {
            prices.add(String.valueOf(event.getPrice()).concat("€"));

        }

        //System.out.println("SUECIA"+nextEvents.get(0).getName());
        mListView.setAdapter(new ListAdapterNext(this.requireContext(), nextEventsFiltered, prices));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), OrganizerEventInfoActivity.class);
              //  String eventJson = (new Gson()).toJson(nextEvents.get((int) id));
                intent.putExtra("EVENT", nextEventsFiltered.get((int)id).getId());
                intent.putExtra("RECOMMENDED", false);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

}