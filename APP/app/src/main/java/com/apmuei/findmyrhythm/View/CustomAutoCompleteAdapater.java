package com.apmuei.findmyrhythm.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomAutoCompleteAdapater extends BaseAdapter implements Filterable {

    public interface OnSelfLocationListener {
        public void onSelfLocationClicked();
    }

    private List<String> mObjects;
    private final Object mLock = new Object();

    private int mResource;
    private int mDropDownResource;

    private ArrayList<String> mOriginalValues;
    private ArrayFilter mFilter;

    private LayoutInflater mInflater;

    // the first item, location
    private String mLocationText;

    // our listener
    private OnSelfLocationListener mListener;

    public CustomAutoCompleteAdapater(Context context, int resource, String[] objects) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = mDropDownResource = resource;
        mObjects = Arrays.asList(objects);

    }

    // Set listener for clicks on the self location item
    public void setOnSelfLocationListener(OnSelfLocationListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int position) {
        // Return the selected province
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {

        View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else
            view = convertView;

        try {
            // If no custom field is assigned, assume the whole resource is a TextView. //TODO: When position is 0. Put Icon Image (?)
            text = (TextView) view;

        } catch (ClassCastException e) {
            Log.e("CustomAutoCompleteAdptr", "Layout XML file is not a text field");
            throw new IllegalStateException("Layout XML file is not a text field", e);
        }

        text.setText(getItem(position).toString());

        // It's a real item. Set click listener to null and reset to original state
        view.setOnClickListener(null);
        view.setClickable(false); //MAYBE WRONG?


        return view;
    }

    @Override
    public Filter getFilter() {

        if (mFilter == null)
            mFilter = new ArrayFilter();

        return mFilter;
    }

    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<String>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<String> list;
                synchronized (mLock) {
                    list = new ArrayList<String>(mOriginalValues);
                }
                results.values = list;


                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<String> values;
                synchronized (mLock) {
                    values = new ArrayList<String>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<String>();

                for (int i = 0; i < count; i++) {
                    final String value = values.get(i);
                    final String valueText = value.toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                // add one since we always show the footer
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<String>) results.values;
            notifyDataSetChanged();
        }
    }
}