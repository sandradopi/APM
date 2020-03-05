package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class OrgProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_profile);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        ListView mListView;
        mListView = (ListView) findViewById(R.id.eventlist);

        String[] events = new String[]{"Viva Suecia", "Dani Fernández", "Antonio José"};
        String[] dates = new String[]{"Sab, 3 Marzo | 22:30", "Viernes, 6 Marzo | 23:30", "Domingo, 4 Abril | 22:00"};
        String[] prices = new String[]{"20€", "20€", "10€"};
        String[] rates = new String[]{};
        mListView.setAdapter(new ListAdapter(this, events, dates, prices, rates));


        ImageView infoButton = findViewById(R.id.info);
        infoButton.setClickable(true);
        infoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                DialogInfoOrg dialogo = new DialogInfoOrg();
                dialogo.show(fragmentManager, "tagAlerta");
            }
        });

        FloatingActionButton añadirEvento = findViewById(R.id.floatingActionButton4);
        añadirEvento.setClickable(true);
        añadirEvento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrgProfile.this, CrearEvento.class);
                startActivity(intent);
            }
        });
    }
}