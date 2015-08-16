package com.example.caique.sup.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.caique.sup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import Coordinator.CustomAdapter;
import Objects.Coordinator;
import Tools.Constants;
import Tools.HandleConnection;
import Tools.Methods;
import Tools.Preferences;
import Tools.Request;

public class HomeActivity extends ActionBarActivity implements HandleConnection {
    ProgressBar mCoordinatorPB;
    RecyclerView mCoordinatorList;
    AsyncTask<String, Void, Request> mRetrieveCoordinators;
    CustomAdapter mAdapter;
    Coordinator[] mDataSet;
    SwipeRefreshLayout mRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mCoordinatorPB = (ProgressBar) findViewById(R.id.coordinator_progress);
        mCoordinatorList = (RecyclerView) findViewById(R.id.coordinator_list);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.coordinator_refresh);
        mCoordinatorList.setLayoutManager(new LinearLayoutManager(mCoordinatorList.getContext()));

        mRefresh.setColorSchemeResources(R.color.MediumAquamarine, R.color.Aqua, R.color.Aquamarine);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Methods.isNetworkAvailable(getApplicationContext())) {
                    retrieveCoordinators();
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
        mCoordinatorList.setVisibility(View.GONE);
        mCoordinatorPB.setVisibility(View.VISIBLE);

        if (Methods.isNetworkAvailable(getApplicationContext())) {
            retrieveCoordinators();
        } else {
            mCoordinatorPB.setVisibility(View.GONE);
            mRefresh.setVisibility(View.VISIBLE);
            mCoordinatorList.setVisibility(View.VISIBLE);
            mRefresh.setRefreshing(false);
            Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveCoordinators() {
        mRetrieveCoordinators = new AsyncTask<String, Void, Request>() {
            JSONObject rawJson = new JSONObject();
            Request request = new Request();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mRefresh.setRefreshing(true);
            }

            @Override
            protected Request doInBackground(String... params) {
                try {
                    request.setResponse(Methods.POST(getApplicationContext(), rawJson, "coordinator/list"));
                    return request;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Falha na conexão", Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Request request) {
                super.onPostExecute(request);
                mCoordinatorPB.setVisibility(View.GONE);
                mRefresh.setVisibility(View.VISIBLE);
                mCoordinatorList.setVisibility(View.VISIBLE);
                mRefresh.setRefreshing(false);
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mRetrieveCoordinators.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_logout) {
            Preferences.saveAccountStatus(getApplicationContext(),false);
            goToLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent activity = new Intent(this, LoginActivity.class);
        startActivity(activity);
        finish();
    }

    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();
        Log.e(getLocalClassName(), "response json: " + response);
        JSONArray coordinators;

        if ( response != null) {
            coordinators = response.getJSONArray("coordinators");
            List<Coordinator> coordinatorList = listCoordinator(coordinators);
            Log.e(getLocalClassName(), "num posts: " + coordinatorList.size());
            if (coordinatorList.size() >= 0) {
                initDataSet(coordinatorList);
                mCoordinatorList.setVisibility(View.VISIBLE);
                mAdapter = new CustomAdapter(getApplicationContext(), mDataSet);
                mCoordinatorList.setAdapter(mAdapter);
            }
        }
        else {
                Toast.makeText(getApplication(), Constants.TRY_LATER, Toast.LENGTH_SHORT).show();
        }
    }

    private List<Coordinator> listCoordinator(JSONArray coordinators) throws JSONException {
        List<Coordinator> list = new ArrayList<>();

        for (int i = 0; i < coordinators.length(); i++) {
            JSONObject JSONCoordinators = coordinators.getJSONObject(i);
            Log.e(getLocalClassName(), "on listCoordinator: " + JSONCoordinators);
            Coordinator coordinator = new Coordinator();
            coordinator.setNetwork(JSONCoordinators.getInt("network"));
            coordinator.setNumOfModules(JSONCoordinators.getInt("numb_of_modules"));
            coordinator.setBattery((float) JSONCoordinators.getDouble("battery"));
            coordinator.setStatus(JSONCoordinators.getString("status").charAt(0));
            coordinator.setId(JSONCoordinators.getInt("id"));
            coordinator.setCreatedAt(Timestamp.valueOf(JSONCoordinators.getString("created_at")));
            list.add(coordinator);
        }

        return list;
    }

    private void initDataSet(List<Coordinator> coordinatorList) {
        mDataSet = new Coordinator[coordinatorList.size()];
        for (int i = 0; i < coordinatorList.size(); i++) {
            mDataSet[i] = coordinatorList.get(i);
            Log.e(getLocalClassName(), "coordinator " +i+ ":" + mDataSet[i].toString());
        }
    }

}
