package com.example.findmyrhythm.View;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.TextView;
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
import com.example.findmyrhythm.Model.Utils.GenericUtils;
import com.example.findmyrhythm.Model.Utils.GeoUtils;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.Photo;
import com.example.findmyrhythm.Model.PhotoService;
import com.example.findmyrhythm.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Crear Evento";

    // Request codes for activities started with startActivityForResult
    private static final int GET_IMG_FROM_GALLERY = 3;
    private static final int SET_LOCATION = 2;
    private static final int PICK_IMAGE = 100;
    private static final String NO_IMAGE = "no_image";

    private String DEFAULT_IMAGE_ID = "-M6PpLTdOhq0sFbyNrHz";
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int STORAGE_PERMISSION_CODE = 1896;
//    private static final int LOCATION_PERMISSION_CODE = 7346;

    //    private boolean useUserDefaultLocation;
    private boolean modifyEvent = false;

    //    private int NUM_GENRES = 9;
    private EditText date, hour, maxAttendees, name, price, description;
    private Button exploreMapButton;
    private TextView addressTextView;
    private Button saveButton;
    private Button uploadPhotoButton;
    private Spinner genresSpinner;
    private Photo photoOriginal;
    private String photoOriginalId;
    private Address eventCompleteAddress;
    private GeoUtils geoUtils;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private Uri imageUri;
    private String selectedGenre = "";
    private Photo photo;
    private String photoId = "";
    private Bitmap imageBitmap = null;

    private Date eventDate;
    private Calendar calendar;

    private EventService eventService = new EventService();
    private PhotoService photoService = new PhotoService();

    private Event eventSelect;
    private PersistentOrganizerInfo persistentOrganizerInfo;
//    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_create_event);

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        useUserDefaultLocation = true;
        final Locale locale = Locale.getDefault(); // new Locale("es", "ES");
        calendar = Calendar.getInstance();
        addressTextView = findViewById(R.id.address);
        exploreMapButton = findViewById(R.id.exploreMap);
        exploreMapButton.setText("Explorar en el mapa");

        // Reference to the different EditText containing the event info
        maxAttendees = findViewById(R.id.max_attendees);
        price = findViewById(R.id.price);
        name = findViewById(R.id.event_name);
        date = findViewById(R.id.day);
        date.setOnClickListener(this);
        hour = findViewById(R.id.hour);
        description = findViewById(R.id.description);
        hour.setOnClickListener(this);
        genresSpinner = findViewById(R.id.genre_selection);
        genresSpinner.setOnItemSelectedListener(this);
        // Button to confirm the event creation
        saveButton = findViewById(R.id.ok);
        saveButton.setOnClickListener(this);
        // Load picture button
        uploadPhotoButton = findViewById(R.id.button_load_picture);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Crear evento");
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStoragePermission();
            }
        });
        uploadPhotoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlert();
                //Toast.makeText(CreateEventActivity.this, "Borrar foto", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        // boolean isPresent = Geocoder.isPresent();

        //No tengo claro si se deberia hacer así
        final String eventSelectId = getIntent().getStringExtra("EVENT");
        if (eventSelectId != null && !eventSelectId.isEmpty()) {
            toolbarTitle.setText("Editar evento");
            modifyEvent = true;
            persistentOrganizerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());
            eventSelect = persistentOrganizerInfo.getEvent(eventSelectId);
            price.setText(eventSelect.getPrice());
            name.setText(eventSelect.getName());

            eventDate= eventSelect.getEventDate();


            DateFormat df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());
            DateFormat df2 = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            date.setText(df.format(eventDate));
            hour.setText(df2.format(eventDate));
            maxAttendees.setText(eventSelect.getMaxAttendees());
            description.setText(eventSelect.getDescription());
            selectedGenre = eventSelect.getGenre();

            Geocoder geocoder = new Geocoder(this, locale);

            HashMap<String, Object> completeAddressDict = eventSelect.getCompleteAddress();
            List<Address> addresses = null;
            try {
                Double latitude = (Double) Objects.requireNonNull(completeAddressDict.get("latitude"));
                Double longitude = (Double) Objects.requireNonNull(completeAddressDict.get("longitude"));
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                for (Address address : addresses) {
                    eventCompleteAddress = address;
                    addressTextView.setText(GeoUtils.getAddressString(eventCompleteAddress));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            saveButton.setText("MODIFICAR");
            uploadPhotoButton.setText("Cambiar cartel");
            new getPhoto().execute();
            String[] genresArr = getResources().getStringArray(R.array.categories);
            genresSpinner.setSelection(Arrays.asList(genresArr).indexOf(selectedGenre));

        } else {
            // Set organizer location as default location
            SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            String organizerLocationName = preferences.getString("location", null);

            addressTextView.setText(organizerLocationName);
            // Use GeoUtils to obtain the complete address from location name
            geoUtils = new GeoUtils(this, Locale.getDefault());
            eventCompleteAddress = geoUtils.getAddressFromLocationName(organizerLocationName);
        }

        exploreMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Address organizerAddress;
                    // Pass event's address in bundle. If it does not exist, initialize an address at (0,0).
                    if (eventCompleteAddress == null) {
                        organizerAddress = new Address(locale);
                        organizerAddress.setLatitude(0.0);
                        organizerAddress.setLongitude(0.0);
                    }
                    startActivityForResult(new Intent(getApplicationContext(), SelectAddressOnMapActivity.class).putExtra("organizerAddress", eventCompleteAddress), SET_LOCATION);
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                }
            }
        });

    }


//    @Override
//    public void onStart() {
//        super.onStart();
//
//        if (useUserDefaultLocation) {
//            // Retrieve organizer's location name
//            SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
//            String organizerLocationName = preferences.getString("location", null);
//
//            addressButton.setText(organizerLocationName);
//            // Use GeoUtils to obtain the complete address from location name
//            geoUtils = new GeoUtils(this, Locale.getDefault());
//            eventCompleteAddress = geoUtils.getAddressFromLocationName(organizerLocationName);
//            // Set the default text location in the view
////            // Initialize the new location through the Location Manager
////            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
////            Criteria crta = new Criteria();
////            String provider = lm.getBestProvider(crta, true);
////            organizerLocation = new Location(provider);
////            // Set location coordinates which had been obtained before and are stored in
////            // the complete address
////            organizerLocation.setLatitude(organizerCompleteAddress.getLatitude());
////            organizerLocation.setLongitude(organizerCompleteAddress.getLongitude());
//        }
//    }


    @Override
    public void onClick(View view) {

        if (view == date) {
            openDialogDate();
        } else if (view == hour) {
            openDialogTime();
        } else if (view == saveButton) {
            Calendar currentCalendar = Calendar.getInstance();
            Date eventDate;
            if (eventSelect != null) {
                eventDate = eventSelect.getEventDate();
            } else {
                eventDate = calendar.getTime();
            }
            boolean isAnyFiledEmpty = GenericUtils.isEmpty(name) || GenericUtils.isEmpty(date) || GenericUtils.isEmpty(hour)
                    || GenericUtils.isEmpty(maxAttendees) || GenericUtils.isEmpty(price) || GenericUtils.isEmpty(description)
                    || GenericUtils.isEmpty(addressTextView) || selectedGenre.equals("")
                    || eventDate.compareTo(currentCalendar.getTime()) < 0;

            if (isAnyFiledEmpty) {
                Toast.makeText(this, "Please cover every field shown in the screen", Toast.LENGTH_LONG).show();
                return;
            }

            if (modifyEvent) {
                new ModifyEventTask().execute();
            } else {
                // TODO: CHECK IF DESCRIPTION AND IMAGE EXISTS.
                new AddEventTask().execute();
            }
        }

    }

    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
        builder.setMessage("Desea quitar la imagen seleccionada?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imageBitmap=null;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog ad= builder.create();
        ad.show();
    }

    private class getPhoto extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            photo = photoService.getPhoto(eventSelect.getEventImage());
            photoOriginal= photo;
            photoId = photo.getId();
            photoOriginalId = photoId;
            return null;
        }
    }


    public void openDialogDate() {

        // Get Current Date
        final Calendar currentCalendar = Calendar.getInstance();
        mYear = currentCalendar.get(Calendar.YEAR);
        mMonth = currentCalendar.get(Calendar.MONTH);
        mDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Por que month of year + 1???
                        //date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        String textdate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
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
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        if (hourOfDay == 0 && minute == 0) {
                            hour.setText(hourOfDay + "0:0" + minute);
                        } else if (hourOfDay == 0) {
                            hour.setText(hourOfDay + "0:" + minute);
                        } else if (minute == 0) {
                            hour.setText(hourOfDay + ":0" + minute);
                        } else {
                            hour.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    public void getImageUri() {
        //MediaStore.Images.Media.INTERNAL_CONTENT_URI
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(gallery, GET_IMG_FROM_GALLERY);
    }


    private void getStoragePermission() {
        String[] permissions = {READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getImageUri();
            return;
        }

        ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {

            // If granResults lenght is > 0 is that something was granted
            if (grantResults.length > 0) {
                for (int item : grantResults)
                    if (item != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                getImageUri();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SET_LOCATION) {
                // Update the address to the new address selected from the map
                eventCompleteAddress = data.getParcelableExtra("pickedAddress");
                addressTextView.setText(GeoUtils.getAddressString(eventCompleteAddress));
            } else if (requestCode == GET_IMG_FROM_GALLERY) {
                if (data != null) {
                    imageUri = data.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        getRealPathFromURI(imageUri);
                        /*Cursor returnCursor = getContentResolver().query(imageUri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        returnCursor.moveToFirst();
                        buttonPhoto.setText(returnCursor.getString(nameIndex));*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        Button buttonPhoto = findViewById(R.id.button_load_picture);
        String[] url = cursor.getString(column_index).split("/");
        buttonPhoto.setText(url[url.length - 1]);
        return cursor.getString(column_index);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedGenre = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing to do.
    }

    private String createEncodedPhoto(Bitmap bitmapImage) {

        Photo photo;
        String photoId;
        String bitmapEncoded;
        byte[] byteArray;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmapImage.compress(Bitmap.CompressFormat.PNG, 10, stream);
        byteArray = stream.toByteArray();
        bitmapEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        bitmapImage.recycle();
        photo = new Photo(bitmapEncoded);
        photoId = photoService.createPhoto(photo);

        return photoId;
    }
    private void modifyEncodedPhoto(Bitmap bitmapImage) {

        Photo photo;
        String bitmapEncoded;
        byte[] byteArray;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmapImage.compress(Bitmap.CompressFormat.PNG, 10, stream);
        byteArray = stream.toByteArray();
        bitmapEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        bitmapImage.recycle();
        photo = photoOriginal;
        photo.setEventImage(bitmapEncoded);
        photoService.updatePhoto(photo);

    }
    private void deletePhoto(Photo photo){
        photoService.deletePhoto(photo.getId());
        Photo defaultP = photoService.getPhoto(DEFAULT_IMAGE_ID);
        if (defaultP != null){
            photoId= DEFAULT_IMAGE_ID;
        }
        else{
            Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.logo_white);
            photoId = createEncodedPhoto(icon);
            DEFAULT_IMAGE_ID = photoId;
        }
    }

    private Event makeEvent(String organizerId) {
        //bitmapEncoded = NO_IMAGE;
        //String path= imageUri.getEncodedPath();
        // creating a My HashTable Dictionary
        HashMap<String, Object> addressDict = new HashMap<>();

        // Using a few dictionary Class methods
        // using put method
        addressDict.put("province", eventCompleteAddress.getSubAdminArea());
        addressDict.put("full_address", GeoUtils.getAddressString(eventCompleteAddress)); // Double
        addressDict.put("latitude", eventCompleteAddress.getLatitude());
        addressDict.put("longitude", eventCompleteAddress.getLongitude());

        Log.e(TAG, (new GeoLocation(eventCompleteAddress.getLatitude(), eventCompleteAddress.getLongitude())).toString());;

        Event event = new Event(name.getText().toString(), eventDate, addressTextView.getText().toString(),
                selectedGenre, organizerId, maxAttendees.getText().toString(), price.getText().toString(),
                description.getText().toString(), photoId, addressDict);

        return event;
    }


    private class AddEventTask extends AsyncTask<Void, Void, Event> {

        @Override
        protected Event doInBackground(Void... voids) {
            // Get the id of the organizer
            SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            final String organizerId = preferences.getString("fb_id", null);

            eventDate = calendar.getTime();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (imageBitmap != null) {
                photoId = createEncodedPhoto(imageBitmap);
            } else {
                Photo defaultP = photoService.getPhoto(DEFAULT_IMAGE_ID);
                if (defaultP != null){
                    photoId= DEFAULT_IMAGE_ID;
                }
                else{
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.logo_white);
                    photoId = createEncodedPhoto(icon);
                    DEFAULT_IMAGE_ID = photoId;
                }

            }

            Event event = makeEvent(organizerId);

            eventService.createEvent(event);

            //event.setEventImage(path);
            final PersistentOrganizerInfo persistentOrganizerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());

            persistentOrganizerInfo.addEvent(getApplicationContext(), event);
            return event;
        }

        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            Toast.makeText(getApplicationContext(), "¡Concierto creado con éxito!", Toast.LENGTH_LONG).show();
            String eventJson = (new Gson()).toJson(event);
            // Start the activity to show the event info
            Intent intent = new Intent(CreateEventActivity.this, OrganizerEventInfoActivity.class);
            intent.putExtra("EVENT", event.getId());
            startActivity(intent);
            finish();
        }
    }


    private class ModifyEventTask extends AsyncTask<Void, Void, Event> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Event doInBackground(Void... voids) {
            // Get the id of the organizer
            SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            final String organizerId = preferences.getString("fb_id", null);

            //bitmapEncoded = NO_IMAGE;
            //String path= imageUri.getEncodedPath();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (imageBitmap != null &&  !photoOriginalId.equals(DEFAULT_IMAGE_ID)) {
                modifyEncodedPhoto(imageBitmap);
            }
            else if (imageBitmap !=null){
                createEncodedPhoto(imageBitmap);
            }
            else{
                deletePhoto(photo);
            }

            Event event = makeEvent(organizerId);

            Event e = persistentOrganizerInfo.modifyEvent(getApplicationContext(), eventSelect, event);
            eventService.modifyEvent(e);

            return e;

        }

        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            Toast.makeText(getApplicationContext(), "¡Concierto modificado con éxito!", Toast.LENGTH_LONG).show();
            String eventJson = (new Gson()).toJson(event);
            // Start the activity to show the event info
            Intent intent = new Intent(CreateEventActivity.this, OrganizerEventInfoActivity.class);
            intent.putExtra("EVENT", event.getId());
            startActivity(intent);
            finish();
        }
    }
}




