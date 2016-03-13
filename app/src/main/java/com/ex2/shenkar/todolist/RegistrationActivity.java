package com.ex2.shenkar.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class RegistrationActivity extends BaseActivity {

    public static int CREATE_TEAM = 1;
    public static int JOIN_TEAM = 2;

    private ViewFlipper screenFlipper;
    private User.Type userType;
    EditText emailInput;
    EditText mobileInput;
    Button   regBtn;
    ProgressBar regLoader;
    EditText regTeamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        screenFlipper = (ViewFlipper) findViewById(R.id.registrationFormFlipper);
        emailInput = (EditText) findViewById(R.id.regEmail);
        mobileInput = (EditText) findViewById(R.id.regMobile);
        regBtn      = (Button) findViewById(R.id.regBtn);
        regLoader = (ProgressBar) findViewById(R.id.regLoader);
        regTeamName = (EditText) findViewById(R.id.regTeamName);

        showLoader(false);

        findViewById(R.id.createTeamBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType = User.Type.MANAGER;
                screenFlipper.setDisplayedChild(CREATE_TEAM);
            }
        });

        findViewById(R.id.joinTeamBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType = User.Type.TEAMMATE;
                screenFlipper.setDisplayedChild(JOIN_TEAM);
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (userType) {

                    case MANAGER:

                        regBtn.setActivated(false);
                        // register new manager with parse
                        new NewUser(emailInput.getText().toString(), mobileInput.getText().toString(), userType)
                                .register(RegistrationActivity.this, mobileInput.getText().toString(), regTeamName.getText().toString(),  new UserCreationCallback() {
                                    @Override
                                    public void success(RegisteredUser user) {

                                        Toast.makeText(RegistrationActivity.this, "registratered succesfully", Toast.LENGTH_LONG).show();
                                        // open create team screen
                                        startActivity(new Intent(RegistrationActivity.this, Welcome.class));
                                        finish();
                                    }

                                    @Override
                                    public void failed() {

                                        regBtn.setActivated(true);
                                        Toast.makeText(RegistrationActivity.this, "registration failed", Toast.LENGTH_LONG).show();
                                    }
                                });


                        break;
                }
            }
        });

        final EditText loginEmail = (EditText) findViewById(R.id.loginEmail);
        final EditText loginPass = (EditText) findViewById(R.id.loginPass);

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoader(true);

                // login
                RegisteredUser.login(loginEmail.getText().toString(), loginPass.getText().toString(), RegistrationActivity.this, new LoginCallback() {
                    @Override
                    public void success(int id) {

                        showLoader(false);

                        // close activity and open welcome activity
                        startActivity(new Intent(RegistrationActivity.this, Welcome.class));
                        finish();
                    }

                    @Override
                    public void failed(String error) {

                        showLoader(false);

                        Toast.makeText(RegistrationActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(screenFlipper.getDisplayedChild() == 0) {
            super.onBackPressed();
        }else{
            screenFlipper.setDisplayedChild(0);
        }
    }

    private void showLoader(boolean show){

        if(regLoader != null)
            regLoader.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}
