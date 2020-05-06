package com.apmuei.findmyrhythm.View.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apmuei.findmyrhythm.R;

public class FinishedEventInfoFragment extends Fragment {



    public static FinishedEventInfoFragment newInstance() {
        return new FinishedEventInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finished_event_info, container, false);
    }

}
