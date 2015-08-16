package com.example.caique.sup.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.caique.sup.R;

import org.json.JSONException;
import org.json.JSONObject;

import Objects.User;
import Tools.Constants;
import Tools.HandleConnection;
import Tools.Preferences;
import Tools.Request;

import static Tools.Methods.POST;
import static Tools.Methods.isNetworkAvailable;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener, HandleConnection {
    EditText mEmailET;
    EditText mPasswordET;
    Button mLoginBT;
    Button mRegisterBT;
    ProgressBar mLoginPB;
    User mUser;
    AsyncTask<String, Void, Request> mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailET = (EditText) findViewById(R.id.login_email);
        mPasswordET = (EditText) findViewById(R.id.login_password);
        mLoginBT = (Button) findViewById(R.id.login_button);
        mRegisterBT = (Button) findViewById(R.id.login_register);
        mLoginPB = (ProgressBar) findViewById(R.id.login_progress);

        mLoginBT.setOnClickListener(this);
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

    private void goToRegister() {
        Intent activity = new Intent(this, RegisterActivity.class);
        startActivity(activity);
        finish();
    }

    private void goToHome() {
        Intent activity = new Intent(this, HomeActivity.class);
        startActivity(activity);
        finish();
    }

    private void loginRequest() {
        String email = mEmailET.getText().toString();
        String pswd = mPasswordET.getText().toString();

        if(isFormOK(email, pswd)){
            mUser = new User();
            mUser.setEmail(email);
            mUser.setPassword(pswd);
            connectionRequest(mUser.getEmail(), mUser.getPassword(), "LOGIN");
        }
        else {
            Toast.makeText(getApplicationContext(), "INFORMAÇÕES INCOMPLETAS!", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectionRequest(final String email, final String password, final String type) {

        mAsyncTask = new AsyncTask<String, Void, Request>(){
            private JSONObject rawJson = new JSONObject();
            private Request checkRequest = new Request();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLoginPB.setVisibility(View.VISIBLE);
                mEmailET.setEnabled(false);
                mPasswordET.setEnabled(false);
                mLoginBT.setEnabled(false);
            }

            @Override
            protected Request doInBackground(String... strings) {
                try {
                    if(!strings[0].isEmpty()) { rawJson.put("email",strings[0]); }
                    if(!strings[1].isEmpty()) { rawJson.put("password",strings[1]); }
                    if(!strings[2].isEmpty()) { rawJson.put("type",strings[2]); }
                    checkRequest.setType(strings[2]);
                    checkRequest.setResponse(POST(getApplicationContext(),rawJson,"user/login"));
                    return checkRequest;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Request request) {
                try {
                    mLoginPB.setVisibility(View.INVISIBLE);
                    mEmailET.setEnabled(true);
                    mPasswordET.setEnabled(true);
                    mLoginBT.setEnabled(true);
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mAsyncTask.execute(email, password, type);
    }

    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();

        switch (request.getType()) {
            case "LOGIN":
                if(response.getString("status").equals("success")) {
                    if(response.getBoolean("wasRegistered")) {
                        Preferences.saveAccountStatus(getApplication(), true);
                        Preferences.saveUserId(getApplication(), response.getJSONObject("user").getInt("id"));
                        goToHome();
                    } else {
                        Preferences.saveAccountStatus(getApplication(),false);
                        Toast.makeText(getApplication(),"Email ou senha incorreta",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplication(),Constants.TRY_LATER,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if(isNetworkAvailable(getApplicationContext())) loginRequest();
                else Toast.makeText(getApplicationContext(),
                        Constants.INTERNET_ERROR,
                        Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.login_register:
                goToRegister();
                break;
        }
    }
}
