package com.apmuei.findmyrhythm.View;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.apmuei.findmyrhythm.R;

public class OrganizerMenuDrawerActivity extends MenuDrawerActivity {

    @Override
    public void inflateMenu(NavigationView navigationView) {
        navigationView.inflateMenu(R.menu.activity_organizer_home_navigation_drawer);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        // check if the current activity is the same as the one selected
        if (menuItemID != menuItem.getItemId()) {
            setMenuItemChecked(menuItem.getItemId());
            switch (menuItem.getItemId()) {
                case R.id.nav_profile:
                    startActivity(new Intent(this, OrganizerProfileActivity.class));
                    break;
                case R.id.nav_search:
                    startActivity(new Intent(this, SearchEventsActivity.class));
                    break;
                case R.id.nav_settings:
                    startActivity(new Intent(this, OrganizerSettingsActivity.class));
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