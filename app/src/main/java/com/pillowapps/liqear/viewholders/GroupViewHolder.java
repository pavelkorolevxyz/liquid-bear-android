package com.pillowapps.liqear.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.helpers.LBPreferencesManager;
import com.pillowapps.liqear.listeners.OnRecyclerItemClickListener;

public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final View mainLayout;
    public final ImageView imageView;
    public final TextView textView;

    public final boolean loadImages;

    public OnRecyclerItemClickListener mListener;

    public GroupViewHolder(View itemLayoutView, OnRecyclerItemClickListener listener) {
        super(itemLayoutView);
        textView = (TextView) itemLayoutView.findViewById(R.id.text_list_item);
        imageView = (ImageView) itemLayoutView.findViewById(R.id.image_view_list_item);
        loadImages = LBPreferencesManager.isDownloadImagesEnabled();
        mainLayout = itemLayoutView.findViewById(R.id.main_layout);

        mListener = listener;
        itemLayoutView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClicked(v, getAdapterPosition());
    }
}

