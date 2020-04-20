package com.example.findmyrhythm.Model.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Telephony;

import java.io.IOException;
import java.util.Locale;

public class GeoUtils {

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
            System.out.println("An error ocurred while creating location from name");
            return address;
        }
    }
}
