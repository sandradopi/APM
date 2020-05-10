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
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.R;
import com.apmuei.findmyrhythm.View.FinishedEventInfoActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PastEventsFragment extends Fragment {

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_past_events, container, false);
        Boolean rated =false;
        ListView mListView;
        Date actualDate = new Date();

        mListView = (ListView) view.findViewById(R.id.eventlist);
        PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        final ArrayList<Event> pastEvents= persistentUserInfo.getEvents();
        final ArrayList<String> eventsRated = persistentUserInfo.getRatedEvents();

        final ArrayList<Event> pastEventsFiltered= new ArrayList<Event>();
        final ArrayList<String> rates= new ArrayList<String>();



        for (Event event : pastEvents) {
            if(event.getEventDate().compareTo(actualDate) < 0  ){
                pastEventsFiltered.add(event);

            }
        }

        Comparator c = Collections.reverseOrder();
        Collections.sort(pastEventsFiltered,c);


        int i=0;
        for (Event event : pastEventsFiltered) {
            rates.add("not_rated");
            for (String ratedEvent : eventsRated) {
                if (event.getId().equals(ratedEvent)) {
                    rates.set(i,"rated");
                    rated=true;
            }

            }
            i++;
        }
        mListView.setAdapter(new ListAdapterPast(this.requireContext(), pastEventsFiltered, rates));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), FinishedEventInfoActivity.class);
                intent.putExtra("EVENT", pastEventsFiltered.get((int)id).getId());
                intent.putExtra("RECOMMENDED", false);
                getActivity().startActivity(intent);


            }
        });
        return view;
    }
}
