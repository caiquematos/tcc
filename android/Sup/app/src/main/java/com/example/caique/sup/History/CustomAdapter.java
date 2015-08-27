package com.example.caique.sup.History;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.caique.sup.Objects.History;
import com.example.caique.sup.R;

/**
 * Created by caique on 21/08/15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static History[] mDataSet;
    private static Context mContext;

    public CustomAdapter(Context context, History[] dataSet){
        mContext = context;
        mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(getClass().getName(), "Element " + position + " set.");
        History local_histories = mDataSet[position];

        holder.getTitle().setText("Usuário " + local_histories.getUser());
        holder.getDate().setText("- em " + local_histories.getCreated_at().toString());
        holder.getInformation().setText(local_histories.getInformation());
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mInformation;
        private TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.log_user);
            mInformation = (TextView) itemView.findViewById(R.id.log_information);
            mDate = (TextView) itemView.findViewById(R.id.log_date);
        }

        public TextView getTitle(){return mTitle;}
        public TextView getInformation(){return mInformation;}
        public TextView getDate(){return mDate;}
    }

}
