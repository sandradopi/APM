package com.example.findmyrhythm.Model;

import android.util.Log;

import com.example.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;

import java.util.ArrayList;

public class SpectatorService {

    private static final String TAG = "SpectatorService";
    private SpectatorDAO spectatorDAO = new SpectatorDAO();


    public Spectator getSpectator(String spectatorId) throws InstanceNotFoundException {

        return spectatorDAO.findById(spectatorId);
    }

    public boolean createSpectator(String idUser, String idEvent) {

        Spectator spectator = new Spectator(
                idUser,
                idEvent
        );

        try {
            spectatorDAO.insert(spectator);
        } catch (DuplicatedInstanceException e) {
            Log.e(TAG, "tried to insert an existing id");
            return false;
        }

        return true;
    }

    public void updateSpectator(Spectator spectator) {
        spectatorDAO.update(spectator);
    }
    public void deleteSpectator(Spectator spectator) {
        spectatorDAO.delete(spectator.getId());
    }

    public Spectator findSpectatorByIds(String idEvent, String idUser) {
        return spectatorDAO.findSpectatorByIds(idEvent,idUser );
    }

}
