package com.kelsey.searchbox.cover;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kelsey.searchbox.bottombar.CalendarFragment;
import com.kelsey.searchbox.bottombar.CollectionFragment;
import com.kelsey.searchbox.bottombar.SearchFragment;
import com.kelsey.searchbox.search.R;
import com.ldf.calendar.model.CalendarDate;
import com.lzy.widget.AlphaIndicator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        AlphaIndicator alphaIndicator = (AlphaIndicator) findViewById(R.id.alphaIndicator);
        alphaIndicator.setViewPager(viewPager);
    }

    private class MainAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        public MainAdapter(FragmentManager fm) {
            super(fm);
            SearchFragment searchFragment = new SearchFragment();
            CalendarFragment calendarFragment = new CalendarFragment();
            CollectionFragment collectionFragment = new CollectionFragment();
            fragments.add(searchFragment);
            fragments.add(calendarFragment);
            fragments.add(collectionFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }



}
