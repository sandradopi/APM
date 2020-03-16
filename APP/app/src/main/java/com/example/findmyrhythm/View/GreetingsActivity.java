package com.example.findmyrhythm.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.findmyrhythm.R;

public class GreetingsActivity extends AppCompatActivity implements View.OnClickListener {

    //Widgets
    Button orgLogin, userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        orgLogin = (Button) findViewById(R.id.orgLogin);
        orgLogin.setOnClickListener(this);

        userLogin = (Button) findViewById(R.id.userLogin);
        userLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == orgLogin) {

            //TODO: Intent to the desired Activity. MAYBE PASS A VARIABLE DEFINING THE USER AS ORGANIZATION
            Intent locationIntent = new Intent(this, OrganizerLog.class);
            startActivity(locationIntent);

        } else {

            //TODO: Intent to the desired Activity. MAYBE PASS A VARIABLE DEFINING THE USER AS USER.
            Intent locationIntent = new Intent(this, SetLocationActivity.class);
            startActivity(locationIntent);
        }
    }
}
