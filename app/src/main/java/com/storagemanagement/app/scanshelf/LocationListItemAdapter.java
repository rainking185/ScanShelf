package com.storagemanagement.app.scanshelf;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by xuboc on 28/02/2018.
 */

public class LocationListItemAdapter extends RecyclerView.Adapter<LocationListItemAdapter.LocationListItemViewHolder>{

    final private SearchResultItemAdapter.ListItemClickListener mOnClickListener;

    private int mNumberItems;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public LocationListItemAdapter(int numberOfItems, SearchResultItemAdapter.ListItemClickListener listener){
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
    }

    @Override
    public LocationListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.search_result_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem,parent,shouldAttachToParentImmediately);
        LocationListItemViewHolder viewHolder = new LocationListItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocationListItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class LocationListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView listItemShelfName;
        TextView listItemRow;
        TextView listItemColumn;

        Button listItemAdd;
        Button listItemMinus;

        public LocationListItemViewHolder(View itemView){
            super(itemView);
            listItemShelfName = (TextView)itemView.findViewById(R.id.tv_shelf_name);
            listItemRow = (TextView)itemView.findViewById(R.id.tv_row_number);
            listItemColumn = (TextView)itemView.findViewById(R.id.tv_column_number);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

}
