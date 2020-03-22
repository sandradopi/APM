package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.findmyrhythm.R;
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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pop = (CardView) findViewById(R.id.pop);
        pop.setOnClickListener(this);
        rock = (CardView) findViewById(R.id.rock);
        rock.setOnClickListener(this);
        hiphop = (CardView) findViewById(R.id.hiphop);
        hiphop.setOnClickListener(this);
        latin = (CardView) findViewById(R.id.latin);
        latin.setOnClickListener(this);
        dance = (CardView) findViewById(R.id.dance);
        dance.setOnClickListener(this);
        indie = (CardView) findViewById(R.id.indie);
        indie.setOnClickListener(this);
        classic = (CardView) findViewById(R.id.classic);
        classic.setOnClickListener(this);
        reggae = (CardView) findViewById(R.id.reggae);
        reggae.setOnClickListener(this);
        trap = (CardView) findViewById(R.id.trap);
        trap.setOnClickListener(this);
        next = (FloatingActionButton) findViewById(R.id.next);
        next.setOnClickListener(this);

        //Get Locations Selected
        Bundle b = getIntent().getExtras();
        selectedLocations = b.getStringArrayList(getString(R.string.locationsListID));
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
