package org.fraunhofer.cese.madcap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        StartFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        QuitFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener{
    private final String TAG = getClass().getSimpleName();
    private String currentTopFragment;

    private FragmentManager mainFragmentManager;
    private StartFragment startFragment;
    private HelpFragment helpFragment;
    private LogoutFragment logoutFragment;
    private QuitFragment quitFragment;
    private AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainFragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            MyApplication.madcapLogger.d(TAG, "SavedInstanceState not null");
            startFragment = (StartFragment) mainFragmentManager.getFragment(savedInstanceState, "startfragment");
        }else{
            startFragment = new StartFragment();
        }

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
        //startFragment = new StartFragment(); // see save instance
        helpFragment = new HelpFragment();
        logoutFragment = new LogoutFragment();
        //quitFragment = new QuitFragment();
        aboutFragment = new AboutFragment();

        //Initial settign up of the main fragement
        FragmentTransaction ft = mainFragmentManager.beginTransaction();
        ft.replace(R.id.fragmentHolder, startFragment);
        ft.commit();

        currentTopFragment = "start";

        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_permissions){
            startActivity(new Intent(MainActivity.this,PermissionsActivity.class));
        }
        if (id == R.id.nav_home) {
            FragmentTransaction ft = mainFragmentManager.beginTransaction();
            ft.replace(R.id.fragmentHolder, startFragment);
            if(!currentTopFragment.equals("start")){
                ft.addToBackStack("start");
                currentTopFragment = "start";
            }
            ft.commit();
        } else if (id == R.id.nav_help) {
            FragmentTransaction ft = mainFragmentManager.beginTransaction();
            ft.replace(R.id.fragmentHolder, helpFragment);
            if(!currentTopFragment.equals("help")){
                ft.addToBackStack("help");
                currentTopFragment = "help";
            }
            ft.commit();
        } else if (id == R.id.nav_sign_out) {
            FragmentTransaction ft = mainFragmentManager.beginTransaction();
            ft.replace(R.id.fragmentHolder, logoutFragment);
            if(!currentTopFragment.equals("logout")){
                ft.addToBackStack("logout");
                currentTopFragment ="logout";
            }
            ft.commit();
        } else if (id == R.id.nav_about) {
            FragmentTransaction ft = mainFragmentManager.beginTransaction();
            ft.replace(R.id.fragmentHolder, aboutFragment);
            if(!currentTopFragment.equals("about")){
                ft.addToBackStack("about");
                currentTopFragment = "about";
            }
            ft.commit();
        } //else if (id == R.id.nav_quit) {
//            FragmentTransaction ft = mainFragmentManager.beginTransaction();
//            ft.replace(R.id.fragmentHolder, quitFragment);
//            if(!currentTopFragment.equals("quit")){
//                ft.addToBackStack("quit");
//                currentTopFragment = "quit";
//            }
//            ft.commit();
//        }

        int checkedID = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if(startFragment == null){
            startFragment = new StartFragment();

            //Initial settign up of the main fragement
            FragmentTransaction ft = mainFragmentManager.beginTransaction();
            ft.add(R.id.fragmentHolder, startFragment);
            ft.commit();
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
//            if (backStackEntryCount == 0) {
//                finish();   // write your code to switch between fragments.
//            } else {
//                super.onBackPressed();
//            }
            super.onBackPressed();
        }
    }

    private void uncheckAll(){
        MenuItem nav = (MenuItem) findViewById(R.id.nav_home);
        nav.setChecked(false);
        MenuItem sign = (MenuItem) findViewById(R.id.nav_sign_out);
        sign.setChecked(false);
        MenuItem hp = (MenuItem) findViewById(R.id.nav_help);
        hp.setChecked(false);
//        MenuItem qt = (MenuItem) findViewById(R.id.nav_quit);
//        qt.setChecked(false);
        MenuItem at = (MenuItem) findViewById(R.id.nav_about);
        at.setChecked(false);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
//        getSupportFragmentManager().putFragment(outState, "startfragment", startFragment);
        super.onSaveInstanceState(outState);
    }
}
