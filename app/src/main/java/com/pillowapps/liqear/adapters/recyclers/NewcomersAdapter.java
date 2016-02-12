package com.pillowapps.liqear.adapters.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.listeners.OnRecyclerItemClickListener;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.viewholders.NewcomersViewHolder;

import java.util.List;

public class NewcomersAdapter extends RecyclerView.Adapter<NewcomersViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private List<Album> items;
    private Context context;
    private boolean loadImages;

    public NewcomersAdapter(Context context, List<Album> items, boolean loadImages, OnRecyclerItemClickListener clickListener) {
        this.context = context;
        this.loadImages = loadImages;
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(NewcomersViewHolder holder, int position) {
        Album album = items.get(position);
        holder.artistAlbum.setText(Html.fromHtml(album.getNotation()));
        holder.genre.setText(Html.fromHtml(album.getGenre()));
        if (loadImages) {
            new ImageModel().loadAlbumListImage(album.getImageUrl(), holder.cover);
        } else {
            holder.cover.setVisibility(View.GONE);
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
    public NewcomersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new NewcomersViewHolder(context, v, clickListener);
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
