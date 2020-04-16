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
import androidx.fragment.app.FragmentTransaction;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.EventInfoActivity;
import com.example.findmyrhythm.View.RecommendedEventsActivity;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NextEventsFragment extends Fragment {


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_next_events, container, false);

        ListView mListView;
        mListView = (ListView) view.findViewById(R.id.eventlist);


        PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        final ArrayList<Event> nextEvents= persistentUserInfo.getEvents();
        final String[] events = new String[nextEvents.size()];
        final String[] dates = new String[nextEvents.size()];
        final String[] prices = new String[nextEvents.size()];

        Date date;
        int i = 0;
        for (Event event : nextEvents) {
            events[i] = event.getName();
            date = event.getEventDate();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
            dates[i] = df.format(date);
            prices[i] = String.valueOf(event.getPrice()).concat("€");
            i++;
        }

        mListView.setAdapter(new ListAdapterNext(this.requireContext(), events, dates, prices));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), EventInfoActivity.class);
                String eventJson = (new Gson()).toJson(nextEvents.get((int) id));
                intent.putExtra("EVENT", eventJson);
                intent.putExtra("RECOMMENDED", false);
                getActivity().startActivity(intent);



            }
        });



        return view;
    }


}