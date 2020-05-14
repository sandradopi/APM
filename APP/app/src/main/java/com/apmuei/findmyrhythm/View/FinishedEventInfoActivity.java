package com.apmuei.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.apmuei.findmyrhythm.Model.PersistentOrganizerInfo;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.Model.Photo;
import com.apmuei.findmyrhythm.Model.PhotoService;
import com.apmuei.findmyrhythm.Model.Rating;
import com.apmuei.findmyrhythm.Model.RatingService;
import com.apmuei.findmyrhythm.Model.User;
import com.apmuei.findmyrhythm.Model.UserService;
import com.apmuei.findmyrhythm.R;
import com.apmuei.findmyrhythm.View.tabs.RatingsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FinishedEventInfoActivity extends AppCompatActivity implements ScoreEventDialog.ScoreEventListener {
    private static final String TAG = "Score Event";
    TextView name, date, descripcion, ubication, time,category;
    Photo photoEvent;
    PhotoService photoService= new PhotoService();
    Event eventSelect;
    ArrayList<Rating> ratings = new ArrayList<>();
    ArrayList<String> comments = new ArrayList<>();
    ArrayList<Float> scores = new ArrayList<>();
    Rating rated;
    Button rateButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //View
        setContentView(R.layout.activity_finished_event_info);

        //ToolBar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        final String account_type = sharedPreferences.getString("account_type", null);

        rateButton = (Button) findViewById(R.id.rateButton);

        if (account_type.equals("organizer")) {

            PersistentOrganizerInfo persistentInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
            eventSelect = persistentInfo.getEvent(eventSelectId);

            rateButton.setVisibility(View.GONE);
        }
        else {
            PersistentUserInfo persistentInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
            eventSelect = persistentInfo.getEvent(eventSelectId);
            //If event is already rated
            if (persistentInfo.getRatedEvents().contains(eventSelect.getId())) {
                rateButton.setText(getString(R.string.rated_btn));
            }
        }



        new isRated().execute();
        new getPhoto().execute();

        new getComments().execute();
        new getUsers().execute();


        name = findViewById(R.id.eventName);
        date =  findViewById(R.id.eventDate);
        descripcion = findViewById(R.id.eventDescContent);
        descripcion.setMovementMethod(new ScrollingMovementMethod());
        ubication = findViewById(R.id.eventLocationContent);
        time = findViewById(R.id.eventTime);
        category = findViewById(R.id.category);


        name.setText(eventSelect.getName());
        Date dateF;
        dateF = eventSelect.getEventDate();
        DateFormat df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());
        DateFormat df2 = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        date.setText(df.format(dateF));
        time.setText(df2.format(dateF));
        descripcion.setText(eventSelect.getDescription());
        ubication.setText(eventSelect.getLocation());
        category.setText(eventSelect.getGenre());


        // SCORES LOGIC
        RatingBar bar=(RatingBar)findViewById(R.id.pastEventScore);
        new getRatingsMedia().execute();
        bar.setClickable(false);
        rateButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                if (account_type.equals("user")) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.w(TAG, "Score event");
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        ScoreEventDialog dialog = new ScoreEventDialog().newInstance(name.getText().toString(), eventSelect.getId(), rated);
                        dialog.show(fragmentManager, "tagAlerta");
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void onDialogPositiveClick() {
        // User touched the dialog's positive button
        rateButton.setText(getString(R.string.rated_btn));
        new getRatingsMedia().execute();
        new isRated().execute();
        new getComments().execute();
        new getUsers().execute();

    }

    private class getPhoto extends AsyncTask<Void, Void, Void> {

        final String eventSelectId = getIntent().getStringExtra("EVENT");
        final PersistentUserInfo persistentUserInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Event eventSelect;
            final boolean recommended = getIntent().getExtras().getBoolean("RECOMMENDED");
            if(recommended) {
                eventSelect  = persistentUserInfo.getEventRecommended(eventSelectId);

            } else{
                eventSelect = persistentUserInfo.getEvent(eventSelectId);
            }

            photoEvent = photoService.getPhoto(eventSelect.getEventImage());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            byte[] decodedString = Base64.decode(photoEvent.getEventImage(),Base64.NO_WRAP);
            InputStream inputStream  = new ByteArrayInputStream(decodedString);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            Bitmap imagenFinal = Bitmap.createScaledBitmap(bitmap,242,152,false);
            final ImageView imageEvent =  findViewById(R.id.imageEvent);
            imageEvent.setImageBitmap(imagenFinal);


        }
    }

    private class getRatingsMedia extends AsyncTask<Void, Void, Void> {

        RatingService ratingService = new RatingService();
        Float ratingsMedia;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ratingsMedia = ratingService.getMediaByEvent(eventSelect.getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            RatingBar bar=(RatingBar)findViewById(R.id.pastEventScore);
            bar.setRating(ratingsMedia);
        }
    }

    private class getComments extends AsyncTask<Void, Void, Void> {

        RatingService ratingService = new RatingService();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ratings.clear();
            ratings = ratingService.getRatingsByEvent(eventSelect.getId());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            comments.clear();
            scores.clear();
            for (Rating r : ratings) {
                scores.add(r.getRatingValue());
                if (! r.getComment().isEmpty())
                    comments.add(r.getComment());
            }

        }
    }

    private class getUsers extends AsyncTask<Void, Void, Void> {

        UserService userService = new UserService();
        ArrayList<User> users = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (Rating r : ratings) {
                try {
                    users.add(userService.getUser(r.getUserId()));
                } catch (InstanceNotFoundException e) {
                    Log.e(TAG, "tried to insert an existing id");
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<String> names = new ArrayList<String>();
            for (User u: users) {
                names.add(u.getName());
            }
            final ListView listview = (ListView) findViewById(R.id.ratingList);
            RatingsAdapter adapter = new RatingsAdapter(getApplicationContext(), comments, scores, names);
            listview.setAdapter(adapter);
        }
    }

    private class isRated extends AsyncTask<Void, Void, Void> {

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        RatingService ratingService = new RatingService();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rated = ratingService.isRated(currentUser.getUid(), eventSelect.getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
