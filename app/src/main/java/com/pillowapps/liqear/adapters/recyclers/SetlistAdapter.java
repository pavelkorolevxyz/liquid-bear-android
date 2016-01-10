package com.pillowapps.liqear.adapters.recyclers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSetlist;
import com.pillowapps.liqear.viewholders.SetlistViewHolder;

import java.util.List;

public class SetlistAdapter extends RecyclerView.Adapter<SetlistViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private SetlistViewHolder holder;
    private List<SetlistfmSetlist> items;

    public SetlistAdapter(List<SetlistfmSetlist> items, OnRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(SetlistViewHolder holder, int position) {
        SetlistfmSetlist setlist = items.get(position);
        holder.textView.setText(setlist.getNotation());
        holder.imageView.setVisibility(View.GONE);
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public SetlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        holder = new SetlistViewHolder(v, clickListener);
        return holder;
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public SetlistfmSetlist getItem(int position) {
        return items.get(position);
    }


    public List<SetlistfmSetlist> getItems() {
        return items;
    }
}
