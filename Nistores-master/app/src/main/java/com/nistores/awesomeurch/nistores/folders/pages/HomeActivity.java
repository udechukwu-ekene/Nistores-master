package com.nistores.awesomeurch.nistores.folders.pages;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.BottomNavigationHelper;
import com.nistores.awesomeurch.nistores.R;
import com.nistores.awesomeurch.nistores.folders.helpers.MyAlarmReceiver;
import com.nistores.awesomeurch.nistores.folders.helpers.Utility;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Intent intent;
    Fragment fragment;
    private TextView mTextMessage;
    SharedPreferences preferences;
    NavigationView navigationView;
    public static int PRODUCTS = 1;
    public static int BUSINESS = 2;
    public static int PORT = 3;
    public static int CHATS = 4;
    public static int STORES = 5;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    fragment = AllProductsFragment.newInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_business_lounge:
                    //mTextMessage.setText(R.string.title_business_lounge);
                    fragment = BusinessLoungeFragment.newInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_display_port:
                    //mTextMessage.setText(R.string.title_display_port);
                    fragment = DisplayPortFragment.newInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_chats:
                    //mTextMessage.setText(R.string.title_chats);
                    fragment = new ChatsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_top_stories:
                    //mTextMessage.setText(R.string.title_top_stories);
                    fragment = TopStoresFragment.newInstance();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle getBundle = this.getIntent().getExtras();
        if (getBundle != null) {
            int fg = getBundle.getInt("fragment");
            //Log.d("FRAG",fg+"");
            fragment = selectFragment(fg);
        }else{
            fragment = new AllProductsFragment();
        }

        loadFragment(fragment);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    public Fragment selectFragment(int frag){
        if(frag == PRODUCTS){
            fragment = AllProductsFragment.newInstance();
        }else if(frag == BUSINESS){
            fragment = BusinessLoungeFragment.newInstance();
        }else if(frag == PORT){
            //fragment = DisplayPortFragment.newInstance();
            fragment = new DisplayPortFragment();
        }else if(frag == CHATS){
            fragment = ChatsFragment.newInstance();
        }else if(frag == STORES){
            fragment = TopStoresFragment.newInstance();
        }else{
            fragment = AllProductsFragment.newInstance();
        }
        return fragment;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_messages) {
            fragment = new ChatsFragment();
            loadFragment(fragment);
            return true;
        } else if(id == R.id.action_notifications){
            intent = new Intent(this,NotificationsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_search){
            intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            //take to profile activity
            intent = new Intent(this,ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_recent_posts) {
            fragment = new AllProductsFragment();
            loadFragment(fragment);

        } else if (id == R.id.nav_favourites) {
            intent = new Intent(this,FavouritesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_dashboard) {
            intent = new Intent(this,DashboardActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_polls) {
            intent = new Intent(this,pollsActivity.class);
            startActivity(intent);

        } else if (id == R.id.about_us) {
            intent = new Intent(this,AboutUsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_help_desk) {
            intent = new Intent(this,HelpDeskActivity.class);
            startActivity(intent);

        } else if (id == R.id.terms_policy) {
            intent = new Intent(this,TermsPolicyActivity.class);
            startActivity(intent);

        } else if (id == R.id.disclaimer) {
            intent = new Intent(this,DisclaimerActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_my_stores) {
            intent = new Intent(this,MyStoresActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_admin) {
            intent = new Intent(this,AdminLoginActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_explore_stores) {
            intent = new Intent(this,ExploreStoresActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categories) {
            intent = new Intent(this,CategoriesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_own_store) {
            intent = new Intent(this,OwnStoreActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_delivery_orders) {
            intent = new Intent(this,DeliveryOrderActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout){
            new Utility(getApplicationContext()).cancelAlarm(navigationView);
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            /*editor.remove("user");
            editor.apply();*/
            editor.clear().apply();
            intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void preventInteraction(){
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction(){
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (alarm != null) {
            alarm.cancel(pIntent);
        }
    }

}
