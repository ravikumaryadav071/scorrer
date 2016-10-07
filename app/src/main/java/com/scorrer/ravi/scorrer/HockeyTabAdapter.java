package com.scorrer.ravi.scorrer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HockeyTabAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String[] tabTitles = {"Score", HockeyTeamFragment.team, HockeyTeamFragment.Oteam};
    public HockeyTabAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return HockeyScoreCardFragment.newInstance((position+1));
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
