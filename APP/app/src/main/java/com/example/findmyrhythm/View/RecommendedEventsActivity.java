package com.example.findmyrhythm.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.tabs.ListAdapterNext;
import com.example.findmyrhythm.View.tabs.ListAdapterRecomended;
import com.example.findmyrhythm.View.tabs.NextEventsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class RecommendedEventsActivity extends UserMenuDrawerActivity {

    ListView mListView;
    TextView eventName;
    TextView eventDate;
    TextView eventDescContent;
    TextView eventLocationContent;

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


        TextView toolbarTitle = findViewById(R.id.tvTitle);
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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            // super.onPostExecute(events);
            Log.e("DEBUG", events.toString());

            String[] names = new String[events.size()];
            String[] dates = new String[events.size()];
            String[] prices = new String[events.size()];
            final String[] ids = new String[events.size()];

            Date date;
            int i = 0;
            for (Event event : events) {
                ids[i] = event.getId();
                names[i] = event.getName();
                date = event.getEventDate();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
                dates[i] = df.format(date);
                prices[i] = String.valueOf(event.getPrice());
                i++;
            }

            ListAdapterNext adapter = new ListAdapterNext(RecommendedEventsActivity.this, names, dates, prices);

            int orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE || isTablet(getApplicationContext())){
                System.out.println(events.get(0).getName());
                eventName.setText(events.get(0).getName());
                eventDate.setText(events.get(0).getEventDate().toString());
                eventDescContent.setText(events.get(0).getDescription());
                eventLocationContent.setText(events.get(0).getLocation());

            }
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    int orientation = getResources().getConfiguration().orientation;

                    if(orientation == Configuration.ORIENTATION_LANDSCAPE || isTablet(getApplicationContext())){
                        eventName.setText(events.get((int) id).getName());
                        eventDate.setText(events.get((int) id).getEventDate().toString());
                        eventDescContent.setText(events.get((int) id).getDescription());
                        eventLocationContent.setText(events.get((int) id).getLocation());
                    }

                    else if (orientation == Configuration.ORIENTATION_PORTRAIT){
                        Intent intent = new Intent(RecommendedEventsActivity.this, EventInfoActivity.class);
                        // String eventJson = (new Gson()).toJson(events.get((int) id));
                        // intent.putExtra("ID", ids[(int) id]);
                        intent.putExtra("EVENT", events.get((int) id).getId());
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
