package com.pillowapps.liqear.adapters.recyclers;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.listeners.OnRecyclerItemClickListener;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.viewholders.ArtistsViewHolder;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistsViewHolder> {

    private OnRecyclerItemClickListener clickListener;
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
            new ImageModel().loadArtistListImage(artist.getImageUrl(), holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public ArtistsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        return new ArtistsViewHolder(v, clickListener);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public Artist getItem(int position) {
        return items.get(position);
    }


    public List<Artist> getItems() {
        return items;
    }


    public void addAll(List<Artist> artists) {
        items.addAll(artists);
        notifyDataSetChanged();
    }


    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}
