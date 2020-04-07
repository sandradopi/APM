package com.example.findmyrhythm.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;


public class MenuDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {

    protected DrawerLayout fullLayout;
    protected LinearLayout actContent;
    private DrawerLayout drawerLayout;
    private int menuItemID;
    private Menu menu;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private String provider = null;

    @Override
    public void setContentView(final int layoutResID) {
        // Your base layout here
        fullLayout= (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);
        actContent= (LinearLayout) fullLayout.findViewById(R.id.home_content);

        // Setting the content of layout your provided to the act_content frame
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        drawerLayout.addDrawerListener(this);

        View header = navigationView.getHeaderView(0);
        header.findViewById(R.id.header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuDrawerActivity.this, getString(R.string.title_click),
                        Toast.LENGTH_SHORT).show();
            }
        });


        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            provider = user.getProviderId();
            if (provider.equals("google.com")) {
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            }
        }

//        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {...}

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.header_title);
        TextView navEmail = headerView.findViewById(R.id.header_email);
//        navUsername.setText(name);
//        navEmail.setText(email);


        try {
            JSONObject jsonInfo = IOFiles.readInfoJSON(getPackageName());
            String userName = jsonInfo.getString("name");
            String userEmail = jsonInfo.getString("email");
            navUsername.setText(userName);
            navEmail.setText(userEmail);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        ImageView profilePicture = headerView.findViewById(R.id.profile_picture);
        try {
            Bitmap bmp2 = IOFiles.loadImageFromStorage(getApplicationContext());
            profilePicture.setImageBitmap(bmp2);
        } catch (FileNotFoundException e) {
            profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
        }

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setMenuItemChecked(int id) {
        menu.findItem(id).setChecked(true);
        menuItemID = id;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        // check if the current activity is the same as the one selected
        if (menuItemID != menuItem.getItemId()) {
            switch (menuItem.getItemId()) {
                case R.id.nav_profile:
                    startActivity(new Intent(this, UserProfileActivity.class));
                    break;
                case R.id.nav_recommended:
                    startActivity(new Intent(this, RecommendedEventsActivity.class));
                    break;
                case R.id.nav_search:
                    startActivity(new Intent(this, SearchEventsActivity.class));
                    break;
                case R.id.nav_notifications:
                    // TODO: Implementar la función de visualización de las notificaciones
                    Toast.makeText(MenuDrawerActivity.this,
                            "Actividad de Notificaciones de los eventos por los que has mostrado interés.",
                            Toast.LENGTH_LONG).show();
                    break;
                case R.id.nav_settings:
                    startActivity(new Intent(this, UserSettingsActivity.class));
                    break;
                case R.id.nav_logout:
                    showExitDialog();
                    break;
                default:
                    throw new IllegalArgumentException("menu option not implemented!!");
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    private void showExitDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Cerrar sesión");
        alertDialog.setMessage("¿Seguro que quieres cerrar sesión?");
        final String finalProvider = provider;
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Sí",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        // Google sign out
                        if (finalProvider.equals("google.com")) {
                            mGoogleSignInClient.signOut().addOnCompleteListener(MenuDrawerActivity.this,
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(MenuDrawerActivity.this, MainActivityAlt.class));
                                        }
                                    });
                        }

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {
        //cambio en la posición del drawer
    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        //el drawer se ha abierto completamente
        // Toast.makeText(this, getString(R.string.navigation_drawer_open),
        //        Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        //el drawer se ha cerrado completamente
    }

    @Override
    public void onDrawerStateChanged(int i) {
        //cambio de estado, puede ser STATE_IDLE, STATE_DRAGGING or STATE_SETTLING
    }

}
