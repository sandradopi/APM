package com.example.findmyrhythm.View;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findmyrhythm.R;

public class EventosRecomendados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendados);
       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
     //   getSupportActionBar().setCustomView(R.layout.action_layout);


        ListView mListView;
        mListView = (ListView) findViewById(R.id.eventlist);

        String[] events = new String[] {"Viva Suecia", "Dani Fernández", "Antonio José"};
        String[] dates = new String[] { "Sab, 3 Marzo | 22:30", "Viernes, 6 Marzo | 23:30", "Domingo, 4 Abril | 22:00" };
        String[] prices = new String[] {"20€", "20€", "10€"};
        String[] rates = new String[] {"5", "2", "4"};
        mListView.setAdapter(new ListAdapter(this, events, dates, prices,rates));


    }}
