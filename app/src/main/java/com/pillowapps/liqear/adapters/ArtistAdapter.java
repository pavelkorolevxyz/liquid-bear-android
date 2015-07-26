package com.pillowapps.liqear.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.OnRecyclerItemClickListener;
import com.pillowapps.liqear.viewholders.ArtistsViewHolder;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.models.ImageModel;

import java.util.List;

public class ArtistAdapter extends UltimateViewAdapter<ArtistsViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private ArtistsViewHolder holder;
    private List<Artist> items;

    public ArtistAdapter(List<Artist> items, OnRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(ArtistsViewHolder holder, int position) {
        Artist artist = items.get(position);
        holder.textView.setText(Html.fromHtml(artist.getName()));
        if (holder.loadImages) {
            new ImageModel().loadArtistListImage(artist.getPreviewUrl(), holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public ArtistsViewHolder getViewHolder(View view) {
        return holder;
    }

    @Override
    public ArtistsViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        holder = new ArtistsViewHolder(v, clickListener);
        return holder;
    }

    @Override
    public int getAdapterItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    @Override
    public long generateHeaderId(int i) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

    }

    public Artist getItem(int position) {
        return items.get(position);
    }


    public List<Artist> getItems() {
        return items;
    }


}
