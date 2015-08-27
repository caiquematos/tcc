package com.example.caique.sup.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, HandleConnection{
    private ProgressBar mProgress;
    private EditText mNameET;
    private EditText mEmailET;
    private EditText mCurrentPasswordET;
    private EditText mNewPasswordET;
    private Button mConfirmBT;
    private Button mCancelBT;
    private View mContent;
    private int mUser;
    private AsyncTask<String, Void, Request> mConnection;
    private final String LOAD_PROFILE = "load";
    private final String EDIT_PROFILE = "edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUser = Preferences.getUserId(getApplicationContext());
        mProgress = (ProgressBar) findViewById(R.id.profile_progress);
        mNameET = (EditText) findViewById(R.id.profile_name);
        mEmailET = (EditText) findViewById(R.id.profile_email);
        mCurrentPasswordET = (EditText) findViewById(R.id.profile_current_password);
        mNewPasswordET = (EditText) findViewById(R.id.profile_new_password);
        mContent = findViewById(R.id.profile_content_layout);
        mConfirmBT = (Button) findViewById(R.id.profile_confirm);
        mCancelBT = (Button) findViewById(R.id.profile_cancel);
        mCancelBT.setOnClickListener(this);
        mConfirmBT.setOnClickListener(this);

        if(Methods.isNetworkAvailable(getApplicationContext())){
            loadProfile(LOAD_PROFILE,
                    mUser,
                    null,
                    null,
                    null,
                    null);
        }else {
            Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR,Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProfile(String type, int user, String name, String email, String currentPassword,
                                                                    String newPassword) {
        mConnection = new AsyncTask<String, Void, Request>() {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Request checkRequest = new Request();

            @Override
            protected void onPreExecute() {
                mContent.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Request doInBackground(String... strings) {
                String type = strings[0];
                checkRequest.setType(type);
                switch (type) {
                    case LOAD_PROFILE:
                        if (!strings[1].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("user", strings[1])); }
                        checkRequest.setResponse(Post2(getApplicationContext(), nameValuePairs, "user/edit"));
                    break;
                    case EDIT_PROFILE:
                        if (!strings[1].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("user", strings[1])); }
                        if (!strings[2].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("name", strings[2])); }
                        if(!strings[3].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("email", strings[3])); }
                        if(!strings[4].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("currentPassword", strings[4]));}
                        if (!strings[5].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("newPassword", strings[5])); }
                        break;
                }
                checkRequest.setResponse(Post2(getApplicationContext(), nameValuePairs, "user/" + type));
                return checkRequest;
            }

            @Override
            protected void onPostExecute(Request request) {
                super.onPostExecute(request);
                mProgress.setVisibility(View.GONE);
                mContent.setVisibility(View.VISIBLE);
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mConnection.execute(type,
                            String.valueOf(user),
                            name,
                            email,
                            currentPassword,
                            newPassword);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_confirm:
                if(Methods.isNetworkAvailable(getApplicationContext())) {
                    User user = new User();
                    user.setName(mNameET.getText().toString());
                    user.setEmail(mEmailET.getText().toString());
                    user.setPassword(mCurrentPasswordET.getText().toString());
                    String newPassword = mNewPasswordET.getText().toString();
                    Log.e(getLocalClassName(),"NAME sent:" + user.getName()
                        + " " + user.getEmail() + " " + user.getPassword() + " " + newPassword);
                    if(isFormOK(user.getEmail(), user.getPassword(), newPassword)){
                        loadProfile(EDIT_PROFILE,
                                mUser,
                                user.getName(),
                                user.getEmail(),
                                user.getPassword(),
                                newPassword);
                    }else {
                        Toast.makeText(getApplicationContext(),"Dados preenchidos incorretamente!",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.profile_cancel:
                finish();
                break;
        }
    }

    private boolean isFormOK( String email, String currentPassword, String newPassword){
        if (email.trim().length() > 0
                && currentPassword.trim().length() > 3
                && newPassword.trim().length() > 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();

        if (response != null){
            switch (request.getType()) {
                case LOAD_PROFILE:
                    if(response.getString("status").equals("success")) {
                        JSONObject JSONUser = response.getJSONObject("user");
                        Log.e(getLocalClassName(), "on handle:" + JSONUser.toString());
                        mNameET.setText(JSONUser.getString("name"), TextView.BufferType.EDITABLE);
                        mEmailET.setText(JSONUser.getString("email"));
                    } else {
                        Toast.makeText(getApplication(),Constants.TRY_LATER,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case EDIT_PROFILE:
                    if(response.getString("status").equals("success")) {
                        if(response.getBoolean("checkPassword")) {
                            JSONObject JSONUser = response.getJSONObject("user");
                            Log.d(getLocalClassName(), "User edited:" + JSONUser.toString());
                            Toast.makeText(getApplicationContext(),"Perfil editado com sucesso", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),"Senha atual incorreta!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplication(),Constants.TRY_LATER,Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }else {
            Toast.makeText(getApplication(),Constants.TRY_LATER,Toast.LENGTH_SHORT).show();
        }

    }
}
