package com.apmuei.findmyrhythm.View;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.apmuei.findmyrhythm.R;

import java.net.Inet4Address;

import static com.facebook.FacebookSdk.getApplicationContext;

public class EulaDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.activity_eula_dialog, null));
        builder.setTitle(getString(R.string.eula_title))
                .setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i= new Intent(getApplicationContext(),GreetingsActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(i);

                    }
                });

        return builder.create();
    }
}

