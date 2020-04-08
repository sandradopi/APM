package com.example.findmyrhythm.View;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import com.example.findmyrhythm.R;
import com.google.android.material.navigation.NavigationView;

public class UserMenuDrawerActivity extends MenuDrawerActivity {

    @Override
    public void inflateMenu(NavigationView navigationView) {
        navigationView.inflateMenu(R.menu.activity_home_navigation_drawer);
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
                    Toast.makeText(UserMenuDrawerActivity.this,
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

}
