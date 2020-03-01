package com.example.findmyrhythm.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.findmyrhythm.R;

public class listAdapter extends BaseAdapter {
    Context context;
    String[] events;
    String[] dates;
    String[] prices;
    private static LayoutInflater inflater = null;

    public listAdapter(Context context, String[] events, String[] dates, String[] prices) {
        this.context = context;
        this.events = events;
        this.dates = dates;
        this.prices = prices;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return events.length;
    }

    @Override
    public Object getItem(int position) {
        return events[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (vi == null)
            vi = inflater.inflate(R.layout.content_list_org, null);

            TextView event = (TextView) vi.findViewById(R.id.event);
            event.setText(events[position]);

            TextView date = (TextView) vi.findViewById(R.id.date);
            date.setText(dates[position]);

            TextView price = (TextView) vi.findViewById(R.id.price);
            price.setText(prices[position]);

        return vi;
    }
}
