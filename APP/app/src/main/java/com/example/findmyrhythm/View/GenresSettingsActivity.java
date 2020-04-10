package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GenresSettingsActivity extends AppCompatActivity implements View.OnClickListener{

    CardView pop, rock, hiphop, latin, dance, indie, classic, reggae, trap;
    FloatingActionButton save;
    ArrayList<String> selectedGenres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres_settings);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedGenres.add("pop");
        selectedGenres.add("latin");
        selectedGenres.add("trap");

        pop = (CardView) findViewById(R.id.pop);
        pop.setOnClickListener(this);
        if (selectedGenres.contains(pop.getContentDescription().toString()))
            pop.setBackground(getResources().getDrawable(R.drawable.image_border));
        rock = (CardView) findViewById(R.id.rock);
        rock.setOnClickListener(this);
        if (selectedGenres.contains(rock.getContentDescription().toString()))
            rock.setBackground(getResources().getDrawable(R.drawable.image_border));
        hiphop = (CardView) findViewById(R.id.hiphop);
        hiphop.setOnClickListener(this);
        if (selectedGenres.contains(hiphop.getContentDescription().toString()))
            hiphop.setBackground(getResources().getDrawable(R.drawable.image_border));
        latin = (CardView) findViewById(R.id.latin);
        latin.setOnClickListener(this);
        if (selectedGenres.contains(latin.getContentDescription().toString()))
            latin.setBackground(getResources().getDrawable(R.drawable.image_border));
        dance = (CardView) findViewById(R.id.dance);
        dance.setOnClickListener(this);
        if (selectedGenres.contains(dance.getContentDescription().toString()))
            dance.setBackground(getResources().getDrawable(R.drawable.image_border));
        indie = (CardView) findViewById(R.id.indie);
        indie.setOnClickListener(this);
        if (selectedGenres.contains(indie.getContentDescription().toString()))
            indie.setBackground(getResources().getDrawable(R.drawable.image_border));
        classic = (CardView) findViewById(R.id.classic);
        classic.setOnClickListener(this);
        if (selectedGenres.contains(classic.getContentDescription().toString()))
            classic.setBackground(getResources().getDrawable(R.drawable.image_border));
        reggae = (CardView) findViewById(R.id.reggae);
        reggae.setOnClickListener(this);
        if (selectedGenres.contains(reggae.getContentDescription().toString()))
            reggae.setBackground(getResources().getDrawable(R.drawable.image_border));
        trap = (CardView) findViewById(R.id.trap);
        trap.setOnClickListener(this);
        if (selectedGenres.contains(trap.getContentDescription().toString()))
            trap.setBackground(getResources().getDrawable(R.drawable.image_border));
        save = (FloatingActionButton) findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {

        if (view == save) {

            /*TODO: CHECK THAT THE GENRES ARRAY HAS AT LEAST 1 GENRE.
               PASS TO THE FINAL VIEW THE ARRAY WITH THE LOCATIONS AND THE GENRES.
             */

            Intent intent = new Intent(this, UserSettingsActivity.class);
            //intent.putExtra(getString(R.string.genresListID), selectedGenres);
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
}
