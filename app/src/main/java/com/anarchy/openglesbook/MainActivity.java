package com.anarchy.openglesbook;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anarchy.openglesbook.lessons.lesson1.Lesson1Fragment;
import com.anarchy.openglesbook.lessons.ripple.RippleFragment;
import com.anarchy.openglesbook.lessons.sierpinski.SierpinskiFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Fragment lastFragment = getFragmentManager().findFragmentById(R.id.content_main);
        if(lastFragment == null){
            lastFragment = new MainFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_main,lastFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.lesson_1:
                replaceFragment(Lesson1Fragment.class);
                break;
            case R.id.lesson_2:
                replaceFragment(RippleFragment.class);
                break;
            case R.id.lesson_3:
                replaceFragment(SierpinskiFragment.class);
                break;
            case R.id.lesson_4:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void replaceFragment(@NonNull Class<? extends Fragment> requireFragment){
        //detect OpenGLES30
        ConfigurationInfo info = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo();
        if(info.reqGlEsVersion < 0x30000){
            Toast.makeText(this,"OpenGL ES 3.0 not supported on device. Exiting...",Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_main);
        if(currentFragment != null && currentFragment.getClass() == requireFragment){
            return;
        }else {
            try {
                getFragmentManager().beginTransaction().replace(R.id.content_main,requireFragment.newInstance()).commit();
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this,"replace fragment failure",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
