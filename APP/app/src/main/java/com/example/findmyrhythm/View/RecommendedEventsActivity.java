package com.example.findmyrhythm.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.tabs.ListAdapterNext;
import com.example.findmyrhythm.View.tabs.ListAdapterRecomended;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RecommendedEventsActivity extends UserMenuDrawerActivity {

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_events);

        setMenuItemChecked(R.id.nav_recommended);

        mListView = findViewById(R.id.eventslist2);

        TextView toolbarTitle = findViewById(R.id.tvTitle);
        toolbarTitle.setText("Recomendados");
//        toolbarTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        new getEvents().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new getEvents().execute();
    }

    private class getEvents extends AsyncTask<Void, Void, ArrayList<Event>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Event> doInBackground(Void... voids) {
            EventService eventService = new EventService();
            UserService userService = new UserService();
            ArrayList<Event> events = new ArrayList<>();
            try {
                SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                User user = userService.getUser(preferences.getString("fb_id", null));

                System.out.println(user.getSubscribedLocations() + "\n");
                System.out.println(user.getSubscribedGenres() + "\n");
                events = eventService.getRecommendedEvents(user);
            } catch (InstanceNotFoundException e) {
                Log.e("DEBUG", "InstanceNotFoundException");
            }

            return events;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> events) {
            // super.onPostExecute(events);
            Log.e("DEBUG", events.toString());

            String[] names = new String[events.size()];
            String[] dates = new String[events.size()];
            String[] prices = new String[events.size()];

            int i = 0;
            for (Event event : events) {
                names[i] = event.getName();
                dates[i] = "fecha";
                prices[i] = String.valueOf(event.getPrice());
                i++;
            }
            mListView.setAdapter(new ListAdapterNext(RecommendedEventsActivity.this, names, dates, prices));
        }
    }
}
