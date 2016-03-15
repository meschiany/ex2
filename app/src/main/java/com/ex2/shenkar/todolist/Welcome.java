package com.ex2.shenkar.todolist;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shenkar.activity.CreateTeamFragment;
import com.shenkar.activity.FragmentDrawer;
import com.shenkar.activity.SettingsFragment;
import com.shenkar.activity.WelcomeFragement;

import java.util.ArrayList;

public class Welcome extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private RegisteredUser user;
    private Team team;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setupMenu();

        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        // display the first navigation drawer view on app launch
        //displayView(0);

        // loading...
        loading(true);

        /**
         * get user from cache, if not existing show registration options
         */
        RegisteredUser.getUser(this, new RegisteredUserCallback() {

            @Override
            public void successful(final RegisteredUser user) {

                loading(false);

                Welcome.this.user = user;

                drawerFragment.setUser(user);

                // check user type
                switch (user.getType()) {

                    case MANAGER:

                        Log.i("Main", "manager user found");

                        /**
                         * load  team
                         */
                        Team.getTeam(user.getTeamId(), Welcome.this, new TeamLoadCallback() {

                            @Override
                            public void successful(Team team) {

                                Welcome.this.team = team;

                                /**
                                 * check if team is empty open team manager
                                 */
                                if(team.getTeamMates().size() == 0){

                                    displayView(2);
                                }else{
                                    displayView(0);
                                }
                            }

                            @Override
                            public void failed() {

                                loading(false);
                                notifyUser(getString(R.string.failed_loading_team) + user.getTeamId());
                            }
                        });

                        break;

                    case TEAMMATE:

                        //show teammate fragement
                        displayView(1);
                        break;

                    default:

                        Log.e("Main", "invalid user found");
                        finish();
                        startActivity(new Intent(Welcome.this, RegistrationActivity.class));
                        break;
                }
            }

            @Override
            public void failed() {

                loading(false);
                Log.e("Main", "failed loading local user");
                finish();
                startActivity(new Intent(Welcome.this, RegistrationActivity.class));
            }
        });
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public  void displayView(int position) {
        android.support.v4.app.Fragment fragment = null;
        String title = getString(R.string.app_name);

        if(user != null && user.getType() == User.Type.MANAGER) {
            switch (position) {


                /**
                 * this will be the teammate list fragment
                 */
                case 0:

                    title = getString(R.string.task_list);
                    break;

                /**
                 * this is the team management frag
                 */
                case 1:

                    fragment = new CreateTeamFragment();
                    title = getString(R.string.team_managment);
                    break;

                /**
                 * settings
                 */
                case 2:
                    fragment = new SettingsFragment();
                    title = "Settings";
                    break;


                /**
                 * logout
                 */
                case 3:

                    RegisteredUser.save(Welcome.this, 0);
                    finish();
                    startActivity(new Intent(Welcome.this, RegistrationActivity.class));
                    break;

            }
        }else{

            switch (position) {


                /**
                 * this will be the teammate list fragment
                 */
                case 0:

                    title = getString(R.string.task_list);
                    break;

                 /* settings
                 */
                case 1:
                    fragment = new SettingsFragment();
                    title = "Settings";
                    break;


                /**
                 * logout
                 */
                case 2:

                    RegisteredUser.save(Welcome.this, 0);
                    finish();
                    startActivity(new Intent(Welcome.this, RegistrationActivity.class));
                    break;

            }
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupMenu(){

        if(drawerFragment == null){

            drawerFragment = (FragmentDrawer)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
            drawerFragment.setDrawerListener(this);
        }
    }

    public void notifyUser(String msg){

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public RegisteredUser getUser(){

        return user;
    }

    public void loading(boolean showLoading){

        if(progressBar != null){
            progressBar.setVisibility(showLoading ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public Team getTeam(){

        return team;
    }
}
