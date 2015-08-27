package com.example.caique.sup.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caique.sup.Module.CustomAdapter;
import com.example.caique.sup.Objects.Module;
import com.example.caique.sup.R;
import com.example.caique.sup.Tools.Constants;
import com.example.caique.sup.Tools.HandleConnection;
import com.example.caique.sup.Tools.Methods;
import com.example.caique.sup.Tools.Preferences;
import com.example.caique.sup.Tools.Request;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ModuleActivity extends AppCompatActivity implements HandleConnection {
    ProgressBar mModulePB;
    RecyclerView mModuleList;
    AsyncTask<String, Void, Request> mRetrieveModules;
    CustomAdapter mAdapter;
    TextView mTitle;
    Module[] mDataSet;
    int mCoordinator;
    SwipeRefreshLayout mRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        mModulePB = (ProgressBar) findViewById(R.id.module_progress);
        mTitle = (TextView) findViewById(R.id.module_title);
        mModuleList = (RecyclerView) findViewById(R.id.module_list);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.module_refresh);
        mModuleList.setLayoutManager(new LinearLayoutManager(mModuleList.getContext()));

        mRefresh.setColorSchemeResources(R.color.MediumAquamarine, R.color.Aqua, R.color.Aquamarine);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Methods.isNetworkAvailable(getApplicationContext())) {
                    loadModules();
                } else {
                    mRefresh.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRefresh.setVisibility(View.GONE);
        mModuleList.setVisibility(View.GONE);
        mModulePB.setVisibility(View.VISIBLE);

        if (Methods.isNetworkAvailable(getApplicationContext())) {
            loadModules();
        } else {
            mModulePB.setVisibility(View.GONE);
            mRefresh.setVisibility(View.VISIBLE);
            mModuleList.setVisibility(View.VISIBLE);
            mRefresh.setRefreshing(false);
            Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadModules() {
        if (getIntent().getExtras() != null ) {
            mCoordinator = getIntent().getExtras().getInt("coordinator");
            mTitle.setText("Coordenador " + mCoordinator);
            Preferences.saveCurrentCoordinatorId(getApplicationContext(), mCoordinator);
            retrieveModules(String.valueOf(mCoordinator));
            Log.e(getClass().getName(), "intent not null");
        } else {
            mCoordinator = Preferences.getCurrentCoordinatorId(getApplicationContext());
            mTitle.setText("Coordenador " + mCoordinator);
            retrieveModules(String.valueOf(mCoordinator));
            Log.e(getClass().getName(), "intent null");
        }
    }

    private void retrieveModules(String coordinator) {
        mRetrieveModules = new AsyncTask<String, Void, Request>() {
            Request request = new Request();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mRefresh.setRefreshing(true);
            }

            @Override
            protected Request doInBackground(String... strings) {
                if(!strings[0].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("coordinator", strings[0])); }
                request.setResponse(Methods.Post2(getApplicationContext(), nameValuePairs, "module/list"));
                return request;
            }

            @Override
            protected void onPostExecute(Request request) {
                super.onPostExecute(request);
                mModulePB.setVisibility(View.GONE);
                mRefresh.setVisibility(View.VISIBLE);
                mModuleList.setVisibility(View.VISIBLE);
                mRefresh.setRefreshing(false);
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mRetrieveModules.execute(coordinator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_module, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.module_log) {
            goToLog();
            return true;
        }
        if (id == R.id.module_logout) {
            Preferences.saveAccountStatus(getApplicationContext(),false);
            goToLogin();
            return true;
        }
        if (id == R.id.module_profile) {
            goToEditProfile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLog() {
        Intent activity = new Intent(this, LogActivity.class);
        startActivity(activity);
    }

    private void goToLogin() {
        Intent activity = new Intent(this, LoginActivity.class);
        startActivity(activity);
        finish();
    }

    private void goToEditProfile() {
        Intent activity = new Intent(this, ProfileActivity.class);
        startActivity(activity);
    }

    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();
        Log.e(getLocalClassName(), "response json: " + response);
        JSONArray modules;

        if ( response != null) {
            modules = response.getJSONArray("modules");
            List<Module> moduleList = listModule(modules);
            Log.e(getLocalClassName(), "num posts: " + moduleList.size());
            if (moduleList.size() >= 0) {
                initDataSet(moduleList);
                mModuleList.setVisibility(View.VISIBLE);
                mAdapter = new CustomAdapter(this, mDataSet);
                mModuleList.setAdapter(mAdapter);
            }
        }
        else {
            Toast.makeText(getApplication(), Constants.TRY_LATER, Toast.LENGTH_SHORT).show();
        }
    }

    private List<Module> listModule(JSONArray modules) throws JSONException {
        List<Module> list = new ArrayList<>();

        for (int i = 0; i < modules.length(); i++) {
            JSONObject JSONModules = modules.getJSONObject(i);
            Module module = new Module();
            module.setId(JSONModules.getInt("id"));
            module.setStatus(JSONModules.getString("status").charAt(0));
            module.setBattery((float) JSONModules.getDouble("battery"));
            module.setNumOfSamples(JSONModules.getInt("numb_of_samples"));
            module.setCoordinator(JSONModules.getInt("coordinator"));
            module.setPackegesFrequency(JSONModules.getInt("packFrequency"));
            module.setSleepTime(Time.valueOf(JSONModules.getString("sleepTime")));
            module.setSleepFrequency(JSONModules.getInt("sleepFrequency"));
            module.setCreatedAt(Timestamp.valueOf(JSONModules.getString("created_at")));
            list.add(module);
        }

        return list;
    }

    private void initDataSet(List<Module> moduleList) {
        mDataSet = new Module[moduleList.size()];
        for (int i = 0; i < moduleList.size(); i++) {
            mDataSet[i] = moduleList.get(i);
            Log.e(getLocalClassName(), "module " +i+ ":" + mDataSet[i].toString());
        }
    }
}
