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
        HelpFragment.OnFragmentInteractionListener,
        LogoutFragment.OnFragmentInteractionListener,
        QuitFragment.OnFragmentInteractionListener{

    private FragmentManager mainFragmentManager = getSupportFragmentManager();
    private StartFragment startFragment;
    private HelpFragment helpFragment;
    private LogoutFragment logoutFragment;
    private QuitFragment quitFragment;

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
        logoutFragment = new LogoutFragment();
        quitFragment = new QuitFragment();

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            goToFragment(startFragment);
        } else if (id == R.id.nav_help) {
            goToFragment(helpFragment);
        } else if (id == R.id.nav_sign_out) {
            goToFragment(logoutFragment);
        } else if (id == R.id.nav_quit) {
            goToFragment(quitFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Sets the main view to a specific fragment.
     * @param fragment the fragment to switch to.
     */
    public void goToFragment(Fragment fragment){
        List<Fragment> fragmentList = mainFragmentManager.getFragments();

        if(!fragmentList.contains(fragment)){
            clearMainViewFromFragements();

            FragmentTransaction ft = mainFragmentManager.beginTransaction();
            ft.add(R.id.fragmentHolder, fragment);
            ft.commit();
        }
    }


    /**
     * Removes all fragement currently attatched to the main view.
     */
    private void clearMainViewFromFragements(){
        List<Fragment> fragmentList = mainFragmentManager.getFragments();

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
