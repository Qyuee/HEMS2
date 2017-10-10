package com.example.development.hems2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.LinearLayout;

/**
 * Created by Development on 2017-09-01.
 */

public class TabPagerAdapter extends FragmentPagerAdapter{

    int tabCount=4;

    public TabPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                Tab1Fragment tab1=new Tab1Fragment();
                return tab1;
            case 1:
                Tab2Fragment tab2=new Tab2Fragment();
                return tab2;
            case 2:
                Tab3Fragment tab3=new Tab3Fragment();
                return tab3;
            case 3:
                Tab4Fragment tab4=new Tab4Fragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "여름철";
            case 1:
                return "겨울철";
            case 2:
                return "전기안전";
            case 3:
                return "재해대비행동";
            default:
                return null;
        }
    }
}
