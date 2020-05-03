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

import java.text.DateFormat;
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

        final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", java.util.Locale.getDefault());
        PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
        final ArrayList<Event> nextEvents= persistentUserInfo.getEvents();


        Date date;
        DateFormat df;
        Date actualDate = new Date();
        int eventsize = 0;

        for (Event event : nextEvents) {
            if(event.getEventDate().compareTo(actualDate) > 0  ) {
                eventsize++;
            }
        }

        String[] events = new String[eventsize];
        String[] dates = new String[eventsize];
        String[] prices = new String[eventsize];
        final String[] ids = new String[eventsize];

        int i = 0;
        for (Event event : nextEvents) {
           if(event.getEventDate().compareTo(actualDate) > 0  ){
            events[i] = event.getName();
            date = event.getEventDate();
            df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());
            dates[i] = df.format(date);
            prices[i] = String.valueOf(event.getPrice()).concat("€");
            ids[i]=event.getId();
            i++;
            }
        }

        mListView.setAdapter(new ListAdapterNext(this.requireContext(), events, dates, prices));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), EventInfoActivity.class);
                intent.putExtra("EVENT", ids[(int)id]);
                intent.putExtra("RECOMMENDED", false);
                getActivity().startActivity(intent);



            }
        });



        return view;
    }


}