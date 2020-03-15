package com.example.findmyrhythm.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.findmyrhythm.R;

public class UserLog extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Creación Usuario";
    EditText name, nickname, email, biography, birthDate;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        name = (EditText) findViewById(R.id.userName);
        nickname = (EditText) findViewById(R.id.userNickname);
        email = (EditText) findViewById(R.id.userEmail);
        biography = (EditText) findViewById(R.id.userBiography);
        birthDate = (EditText) findViewById(R.id.userBirthdate);

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        /*TODO: Check if every field are covered.
           If not ask for the user to cover them.
           Insert the new user in the DataBase as USER With all the important information
           Create the intent to go to the next Activity
         */

//        if (isEmpty(name) || isEmpty(nickname) || isEmpty(email) || isEmpty(biography) || isEmpty(birthDate)) {
//            Toast.makeText(this, "Please cover every field shown in the screen", Toast.LENGTH_LONG).show();
//            return;
//        }

        //TODO: Introduce into database by getting the value of every field. Check Android Service.

        //TODO: Intent to new Activity
        Log.w(TAG, "Creación de la cuenta del usuario");
        Toast.makeText(UserLog.this, getString(R.string.notiCreation),  Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(this, PerfilUsuario.class);
        startActivity(intent);
    }

    private boolean isEmpty(EditText text) {

        return text.getText().toString().equals("");
    }
}
