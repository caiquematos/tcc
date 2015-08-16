package com.example.caique.sup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.caique.sup.R;

import Tools.Preferences;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Preferences.getAccountStatus(getApplicationContext())) {
            goToHome();
        } else {
            setContentView(R.layout.activity_main);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            goToLogin();
        }
    }

    private void goToHome() {
        Intent activity = new Intent(this, HomeActivity.class);
        startActivity(activity);
        finish();
    }

    private void goToLogin() {
        Intent activity = new Intent(this, LoginActivity.class);
        startActivity(activity);
        finish();
    }

}
