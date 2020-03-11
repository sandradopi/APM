package com.example.findmyrhythm.View.tabs;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterOrg extends FragmentPagerAdapter {

    //@StringRes
    //private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapterOrg(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new NextEventsFragmentOrg();
            case 1:
                return new PastEventsFragmentOrg();
            case 2:
                return new ValorationEventsFragment();
             default:
                 return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "PRÓXIMOS";
            case 1:
                return "PASADOS";
            case 2:
                return "VALORACIONES";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }

}