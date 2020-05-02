package com.example.findmyrhythm.Model.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Telephony;
import android.util.Log;

import java.io.IOException;
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
        Address address = new Address(this.locale);
        try {
            Geocoder geocoder = new Geocoder(this.context, this.locale);
            address =  geocoder.getFromLocationName(locationName, 1).get(0);
            return address;
        } catch (IOException exception) {
            Log.w(TAG, exception.toString());
            return address;
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
