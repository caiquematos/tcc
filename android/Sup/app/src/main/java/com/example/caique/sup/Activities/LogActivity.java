package com.example.caique.sup.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.caique.sup.History.CustomAdapter;
import com.example.caique.sup.Objects.History;
import com.example.caique.sup.R;
import com.example.caique.sup.Tools.Constants;
import com.example.caique.sup.Tools.HandleConnection;
import com.example.caique.sup.Tools.Methods;
import com.example.caique.sup.Tools.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LogActivity extends AppCompatActivity implements HandleConnection {
    RecyclerView mList;
    SwipeRefreshLayout mRefresh;
    ProgressBar mProgress;
    History[] mDataSet;
    CustomAdapter mAdapter;
    AsyncTask<String, Void, Request> mRetrieveHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.log_refresh);
        mProgress = (ProgressBar) findViewById(R.id.log_progress);
        mList = (RecyclerView) findViewById(R.id.log_list);
        mList.setLayoutManager(new LinearLayoutManager(mList.getContext()));

        mRefresh.setColorSchemeResources(R.color.MediumAquamarine, R.color.Aqua, R.color.Aquamarine);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Methods.isNetworkAvailable(getApplicationContext())) {
                    retrieveHistory();
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
        mList.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        if (Methods.isNetworkAvailable(getApplicationContext())) {
            retrieveHistory();
        } else {
            mProgress.setVisibility(View.GONE);
            mRefresh.setVisibility(View.VISIBLE);
            mList.setVisibility(View.VISIBLE);
            mRefresh.setRefreshing(false);
            Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveHistory() {
        mRetrieveHistory = new AsyncTask<String, Void, Request>() {
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
                    request.setResponse(Methods.POST(getApplicationContext(), rawJson, "history/list"));
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
                mProgress.setVisibility(View.GONE);
                mRefresh.setVisibility(View.VISIBLE);
                mList.setVisibility(View.VISIBLE);
                mRefresh.setRefreshing(false);
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mRetrieveHistory.execute();
    }

    @Override
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();
        Log.e(getLocalClassName(), "response json: " + response);
        JSONArray histories;

        if ( response != null) {
            histories = response.getJSONArray("histories");
            List<History> historyList = listHistory(histories);
            Log.e(getLocalClassName(), "num posts: " + historyList.size());
            if (historyList.size() >= 0) {
                initDataSet(historyList);
                mList.setVisibility(View.VISIBLE);
                mAdapter = new CustomAdapter(getApplicationContext(), mDataSet);
                mList.setAdapter(mAdapter);
            }
        }
        else {
            Toast.makeText(getApplication(), Constants.TRY_LATER, Toast.LENGTH_SHORT).show();
        }
    }

    private List<History> listHistory(JSONArray histories) throws JSONException {
        List<History> list = new ArrayList<>();

        for (int i = 0; i < histories.length(); i++) {
            JSONObject JSONHistories = histories.getJSONObject(i);
            Log.e(getLocalClassName(), "on listHistory: " + JSONHistories);
            History history = new History();
            history.setId(JSONHistories.getInt("id"));
            history.setUser(JSONHistories.getInt("user"));
            history.setInformation(JSONHistories.getString("message"));
            history.setCreated_at(Timestamp.valueOf(JSONHistories.getString("created_at")));
            list.add(history);
        }

        return list;
    }

    private void initDataSet(List<History> historyList) {
        mDataSet = new History[historyList.size()];
        for (int i = 0; i < historyList.size(); i++) {
            mDataSet[i] = historyList.get(i);
            Log.e(getLocalClassName(), "history " +i+ ":" + mDataSet[i].toString());
        }
    }
}
