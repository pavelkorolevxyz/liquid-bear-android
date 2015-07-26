package com.pillowapps.liqear.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.OnRecyclerItemClickListener;
import com.pillowapps.liqear.helpers.LBPreferencesManager;

public class NeighbourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final View mainLayout;
    public final RoundedImageView imageView;
    public final TextView textView;

    public final boolean loadImages;

    public OnRecyclerItemClickListener mListener;

    public NeighbourViewHolder(View itemLayoutView, OnRecyclerItemClickListener listener) {
        super(itemLayoutView);
        textView = (TextView) itemLayoutView.findViewById(R.id.text_list_item);
        imageView = (RoundedImageView) itemLayoutView.findViewById(R.id.image_view_list_item);
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

