package com.storagemanagement.app.scanshelf;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.storagemanagement.app.scanshelf.database.DBContract;

/**
 * Created by xuboc on 28/02/2018.
 */

public class ItemListItemAdapter extends RecyclerView.Adapter<ItemListItemAdapter.ItemListItemViewHolder>{

    final private ListItemClickListener mOnClickListener;

    private int mNumberItems;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public ItemListItemAdapter(int numberOfItems, ListItemClickListener listener){
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
    }

    @Override
    public ItemListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.search_result_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem,parent,shouldAttachToParentImmediately);
        ItemListItemViewHolder viewHolder = new ItemListItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemListItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class ItemListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView listItemCategory;
        TextView listItemName;
        TextView listItemUnit;
        TextView listItemAvailable;
        TextView listItemInUse;

        String type = DBContract.ITEM_INSTANCE;
        //TODO: record type

        Button listItemAdd;
        Button listItemMinus;

        public ItemListItemViewHolder(View itemView){
            super(itemView);
            listItemCategory = (TextView)itemView.findViewById(R.id.tv_item_category);
            listItemName = (TextView)itemView.findViewById(R.id.tv_item_name);
            listItemUnit = (TextView)itemView.findViewById(R.id.tv_item_unit);
            listItemAvailable = (TextView)itemView.findViewById(R.id.tv_item_available);
            listItemInUse = (TextView)itemView.findViewById(R.id.tv_item_in_use);

            listItemAdd = (Button)itemView.findViewById(R.id.bt_item_add_available);
            listItemMinus = (Button)itemView.findViewById(R.id.bt_item_minus_finished);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
            if(type == DBContract.ITEM_INSTANCE){
                //TODO: start ItemDeatils Activity
            }else if(type == DBContract.ITEM_STACK){
                //TODO: start new ItemList Activity
            }
        }
    }

}
