package com.pillowapps.liqear.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;

public class NewcomersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView artistAlbum;
    public final TextView genre;
    public final ImageView cover;
    public final boolean loadImages;

    public OnRecyclerItemClickListener mListener;

    public NewcomersViewHolder(View itemLayoutView, OnRecyclerItemClickListener listener) {
        super(itemLayoutView);
        artistAlbum = (TextView) itemLayoutView.findViewById(R.id.text_list_item);
        genre = (TextView) itemLayoutView.findViewById(R.id.genre_list_item);
        cover = (ImageView) itemLayoutView.findViewById(
                R.id.cover_image_view_album_list_item);
        loadImages = SharedPreferencesManager.getPreferences()
                .getBoolean("download_images_check_box_preferences", true);
        mListener = listener;
        itemLayoutView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClicked(v, getAdapterPosition());
    }
}

