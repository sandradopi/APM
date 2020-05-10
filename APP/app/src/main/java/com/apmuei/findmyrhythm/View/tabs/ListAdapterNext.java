package com.apmuei.findmyrhythm.View.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListAdapterNext extends BaseAdapter {

    Context context;
    ArrayList<Event> events;
    //String[] dates;
    ArrayList<String> prices;
    private static LayoutInflater inflater = null;


    public ListAdapterNext(Context context, ArrayList<Event> events, ArrayList<String> prices) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.events = events;
        this.prices = prices;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
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
            vi = inflater.inflate(R.layout.list_row_next, null);
        TextView event = (TextView) vi.findViewById(R.id.event);
        event.setText(events.get(position).getName());
        TextView date = (TextView) vi.findViewById(R.id.date);
        DateFormat df = new SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault());
        date.setText(df.format(events.get(position).getEventDate()));
        if (prices.size() != 0) {
            TextView price = (TextView) vi.findViewById(R.id.price);
            price.setText(prices.get(position));
        }
        return vi;
    }

}
