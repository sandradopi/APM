package com.example.findmyrhythm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class OrgProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_profile);

        ImageView infoButton = findViewById(R.id.info);
        infoButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                DialogInfoOrg dialogo = new DialogInfoOrg();
                dialogo.show(fragmentManager, "tagAlerta");
            }
        });


    }
}
