package org.fraunhofer.cese.madcap;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        StartFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener{

    private FragmentManager mainFragmentManager = getSupportFragmentManager();
    private StartFragment startFragment;
    private HelpFragment helpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Hamburger Menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Top navigation bar
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Section for all fragments being shown in the main activity
        startFragment = new StartFragment();
        helpFragment = new HelpFragment();

        //Initial settign up of the main fragement
        FragmentTransaction ft = mainFragmentManager.beginTransaction();
        ft.add(R.id.fragmentHolder, startFragment);
        ft.commit();

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_help) {
            goToHelpFragment();
        } else if (id == R.id.nav_sign_out) {

        } else if (id == R.id.nav_quit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Sets the main view to help fragment.
     */
    public void goToHelpFragment(){
        List<Fragment> fragmentList = getCurrentMainFragments();

        if(!fragmentList.contains(helpFragment)){
            clearMainViewFromFragements();

            FragmentTransaction ft = mainFragmentManager.beginTransaction();
            ft.add(R.id.fragmentHolder, helpFragment);
            ft.commit();
        }
    }

    /**
     * Gets a list of current Fragemnts attached to the main view.
     * @return a list of Fragments.
     */
    private List<Fragment> getCurrentMainFragments(){
        return mainFragmentManager.getFragments();
    }

    /**
     * Removes all fragement currently attatched to the main view.
     */
    private void clearMainViewFromFragements(){
        List<Fragment> fragmentList = getCurrentMainFragments();

        for(Fragment f : fragmentList){
            FragmentTransaction ft = mainFragmentManager.beginTransaction();
            ft.remove(f);
            ft.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
