package com.example.findmyrhythm.View;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.provider.MediaStore;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.Uri;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.R;
import com.google.gson.Gson;

import android.widget.ImageView;
import java.util.Calendar;
import java.util.Date;


public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Crear Evento";
    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText date, hour, address, maxAttendees, name, price;
    private Spinner genres;
    private Button saveButton;
    Uri imageUri;
    private static final int PICK_IMAGE = 100;
    static final Integer READ_EXST = 0x4;
    ImageView imageView;
    String selectedGenre;
    Date eventDate;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_create_event);

        calendar = Calendar.getInstance();

        // Reference to the different EditText containing the event info
        address = findViewById(R.id.address);
        maxAttendees = findViewById(R.id.max_attendees);
        price = findViewById(R.id.price);
        name = findViewById(R.id.event_name);
        date = findViewById(R.id.day);
        date.setOnClickListener(this);
        hour = findViewById(R.id.hour);
        hour.setOnClickListener(this);

        genres = (Spinner) findViewById(R.id.genre_selection);
        genres.setOnItemSelectedListener(this);
        // Button to confirm the event creation
        saveButton = findViewById(R.id.ok);
        saveButton.setOnClickListener(this);

        // Load picture button
        Button buttonPhoto = findViewById(R.id.button_load_picture);

        /*date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openDialogDate();
            }
        });

        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openDialogTime();
            }
        });*/

        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }



    @Override
    public void onClick(View v) {

        if (v == date)
            openDialogDate();

        else if (v == hour)
            openDialogTime();

        else if( v == saveButton) {
            Calendar currentCalendar = Calendar.getInstance();
            // TODO: CHECK IF DESCRIPTION AND IMAGE EXISTS.
            if (isEmpty(name) || isEmpty(date) || isEmpty(hour) || isEmpty(address) || isEmpty(maxAttendees) || isEmpty(price) || selectedGenre.equals("") || calendar.getTime().compareTo(currentCalendar.getTime()) < 0) {
                Toast.makeText(this, "Please cover every field shown in the screen", Toast.LENGTH_LONG).show();
                return;
            }
            //TODO: INSERT EVENT
            new AddEventTask().execute();
        }
    }

    /**
     * Gets the data of the event and calls to EventService to add the event to the database.
     * Then, changes the activity to the one used to show the event info.
     */
    // ESTA LLAMADA A LA BASE DE DATOS DEBERÍA DE SER ASÍNCRONA.
    /*private void createEvent() {
        // Get the id of the organizer
        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        String organizerId = preferences.getString("fb_id", null);

        // Create the event
        Event event = new Event();
        event.setName(name.getText().toString());
        String dateStr =  date.getText().toString();

        event.setPrice(Integer.parseInt(price.getText().toString()));
        event.setMaxAttendees(Integer.parseInt(maxAttendees.getText().toString()));
        event.setOrganizerId(organizerId);

        // Call to EventService to add the event to the database
        EventService eventService = new EventService();
        eventService.createEvent(event);

        Log.w(TAG, "Se ha creado el evento con éxito");
        Toast.makeText(CreateEventActivity.this, getString(R.string.notiCreationEve),  Toast.LENGTH_SHORT).show();

        String eventJson = (new Gson()).toJson(event);
        // Start the activity to show the event info
        Intent intent = new Intent(CreateEventActivity.this, OrganizerEventInfoActivity.class);
        intent.putExtra("EVENT", eventJson);
        startActivity(intent);
    }*/


    public void openDialogDate(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Por que month of year + 1???
                        //date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        String textdate = dayOfMonth + "/" + monthOfYear + "/" + year;
                        date.setText(textdate);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        Toast.makeText(getApplicationContext(), "hello: " + calendar.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void openDialogTime(){

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        if (hourOfDay == 0 && minute == 0)
                            hour.setText(hourOfDay + "0:0" + minute);

                        else if (hourOfDay == 0)
                            hour.setText(hourOfDay + "0:" + minute);

                        else if (minute == 0)
                            hour.setText(hourOfDay + ":0" + minute);

                        else
                            hour.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void askPermissions(String permission,Integer requestCode){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    private void openGallery() {
        askPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private boolean isEmpty(EditText text) {

        return text.getText().toString().equals("");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedGenre = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing to do.
    }

    private class AddEventTask extends AsyncTask<Void, Void, Event> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Event doInBackground(Void... voids) {
            // Get the id of the organizer
            SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            final String organizerId = preferences.getString("fb_id", null);

            eventDate = calendar.getTime();
            EventService service = new EventService();

            final Event event = new Event(name.getText().toString(), eventDate, address.getText().toString(), selectedGenre, organizerId, maxAttendees.getText().toString(), price.getText().toString(), "", "");
            service.createEvent(event);

            return event;
        }

        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            Toast.makeText(getApplicationContext(), "Concierto creado con éxito!", Toast.LENGTH_LONG).show();
            String eventJson = (new Gson()).toJson(event);
            // Start the activity to show the event info
            Intent intent = new Intent(CreateEventActivity.this, OrganizerEventInfoActivity.class);
            intent.putExtra("EVENT", eventJson);
            startActivity(intent);
            finish();
        }
    }

}

