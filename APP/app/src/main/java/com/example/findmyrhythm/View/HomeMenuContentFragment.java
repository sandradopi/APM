package com.example.findmyrhythm.View;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.findmyrhythm.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by dani on 28/12/16.
 */
public class HomeMenuContentFragment extends Fragment implements OnMapReadyCallback {


  private static final String TEXT = "text";
  // Array of strings...
  ListView simpleList;
  String countryList[] = {"Concierto de Radiohead", "Concierto de Harrison Ford Fiesta",
          "Concierto de David Gilmour", "Concierto de Viva Suecia",
          "Concierto de Jarabe de Palo"};

  private GoogleMap mMap;
  private ArrayAdapter adapter;

  public static HomeMenuContentFragment newInstance(String text) {
    HomeMenuContentFragment frag = new HomeMenuContentFragment();

    Bundle args = new Bundle();
//    args.putString(TEXT, text);
    frag.setArguments(args);

    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
          Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.home_menu_fragment, container, false);

    if (getArguments() != null) {
//      ((TextView) layout.findViewById(R.id.text)).setText(getArguments().getString(TEXT));
    }
    adapter = new ArrayAdapter<String>(getActivity(),
            R.layout.activity_listview, countryList);

    return layout;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ListView listView = (ListView) view.findViewById(R.id.listview);
    listView.setAdapter(adapter);
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

