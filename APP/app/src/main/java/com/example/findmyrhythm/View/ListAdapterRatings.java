package com.example.findmyrhythm.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.findmyrhythm.R;

public class ListAdapterRatings extends BaseAdapter {

    Context context;
    String[] users;
    String[] comments;

    private static LayoutInflater inflater = null;

    public ListAdapterRatings(Context context, String[] users, String[] comments) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.users = users;
        this.comments = comments;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return users.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return users[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_row_ratings, null);
        TextView user = (TextView) vi.findViewById(R.id.user);
        user.setText(users[position]);
        TextView comment = (TextView) vi.findViewById(R.id.comment);
        comment.setText(comments[position]);

        return vi;
    }
}
