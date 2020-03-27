package com.example.findmyrhythm.View.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmyrhythm.R;

public class ListAdapterPast extends BaseAdapter {

        Context context;
        String[] events;
        String[] dates;
        String[] rates;
        private static LayoutInflater inflater = null;

        public ListAdapterPast(Context context, String[] events, String[] dates, String[] rates) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.events = events;
            this.dates = dates;
            this.rates = rates;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return events.length;
        }

        @Override
        public Object getItem(int position) {
            return events[position];
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
                vi = inflater.inflate(R.layout.list_row_past, null);
            TextView event = (TextView) vi.findViewById(R.id.event);
            event.setText(events[position]);
            TextView date = (TextView) vi.findViewById(R.id.date);
            date.setText(dates[position]);
            ImageView star = (ImageView) vi.findViewById(R.id.star);
            if (rates.length != 0) {
                if (rates[position] == "not_rated")
                    star.setImageResource(R.drawable.ic_star_border_24px);
                else if (rates[position] == "rated")
                    star.setImageResource(R.drawable.ic_star_24px);
            }
            return vi;
        }

}