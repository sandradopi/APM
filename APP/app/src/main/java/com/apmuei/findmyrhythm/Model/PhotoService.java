package com.apmuei.findmyrhythm.Model;

import android.util.Log;

import com.apmuei.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

public class PhotoService {

    private PhotoDAO photoDAO = new PhotoDAO();
    private static final String TAG = "PhotoService";


    public Photo getPhoto(String photoId) {

        try {
            return photoDAO.findById(photoId);

        } catch (InstanceNotFoundException e) {
            Log.e(TAG, "getPhoto: Photo not found");
            return null;
        }
    }

    public String createPhoto(Photo photo) {

        try {
            // Insert event
            String photoId = photoDAO.insert(photo);
            return photoId;
        }
        catch (DuplicatedInstanceException e) {
            Log.e(TAG, "Photo id was already taken");
            return null;
        }

    }

    public void modifyPhoto(Photo original, Photo photo) {
            // Insert event
            photoDAO.modify(original,photo);

    }

    public void updatePhoto(Photo photo) {
        photoDAO.update(photo);
    }

    public void deletePhoto(String photoId) {
        photoDAO.delete(photoId);
    }

}
