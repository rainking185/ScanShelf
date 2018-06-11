package com.storagemanagement.app.scanshelf;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.storagemanagement.app.scanshelf.database.DBContract;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by xuboc on 28/02/2018.
 */

public class ItemListItemAdapter extends RecyclerView.Adapter<ItemListItemAdapter.ViewHolder> {

    private List<Item> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    ItemListItemAdapter(Context context, List<Item> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item data = mData.get(position);
        holder.Category.setText(data.getCategory());
        holder.Name.setText(data.getName());
        holder.Unit.setText(data.getUnit());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Category;
        TextView Name;
        TextView Unit;

        ViewHolder(View itemView) {
            super(itemView);
            Category = itemView.findViewById(R.id.tv_item_category);
            Name = itemView.findViewById(R.id.tv_item_name);
            Unit = itemView.findViewById(R.id.tv_item_unit);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    // convenience method for getting data at click position
    Item getItem(int id) {
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
