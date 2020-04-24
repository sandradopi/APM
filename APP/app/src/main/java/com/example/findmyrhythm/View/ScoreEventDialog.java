package com.example.findmyrhythm.View;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.findmyrhythm.Model.IOFiles;
import com.example.findmyrhythm.Model.OrganizerService;
import com.example.findmyrhythm.Model.Rating;
import com.example.findmyrhythm.Model.RatingService;
import com.example.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ScoreEventDialog extends DialogFragment {

    RatingService ratingService = new RatingService();
    EditText comment;
    Float rate;
    String eventId;

    public interface ScoreEventListener {
        public void onDialogPositiveClick();
    }

    ScoreEventListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_score_event, null);
        builder.setView(view);

        String eventName = getArguments().getString("name");
        eventId = getArguments().getString("id");

        // SCORES LOGIC
        final RatingBar bar=(RatingBar)view.findViewById(R.id.pastEventScore);
        bar.setClickable(true);
        bar.setRating(3.5f);


        builder.setTitle("Valorar evento " + eventName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        comment = view.findViewById(R.id.comment);
                        rate = bar.getRating();
                        new createRating().execute();
                        Toast.makeText(getActivity(), "Evento valorado con éxito",  Toast.LENGTH_SHORT).show();
                        Log.w("VALORAR", dialog.toString());
                        listener.onDialogPositiveClick();
                        dialog.cancel();

                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ScoreEventListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("FinishedEventInfoActivity must implement NoticeDialogListener");
        }
    }

    public static ScoreEventDialog newInstance(String name, String id) {
        ScoreEventDialog fragment = new ScoreEventDialog();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("id", id);
        fragment.setArguments(bundle);

        return fragment;
    }

    private class createRating extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            Rating rating = new Rating(eventId, currentUser.getUid(),comment.getText().toString(), rate);
            ratingService.createRating(rating);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
