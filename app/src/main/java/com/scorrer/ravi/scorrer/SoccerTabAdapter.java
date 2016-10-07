package com.scorrer.ravi.scorrer;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SoccerTabAdapter extends FragmentPagerAdapter{

    final int PAGE_COUNT = 3;
    private String[] tabTitles = {"Score", SoccerTeamFragment.team, SoccerTeamFragment.Oteam};
    public SoccerTabAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return SoccerScoreCardFragment.newInstance((position+1));
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
