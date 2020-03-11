package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.tabs.SectionsPagerAdapterOrg;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class OrgProfile extends AppCompatActivity {
    private static final String TAG = "Perfil Organizador";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_profile);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);


        SectionsPagerAdapterOrg sectionsPagerAdapter = new SectionsPagerAdapterOrg(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);



        ImageView infoButton = findViewById(R.id.info);
        infoButton.setClickable(true);
        infoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en el dialogo de la información del local");
                FragmentManager fragmentManager = getSupportFragmentManager();
                DialogInfoOrg dialogo = new DialogInfoOrg();
                dialogo.show(fragmentManager, "tagAlerta");

            }
        });

        ImageView editButton = findViewById(R.id.edit);
        editButton.setClickable(true);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en ajustes del local");
                Intent intent = new Intent(OrgProfile.this, AjustesOrganizador.class);
                startActivity(intent);
            }
        });


        FloatingActionButton añadirEvento = findViewById(R.id.floatingActionButton4);
        añadirEvento.setClickable(true);
        añadirEvento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en añadir nuevo evento");
                Intent intent = new Intent(OrgProfile.this, CrearEvento.class);
                startActivity(intent);
            }
        });






    }
}