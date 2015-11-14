package com.pillowapps.liqear.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.viewholders.NeighbourViewHolder;

import java.util.List;

public class NeighbourAdapter extends RecyclerView.Adapter<NeighbourViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private NeighbourViewHolder holder;
    private List<User> items;

    public NeighbourAdapter(List<User> items, OnRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public NeighbourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.neighbour_image_list_item, parent, false);
        holder = new NeighbourViewHolder(v, clickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(NeighbourViewHolder holder, int position) {
        User user = items.get(position);
        holder.textView.setText(Html.fromHtml(user.getName()));
        if (holder.loadImages) {
            new ImageModel().loadAvatarListImage(user.getImageUrl(), holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public User getItem(int position) {
        return items.get(position);
    }


    public List<User> getItems() {
        return items;
    }
}
