package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.tabs.ScoresAdapter;
import com.example.findmyrhythm.View.tabs.SectionsPagerAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

public class PerfilEventoFinalizado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        setContentView(R.layout.activity_perfil_evento_finalizado);

        ScoresAdapter scoresAdapter = new ScoresAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.eventPager);
        viewPager.setAdapter(scoresAdapter);
        TabLayout tabs = findViewById(R.id.eventTabs);
        tabs.setupWithViewPager(viewPager);

        final Button scoreButton = (Button) findViewById(R.id.scoreBtn);

        scoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Score Event";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }
}
