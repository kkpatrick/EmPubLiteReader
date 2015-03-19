package com.commonsware.empublite;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by abc on 3/19/15.
 */
public class ContentsAdapter extends FragmentStatePagerAdapter {

    public ContentsAdapter(Activity context) {
        super(context.getFragmentManager());
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
