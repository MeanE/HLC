package com.example.minge.hlc_smarthome;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navView;

    private Sensor sensorDHT11 = null, sensorFire = null, sensorWash = null, sensorWindow = null, sensorCO = null, sensorDoor = null;
    private Intent itSensorDHT11, itSensorFire, itSensorWash, itSensorWindow, itSensorCO, itSensorDoor;

    boolean doubleBackCheck = false;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            if (doubleBackCheck) super.onBackPressed();

            doubleBackCheck = true;
            Toast.makeText(this, "再按一次back鍵離開程式", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackCheck = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initDrawer();
        initFragment();
        initService();
    }

    private void initService() {
        sensorDHT11 = new SensorDHT11();
        itSensorDHT11 = new Intent(this, SensorDHT11.class);
        startService(itSensorDHT11);

        sensorFire = new SensorFire();
        itSensorFire = new Intent(this, SensorFire.class);
        startService(itSensorFire);

        sensorWash = new SensorWash();
        itSensorWash = new Intent(this, SensorWash.class);
        startService(itSensorWash);

        sensorWindow = new SensorWindow();
        itSensorWindow = new Intent(this, SensorWindow.class);
        startService(itSensorWindow);

        sensorCO = new SensorCO();
        itSensorCO = new Intent(this, SensorCO.class);
        startService(itSensorCO);

        sensorDoor = new SensorDoor();
        itSensorDoor = new Intent(this, SensorDoor.class);
        startService(itSensorDoor);
    }

    public Sensor getSensorDHT11() {
        return sensorDHT11;
    }

    public Sensor getSensorFire() {
        return sensorFire;
    }

    public Sensor getSensorWash() {
        return sensorWash;
    }

    public Sensor getSensorWindow() {
        return sensorWindow;
    }

    public Sensor getSensorCO() {
        return sensorCO;
    }

    public Sensor getSensorDoor() {
        return sensorDoor;
    }

    private void initFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentWatch()).commit();
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navigation_view);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectItem(menuItem);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void selectItem(MenuItem menuItem) {
        int id = menuItem.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;

        //Log.i("NevigationSelect",String.valueOf(id));
        switch (id) {
            case R.id.navigation_item_1:
                //FragmentWatch currentFragment = (FragmentWatch)fragmentManager.findFragmentById(id);
                //if(currentFragment == null || !currentFragment.isVisible()) return;
                //Log.i("FragmentWatch","create");
                //if (fragment.getId() == R.id.navigation_item_1) break;
                fragment = new FragmentWatch();
                break;
            case R.id.navigation_item_2:
                //FragmentLock currentFragment2 = (FragmentLock)fragmentManager.findFragmentById(id);
                //if(currentFragment2 == null || !currentFragment2.isVisible()) return;
                //Log.i("FragmentLock","create");
                //if (fragment.getId() == R.id.navigation_item_2) break;
                fragment = new FragmentLock();
                break;

            default:
                fragment = new FragmentWatch();
        }
        //Log.i("123","123");
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        menuItem.setChecked(true);
        toolbarTitle.setText(menuItem.getTitle());
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
}