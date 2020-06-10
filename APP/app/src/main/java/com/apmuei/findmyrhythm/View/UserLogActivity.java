package com.apmuei.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.EventService;
import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
import com.apmuei.findmyrhythm.Model.IOFiles;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.Model.UserService;
import com.apmuei.findmyrhythm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UserLogActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Creación Usuario";

    EditText name, nickname, email, biography;
    TextView  birthDate;
    FloatingActionButton next;
    FirebaseUser currentUser;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);

        ActionBar actionBar = getSupportActionBar();
        Assert.assertNotNull(actionBar, "No ActionBar found");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.layout_actionbar_empty);
        actionBar.setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.userName);
        nickname = findViewById(R.id.userNickname);
        email = findViewById(R.id.userEmail);
        biography = findViewById(R.id.userBiography);
        birthDate = findViewById(R.id.userBirthdate);
        birthDate.setOnClickListener(this);

        next = findViewById(R.id.next);
        next.setOnClickListener(this);

        // Get the user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Assert.assertNotNull(currentUser, "No user found");
        name.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());

    }

    public void openDialogDate() throws ParseException {
        int mYear, mMonth, mDay;
        calendar = Calendar.getInstance();

        if(birthDate.getText().toString().equals("")) {
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

        }
        else{
            DateFormat dateFormat=new SimpleDateFormat(getString(R.string.date_pattern));
            Date date = dateFormat.parse(birthDate.getText().toString());
            calendar.setTime(date);
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
        }



        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        Date fecha = new Date(year, monthOfYear, dayOfMonth);
                        DateFormat df = new SimpleDateFormat(getString(R.string.date_pattern), java.util.Locale.getDefault());
                        birthDate.setText(df.format(fecha));
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(mYear - 100);
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view == birthDate) {
            try {
                openDialogDate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else  {

        // Check if every field is covered
        if (isEmpty(name) || isEmpty(nickname) || isEmpty(email) || isEmpty(biography) || birthDate == null) {
            // If not ask for the user to cover them
            Toast.makeText(this, getString(R.string.cover_fields), Toast.LENGTH_LONG).show();
            return;
        }

        // Insert the new user in the DataBase as USER With all the important information

        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("fb_name", currentUser.getDisplayName());
        editor.putString("fb_email", currentUser.getEmail());
        editor.putString("fb_id", currentUser.getUid());

        editor.putString("name", name.getText().toString());
        editor.putString("email", email.getText().toString());
        editor.putString("nickname", nickname.getText().toString());
        editor.putString("account_type", "user");

        editor.apply(); // or apply


        ArrayList<String> genres, locations;
        Bundle bundle = getIntent().getExtras();
        Assert.assertNotNull(bundle, "No extras found");
        locations = bundle.getStringArrayList(getString(R.string.locationsListID));
        genres = bundle.getStringArrayList(getString(R.string.genresListID));

        PersistentUserInfo persistentUserInfo = new PersistentUserInfo(currentUser.getUid(),name.getText().toString(),
                nickname.getText().toString(),email.getText().toString(), biography.getText().toString(),
                birthDate.getText().toString(), locations, genres,  new ArrayList<Event>(), new ArrayList<String>());

        PersistentUserInfo.setPersistentUserInfo(getApplicationContext(), persistentUserInfo);

        new CreateUserTask().execute();

        EventService service = new EventService();
        service.subscribeEventNotificationListener(this, currentUser.getUid());

        Toast.makeText(UserLogActivity.this, getString(R.string.notiCreation),  Toast.LENGTH_SHORT).show();

        // Create the intent to go to the next Activity

        Intent intent = new Intent(this, UserProfileActivity.class);
        // Flags for start a new activity and clear all stack
        // This prevents users from going back once they have registered
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        }
    }


    private boolean isEmpty(EditText text) {
        return text.getText().toString().equals("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() { }


    //================================================================================
    // AsyncTasks
    //================================================================================

    private class CreateUserTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            ArrayList<String> genres, locations;
            Bundle bundle = getIntent().getExtras();
            Assert.assertNotNull(bundle, "No extras found");
            locations = bundle.getStringArrayList(getString(R.string.locationsListID));
            genres = bundle.getStringArrayList(getString(R.string.genresListID));

            UserService userService = new UserService();
            userService.createUser(currentUser.getUid(), name.getText().toString(), nickname.getText().toString(),
                    email.getText().toString(), biography.getText().toString(), birthDate.getText().toString(),
                    locations, genres);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            IOFiles.downloadProfilePicture(currentUser, getApplicationContext());
        }
    }
}
