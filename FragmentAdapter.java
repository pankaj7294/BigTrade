package com.gupta.ram.assignment.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by RAMJEE on 06-06-2018.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                return new BusinessProfileFragment();
            case 1:
                return new ProductFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
         super.getPageTitle(position);

         switch (position){
             case 0:
                 return "Business Profile";
             case 1:
                 return "Product";
                 default:
                     return null;
         }
    }
}
