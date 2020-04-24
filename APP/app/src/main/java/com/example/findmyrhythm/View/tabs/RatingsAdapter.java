package com.example.findmyrhythm.View.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmyrhythm.R;

import java.util.ArrayList;

public class RatingsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> comments = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();

    public RatingsAdapter(Context context, ArrayList<String> comments, ArrayList<String> names) {
        super(context, R.layout.list_row_ratings, comments);
        this.context = context;
        this.comments = comments;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_row_ratings, parent, false);
        TextView commentView = (TextView) rowView.findViewById(R.id.comment);
        TextView userView = (TextView) rowView.findViewById(R.id.user);
        commentView.setText(comments.get(position));
        userView.setText(names.get(position));

        return rowView;
    }
}