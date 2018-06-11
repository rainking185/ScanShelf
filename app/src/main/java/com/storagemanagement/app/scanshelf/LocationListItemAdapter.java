package com.storagemanagement.app.scanshelf;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xuboc on 28/02/2018.
 */

public class LocationListItemAdapter extends RecyclerView.Adapter<LocationListItemAdapter.ViewHolder> {

    private List<Location> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    LocationListItemAdapter(Context context, List<Location> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.location_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Location location = mData.get(position);
        holder.mShelfName.setText(location.getmShelfName());
        holder.mRowNum.setText(location.getmRow()+"");
        holder.mColumnNum.setText(location.getmColumn()+"");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mShelfName;
        TextView mRowNum;
        TextView mColumnNum;

        ViewHolder(View itemView) {
            super(itemView);
            mShelfName = itemView.findViewById(R.id.tv_shelf_name);
            mRowNum = itemView.findViewById(R.id.tv_row_number);
            mRowNum = itemView.findViewById(R.id.tv_row_number);
            mColumnNum = itemView.findViewById(R.id.tv_column_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Location getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
