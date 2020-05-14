package com.apmuei.findmyrhythm.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.EventService;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.Model.Photo;
import com.apmuei.findmyrhythm.Model.PhotoService;
import com.apmuei.findmyrhythm.Model.User;
import com.apmuei.findmyrhythm.Model.UserService;
import com.apmuei.findmyrhythm.R;
import com.apmuei.findmyrhythm.View.tabs.ListAdapterNext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class RecommendedEventsActivity extends UserMenuDrawerActivity {

    ListView mListView;
    TextView eventName;
    TextView eventDate;
    TextView eventDescContent;
    TextView eventLocationContent;
    TextView eventCapacity;
    TextView eventGenre, eventCost;
    Photo photoEvent;
    PhotoService photoService= new PhotoService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_events);

        setMenuItemChecked(R.id.nav_recommended);

        mListView = findViewById(R.id.eventslist2);
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventDescContent = findViewById(R.id.eventDescContent);
        eventLocationContent = findViewById(R.id.eventLocationContent);
        eventCapacity = findViewById(R.id.eventCapacity);
        eventCost = findViewById(R.id.eventCost);
        eventGenre = findViewById(R.id.category);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Recomendados");
//        toolbarTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        new getEvents().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setMenuItemChecked(R.id.nav_recommended);
        new getEvents().execute();
    }

    private class getEvents extends AsyncTask<Void, Void, ArrayList<Event>> {
        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=ProgressDialog.show(RecommendedEventsActivity.this,"","Estamos buscando los mejores eventos para ti...",false);
        }

        @Override
        protected ArrayList<Event> doInBackground(Void... voids) {
            EventService eventService = new EventService();
            UserService userService = new UserService();
            ArrayList<Event> events = new ArrayList<>();
            final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
            try {
                SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                User user = userService.getUser(preferences.getString("fb_id", null));

                events = eventService.getRecommendedEvents(user);
                Log.e("AQUI", events.toString());
                persistentUserInfo.addEventRecommended(getApplicationContext(), events);

            } catch (InstanceNotFoundException e) {
                Log.e("DEBUG", "InstanceNotFoundException");
            }


            return events;
        }

        @Override
        protected void onPostExecute(final ArrayList<Event> events) {
            progress.dismiss();
            super.onPostExecute(events);

            Log.e("DEBUG", events.toString());


            final ArrayList<Event> nextEventsFiltered= new ArrayList<Event>();
            final ArrayList<String> prices= new ArrayList<String>();
            Date actualDate = new Date();


                for (Event event : events) {
                    if(event.getEventDate().compareTo(actualDate) > 0  ) {
                        nextEventsFiltered.add(event);

                    }
                }
                Comparator c = Collections.reverseOrder();
                Collections.sort(nextEventsFiltered,c);


                for (Event event : nextEventsFiltered) {
                    prices.add(String.valueOf(event.getPrice()).concat("€"));

                }


            ListAdapterNext adapter = new ListAdapterNext(RecommendedEventsActivity.this, nextEventsFiltered, prices);

            int orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE || isTablet(getApplicationContext())){
                if(!nextEventsFiltered.isEmpty()){
                    Event e = nextEventsFiltered.get(0);
                    eventName.setText(e.getName());
                    eventDate.setText(e.getEventDate().toString());
                    eventDescContent.setText(e.getDescription());
                    eventLocationContent.setText(e.getLocation());
                    eventCapacity.setText(e.getMaxAttendees());
                    eventCost.setText(e.getPrice());
                    eventGenre.setText(e.getGenre());
                }



            }
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    int orientation = getResources().getConfiguration().orientation;

                    if(orientation == Configuration.ORIENTATION_LANDSCAPE || isTablet(getApplicationContext())){
                        Event e = nextEventsFiltered.get((int) id);
                        eventName.setText(e.getName());
                        eventDate.setText(e.getEventDate().toString());
                        eventDescContent.setText(e.getDescription());
                        eventLocationContent.setText(e.getLocation());
                        eventCapacity.setText(e.getMaxAttendees());
                        eventCost.setText(e.getPrice());
                        eventGenre.setText(e.getGenre());

                    }

                    else if (orientation == Configuration.ORIENTATION_PORTRAIT){
                        Intent intent = new Intent(RecommendedEventsActivity.this, EventInfoActivity.class);
                        intent.putExtra("EVENT", nextEventsFiltered.get((int) id).getId());
                        intent.putExtra("RECOMMENDED", true);
                        RecommendedEventsActivity.this.startActivity(intent);
                    }



                }
            });

        }
        public boolean isTablet(Context context) {
            return (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }

    }

}
