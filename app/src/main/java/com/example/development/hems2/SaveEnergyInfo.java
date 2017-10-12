package com.example.development.hems2;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class SaveEnergyInfo extends AppCompatActivity {

    LinearLayout layout=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("SaveInfo","Oncreate 동작");

        setContentView(R.layout.activity_save_energy_info);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("전력절감안내");
        toolbar.setTitleTextColor(Color.WHITE);

        TabLayout tabLayout=(TabLayout) findViewById(R.id.tab_layout);

        final ViewPager viewPager=(ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter=new TabPagerAdapter(getSupportFragmentManager());
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.BLACK));

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        recycleAllImageBitmap(tabLayout);

    }


    public static void recycleAllImageBitmap(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();

        for (int i = 0; i < count; i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                recycleAllImageBitmap((ViewGroup) viewGroup.getChildAt(i));
            }
            else {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ImageView) {
                    recycleImageView(((ImageView) v));
                }
            }
        }
    }
    public static void recycleImageView(ImageView iv) {
        try {
            iv.setImageBitmap(null);
        } catch (Exception e) {

        }

        try {
            iv.setImageResource(android.R.color.transparent);
        } catch (Exception e) {

        }

        try {
            iv.setImageDrawable(null);
        } catch (Exception e) {

        }

        try {
            iv.setBackgroundResource(android.R.color.transparent);
        } catch (Exception e) {

        }

        try {
            iv.setBackground(null);
        } catch (Exception e) {

        }

        try {
            iv.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {

        }

        try {
            iv.destroyDrawingCache();
        } catch (Exception e) {

        }

        try {
            iv.destroyDrawingCache();
        } catch (Exception e) {

        }
    }
}

