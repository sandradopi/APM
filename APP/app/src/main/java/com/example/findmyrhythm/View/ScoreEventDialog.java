package com.example.findmyrhythm.View;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.findmyrhythm.Model.Rating;
import com.example.findmyrhythm.Model.RatingService;
import com.example.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ScoreEventDialog extends DialogFragment {

    RatingService ratingService = new RatingService();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_score_event, null);
        builder.setView(view);

        String eventName = getArguments().getString("name");
        final String eventId = getArguments().getString("id");

        // SCORES LOGIC
        final RatingBar bar=(RatingBar)view.findViewById(R.id.pastEventScore);
        bar.setClickable(true);
        bar.setRating(3.5f);


        builder.setTitle("Valorar evento " + eventName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        EditText comment = view.findViewById(R.id.comment);
                        Float rate = bar.getRating();
                        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                        Rating rating = new Rating(eventId, currentUser.getUid(),comment.getText().toString(), rate);
                        ratingService.createRating(rating);

                        Log.w("VALORAR", dialog.toString());

                        dialog.cancel();

                    }
                });

        return builder.create();
    }

    public static ScoreEventDialog newInstance(String name, String id) {
        ScoreEventDialog fragment = new ScoreEventDialog();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("id", id);
        fragment.setArguments(bundle);

        return fragment;
    }
}
