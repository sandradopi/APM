package com.apmuei.findmyrhythm.Model.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoUtils {

    private static final String TAG = "Geolocation Utils";
    private Context context;
    private Locale locale;

    public GeoUtils(Context context, Locale locale) {
        this.context = context;
        this.locale = locale;
    }

    public Address getAddressFromLocation(Location location) {
        Address address = new Address(this.locale);
        try {
            Geocoder geocoder = new Geocoder(this.context, this.locale);
            address =  geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
            return address;
        } catch (IOException exception) {
            Log.w(TAG, exception.toString());
            return address;
        }
    }

    public Address getAddressFromLocationName(String locationName) {
        List<Address> addresses = new ArrayList<>();
        Address address = null;
        try {
            Geocoder geocoder = new Geocoder(this.context, this.locale);
            addresses =  geocoder.getFromLocationName(locationName, 1);
            for (Address addr : addresses) {
                address = addr;
            }
        } catch (IOException exception) {
            Log.w(TAG, exception.toString());
        }
        return address;
    }


    public static void checkLocationEnabled(final Activity activity){
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(activity)
                    .setMessage("El GPS no está activado")
                    .setPositiveButton("Abrir ajustes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancelar",null)
                    .show();
        }
    }


    public static String getAddressString(Address fullAddress) {
        String address = fullAddress.getAddressLine(0);
        String city = fullAddress.getLocality();
        String country = fullAddress.getCountryName();
        String postalCode = fullAddress.getPostalCode();
        String street = fullAddress.getThoroughfare();
        String streetNumber = fullAddress.getSubThoroughfare();
        return address; // street + ", " + streetNumber + ", " + postalCode + ", " + city + ", " + country;
    }

}
