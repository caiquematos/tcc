package com.example.caique.sup.Coordinator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caique.sup.Activities.CoordinatorDialog;
import com.example.caique.sup.Activities.ModuleActivity;
import com.example.caique.sup.Objects.Coordinator;
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

/**
 * Created by caique on 12/08/15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private static Coordinator[] mDataSet;
    private static Context mContext;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements HandleConnection {
        private final TextView mTitle;
        private final TextView mBody;
        private final View mMoreInfo;
        private final Switch mSwitch;
        private final CheckBox mCheckbox;
        private final View mViewButton;
        private final int mUser;

        public ViewHolder(View v) {
            super(v);

            mBody = (TextView) v.findViewById(R.id.coordinator_body_title_item);
            mTitle = (TextView) v.findViewById(R.id.coordinator_main_title_item);
            mSwitch = (Switch) v.findViewById(R.id.coordinator_switch);
            mCheckbox = (CheckBox) v.findViewById(R.id.coordinator_checkbox);
            mMoreInfo = v.findViewById(R.id.coordinator_more_info);
            mViewButton = v.findViewById(R.id.coordinator_item_button);
            mUser = Preferences.getUserId(mContext);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });

            mViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToModule(mDataSet[getAdapterPosition()].getId());
                }
            });

            mMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToCoordinatorDialog(mDataSet[getAdapterPosition()]);
                }
            });

            if (mSwitch != null)
                mSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDataSet[getAdapterPosition()].getStatus() == '1')
                            switchCoordinator(mDataSet[getAdapterPosition()].getId(), 0, mUser);
                        else switchCoordinator(mDataSet[getAdapterPosition()].getId(), 1, mUser);
                    }
                });

            if (mCheckbox != null)
                mCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDataSet[getAdapterPosition()].getStatus() == '1')
                            switchCoordinator(mDataSet[getAdapterPosition()].getId(), 0, mUser);
                        else switchCoordinator(mDataSet[getAdapterPosition()].getId(), 1, mUser);
                    }
                });
        }

        private void switchCoordinator(int coordinator, int status, int user) {
            AsyncTask<String, Void, Request> switchRequest = new AsyncTask<String, Void, Request>() {
                Request request = new Request();
                List<NameValuePair> nameValuePairs = new ArrayList<>();

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (mSwitch != null) mSwitch.setEnabled(false);
                    if (mCheckbox != null) mCheckbox.setEnabled(false);
                }

                @Override
                protected Request doInBackground(String... strings) {
                    if(!strings[0].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("coordinator", strings[0])); }
                    if(!strings[1].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("status", strings[1])); }
                    if(!strings[2].isEmpty()) { nameValuePairs.add(new BasicNameValuePair("user", strings[2])); }
                    request.setResponse(Methods.Post2(mContext, nameValuePairs, "coordinator/switch"));
                    return request;
                }

                @Override
                protected void onPostExecute(Request request) {
                    super.onPostExecute(request);
                    if (mSwitch != null) mSwitch.setEnabled(true);
                    if (mCheckbox != null) mCheckbox.setEnabled(true);
                    try {
                        handleConnection(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            switchRequest.execute(String.valueOf(coordinator), String.valueOf(status), String.valueOf(user));
        }

        private void goToCoordinatorDialog(Coordinator coordinator) {
            try {
                Intent activity = new Intent(mContext, CoordinatorDialog.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.putExtra("id", coordinator.getId());
                activity.putExtra("status", coordinator.getStatus());
                activity.putExtra("battery", coordinator.getBattery());
                activity.putExtra("numb_of_modules", coordinator.getNumOfModules());
                mContext.startActivity(activity);
            } catch (Exception e) {
                Log.e(getClass().getName(), "" + e);
            }
        }

        private void goToModule(int coordinator) {
            try {
                Intent activity = new Intent(mContext, ModuleActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.putExtra("coordinator", coordinator);
                mContext.startActivity(activity);
            } catch (Exception e) {
                Log.e(getClass().getName(), "" + e);
            }
        }

        public TextView getTitle() {
            return mTitle;
        }

        public TextView getBody() {
            return mBody;
        }

        public Switch getSwitch() {
            return mSwitch;
        }

        public CheckBox getCheckbox() {
            return mCheckbox;
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public void handleConnection(Request request) throws JSONException {
            JSONObject response;
            response = request.getResponse();
            Log.e(getClass().getName(), "response json: " + response);

            if (response != null && response.getString("result").equals("success")) {
                if (response.getInt("status") == 1) {
                    if (mSwitch != null) mSwitch.setChecked(true);
                    if (mCheckbox != null) mCheckbox.setChecked(true);
                    mDataSet[getAdapterPosition()].setStatus('1');
                } else {
                    if (mSwitch != null) mSwitch.setChecked(false);
                    if (mCheckbox != null) mCheckbox.setChecked(false);
                    mDataSet[getAdapterPosition()].setStatus('0');
                }
            } else {
                if (mSwitch != null)
                    if (mDataSet[getAdapterPosition()].getStatus() == '1') mSwitch.setChecked(true);
                    else mSwitch.setChecked(false);

                if (mCheckbox != null)
                    if (mDataSet[getAdapterPosition()].getStatus() == '1')
                        mCheckbox.setChecked(true);
                    else mCheckbox.setChecked(false);

                Toast.makeText(mContext, Constants.TRY_LATER, Toast.LENGTH_SHORT).show();
            }
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(Context context, Coordinator[] dataSet) {
        mDataSet = dataSet;
        mContext = context;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.coordinator_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        Coordinator local_coordinators = mDataSet[position];

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTitle().setText("Coordenador " + local_coordinators.getId());
        viewHolder.getBody().setText("número de módulos: " + local_coordinators.getNumOfModules()
                + "  battery: " + local_coordinators.getBattery() + "%");
        if (local_coordinators.getStatus() == '1') {
            if (viewHolder.getSwitch() != null) viewHolder.getSwitch().setChecked(true);
            if (viewHolder.getCheckbox() != null) viewHolder.getCheckbox().setChecked(true);
        } else {
            if (viewHolder.getSwitch() != null) viewHolder.getSwitch().setChecked(false);
            if (viewHolder.getCheckbox() != null) viewHolder.getCheckbox().setChecked(false);
        }
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }


}
