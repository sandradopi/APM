package com.apmuei.findmyrhythm.View;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.apmuei.findmyrhythm.Model.SearchFilters;
import com.apmuei.findmyrhythm.R;

// Source: https://developer.android.com/guide/topics/ui/dialogs


public class SearchFiltersDialogFragment extends DialogFragment {

    private static final String TAG = "SearchFiltersDF";

    private View view;

    private FiltersDialogInterface filtersDialogInterface;


    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        view = inflater.inflate(R.layout.dialog_fragment_search_filters, container, false);

        Button applyFilters = view.findViewById(R.id.apply);
        applyFilters.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filtersDialogInterface.finishEvent();
            }
        });

        return view;
    }


    public void setInterface(FiltersDialogInterface filtersDialogInterface) {
        this.filtersDialogInterface = filtersDialogInterface;
    }

    public SearchFilters getSearchFilters() {
        CheckBox showPastCB = view.findViewById(R.id.checkBox_show_past_events);
        return new SearchFilters(showPastCB.isChecked());
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

