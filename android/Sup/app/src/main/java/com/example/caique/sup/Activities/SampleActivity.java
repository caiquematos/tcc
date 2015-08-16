package com.example.caique.sup.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caique.sup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import Objects.Sample;
import Sample.CustomAdapter;
import Tools.Constants;
import Tools.HandleConnection;
import Tools.Methods;
import Tools.Request;

public class SampleActivity extends ActionBarActivity implements HandleConnection {
    RecyclerView mSampleList;
    ProgressBar mSamplePB;
    CustomAdapter mAdapter;
    AsyncTask<String, Void, Request> mRetrieveSamples;
    Sample[] mDataSet;
    TextView mTitle;
    SwipeRefreshLayout mRefresh;
    int mModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mTitle = (TextView) findViewById(R.id.sample_title);
        mSamplePB = (ProgressBar) findViewById(R.id.sample_progress);
        mSampleList = (RecyclerView) findViewById(R.id.sample_list);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.sample_refresh);
        mSampleList.setLayoutManager(new LinearLayoutManager(mSampleList.getContext()));

        mRefresh.setColorSchemeResources(R.color.MediumAquamarine, R.color.Aqua, R.color.Aquamarine);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Methods.isNetworkAvailable(getApplicationContext())) {
                    mModule = getIntent().getExtras().getInt("module");
                    mTitle.setText("Módulo " + mModule);
                    retrieveSamples(String.valueOf(mModule));
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
        mSampleList.setVisibility(View.GONE);
        mSamplePB.setVisibility(View.VISIBLE);

        if (Methods.isNetworkAvailable(getApplicationContext())) {
            mModule = getIntent().getExtras().getInt("module");
            mTitle.setText("Módulo " + mModule);
            retrieveSamples(String.valueOf(mModule));
        } else {
            mSamplePB.setVisibility(View.GONE);
            mRefresh.setVisibility(View.VISIBLE);
            mSampleList.setVisibility(View.VISIBLE);
            mRefresh.setRefreshing(false);
            Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveSamples(String module) {
        mRetrieveSamples = new AsyncTask<String, Void, Request>() {
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
                    rawJson.put("module",Integer.valueOf(params[0]));
                    request.setResponse(Methods.POST(getApplicationContext(), rawJson, "module/sample"));
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
                mSamplePB.setVisibility(View.GONE);
                mRefresh.setVisibility(View.VISIBLE);
                mSampleList.setVisibility(View.VISIBLE);
                mRefresh.setRefreshing(false);
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mRetrieveSamples.execute(module);
    }

    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();
        Log.e(getLocalClassName(), "response json: " + response);
        JSONArray samples;

        if ( response != null) {
            samples = response.getJSONArray("samples");
            List<Sample> sampleList = listSamples(samples);
            Log.e(getLocalClassName(), "num posts: " + sampleList.size());
            if (sampleList.size() >= 0) {
                initDataSet(sampleList);
                mSampleList.setVisibility(View.VISIBLE);
                mAdapter = new CustomAdapter(getApplicationContext(), mDataSet);
                mSampleList.setAdapter(mAdapter);
            }
        }
        else {
            Toast.makeText(getApplication(), Constants.TRY_LATER, Toast.LENGTH_SHORT).show();
        }
    }

    private List<Sample> listSamples(JSONArray samples) throws JSONException {
        List<Sample> list = new ArrayList<>();

        for (int i = 0; i < samples.length(); i++) {
            JSONObject JSONSamples = samples.getJSONObject(i);

            Sample sample = new Sample();
            sample.setId(JSONSamples.getInt("id"));
            sample.setValue((float) JSONSamples.getDouble("value"));
            sample.setCreatedAt(Timestamp.valueOf(JSONSamples.getString("created_at")));
            list.add(sample);
        }

        return list;
    }

    private void initDataSet(List<Sample> sampleList) {
        mDataSet = new Sample[sampleList.size()];
        for (int i = 0; i < sampleList.size(); i++) {
            mDataSet[i] = sampleList.get(i);
            Log.e(getLocalClassName(), "sample " + i + ":" + mDataSet[i].toString());
        }
    }
}
