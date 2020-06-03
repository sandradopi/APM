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
import com.apmuei.findmyrhythm.Model.EventService;
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
    private Event eventSelect;
    private ArrayList<Rating> ratings = new ArrayList<>();
    private ArrayList<String> comments = new ArrayList<>();
    private ArrayList<Float> scores = new ArrayList<>();
    private Rating rated;
    private Button rateButton;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View
        setContentView(R.layout.activity_finished_event_info);

        // ToolBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null";
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.layout_actionbar_empty);
        actionBar.setDisplayHomeAsUpEnabled(true);

        RatingBar bar=(RatingBar)findViewById(R.id.pastEventScore);

        rateButton = findViewById(R.id.rateButton);

        // Event
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        accountType = sharedPreferences.getString("account_type", null);

        assert accountType != null : "Invalid Account Type (null)";
        if (accountType.equals("organizer")) {

            PersistentOrganizerInfo persistentInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
            eventSelect = persistentInfo.getEvent(eventSelectId);

            rateButton.setVisibility(View.GONE);
            bar.setIsIndicator(true);

        } else {
            PersistentUserInfo persistentInfo = PersistentUserInfo.getPersistentUserInfo(getApplicationContext());
            eventSelect = persistentInfo.getEvent(eventSelectId);
            if (eventSelect != null) {
                if (persistentInfo.getRatedEvents().contains(eventSelect.getId())) {
                    rateButton.setText(getString(R.string.rated_btn));
                }
            }
        }


        if (eventSelect == null) {
            new getEvent().execute(eventSelectId);
            rateButton.setText(getString(R.string.rate_btn));
        } else {
            fillEventInfo(accountType);
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private void fillEventInfo(final String accountType) {
        new isRated().execute();
        new getPhoto().execute();

        new getComments().execute();
        new getUsers().execute();


        final TextView name = findViewById(R.id.eventName);
        TextView date = findViewById(R.id.eventDate);
        TextView descripcion = findViewById(R.id.eventDescContent);
      //  descripcion.setMovementMethod(new ScrollingMovementMethod());
        TextView ubication = findViewById(R.id.eventLocationContent);
        TextView time = findViewById(R.id.eventTime);
        TextView category = findViewById(R.id.category);


        name.setText(eventSelect.getName());
        Date dateF;
        dateF = eventSelect.getEventDate();
        DateFormat df = new SimpleDateFormat(getString(R.string.date_pattern), java.util.Locale.getDefault());
        DateFormat df2 = new SimpleDateFormat(getString(R.string.hour_pattern), java.util.Locale.getDefault());
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
                if (accountType.equals(getString(R.string.usr))) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        ScoreEventDialog dialog = new ScoreEventDialog().newInstance(name.getText().toString(), eventSelect.getId(), rated);
                        dialog.show(fragmentManager, getString(R.string.tag_alert));
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


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    //================================================================================
    // AsyncTasks
    //================================================================================

    private class getPhoto extends AsyncTask<Void, Void, Photo> {

        @Override
        protected Photo doInBackground(Void... voids) {
            PhotoService photoService= new PhotoService();
            Photo eventPhoto = photoService.getPhoto(eventSelect.getEventImage());
            return eventPhoto;
        }

        @Override
        protected void onPostExecute(Photo eventPhoto) {

            byte[] decodedString = Base64.decode(eventPhoto.getEventImage(),Base64.NO_WRAP);
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

        @Override
        protected Void doInBackground(Void... voids) {
            ratings.clear();
            RatingService ratingService = new RatingService();
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


    private class getUsers extends AsyncTask<Void, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(Void... voids) {
            ArrayList<User> users = new ArrayList<>();
            UserService userService = new UserService();

            for (Rating r : ratings) {
                try {
                    users.add(userService.getUser(r.getUserId()));
                } catch (InstanceNotFoundException e) {
                    Log.e(TAG, getString(R.string.existing_id));
                    return users;
                }
            }
            return users;
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            ArrayList<String> names = new ArrayList<String>();
            for (User u: users) {
                names.add(u.getName());
            }
            final ListView listview = findViewById(R.id.ratingList);
            RatingsAdapter adapter = new RatingsAdapter(getApplicationContext(), comments, scores, names);
            listview.setAdapter(adapter);
        }
    }


    private class isRated extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RatingService ratingService = new RatingService();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            rated = ratingService.isRated(currentUser.getUid(), eventSelect.getId());
            return null;
        }
    }


    private class getEvent extends AsyncTask<String, Void, Event> {

        @Override
        protected Event doInBackground(String... ids) {
            String id = ids[0];
            EventService eventService = new EventService();
            return eventService.getEvent(id);
        }

        @Override
        protected void onPostExecute(final Event event) {
            eventSelect = event;
            fillEventInfo(accountType);
        }

    }


}
