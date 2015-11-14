package com.pillowapps.liqear.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.entities.Group;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.viewholders.GroupViewHolder;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private GroupViewHolder holder;
    private List<Group> items;

    public GroupAdapter(List<Group> items, OnRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        Group group = items.get(position);
        holder.textView.setText(Html.fromHtml(group.getName()));
        if (holder.loadImages) {
            new ImageModel().loadGroupListImage(group.getImageUrl(), holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        holder = new GroupViewHolder(v, clickListener);
        return holder;
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public Group getItem(int position) {
        return items.get(position);
    }


    public List<Group> getItems() {
        return items;
    }
}
