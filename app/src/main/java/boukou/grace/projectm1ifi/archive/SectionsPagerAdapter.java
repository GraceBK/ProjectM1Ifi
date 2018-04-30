package boukou.grace.projectm1ifi.archive;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import boukou.grace.projectm1ifi.fragment_files.ContactFragment;
import boukou.grace.projectm1ifi.fragment_files.DiscussionFragment;

/**
 * boukou.grace.projectm1ifi.adapter_files
 *
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 *
 * Created by grace on 11/04/2018.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return DiscussionFragment.newInstance("", "", 0);
            case 1:
                return ContactFragment.newInstance("", "", 1);
        }
        return null;//MainActivity.PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return NUM_ITEMS;
    }
}