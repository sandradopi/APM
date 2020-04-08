package com.example.findmyrhythm.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.findmyrhythm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchActivity extends UserMenuDrawerActivity implements OnMapReadyCallback {

    private static final String TEXT = "text";
    // Array of strings...
    ListView simpleList;
    String countryList[] = {"Concierto de Radiohead", "Concierto de Harrison Ford Fiesta",
            "Concierto de David Gilmour", "Concierto de Viva Suecia",
            "Concierto de Jarabe de Palo"};

    private GoogleMap mMap;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        setMenuItemChecked(R.id.nav_search);

        adapter = new ArrayAdapter<String>(SearchActivity.this,
                R.layout.item_listview, countryList);

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startActivity(new Intent(SearchActivity.this, EventInfoActivity.class));
            }
        });

        TextView toolbarTitle = findViewById(R.id.tvTitle);
        toolbarTitle.setText("Buscador");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-48, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
