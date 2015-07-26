package com.pillowapps.liqear.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.OnRecyclerItemClickListener;
import com.pillowapps.liqear.activities.modes.OnRecyclerLongItemClickListener;

public class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public final View mainLayout;
    public final TextView textView;
    public final TextView secondTextView;
    public final TextView positionTextView;

    public OnRecyclerItemClickListener clickListener;
    private OnRecyclerLongItemClickListener longItemClickListener;

    public TrackViewHolder(View itemLayoutView, OnRecyclerItemClickListener listener) {
        super(itemLayoutView);
        textView = (TextView) itemLayoutView.findViewById(R.id.artist_list_item);
        secondTextView = (TextView) itemLayoutView.findViewById(
                R.id.title_list_item);
        positionTextView = (TextView) itemLayoutView.findViewById(
                R.id.position_text_view_list_item);
        mainLayout = itemLayoutView.findViewById(
                R.id.playlist_tab_item_main_layout);

        this.clickListener = listener;
        itemLayoutView.setOnClickListener(this);
    }

    public TrackViewHolder(View itemLayoutView, OnRecyclerItemClickListener clickListener,
                           OnRecyclerLongItemClickListener longItemClickListener) {
        this(itemLayoutView, clickListener);

        if (longItemClickListener == null) return;
        this.longItemClickListener = longItemClickListener;
        itemLayoutView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clickListener.onItemClicked(v, getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        longItemClickListener.onItemLongClicked(v, getAdapterPosition());
        return true;
    }
}

