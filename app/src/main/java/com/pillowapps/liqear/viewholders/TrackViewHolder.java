package com.pillowapps.liqear.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;

public class TrackViewHolder extends RecyclerView.ViewHolder {

    public final View mainLayout;
    public final TextView textView;
    public final TextView secondTextView;
    public final TextView positionTextView;
    private final View itemLayoutView;
    public final View handleView;

    public OnRecyclerItemClickListener clickListener;

    public TrackViewHolder(View itemLayoutView, final OnRecyclerItemClickListener clickListener) {
        super(itemLayoutView);
        textView = (TextView) itemLayoutView.findViewById(R.id.artist_list_item);
        secondTextView = (TextView) itemLayoutView.findViewById(
                R.id.title_list_item);
        positionTextView = (TextView) itemLayoutView.findViewById(
                R.id.position_text_view_list_item);
        mainLayout = itemLayoutView.findViewById(
                R.id.playlist_tab_item_main_layout);
        handleView = itemLayoutView.findViewById(
                R.id.grabber_list_item);
        this.itemLayoutView = itemLayoutView;
        itemLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClicked(v, getAdapterPosition());
            }
        });
    }
}

