package com.example.caique.sup.Activities;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caique.sup.R;
import com.example.caique.sup.Tools.Constants;
import com.example.caique.sup.Tools.HandleConnection;
import com.example.caique.sup.Tools.Methods;
import com.example.caique.sup.Tools.Preferences;
import com.example.caique.sup.Tools.Request;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorDialog extends AppCompatActivity implements HandleConnection {
    TextView mBattery;
    TextView mNumbOfMod;
    Switch mSwitch;
    CheckBox mCheckbox;
    TextView mCoordinatorTitle;
    char mStatus;
    int mUser;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_dialog);

        mBattery = (TextView) findViewById(R.id.coo_dial_battery);
        mSwitch = (Switch) findViewById(R.id.coo_dial_switch);
        mCheckbox = (CheckBox) findViewById(R.id.coo_dial_checkbox);
        mNumbOfMod = (TextView) findViewById(R.id.coo_dial_modules);
        mCoordinatorTitle = (TextView) findViewById(R.id.coo_dial_title);
        mUser = Preferences.getUserId(this);

        mCoordinatorTitle.setText("Cordenador " + getIntent().getExtras().getInt("id"));
        mBattery.setText("" + getIntent().getExtras().getFloat("battery") + "%");
        mNumbOfMod.setText("" + getIntent().getExtras().getInt("numb_of_modules"));
        mStatus = getIntent().getExtras().getChar("status");
        Log.e(getLocalClassName(),"Status: " + mStatus);

        if (mStatus == '1') {
            if (mSwitch != null) mSwitch.setChecked(true);
            if (mCheckbox != null) mCheckbox.setChecked(true);
        }
        else {
            if (mSwitch != null) mSwitch.setChecked(false);
            if (mCheckbox != null) mCheckbox.setChecked(false);
        }

        if (mSwitch != null)
            mSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Methods.isNetworkAvailable(getApplicationContext()))
                        if (mStatus == '1') switchCoordinator(getIntent().getExtras().getInt("id"), 0, mUser);
                        else switchCoordinator(getIntent().getExtras().getInt("id"), 1, mUser);
                    else {
                        Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        if (mSwitch.isChecked()) mSwitch.setChecked(false);
                        else mSwitch.setChecked(true);
                    }
                }
            });

        if (mCheckbox != null)
            mCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Methods.isNetworkAvailable(getApplicationContext()))
                        if (mStatus == '1') switchCoordinator(getIntent().getExtras().getInt("id"), 0, mUser);
                        else switchCoordinator(getIntent().getExtras().getInt("id"), 1, mUser);
                    else {
                        Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        if (mCheckbox.isChecked()) mCheckbox.setChecked(false);
                        else mCheckbox.setChecked(true);
                    }
                }
            });
    }

    private void switchCoordinator(int coordinator, int status, int user) {
        AsyncTask<String, Void, Request> switchRequest = new AsyncTask<String, Void, Request>() {
            Request request = new Request();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mSwitch != null) mSwitch.setEnabled(false);
                if (mCheckbox != null) mCheckbox.setEnabled(false);
            }

            @Override
            protected Request doInBackground(String... strings) {
                if(!strings[0].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("coordinator", strings[0])); }
                if(!strings[1].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("status", strings[1])); }
                if(!strings[2].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("user", strings[2])); }
                request.setResponse(Methods.Post2(getApplicationContext(), nameValuePairs, "coordinator/switch"));
                return request;
            }

            @Override
            protected void onPostExecute(Request request) {
                super.onPostExecute(request);
                if (mSwitch != null) mSwitch.setEnabled(true);
                if (mCheckbox != null) mCheckbox.setEnabled(true);
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        switchRequest.execute(String.valueOf(coordinator), String.valueOf(status), String.valueOf(user));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();
        Log.e(getClass().getName(), "response json: " + response);

        if ( response != null && response.getString("result").equals("success")) {
            if (response.getInt("status") == 1) {
                if (mSwitch != null) mSwitch.setChecked(true);
                if (mCheckbox != null) mCheckbox.setChecked(true);
                mStatus = '1';
            }
            else {
                if (mSwitch != null) mSwitch.setChecked(false);
                if (mCheckbox != null) mCheckbox.setChecked(false);
                mStatus = '0';
            }
        }
        else {
            Toast.makeText(getApplicationContext(), Constants.TRY_LATER, Toast.LENGTH_SHORT).show();
        }
    }
}
