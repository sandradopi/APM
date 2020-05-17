package com.apmuei.findmyrhythm.View;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.apmuei.findmyrhythm.Model.SearchFilters;
import com.apmuei.findmyrhythm.R;

// Source: https://developer.android.com/guide/topics/ui/dialogs


public class SearchFiltersDialogFragment extends DialogFragment {

    private static final String TAG = "SearchFiltersDF";

    private View mView;
    private CheckBox showPastEventsCheckBox;
    private SearchFilters previousFilters = null;

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
        return new SearchFilters(showPast);
    }

    public static SearchFilters getDefaultFilters() {
        return new SearchFilters(false);
    }

    private void restorePreviousFilters() {
        showPastEventsCheckBox.setChecked(previousFilters.getShowPastEvents());
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

