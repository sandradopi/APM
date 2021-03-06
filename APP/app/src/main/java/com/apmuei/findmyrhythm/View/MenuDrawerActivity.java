package com.apmuei.findmyrhythm.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apmuei.findmyrhythm.Model.Exceptions.Assert;
import com.apmuei.findmyrhythm.Model.IOFiles;
import com.apmuei.findmyrhythm.R;
import com.facebook.login.LoginManager;
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

import java.io.FileNotFoundException;


public abstract class MenuDrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    protected DrawerLayout fullLayout;
    protected LinearLayout actContent;
    protected DrawerLayout drawerLayout;
    protected int menuItemID, prevMenuItemID;
    private Menu menu;
    NavigationView navigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private String provider = null;

    @Override
    public void setContentView(final int layoutResID) {
        // Base layout
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);
        actContent = (LinearLayout) fullLayout.findViewById(R.id.home_content);

        // Setting the content of layout provided to the act_content frame
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigation_view);

        navigationView.getMenu().clear();

        inflateMenu(navigationView);

        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        drawerLayout.addDrawerListener(this);

        View header = navigationView.getHeaderView(0);
        header.findViewById(R.id.header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MenuDrawerActivity.this, getString(R.string.title_click),
                        //Toast.LENGTH_SHORT).show();
            }
        });


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Assert.assertNotNull(currentUser, "No FirebaseUser found");
        for (UserInfo user: currentUser.getProviderData()) {
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


        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        String userName = preferences.getString("name", null);
        String userEmail = preferences.getString("email", null);
        navUsername.setText(userName);
        navEmail.setText(userEmail);


        ImageView profilePicture = headerView.findViewById(R.id.profile_picture);
        try {
            Bitmap bmp2 = IOFiles.loadImageFromStorage(getApplicationContext());
            profilePicture.setImageBitmap(bmp2);
        } catch (FileNotFoundException e) {
            profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
        }

    }


    protected abstract void inflateMenu(NavigationView navigationView);

    @Override
    public abstract boolean onNavigationItemSelected(@NonNull MenuItem menuItem);


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setMenuItemChecked(int id) {
        menu.findItem(menuItemID).setChecked(false);
        prevMenuItemID = menuItemID;
        menuItemID = id;

        menu.findItem(id).setChecked(true);
    }



    public void showExitDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.menu_logout);
        alertDialog.setMessage(getString(R.string.ask_logout));
        final String finalProvider = provider;
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sí",
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
                                            // Flags for start a new activity and clear all stack
                                            Intent intent = new Intent(MenuDrawerActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    });
                        } else if (finalProvider.equals("facebook.com")) {
                            LoginManager.getInstance().logOut();
                            Intent intent = new Intent(MenuDrawerActivity.this, LoginActivity.class);
                            // Flags for start a new activity and clear all stack
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString("account_type", null);

                        editor.apply();


                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setMenuItemChecked(prevMenuItemID);
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
