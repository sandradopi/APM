package com.example.findmyrhythm.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OrganizerInfoDialog extends DialogFragment {
    TextView contentdireccion, tv_detail;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final PersistentOrganizerInfo persistentOrganizerInfo = PersistentOrganizerInfo.getPersistentOrganizerInfo(getApplicationContext());


        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_organizer_info, null);

        contentdireccion = (TextView) view.findViewById(R.id.contentdireccion);
        tv_detail = (TextView) view.findViewById(R.id.tv_detail);

        contentdireccion.setText(persistentOrganizerInfo.getLocation());
        tv_detail.setText(persistentOrganizerInfo.getBiography());

        builder.setView(view);

        builder.setTitle(persistentOrganizerInfo.getName())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}
