package com.pillowapps.liqear.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.OnRecyclerItemClickListener;
import com.pillowapps.liqear.viewholders.TagViewHolder;
import com.pillowapps.liqear.entities.Tag;

import java.util.List;

public class TagAdapter extends UltimateViewAdapter<TagViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private TagViewHolder holder;
    private List<Tag> items;

    public TagAdapter(List<Tag> items, OnRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        Tag tag = items.get(position);
        holder.textView.setText(Html.fromHtml(tag.getName()));
        holder.imageView.setVisibility(View.GONE);
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public TagViewHolder getViewHolder(View view) {
        return holder;
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        holder = new TagViewHolder(v, clickListener);
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

    public Tag getItem(int position) {
        return items.get(position);
    }


    public List<Tag> getItems() {
        return items;
    }
}
