package com.example.caique.sup.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.List;

public class ModuleDialog extends ActionBarActivity implements HandleConnection{
    Switch mSwitch;
    CheckBox mCheckBox;
    TextView mBattery;
    TextView mPackFreq;
    TextView mSleepTime;
    TextView mSleepFreq;
    TextView mNumbOfSamp;
    TextView mCoordinatorTV;
    TextView mTitle;
    char mStatus;
    int mCoordinator;
    float mPackFreqValue;
    int mSleepFreqValue;
    View mPackFreqLayout;
    View mSleepTimeLayout;
    View mSleepFreqLayout;
    EditText mSleepFreqET;
    NumberPicker mSleepFreqNP;
    EditText mPackFreqET;
    NumberPicker mPackFreqNP;
    AsyncTask<String, Void, Request> mModuleRequest;
    int mUser;

    static final String STATUS = "status";
    static final String PACK_FREQUENCY = "pack_frequency";
    static final String SLEEP_TIME = "sleep_time";
    static final String SLEEP_FREQUENCY = "sleep_frequency";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_dialog);

        mSwitch = (Switch) findViewById(R.id.mod_dial_switch);
        mCheckBox = (CheckBox) findViewById(R.id.mod_dial_checkbox);
        mBattery = (TextView) findViewById(R.id.mod_dial_battery);
        mPackFreq = (TextView) findViewById(R.id.mod_dial_pack_freq);
        mSleepTime = (TextView) findViewById(R.id.mod_dial_sleep_time);
        mSleepFreq = (TextView) findViewById(R.id.mod_dial_sleep_frequency);
        mNumbOfSamp = (TextView) findViewById(R.id.mod_dial_samples);
        mCoordinatorTV = (TextView) findViewById(R.id.mod_dial_coordinator);
        mTitle = (TextView) findViewById(R.id.mod_dial_title);
        mPackFreqLayout = findViewById(R.id.mod_pack_freq_layout);
        mSleepTimeLayout = findViewById(R.id.mod_sleep_time_layout);
        mSleepFreqLayout = findViewById(R.id.mod_sleep_freq_layout);
        mUser = Preferences.getUserId(this);

        mBattery.setText("" + getIntent().getExtras().getFloat("battery") + "%");
        mSleepFreqValue = getIntent().getExtras().getInt("sleepFrequency");
        mSleepFreq.setText("" + mSleepFreqValue);
        mSleepTime.setText("" + getIntent().getExtras().getString("sleepTime"));
        mPackFreqValue = getIntent().getExtras().getFloat("packFrequency");
        mPackFreq.setText("" + mPackFreqValue);
        mNumbOfSamp.setText("" + getIntent().getExtras().getInt("numbOfSamples"));
        mTitle.setText("Módulo " + getIntent().getExtras().getInt("id"));
        mStatus = getIntent().getExtras().getChar("status");
        mCoordinator = getIntent().getExtras().getInt("coordinator");
        mCoordinatorTV.setText("" + mCoordinator);

        if (mStatus == '1') {
            if (mSwitch != null) mSwitch.setChecked(true);
            if (mCheckBox != null) mCheckBox.setChecked(true);
        }
        else {
            if (mSwitch != null) mSwitch.setChecked(false);
            if (mCheckBox != null) mCheckBox.setChecked(false);
        }

        if (mSwitch != null)
            mSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Methods.isNetworkAvailable(getApplicationContext()))
                        if (mStatus == '1')
                            moduleRequest(getIntent().getExtras().getInt("id"),
                                0,
                                -1,
                                null,
                                -1,
                                STATUS,
                                    mUser);
                        else
                            moduleRequest(getIntent().getExtras().getInt("id"),
                                    1,
                                    -1,
                                    null,
                                    -1,
                                    STATUS,
                                    mUser);
                    else {
                        Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        if (mSwitch.isChecked()) mSwitch.setChecked(false);
                        else mSwitch.setChecked(true);
                    }
                }
            });

        if (mCheckBox != null)
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Methods.isNetworkAvailable(getApplicationContext())) {
                        if (mStatus == '1')
                            moduleRequest(getIntent().getExtras().getInt("id"),
                                    0,
                                    -1,
                                    null,
                                    -1,
                                    STATUS,
                                    mUser);
                        else
                            moduleRequest(getIntent().getExtras().getInt("id"),
                                    1,
                                    -1,
                                    null,
                                    -1,
                                    STATUS,
                                    mUser);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        if (mCheckBox.isChecked()) mCheckBox.setChecked(false);
                        else mCheckBox.setChecked(true);
                    }
                }
            });

        mPackFreqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ModuleDialog.this);
                builder.setTitle("Frequência de Pacotes");
                LayoutInflater inflater = ModuleDialog.this.getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.pick_number_dialog, null));

                builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mPackFreqNP != null) {
                            Log.e(getLocalClassName(), "Pack Freq em NP:" + mPackFreqNP.getValue());
                            moduleRequest(getIntent().getExtras().getInt("id"),
                                    -1,
                                    mPackFreqNP.getValue(),
                                    null,
                                    -1,
                                    PACK_FREQUENCY,
                                    mUser);
                        } else {
                            if (mPackFreqET != null) {
                                Log.e(getLocalClassName(), "Pack Freq em ET:" + mPackFreqET.getText().toString());
                                moduleRequest(getIntent().getExtras().getInt("id"),
                                        -1,
                                        Integer.parseInt(mPackFreqET.getText().toString()),
                                        null,
                                        -1,
                                        PACK_FREQUENCY,
                                        mUser);
                            }
                        }
                    }
                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                mPackFreqET = (EditText) dialog.findViewById(R.id.dial_pick_number_ET);
                mPackFreqNP = (NumberPicker) dialog.findViewById(R.id.dial_pick_number);

                if(mPackFreqNP != null) {
                    mPackFreqNP.setMaxValue(500);
                    mPackFreqNP.setMinValue(0);
                    mPackFreqNP.setWrapSelectorWheel(false);
                    mPackFreqNP.setValue((int) mPackFreqValue);
                    Log.e(getLocalClassName(), "NP not null");
                }

                if(mPackFreqET != null) {
                    Log.e(getLocalClassName(), "ET not null");
                }

            }
        });

        mSleepTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar currentTime = Calendar.getInstance();
                final int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(ModuleDialog.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        moduleRequest(getIntent().getExtras().getInt("id"),
                                -1,
                                -1,
                                hourOfDay + ":" + minute + ":00",
                                -1,
                                SLEEP_TIME,
                                mUser);
                    }

                },hour, minute, true);
                dialog.setTitle("Sleep Time");
                dialog.show();

            }
        });

        mSleepFreqLayout.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ModuleDialog.this);
                builder.setTitle("Sleep Frequency");
                LayoutInflater inflater = ModuleDialog.this.getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.pick_number_dialog, null));

                builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(mSleepFreqNP != null) {
                            Log.e(getLocalClassName(), "Sleep Freq NP:" + mSleepFreqNP.getValue());
                            moduleRequest(getIntent().getExtras().getInt("id"),
                                    -1,
                                    -1,
                                    null,
                                    mSleepFreqNP.getValue(),
                                    SLEEP_FREQUENCY,
                                    mUser);
                        } else {
                            if (mSleepFreqET != null) {
                                Log.e(getLocalClassName(), "Sleep Freq ET:" + mSleepFreqET.getText().toString());
                                moduleRequest(getIntent().getExtras().getInt("id"),
                                        -1,
                                        -1,
                                        null,
                                        Integer.parseInt(mSleepFreqET.getText().toString()),
                                        SLEEP_FREQUENCY,
                                        mUser);
                            }
                        }
                    }
                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                mSleepFreqET = (EditText) dialog.findViewById(R.id.dial_pick_number_ET);
                mSleepFreqNP = (NumberPicker) dialog.findViewById(R.id.dial_pick_number);

                if(mSleepFreqNP != null) {
                    mSleepFreqNP.setMaxValue(30);
                    mSleepFreqNP.setMinValue(0);
                    mSleepFreqNP.setWrapSelectorWheel(false);
                    mSleepFreqNP.setValue(mSleepFreqValue);
                    Log.e(getLocalClassName(), "NP not null");
                }

                if(mSleepFreqET != null) {
                    Log.e(getLocalClassName(), "ET not null");
                }

            }
        });

    }

    private void moduleRequest(int module, int status, int packFrequency, String sleepTime,
                               int sleepFrequency, String type, int user) {

        mModuleRequest = new AsyncTask<String, Void, Request>() {
            Request request = new Request();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mSwitch != null) mSwitch.setEnabled(false);
                if (mCheckBox != null) mCheckBox.setEnabled(false);
            }

            @Override
            protected Request doInBackground(String... strings) {
                String type = null;
                Log.e(getLocalClassName(),"TYPE REQUEST: " + strings[0]);
                if(!strings[0].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("module", strings[0])); }
                if(!strings[6].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("user", strings[6])); }

                switch (strings[5]) {
                     case STATUS:
                         if(!strings[1].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("status", strings[1])); }
                         type = "status";
                         break;
                     case PACK_FREQUENCY:
                         if(!strings[2].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("packFrequency", strings[2])); }
                         type = "pack-frequency";
                         break;
                     case SLEEP_TIME:
                         if(!strings[3].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("sleepTime", strings[3])); }
                         type = "sleep-time";
                         break;
                     case SLEEP_FREQUENCY:
                         if(!strings[4].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("sleepFrequency", strings[4])); }
                         type = "sleep-frequency";
                         break;
                 }
                request.setResponse(Methods.Post2(getApplicationContext(), nameValuePairs, "module/" + type));
                request.setType(strings[5]);
                return request;
            }

            @Override
            protected void onPostExecute(Request request) {
                super.onPostExecute(request);
                if (mSwitch != null) mSwitch.setEnabled(true);
                if (mCheckBox != null) mCheckBox.setEnabled(true);
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Log.e(getLocalClassName(), "ESTOU AQUI!");

        mModuleRequest.execute(String.valueOf(module),
                String.valueOf(status),
                String.valueOf(packFrequency),
                sleepTime,
                String.valueOf(sleepFrequency),
                type,
                String.valueOf(user));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();
        Log.e(getClass().getName(), "response json: " + response);

        if ( response != null && response.getString("result").equals("success")) {
            switch (request.getType()){
                case STATUS:
                    if (response.getInt("status") == 1) {
                        if (mSwitch != null) mSwitch.setChecked(true);
                        if (mCheckBox != null) mCheckBox.setChecked(true);
                        mStatus = '1';
                    }
                    else {
                        if (mSwitch != null) mSwitch.setChecked(false);
                        if (mCheckBox != null) mCheckBox.setChecked(false);
                        mStatus = '0';
                    }
                    break;
                case PACK_FREQUENCY:
                    mPackFreq.setText(response.getString("packFrequency"));
                    break;
                case SLEEP_TIME:
                    mSleepTime.setText(response.getString("sleepTime"));
                    break;
                case SLEEP_FREQUENCY:
                    mSleepFreq.setText(response.getString("sleepFrequency"));
                    break;
            }
        }
        else {
            Toast.makeText(getApplicationContext(), Constants.TRY_LATER, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent();
        Log.e(getLocalClassName(), "coordinator passed: " + mCoordinator);
        intent.putExtra("coordinator", mCoordinator);
        setResult(ModuleActivity.RESULT_OK, intent);
        super.onDestroy();
    }
}
