package com.storagemanagement.app.scanshelf;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by xuboc on 24/02/2018.
 */

public class SearchResultItemAdapter extends RecyclerView.Adapter<SearchResultItemAdapter.SearchResultItemViewHolder> {

    final private ListItemClickListener mOnClickListener;

    private int mNumberItems;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public SearchResultItemAdapter(int numberOfItems, ListItemClickListener listener){
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
    }

    @Override
    public SearchResultItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.search_result_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem,parent,shouldAttachToParentImmediately);
        SearchResultItemViewHolder viewHolder = new SearchResultItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchResultItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class SearchResultItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView listItemTextView;

        public SearchResultItemViewHolder(View itemView){
            super(itemView);
            listItemTextView = (TextView)itemView.findViewById(R.id.tv_search_result_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

}
