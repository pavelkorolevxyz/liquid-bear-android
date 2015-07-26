package com.pillowapps.liqear.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.OnRecyclerItemClickListener;
import com.pillowapps.liqear.activities.modes.OnRecyclerLongItemClickListener;
import com.pillowapps.liqear.viewholders.TrackViewHolder;
import com.pillowapps.liqear.entities.Track;

import java.util.List;

public class TrackAdapter extends UltimateViewAdapter<TrackViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private TrackViewHolder holder;
    private List<Track> items;
    private OnRecyclerLongItemClickListener longClickListener;

    public TrackAdapter(List<Track> items, OnRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    public TrackAdapter(List<Track> items, OnRecyclerItemClickListener clickListener, OnRecyclerLongItemClickListener longClickListener) {
        this.clickListener = clickListener;
        this.items = items;
        this.longClickListener = longClickListener;
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
    public TrackViewHolder getViewHolder(View view) {
        return holder;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_tab_list_item, parent, false);
        holder = new TrackViewHolder(v, clickListener, longClickListener);
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

    public Track getItem(int position) {
        return items.get(position);
    }

    public List<Track> getItems() {
        return items;
    }

    public void addItems(final List<Track> trackList) {
        items.addAll(trackList);
        notifyDataSetChanged();
    }
}
