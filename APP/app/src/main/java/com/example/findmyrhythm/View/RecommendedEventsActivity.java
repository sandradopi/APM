package com.example.findmyrhythm.View;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.findmyrhythm.View.tabs.ListAdapterRecomended;
import com.google.firebase.auth.FirebaseAuth;

public class RecommendedEventsActivity extends UserMenuDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_events);
       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
     //   getSupportActionBar().setCustomView(R.layout.action_layout);

        setMenuItemChecked(R.id.nav_recommended);

        ListView mListView;
        mListView = (ListView) findViewById(R.id.eventlist2);

        String[] events = new String[] {"Viva Suecia", "Dani Fernández", "Antonio José"};
        String[] dates = new String[] { "Sab, 3 Marzo | 22:30", "Viernes, 6 Marzo | 23:30", "Domingo, 4 Abril | 22:00" };
        String[] prices = new String[] {"20€", "20€", "10€"};
        String[] rates = new String[] {"5", "2", "4"};
        mListView.setAdapter(new ListAdapterRecomended(this,events, dates, prices));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startActivity(new Intent(RecommendedEventsActivity.this, EventInfoActivity.class));
            }
        });

        TextView toolbarTitle = findViewById(R.id.tvTitle);
        toolbarTitle.setText("Recomendados");
//        toolbarTitle.setGravity(Gravity.CENTER_HORIZONTAL);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Esto peta
        // new getEvents().execute();
    }

    private class getEvents extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            EventService eventService = new EventService();
            UserService userService = new UserService();
            try {
                User user = userService.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

                System.out.println(user.getSubscribedLocations() + "\n");
                System.out.println(user.getSubscribedGenres() + "\n");
                eventService.getRecommendedEvents(user);
            } catch (InstanceNotFoundException e) {
                System.out.println("PUTO TONTO");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
