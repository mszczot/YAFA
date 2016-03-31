package com.example.szczocik.yafa_yetanotherfitnessapp;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.Fragments.HistoryFragment;
import com.example.szczocik.yafa_yetanotherfitnessapp.Fragments.RunningFragment;
import com.example.szczocik.yafa_yetanotherfitnessapp.Fragments.StatisticFragment;
import com.example.szczocik.yafa_yetanotherfitnessapp.Fragments.TimerFragment;
import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.DatabaseHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.LocationHandler;

import com.facebook.FacebookSdk;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity
    implements RunningFragment.OnFragmentInteractionListener,
        TimerFragment.OnFragmentInteractionListener,
        HistoryFragment.OnFragmentInteractionListener,
        StatisticFragment.OnFragmentInteractionListener,
        Serializable {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * My variables: Database handler
     */
    DatabaseHandler db;
    LocationHandler locationHandler;

    RunningFragment runningFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setup();
    }


    private void setup() {
        db = new DatabaseHandler(this);
        //test(db);
        locationHandler = locationHandler.getInstance(db);
        runningFragment = (RunningFragment) mSectionsPagerAdapter.getItem(1);
    }

    public void updateList(LocationHandler lh) {

        mSectionsPagerAdapter.historyFragment.adapter.insert(lh.getCurrentSession(), 0);
        mSectionsPagerAdapter.historyFragment.adapter.notifyDataSetChanged();
    }

    public void updateListAfterRemoval(RunningSession rs) {
        mSectionsPagerAdapter.historyFragment.adapter.remove(rs);
        mSectionsPagerAdapter.historyFragment.adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onStop();
    }

    public void test(DatabaseHandler db) {
        Calendar cl = Calendar.getInstance();

        Random r = new Random();

        RunningSession rs;
        for (int i =0; i<3; i++) {
            cl.set(Calendar.DAY_OF_MONTH, 1);
            Log.d("First", cl.getTime().toString());
            cl.set(Calendar.MONTH, i);
            Log.d("Second", cl.getTime().toString());
            Log.d("Month", String.valueOf(i));
            for (int j=1; j<cl.getActualMaximum(Calendar.DAY_OF_MONTH); j++) {
                cl.set(Calendar.DAY_OF_MONTH, j);
                rs = new RunningSession();
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.HOUR_OF_DAY, cl.get(Calendar.HOUR_OF_DAY));
                rs.setStartTime(cl.getTimeInMillis());

                int minutes = r.nextInt(60 - 30) +30;

                cl.set(Calendar.MINUTE, minutes);
                rs.setEndTime(cl.getTimeInMillis());
                rs.setPace(r.nextInt(15 - 5) + 5); //random number between 5 and 15
                rs.setElevationGain(r.nextInt(50 - 10) + 10); //random number between 10 and 50
                rs.setElevationLoss(r.nextInt(50 - 10) + 10);
                rs.setAvgSpeed(r.nextInt(10 - 2) + 2); // 2 - 10
                rs.setDistance(r.nextInt(3000 - 500) + 500); //500 - 3000
                rs.setMaxSpeed(r.nextInt(15 - 2) + 2);
                db.addSession(rs);
            }
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
        implements Serializable {

        public HistoryFragment historyFragment;
        public Fragment fragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return StatisticFragment.newInstance(locationHandler);
                case 1:
                    return RunningFragment.newInstance(locationHandler);
                case 2:
                    historyFragment = HistoryFragment.newInstance(locationHandler);
                    return historyFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "STATISTICS";
                case 1:
                    return "RUN";
                case 2:
                    return "HISTORY";
            }
            return null;
        }


    }
}
