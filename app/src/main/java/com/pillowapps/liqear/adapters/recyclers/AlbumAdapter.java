package com.pillowapps.liqear.adapters.recyclers;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.viewholders.AlbumViewHolder;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private AlbumViewHolder holder;
    private List<Album> items;

    public AlbumAdapter(List<Album> items, OnRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = items.get(position);
        holder.textView.setText(Html.fromHtml(album.getNotation()));
        if (holder.loadImages) {
            new ImageModel().loadAlbumListImage(album.getImageUrl(), holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        holder = new AlbumViewHolder(v, clickListener);
        return holder;
    }

    public Album getItem(int position) {
        return items.get(position);
    }


    public List<Album> getItems() {
        return items;
    }

    public void addAll(List<Album> albums) {
        items.addAll(albums);
        notifyDataSetChanged();
    }
}
