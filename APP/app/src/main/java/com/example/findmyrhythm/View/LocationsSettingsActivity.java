package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class LocationsSettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, CustomAutoCompleteAdapater.OnSelfLocationListener, View.OnClickListener {

    AutoCompleteTextView provinces;
    ArrayList<String> selectedProvinces = new ArrayList<String>();
    GridLayout locations;
    FloatingActionButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_settings);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        save = (FloatingActionButton) findViewById(R.id.save);
        save.setOnClickListener(this);

        provinces = (AutoCompleteTextView) findViewById(R.id.auto_province);
        String selflocation = getResources().getString(R.string.selfLocation);
        String[] countries = getResources().getStringArray(R.array.provinces_array);
        CustomAutoCompleteAdapater adapter = new CustomAutoCompleteAdapater(this, android.R.layout.simple_list_item_1, selflocation, countries);
        adapter.setOnSelfLocationListener(this);

        provinces.setThreshold(0);
        provinces.setAdapter(adapter);
        provinces.setOnItemClickListener(this);

        locations = (GridLayout) findViewById(R.id.locations);

        addProvince("A Coruña");
        addProvince("Lugo");
        addProvince("Pontevedra");
    }

    @Override
    public void onSelfLocationClicked() {

        Toast toast = Toast.makeText(getApplicationContext(), "GET THE LOCATION OF THE USER WITH GPS. ASK IF GPS IS NOT ACTIVATED", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        //TODO: GET THE PROVINCE FROM GPS, ADD IT AND SET THE VALUE INTO THE AUTOCOMPLETE TEXT VIEW.
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String value = (String) adapterView.getItemAtPosition(i);
        addProvince(value);
        provinces.setText("");

    }

    public void addProvince(String province){

        selectedProvinces.add(province);
        TextView text = new TextView(this);
        text.setText(province);
        LinearLayout.LayoutParams params = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(10, 0, 10, 25);
        text.setLayoutParams(params);
        text.setBackground(getResources().getDrawable(R.drawable.recover));
        text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close, 0);

        locations.addView(text);
    }

    @Override
    public void onClick(View view) {

        //TODO: PASS TO THE GENRES CLASS THE ARRAYlIST OF LOCATIONS AND WHEN ALL THE INFORMATION IS KNOWN ADD THE USER IN THE DATABASE.
        Intent intent = new Intent(this, UserSettingsActivity.class);
        intent.putExtra(getString(R.string.locationsListID), selectedProvinces);
        startActivity(intent);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}