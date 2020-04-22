package com.example.findmyrhythm.View;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.Uri;

import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Utils.GeoUtils;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.Photo;
import com.example.findmyrhythm.Model.PhotoService;
import com.example.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;


public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Crear Evento";
    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText date, hour, maxAttendees, name, price, description;
    private Button address, exploreMapButton;
    private Address eventCompleteAddress;
    private Location organizerLocation;
    private GeoUtils geoUtils;
    private Boolean userDefaultLocation;
    private Spinner genres;
    private Button saveButton;
    Uri imageUri;
    private static final int PICK_IMAGE = 100;
    public static final String NO_IMAGE = "no_image";
    String selectedGenre = "";

    Date eventDate;
    Calendar calendar;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int STORAGE_PERMISSION_CODE = 1896;
    Boolean mStoragePermissionGranted = false;
    public static final int GET_FROM_GALLERY = 3;
    Bitmap imageBitmap = null;
    String bitmapEncoded = "";
    byte[] byteArray;

    EventService eventService = new EventService();
    PhotoService photoService = new PhotoService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_create_event);

        userDefaultLocation = true;
        calendar = Calendar.getInstance();
        address = (Button) findViewById(R.id.address);
        exploreMapButton = (Button) findViewById(R.id.exploreMap);
        exploreMapButton.setText("Explorar en el mapa");
        exploreMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivityForResult(new Intent(getApplicationContext(), SearchOrganizerLocation.class).putExtra("lastLocation", organizerLocation), 2);
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                }
            }
        });
        // Reference to the different EditText containing the event info
        maxAttendees = findViewById(R.id.max_attendees);
        price = findViewById(R.id.price);
        name = findViewById(R.id.event_name);
        date = findViewById(R.id.day);
        date.setOnClickListener(this);
        hour = findViewById(R.id.hour);
        description = findViewById(R.id.description);
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
                openDialogTime();
            }
        });

        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
            }
        });*/

        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStoragePermission();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if (userDefaultLocation) {
            // Retrieve organizer's location name
            SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            String organizerLocationName = preferences.getString("location", null);
            // Use GeoUtils to obtain the complete address from location name
            geoUtils = new GeoUtils(this, Locale.getDefault());
            Address organizerCompleteAddress = geoUtils.getAddressFromLocationName(organizerLocationName);
            eventCompleteAddress = organizerCompleteAddress;
            // Set the default text location in the view
            address.setText(organizerLocationName);
            // Initialize the new location through the Location Manager
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria crta = new Criteria();
            String provider = lm.getBestProvider(crta, true);
            organizerLocation = new Location(provider);
            // Set location coordinates which had been obtained before and are stored in
            // the complete address
            organizerLocation.setLatitude(organizerCompleteAddress.getLatitude());
            organizerLocation.setLongitude(organizerCompleteAddress.getLongitude());
        }
    }

    @Override
    public void onClick(View v) {

        if (v == date)
            openDialogDate();

        else if (v == hour)
            openDialogTime();

        else if (v == saveButton) {
            Calendar currentCalendar = Calendar.getInstance();
            // TODO: CHECK IF DESCRIPTION AND IMAGE EXISTS.
            if (isEmpty(name) || isEmpty(date) || isEmpty(hour) || isEmpty(maxAttendees) || isEmpty(price) || isEmpty(description) || address.getText().toString().isEmpty() || imageBitmap == null || selectedGenre.equals("") || calendar.getTime().compareTo(currentCalendar.getTime()) < 0) {
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
    public void openDialogDate() {

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
                        String textdate = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                        Date fecha = new Date(year, monthOfYear, dayOfMonth);
                        DateFormat df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());
                        date.setText(df.format(fecha));
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void openDialogTime() {

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

    public void getImageUri() {

        //MediaStore.Images.Media.INTERNAL_CONTENT_URI
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(gallery, GET_FROM_GALLERY);

    }

    private void getStoragePermission() {

        String[] permissions = {READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            mStoragePermissionGranted = true;
            getImageUri();
            return;
        }

        ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mStoragePermissionGranted = false;

        if (requestCode == STORAGE_PERMISSION_CODE) {

            // If granResults lenght is > 0 is that something was granted
            if (grantResults.length > 0) {
                for (int item : grantResults)
                    if (item != PackageManager.PERMISSION_GRANTED) {
                        mStoragePermissionGranted = false;
                        return;
                    }

                mStoragePermissionGranted = true;
                getImageUri();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                userDefaultLocation = false;
                Location newLocation = data.getParcelableExtra("pickedLocation");
                geoUtils = new GeoUtils(this, Locale.getDefault());
                eventCompleteAddress = geoUtils.getAddressFromLocation(newLocation);
                address.setText(eventCompleteAddress.getLocality());
            }
        } else if (resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            if (imageBitmap != null) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                System.out.println(imageBitmap.getByteCount());
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
                System.out.println(imageBitmap.getByteCount());
                byteArray = stream.toByteArray();
                bitmapEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                imageBitmap.recycle();
            } else
                bitmapEncoded = NO_IMAGE;
            //bitmapEncoded = NO_IMAGE;
            //String path= imageUri.getEncodedPath();
            final Photo photo = new Photo (bitmapEncoded);
            String photoId = photoService.createPhoto(photo);
            // creating a My HashTable Dictionary
            HashMap<String, String> addressDict = new HashMap<>();

            // Using a few dictionary Class methods
            // using put method
            addressDict.put("province", eventCompleteAddress.getSubAdminArea());
            addressDict.put("full_address", GeoUtils.getAddressString(eventCompleteAddress)); // Double
            addressDict.put("latitude", String.valueOf(eventCompleteAddress.getLatitude()));
            addressDict.put("longitude", String.valueOf(eventCompleteAddress.getLongitude()));

            final Event event = new Event(name.getText().toString(), eventDate, address.getText().toString(),
                    selectedGenre, organizerId, maxAttendees.getText().toString(), price.getText().toString(),
                    description.getText().toString(), photoId, addressDict);
            eventService.createEvent(event);
            //event.setEventImage(path);
            final PersistentOrganizerInfo persistentOrganizerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());

            persistentOrganizerInfo.addEvent(getApplicationContext(), event);
            return event;
        }

        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            Toast.makeText(getApplicationContext(), "Concierto creado con éxito!", Toast.LENGTH_LONG).show();
            String eventJson = (new Gson()).toJson(event);
            // Start the activity to show the event info
            Intent intent = new Intent(CreateEventActivity.this, OrganizerEventInfoActivity.class);
            intent.putExtra("EVENT", event.getId());
            startActivity(intent);
            finish();
        }
    }
}




