package com.pillowapps.liqear.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.OnRecyclerItemClickListener;
import com.pillowapps.liqear.activities.modes.OnRecyclerLongItemClickListener;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.viewholders.TrackViewHolder;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackViewHolder> {

    private final Context context;
    private OnRecyclerItemClickListener clickListener;
    private TrackViewHolder holder;
    private List<Track> items;
    private OnRecyclerLongItemClickListener longClickListener;

    public TrackAdapter(Context context, List<Track> values) {
        this.context = context;
        this.items = values;
    }

    public TrackAdapter(Context context, List<Track> items, OnRecyclerItemClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        this.items = items;
    }

    public TrackAdapter(Context context, List<Track> items, OnRecyclerItemClickListener clickListener, OnRecyclerLongItemClickListener longClickListener) {
        this.context = context;
        this.clickListener = clickListener;
        this.items = items;
        this.longClickListener = longClickListener;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_tab_list_item, parent, false);
        holder = new TrackViewHolder(v, clickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        Track track = items.get(position);
        holder.textView.setText(Html.fromHtml(track.getArtist()));
        holder.secondTextView.setText(Html.fromHtml(track.getTitle()));
        holder.positionTextView.setText(Integer.toString(position + 1));
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public Track getItem(int position) {
        return items.get(position);
    }

    public List<Track> getItems() {
        return items;
    }

    public void addAll(final List<Track> trackList) {
        items.addAll(trackList);
        notifyDataSetChanged();
    }
}
