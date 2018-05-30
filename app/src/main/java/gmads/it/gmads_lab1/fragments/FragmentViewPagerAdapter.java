package gmads.it.gmads_lab1.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giorgiocrepaldi on 29/05/18.
 */

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {
    public final List<Fragment> mFragmentList = new ArrayList<>();
    public FragmentViewPagerAdapter( FragmentManager manager ) {
        super(manager);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

}

