package com.example.caique.sup.GCM;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.caique.sup.Tools.Constants;
import com.example.caique.sup.Tools.HandleConnection;
import com.example.caique.sup.Tools.Methods;
import com.example.caique.sup.Tools.Preferences;
import com.example.caique.sup.Tools.Request;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by caique on 18/08/15.
 */
public class RegistrationIntentService extends IntentService implements HandleConnection{
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    AsyncTask<String, Void, Request> mRegistrationToServer;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken("510998002514",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);

                if (Methods.isNetworkAvailable(getApplicationContext()) && Preferences.getAccountStatus(this))
                    sendRegistrationToServer(token, String.valueOf(Preferences.getUserId(getApplicationContext())), "GCM");
                else
                    Toast.makeText(getApplication(),Constants.INTERNET_ERROR,Toast.LENGTH_SHORT).show();

                subscribeTopics(token);

                sharedPreferences.edit().putString(Constants.GCM_ID, token).apply();
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    private void sendRegistrationToServer(String token, String user, String gcm) {
        mRegistrationToServer = new AsyncTask<String, Void, Request>() {
            Request request = new Request();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Request doInBackground(String... strings) {
                Log.e(getClass().getName(),"Registration Token REQUEST: " + strings[0]);
                if(!strings[0].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("gcm", strings[0])); }
                if(!strings[1].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("user", strings[1])); }
                if(!strings[2].isEmpty()) { request.setType(strings[2]); }
                request.setResponse(Methods.Post2(getApplicationContext(), nameValuePairs, "user/save-gcm"));
                return request;
            }

            @Override
            protected void onPostExecute(Request request) {
                super.onPostExecute(request);
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mRegistrationToServer.execute(token,user,gcm);
    }

    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (response != null)
            switch (request.getType()) {
                case "GCM":
                    if(response.getString("status").equals("success")) {
                        Log.e(getClass().getName(), "gcm registrado no server: "
                                + response.getString("gcm"));
                        sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
                    } else {
                        Toast.makeText(getApplication(),Constants.TRY_LATER,Toast.LENGTH_SHORT).show();
                        sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
                    }
                    break;
            }
        else
            Toast.makeText(getApplication(),Constants.INTERNET_ERROR,Toast.LENGTH_SHORT).show();
    }
}
