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
import Tools.Methods;
import Tools.Preferences;
import Tools.Request;

import static Tools.Methods.POST;

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
        String email = mEmailET.getText().toString();
        String pswd = mPasswordET.getText().toString();
        String name = mNameET.getText().toString();

        if(isFormOK(email, pswd)){
            mUser = new User();
            mUser.setEmail(email);
            mUser.setPassword(pswd);
            mUser.setName(name);
            connectionRequest(mUser.getName(), mUser.getEmail(), mUser.getPassword(), "REGISTER");
        }
        else {
            Toast.makeText(getApplicationContext(), "INFORMAÇÕES INCOMPLETAS!", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectionRequest(final String name, final String email, final String password, final String type) {

        mAsyncTask = new AsyncTask<String, Void, Request>(){
            private JSONObject rawJson = new JSONObject();
            private Request checkRequest = new Request();

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
                try {
                    if(!strings[0].isEmpty()) { rawJson.put("name",strings[0]); }
                    if(!strings[1].isEmpty()) { rawJson.put("email",strings[1]); }
                    if(!strings[2].isEmpty()) { rawJson.put("password",strings[2]); }
                    if(!strings[3].isEmpty()) { rawJson.put("type",strings[3]); }
                    checkRequest.setType(strings[3]);
                    checkRequest.setResponse(POST(getApplicationContext(),rawJson,"user/register"));
                    return checkRequest;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
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

        mAsyncTask.execute(name, email, password, type);
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
        Intent activity = new Intent(this, HomeActivity.class);
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
