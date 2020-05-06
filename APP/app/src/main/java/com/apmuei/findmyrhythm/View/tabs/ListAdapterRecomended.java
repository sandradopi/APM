package com.apmuei.findmyrhythm.View.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apmuei.findmyrhythm.R;

public class ListAdapterRecomended extends BaseAdapter {

    Context context;
    String[] events;
    String[] dates;
    String[] prices;
    String[] rates;
    private static LayoutInflater inflater = null;

    public ListAdapterRecomended(Context context, String[] events, String[] dates, String[] prices) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.events = events;
        this.dates = dates;
        this.prices = prices;

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
            vi = inflater.inflate(R.layout.event_list, null);
        TextView event = (TextView) vi.findViewById(R.id.event);
        event.setText(events[position]);
        TextView date = (TextView) vi.findViewById(R.id.date);
        date.setText(dates[position]);
        if (prices.length != 0) {
            TextView price = (TextView) vi.findViewById(R.id.price);
            price.setText(prices[position]);
        }
        return vi;
    }

}
