package com.example.findmyrhythm.View.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findmyrhythm.R;

public class OrganizerEventInfoFragment extends Fragment {



    public static OrganizerEventInfoFragment newInstance() {
        return new OrganizerEventInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_event_info, container, false);
    }

}
