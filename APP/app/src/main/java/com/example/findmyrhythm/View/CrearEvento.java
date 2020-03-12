package com.example.findmyrhythm.View;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.findmyrhythm.R;

import java.util.Calendar;

public class CrearEvento extends AppCompatActivity {

    private static final String TAG = "Crear Evento";
    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText fecha,hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        setContentView(R.layout.activity_crearevento);


        fecha = (EditText) findViewById(R.id.dia);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openDialogDate();

            }
        });

        hora = (EditText) findViewById(R.id.hora);
        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openDialogTime();

            }
        });

        Button savebutton = findViewById(R.id.ok);
        savebutton.setClickable(true);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Se ha creado el evento con éxito");
                Toast.makeText(CrearEvento.this, getString(R.string.notiCreationEve),  Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CrearEvento.this, PerfilEventoOrganizador.class);
                startActivity(intent);
            }
        });


    }

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

                        fecha.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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

                        hora.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

