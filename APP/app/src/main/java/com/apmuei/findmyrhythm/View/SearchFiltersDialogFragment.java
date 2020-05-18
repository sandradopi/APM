package com.apmuei.findmyrhythm.View;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.apmuei.findmyrhythm.Model.SearchFilters;
import com.apmuei.findmyrhythm.R;

import java.util.ArrayList;
import java.util.Arrays;

// Source: https://developer.android.com/guide/topics/ui/dialogs


public class SearchFiltersDialogFragment extends DialogFragment {

    private static final String TAG = "SearchFiltersDF";

    private View mView;
    private CheckBox showPastEventsCheckBox;
    private EditText minPrizeEditText;
    private EditText maxPrizeEditText;
    private SearchFilters previousFilters = null;
    private ArrayList<Integer> genresViewIds = new ArrayList<>(Arrays.asList(R.id.genre_rock, R.id.genre_pop,
            R.id.genre_trap, R.id.genre_classical, R.id.genre_hiphop, R.id.genre_blues, R.id.genre_dance,
            R.id.genre_indie, R.id.genre_reggae));
    private ArrayList<CheckBox> genresCheckboxes = new ArrayList<>();

    private FiltersDialogInterface filtersDialogInterface;


    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.dialog_fragment_search_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;

        showPastEventsCheckBox = mView.findViewById(R.id.checkBox_show_past_events);
        minPrizeEditText = mView.findViewById(R.id.min_prize);
        maxPrizeEditText = mView.findViewById(R.id.max_prize);

        for (Integer id : genresViewIds) {
            CheckBox genreCheckBox = mView.findViewById(id);
            genresCheckboxes.add(genreCheckBox);
        }


        if (previousFilters == null) {
            // TODO: set default
            previousFilters = getSearchFilters();
        }

        Button applyFiltersButton = mView.findViewById(R.id.apply);
        applyFiltersButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SearchFilters newFilters = getSearchFilters();
                previousFilters = newFilters;
                filtersDialogInterface.applyFilters(newFilters);
                dismiss();
            }
        });

        Button cancelButton = mView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                restorePreviousFilters();
                dismiss();
            }
        });

    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        restorePreviousFilters();
        super.onCancel(dialog);
    }


    public void setInterface(FiltersDialogInterface filtersDialogInterface) {
        this.filtersDialogInterface = filtersDialogInterface;
    }


    public SearchFilters getSearchFilters() {
        boolean showPast = showPastEventsCheckBox.isChecked();
        String minPrizeString = minPrizeEditText.getText().toString();
        String maxPrizeString = maxPrizeEditText.getText().toString();
        int minPrize = 0;
        int maxPrize = 10000;
        if (!minPrizeString.isEmpty()) {
            minPrize = Integer.parseInt(minPrizeString);
        }
        if (!maxPrizeString.isEmpty()) {
            maxPrize = Integer.parseInt(maxPrizeString);
        }

        ArrayList<String> genres = new ArrayList<>();
        for (CheckBox genreCheckbox : genresCheckboxes) {
            if (genreCheckbox.isChecked()) {
                genres.add(genreCheckbox.getText().toString());
            }
        }

        Log.e(TAG, "########" + genres);

        return new SearchFilters(showPast, minPrize, maxPrize, genres);
    }

    public static SearchFilters getDefaultFilters(Activity activity) {
        ArrayList<String> genres = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(R.array.categories)));
        return new SearchFilters(false, 0, 10000, genres);
    }

    private void restorePreviousFilters() {
        showPastEventsCheckBox.setChecked(previousFilters.getShowPastEvents());
        for (CheckBox genreCheckbox : genresCheckboxes) {
            String checkBoxText = genreCheckbox.getText().toString();
            genreCheckbox.setChecked(previousFilters.getGenres().contains(checkBoxText));
        }
        minPrizeEditText.setText(String.valueOf(previousFilters.getMinPrize()));
        maxPrizeEditText.setText(String.valueOf(previousFilters.getMaxPrize()));
    }



    /** The system calls this only when creating the layout in a dialog. */
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // The only reason you might override this method when using onCreateView() is
//        // to modify any dialog characteristics. For example, the dialog includes a
//        // title by default, but your custom layout might not need it. So here you can
//        // remove the dialog title, but you must call the superclass to get the Dialog.
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }

}

