package com.example.findmyrhythm.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.findmyrhythm.R;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findmyrhythm.View.tabs.SectionsPagerAdapter;

public class PerfilUsuario extends AppCompatActivity {
    private static final String TAG = "Perfil Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        setContentView(R.layout.activity_perfil_usuario);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        ImageView editButton = findViewById(R.id.edit);
        editButton.setClickable(true);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en ajustes del usuario");
                Intent intent = new Intent(PerfilUsuario.this, AjustesUsuario.class);
                startActivity(intent);
            }
        });

    }
}