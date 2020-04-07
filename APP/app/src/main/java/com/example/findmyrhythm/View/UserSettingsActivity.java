package com.example.findmyrhythm.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserSettingsActivity extends MenuDrawerActivity {
    private static final String TAG = "Ajustes Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);


        setMenuItemChecked(R.id.nav_settings);

        TextView toolbarTitle = findViewById(R.id.tvTitle);
        toolbarTitle.setText("Ajustes");

        Button genres = findViewById(R.id.genres);
        genres.setClickable(true);
        genres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en Editar Géneros");
                Intent intent = new Intent(UserSettingsActivity.this, GenresSettingsActivity.class);
                startActivity(intent);
            }
        });

        Button locations = findViewById(R.id.locations);
        locations.setClickable(true);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en Editar Localidades");
                Intent intent = new Intent(UserSettingsActivity.this, LocationsSettingsActivity.class);
                startActivity(intent);
            }
        });


        FloatingActionButton savebutton = (FloatingActionButton) findViewById(R.id.save);
        savebutton.setClickable(true);
        savebutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.w(TAG, "Ha clickeado en guardar ajustes");
            Toast.makeText(UserSettingsActivity.this, getString(R.string.guardar),  Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserSettingsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }
        });

        Switch notification = findViewById(R.id.switch1);
        notification.setClickable(true);
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                Log.w(TAG, "Ha activado las notificaciones");
                Toast.makeText(UserSettingsActivity.this, getString(R.string.noti),  Toast.LENGTH_SHORT).show();
            }else{
                Log.w(TAG, "Ha desactivado las notificaciones");
                Toast.makeText(UserSettingsActivity.this, getString(R.string.desnoti),  Toast.LENGTH_SHORT).show();
            }
        }
        });
    }
}
