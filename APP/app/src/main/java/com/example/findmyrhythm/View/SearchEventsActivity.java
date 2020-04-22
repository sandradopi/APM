package com.example.findmyrhythm.View;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.findmyrhythm.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;


public class SearchEventsActivity extends FragmentActivity implements OnMapReadyCallback {
    final String TAG  = "SearchEventActivity";
    private GoogleMap mMap;
    private StringBuilder mResult;
    private EditText searchText;
    private ListView mSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        String apiKey = getString(R.string.google_api_key);
        mSearchResult = findViewById(R.id.searchResult);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        final PlacesClient placesClient = Places.createClient(this);


        searchText = findViewById(R.id.input_search);

        searchText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                // Toast.makeText(SearchEventsActivity.this, searchText.getText().toString(), Toast.LENGTH_SHORT).show();
//                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
//                // and once again when the user makes a selection (for example when calling fetchPlace()).
//                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
//                // Create a RectangularBounds object.
//                RectangularBounds bounds = RectangularBounds.newInstance(
//                        new LatLng(24.253831, -27.396693), //dummy lat/lng
//                        new LatLng(43.633574, 6.265417));
//                // Use the builder to create a FindAutocompletePredictionsRequest.
//                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                        // Call either setLocationBias() OR setLocationRestriction().
//                        //.setLocationBias(bounds)
//                        //.setLocationRestriction(bounds)
//                        .setCountry("es")//Nigeria
//                        //.setTypeFilter(TypeFilter.ADDRESS)
//                        .setSessionToken(token)
//                        .setQuery(searchText.getText().toString())
//                        .build();
//
//
//                placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
//                    mResult = new StringBuilder();
//                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                        mResult.append(" ").append(prediction.getFullText(null) + "\n");
//                        Log.i(TAG, prediction.getPlaceId());
//                        Log.i(TAG, prediction.getPrimaryText(null).toString());
//                        // Toast.makeText(SearchEventsActivity.this, prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
//                    }
//                    mSearchResult.setText(String.valueOf(mResult));
//                }).addOnFailureListener((exception) -> {
//                    if (exception instanceof ApiException) {
//                        ApiException apiException = (ApiException) exception;
//                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//                    }
//                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
