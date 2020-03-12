package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PerfilEventoOrganizador extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        setContentView(R.layout.activity_perfil_evento_organizador);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.eventMap);
        mapFragment.getMapAsync(this);

        ImageView editButton = (ImageView) findViewById(R.id.editBtn);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Editar evento";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng latLong = new LatLng(43.3713500, -8.3960000);
        map.addMarker(new MarkerOptions()
                .position(latLong)
                .title("Viva Suecia"));
        map.animateCamera(CameraUpdateFactory.newLatLng(latLong));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
