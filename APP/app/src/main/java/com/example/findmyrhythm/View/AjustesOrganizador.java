package com.example.findmyrhythm.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.findmyrhythm.R;

public class AjustesOrganizador extends MenuDrawerActivity {
    private static final String TAG = "Ajustes Organizador";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes_organizador);

        setMenuItemChecked(R.id.nav_settings);

        Button savebutton = findViewById(R.id.button);
        savebutton.setClickable(true);
        savebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.w(TAG, "Ha clickeado en guardar ajustes");
                Toast.makeText(AjustesOrganizador.this, getString(R.string.guardar),  Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AjustesOrganizador.this, OrgProfile.class);
                startActivity(intent);
            }
        });

        Switch notification = findViewById(R.id.switch1);
        notification.setClickable(true);
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.w(TAG, "Ha activado las notificaciones");
                    Toast.makeText(AjustesOrganizador.this, getString(R.string.noti),  Toast.LENGTH_SHORT).show();
                }else{
                    Log.w(TAG, "Ha desactivado las notificaciones");
                    Toast.makeText(AjustesOrganizador.this, getString(R.string.desnoti),  Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

}
