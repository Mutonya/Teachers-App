package com.hudutech.kcpeteacherapp.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hudutech.kcpeteacherapp.models.TeacherProfile;
import com.hudutech.kcpeteacherapp.ui.about.AboutFragment;
import com.hudutech.kcpeteacherapp.ui.about.ReviewsFragment;

public class AboutSectionAdapter extends FragmentPagerAdapter {



    public AboutSectionAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AboutFragment.newInstance();
            case 1:

                return ReviewsFragment.newInstance();

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
        switch (position) {
            case 0:
                return "About";

            case 1:
                return "Reviews";
            default:
                return null;
        }
    }
}