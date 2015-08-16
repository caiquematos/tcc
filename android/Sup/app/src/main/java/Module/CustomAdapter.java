package Module;

import android.annotation.TargetApi;
import android.app.Activity;
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

import com.example.caique.sup.Activities.ModuleDialog;
import com.example.caique.sup.Activities.SampleActivity;
import com.example.caique.sup.R;

import org.json.JSONException;
import org.json.JSONObject;

import Objects.Module;
import Tools.Constants;
import Tools.HandleConnection;
import Tools.Methods;
import Tools.Request;

/**
 * Created by caique on 12/08/15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
    private static final String TAG = "CustomAdapter";

    private static Module[] mDataSet;
    private static Context mContext;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements HandleConnection {
        private final TextView mTitle;
        private final TextView mBody;
        private final Switch mSwitch;
        private final CheckBox mCheckBox;
        private final View mMoreInfo;
        private final View mViewButton;

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public ViewHolder(View v) {
            super(v);

            mBody = (TextView) v.findViewById(R.id.module_body_title_item);
            mTitle = (TextView) v.findViewById(R.id.module_main_title_item);
            mSwitch = (Switch) v.findViewById(R.id.module_switch);
            mCheckBox = (CheckBox) v.findViewById(R.id.module_checkbox);
            mMoreInfo = v.findViewById(R.id.module_more_info);
            mViewButton = v.findViewById(R.id.module_item_button);

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
                    goToSample(mDataSet[getAdapterPosition()].getId());
                }
            });

            mMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToModuleDialog(mDataSet[getAdapterPosition()]);
                }
            });

            if (mSwitch != null)
                mSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDataSet[getAdapterPosition()].getStatus() == '1') moduleRequest(mDataSet[getAdapterPosition()].getId(), 0);
                        else moduleRequest(mDataSet[getAdapterPosition()].getId(), 1);
                    }
                });

            if (mCheckBox != null)
                mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDataSet[getAdapterPosition()].getStatus() == '1') moduleRequest(mDataSet[getAdapterPosition()].getId(), 0);
                        else moduleRequest(mDataSet[getAdapterPosition()].getId(), 1);
                    }
                });
        }

        private void moduleRequest(int module, int status) {
            AsyncTask<String, Void, Request> switchRequest = new AsyncTask<String, Void, Request>() {
                JSONObject rawJson = new JSONObject();
                Request request = new Request();

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (mSwitch != null) mSwitch.setEnabled(false);
                    if (mCheckBox != null) mCheckBox.setEnabled(false);
                }

                @Override
                protected Request doInBackground(String... params) {
                    try {
                        rawJson.put("module",params[0]);
                        rawJson.put("status", params[1]);
                        request.setResponse(Methods.POST(mContext, rawJson, "module/status"));
                        return request;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Falha na conexão", Toast.LENGTH_SHORT).show();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Request request) {
                    super.onPostExecute(request);
                    if (mSwitch != null) mSwitch.setEnabled(true);
                    if (mCheckBox != null) mCheckBox.setEnabled(true);
                    try {
                        handleConnection(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            switchRequest.execute(String.valueOf(module), String.valueOf(status));
        }

        private void goToSample(int module) {
            try
            {
                Intent activity = new Intent(mContext, SampleActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.putExtra("module",module);
                mContext.startActivity(activity);
            }catch(Exception e){
                Log.e(getClass().getName(),"" + e);
            }
        }

        private void goToModuleDialog(Module module) {
            try
            {
                Intent activity = new Intent(mContext, ModuleDialog.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.putExtra("id", module.getId());
                activity.putExtra("coordinator", module.getCoordinator());
                activity.putExtra("status", module.getStatus());
                activity.putExtra("battery", module.getBattery());
                activity.putExtra("packFrequency", module.getPackegesFrequency());
                activity.putExtra("sleepTime", module.getSleepTime().toString());
                activity.putExtra("sleepFrequency", module.getSleepFrequency());
                activity.putExtra("numbOfSamples", module.getNumOfSamples());
                ((Activity)mContext).startActivityForResult(activity, 1);
            }catch(Exception e){
                Log.e(getClass().getName(),"" + e);
            }
        }

        public TextView getTitle() { return mTitle; }
        public TextView getBody() { return mBody; }
        public Switch getSwitch() { return mSwitch; }
        public CheckBox getCheckBox() { return mCheckBox; }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public void handleConnection(Request request) throws JSONException {
            JSONObject response;
            response = request.getResponse();
            Log.e(getClass().getName(), "response json: " + response);

            if ( response != null && response.getString("result").equals("success")) {
                if (response.getInt("status") == 1) {
                    if (mSwitch != null) mSwitch.setChecked(true);
                    if (mCheckBox != null) mCheckBox.setChecked(true);
                    mDataSet[getAdapterPosition()].setStatus('1');
                }
                else {
                    if (mSwitch != null) mSwitch.setChecked(false);
                    if (mCheckBox != null) mCheckBox.setChecked(false);
                    mDataSet[getAdapterPosition()].setStatus('0');
                }
            }
            else {
                if(mSwitch != null)
                    if (mDataSet[getAdapterPosition()].getStatus() == '1') mSwitch.setChecked(true);
                    else mSwitch.setChecked(false);

                if(mCheckBox != null)
                    if (mDataSet[getAdapterPosition()].getStatus() == '1') mCheckBox.setChecked(true);
                    else mCheckBox.setChecked(false);

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
    public CustomAdapter(Context context, Module[] dataSet) {
        mDataSet = dataSet;
        mContext = context;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.module_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        Module local_modules = mDataSet[position];

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTitle().setText("Módulo " + local_modules.getId());
        viewHolder.getBody().setText("número de amostras: " + local_modules.getNumOfSamples()
                + "  battery: " + local_modules.getBattery());
        Log.d(TAG, "status view " + position + " set: " +local_modules.getStatus());
        if (local_modules.getStatus() == '1') {
            if (viewHolder.getSwitch() != null) viewHolder.getSwitch().setChecked(true);
            if (viewHolder.getCheckBox() != null)viewHolder.getCheckBox().setChecked(true);
            Log.d(TAG, "Element " + position + " TRUE");
        }
        else{
            if (viewHolder.getCheckBox() != null) viewHolder.getCheckBox().setChecked(false);
            if (viewHolder.getSwitch() != null) viewHolder.getSwitch().setChecked(false);
            Log.d(TAG, "Element " + position + " FALSE");
        }
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

}
