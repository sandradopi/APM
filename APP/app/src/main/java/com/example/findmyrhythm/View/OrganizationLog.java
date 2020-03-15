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

public class OrganizationLog extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Creación Organizador";
    EditText name, nickname, email, biography, location;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_log);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        name = (EditText) findViewById(R.id.orgName);
        nickname = (EditText) findViewById(R.id.orgNickName);
        email = (EditText) findViewById(R.id.orgEmail);
        biography = (EditText) findViewById(R.id.orgBiography);
        location = (EditText) findViewById(R.id.orgLocation);

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        /*TODO: Check if every field are covered.
         * If not ask for the user to cover them.
         * Insert the new user in the DataBase as ORGANIZATION with all the important information
         * Create the intent to go to the next Activity
         */

        /*TODO: PROBABLY IT WOULD BE WISE TO GET THE LOCATION NOT BY GETTING THE TEXT OF THE EDITTEXT
         * BUT BY INTRODUCING THE CITY, PROVINCE, STREET ETC SEPARATED BY USING THE GOOGLE MAP APP.
         * IT WOULD MAKE EASIER THE CHECK OF EVENTS TO USERS AFTERWARDS.
         */

//        if (isEmpty(name) || isEmpty(nickname) || isEmpty(email) || isEmpty(biography) || isEmpty(location)) {
//            Toast.makeText(this, "Porfavor rellene todos los campos", Toast.LENGTH_LONG).show();
//            return;
//        }

        //TODO: Introduce into database by getting the value of every field. Check Android Service.

        //TODO: Intent to new Activity
        Log.w(TAG, "Creación de la cuenta del organizador");
        Toast.makeText(OrganizationLog.this, getString(R.string.notiCreation),  Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, OrgProfile.class);
        startActivity(intent);
    }

    private boolean isEmpty(EditText text) {

        return text.getText().toString().equals("");
    }
}
