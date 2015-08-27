package com.example.caique.sup.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.caique.sup.Objects.User;
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

import static com.example.caique.sup.Tools.Methods.Post2;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener, HandleConnection {
    EditText mNameET;
    EditText mEmailET;
    EditText mPasswordET;
    Button mRegisterBT;
    ProgressBar mRegisterPB;
    User mUser;
    AsyncTask<String, Void, Request> mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameET = (EditText) findViewById(R.id.register_name);
        mEmailET = (EditText) findViewById(R.id.register_email);
        mPasswordET = (EditText) findViewById(R.id.register_password);
        mRegisterBT = (Button) findViewById(R.id.register_button);
        mRegisterPB = (ProgressBar) findViewById(R.id.register_progress);

        mRegisterBT.setOnClickListener(this);

    }

    private boolean isFormOK( String email, String password){
        if (email.trim().length() > 0
                && password.trim().length() > 3 ) {
            return true;
        } else {
            return false;
        }
    }

    private void registerRequest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = mEmailET.getText().toString();
        String pswd = mPasswordET.getText().toString();
        String name = mNameET.getText().toString();

        if(isFormOK(email, pswd)){
            mUser = new User();
            mUser.setEmail(email);
            mUser.setPassword(pswd);
            mUser.setName(name);
            mUser.setGcm(sharedPreferences.getString(Constants.GCM_ID, ""));
            connectionRequest(mUser.getName(), mUser.getEmail(), mUser.getPassword(), mUser.getGcm(), "REGISTER");
        }
        else {
            Toast.makeText(getApplicationContext(), "INFORMAÇÕES INCOMPLETAS!", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectionRequest(final String name, final String email, final String password,
                                   final String gcm, final String type) {

        mAsyncTask = new AsyncTask<String, Void, Request>(){
//            private JSONObject rawJson = new JSONObject();
            private Request checkRequest = new Request();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mRegisterPB.setVisibility(View.VISIBLE);
                mNameET.setEnabled(false);
                mEmailET.setEnabled(false);
                mPasswordET.setEnabled(false);
                mRegisterBT.setEnabled(false);
            }

            @Override
            protected Request doInBackground(String... strings) {
                /*
                if(!strings[0].isEmpty()) { rawJson.put("name",strings[0]); }
                if(!strings[1].isEmpty()) { rawJson.put("email",strings[1]); }
                if(!strings[2].isEmpty()) { rawJson.put("password",strings[2]); }
                if(!strings[3].isEmpty()) { rawJson.put("type",strings[3]); }
                */
                if(!strings[0].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("name", strings[0])); }
                if(!strings[1].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("email", strings[1])); }
                if(!strings[2].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("password", strings[2])); }
                if(!strings[3].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("gcm", strings[3])); }
                if(!strings[4].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("type", strings[4])); }
                checkRequest.setType(strings[4]);
                checkRequest.setResponse(Post2(getApplicationContext(), nameValuePairs, "user/register"));
                return checkRequest;
            }

            @Override
            protected void onPostExecute(Request request) {
                try {
                    mRegisterPB.setVisibility(View.INVISIBLE);
                    mNameET.setEnabled(true);
                    mEmailET.setEnabled(true);
                    mPasswordET.setEnabled(true);
                    mRegisterBT.setEnabled(true);
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mAsyncTask.execute(name, email, password, gcm, type);
    }

    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();

        switch (request.getType()) {
            case "REGISTER":
                if(response.getString("status").equals("success")) {
                    if(!response.getBoolean("wasRegistered")) {
                        Preferences.saveAccountStatus(getApplication(), true);
                        Preferences.saveUserId(getApplication(), response.getJSONObject("user").getInt("id"));
                        goToHome();
                    } else {
                        Preferences.saveAccountStatus(getApplication(),false);
                        Toast.makeText(getApplication(),"Usuário já cadastrado",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplication(),Constants.TRY_LATER,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void goToHome() {
        Intent activity = new Intent(this, CoordinatorActivity.class);
        startActivity(activity);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                if(Methods.isNetworkAvailable(getApplicationContext())) registerRequest();
                else Toast.makeText(getApplicationContext(),
                        Constants.INTERNET_ERROR,
                        Toast.LENGTH_SHORT)
                        .show();
                break;
        }
    }
}
