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
import com.apmuei.findmyrhythm.View.EventInfoActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NextEventsFragment extends Fragment {


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_next_events, container, false);
        Date actualDate = new Date();
        ListView mListView;
        mListView = (ListView) view.findViewById(R.id.eventlist);


        PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        final ArrayList<Event> nextEvents= persistentUserInfo.getEvents();
        final ArrayList<Event> nextEventsFiltered= new ArrayList<Event>();
        final ArrayList<String> prices= new ArrayList<String>();




        for (Event event : nextEvents) {
           if(event.getEventDate().compareTo(actualDate) > 0  ){
               nextEventsFiltered.add(event);
            }
        }
        Comparator c = Collections.reverseOrder();
        Collections.sort(nextEventsFiltered,c);


        for (Event event : nextEventsFiltered) {
            prices.add(String.valueOf(event.getPrice()).concat("€"));

        }

        mListView.setAdapter(new ListAdapterNext(this.requireContext(), nextEventsFiltered, prices));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), EventInfoActivity.class);
                intent.putExtra("EVENT",  nextEventsFiltered.get((int)id).getId());
                intent.putExtra("RECOMMENDED", false);
                getActivity().startActivity(intent);



            }
        });



        return view;
    }


}