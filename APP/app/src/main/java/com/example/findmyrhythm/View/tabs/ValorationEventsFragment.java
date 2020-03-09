package com.example.findmyrhythm.View.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findmyrhythm.R;
import com.example.findmyrhythm.View.ListAdapter;
import com.example.findmyrhythm.View.ListAdapterValoraciones;


public class ValorationEventsFragment extends Fragment {


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.valoration_events_fragment, container, false);

        ListView mListView;
        mListView = (ListView) view.findViewById(R.id.eventlist);
        String[] users = new String[] {"Sandra", "Luis", "Marta"};
        String[] comments = new String[] { "Muy buen local y buena música", "Que conciertazo!", "Una noche para repetir" };
        mListView.setAdapter(new ListAdapterValoraciones(this.requireContext(), users, comments));

        return view;
    }

}