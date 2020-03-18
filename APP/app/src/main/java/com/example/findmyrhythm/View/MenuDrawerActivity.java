package com.example.findmyrhythm.View;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyrhythm.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class MenuDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {

    protected DrawerLayout fullLayout;
    protected LinearLayout actContent;
    private DrawerLayout drawerLayout;
    private int menuItemID;
    private Menu menu;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    public void setContentView(final int layoutResID) {
        // Your base layout here
        fullLayout= (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);
        actContent= (LinearLayout) fullLayout.findViewById(R.id.home_content);

        // Setting the content of layout your provided to the act_content frame
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // here you can get your drawer buttons and define how they
        // should behave and what must they do, so you won't be
        // needing to repeat it in every activity class
        // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.layout_actionbar_empty);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

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


//        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d("TAG", "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String name = currentFirebaseUser.getDisplayName();
        //Log.e("TAG", name);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.header_title);
        navUsername.setText(name);

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
        final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
        int title;

        // check if the current activity is the same as the one selected
        if (menuItemID != menuItem.getItemId()) {
            switch (menuItem.getItemId()) {
                case R.id.nav_profile:
                    title = R.string.menu_profile;
                    startActivity(new Intent(this, UserProfileActivity.class));
                    break;
                case R.id.nav_recommended:
                    title = R.string.menu_recommended;
                    startActivity(new Intent(this, RecommendedEventsActivity.class));
                    break;
                case R.id.nav_search:
                    title = R.string.menu_search;
                    startActivity(new Intent(this, SearchActivity.class));
                    break;
                case R.id.nav_notifications:
                    title = R.string.menu_notifications;
                    // TODO: Implementar la función de visualización de las notificaciones
                    Toast.makeText(MenuDrawerActivity.this,
                            "Actividad de Notificaciones de los eventos por los que has mostrado interés.",
                            Toast.LENGTH_LONG).show();
                    break;
                case R.id.nav_settings:
                    startActivity(new Intent(this, UserSettingsActivity.class));
                    title = R.string.menu_settings;
                    break;
                default:
                    throw new IllegalArgumentException("menu option not implemented!!");
            }
        }


        // Fragment fragment = HomeMenuContentFragment.newInstance(getString(title));
        // FragmentManager fragmentManager = getSupportFragmentManager();
        // fragmentManager.beginTransaction().replace(R.id.home_content, fragment).commit();

        //setTitle(getString(title));

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (drawerLayout.isDrawerOpen(GravityCompat.START))
//            drawerLayout.closeDrawer(GravityCompat.START);
//        else
//            drawerLayout.openDrawer(GravityCompat.START);
//
//        return true;
//    }
}
