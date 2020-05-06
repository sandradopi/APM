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


        // TODO: POR FAVOR ESTO DE AQUI HAY QUE CAMBIARLO AMIGUITOS :)
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
            if(event.getEventDate().compareTo(actualDate) > 0  ) {
                events[i] = event.getName();
                date = event.getEventDate();
                df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());
                dates[i] = df.format(date);
                prices[i] = String.valueOf(event.getPrice()).concat("€");
                ids[i]=event.getId();
                i++;
            }
        }

        //System.out.println("SUECIA"+nextEvents.get(0).getName());
        mListView.setAdapter(new ListAdapterNext(this.requireContext(), events, dates, prices));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), OrganizerEventInfoActivity.class);
              //  String eventJson = (new Gson()).toJson(nextEvents.get((int) id));
                intent.putExtra("EVENT", ids[(int)id]);
                intent.putExtra("RECOMMENDED", false);
                getActivity().startActivity(intent);


            }
        });

        return view;
    }

}