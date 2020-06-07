package com.apmuei.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
import com.apmuei.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SetGenresActivity extends AppCompatActivity implements View.OnClickListener {

    CardView pop, rock, hiphop, latin, dance, indie, classic, reggae, trap;
    FloatingActionButton next;
    ArrayList<String> selectedGenres = new ArrayList<>();
    ArrayList<String> selectedLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_genres);

        ActionBar actionBar = getSupportActionBar();
        Assert.assertNotNull(actionBar, "ActionBar not found");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.layout_actionbar_empty);
        actionBar.setDisplayHomeAsUpEnabled(true);

        pop = findViewById(R.id.pop);
        pop.setOnClickListener(this);
        rock = findViewById(R.id.rock);
        rock.setOnClickListener(this);
        hiphop = findViewById(R.id.hiphop);
        hiphop.setOnClickListener(this);
        latin = findViewById(R.id.latin);
        latin.setOnClickListener(this);
        dance = findViewById(R.id.dance);
        dance.setOnClickListener(this);
        indie = findViewById(R.id.indie);
        indie.setOnClickListener(this);
        classic = findViewById(R.id.classic);
        classic.setOnClickListener(this);
        reggae = findViewById(R.id.reggae);
        reggae.setOnClickListener(this);
        trap = findViewById(R.id.trap);
        trap.setOnClickListener(this);
        next = findViewById(R.id.next);
        next.setOnClickListener(this);

        //Get Locations Selected
        Bundle bundle = getIntent().getExtras();
        Assert.assertNotNull(bundle, "No extras found");
        selectedLocations = bundle.getStringArrayList(getString(R.string.locationsListID));
    }

    @Override
    public void onClick(View view) {

        if (view == next) {

            /*TODO: CHECK THAT THE GENRES ARRAY HAS AT LEAST 1 GENRE.
               PASS TO THE FINAL VIEW THE ARRAY WITH THE LOCATIONS AND THE GENRES.
             */

            Intent intent = new Intent(this, UserLogActivity.class);
            intent.putExtra(getString(R.string.locationsListID), selectedLocations);
            intent.putExtra(getString(R.string.genresListID), selectedGenres);
            startActivity(intent);

        } else {

            String genre = view.getContentDescription().toString();

            if (selectedGenres.contains(genre)) {
                selectedGenres.remove(genre);
                view.setBackgroundResource(0);

            } else {
                selectedGenres.add(genre);
                view.setBackground(getResources().getDrawable(R.drawable.image_border));
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
