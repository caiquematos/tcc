package com.example.caique.sup.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.caique.sup.GCM.RegistrationIntentService;
import com.example.caique.sup.R;
import com.example.caique.sup.Tools.Constants;
import com.example.caique.sup.Tools.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import static com.example.caique.sup.Tools.Constants.GCM_ID;


public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean gcm = sharedPreferences
                .getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);

        if (Preferences.getAccountStatus(getApplicationContext()) && gcm) {
            goToHome();
        } else {
            setContentView(R.layout.activity_main);
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);

                    if (sentToken || !sharedPreferences.getString(GCM_ID,"").isEmpty()) {
                        Log.e(getLocalClassName(), "ON RECEIVE: " + getString(R.string.gcm_send_message));
                        goToLogin();
                    } else {
                        Log.e(getLocalClassName(), "ON RECEIVE: " + getString(R.string.token_error_message));
                        finish();
                    }
                }
            };

            //mInformationTextView = (TextView) findViewById(R.id.informationTextView);

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            } else {
                Toast.makeText(this,Constants.INTERNET_ERROR,Toast.LENGTH_LONG).show();
            }

        }

    }

    private void goToHome() {
        Intent activity = new Intent(this, CoordinatorActivity.class);
        startActivity(activity);
        finish();
    }

    private void goToLogin() {
        Intent activity = new Intent(this, LoginActivity.class);
        startActivity(activity);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(getLocalClassName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
