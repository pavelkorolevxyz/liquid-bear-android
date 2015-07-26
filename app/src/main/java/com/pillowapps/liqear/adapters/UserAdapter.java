package com.pillowapps.liqear.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.OnRecyclerItemClickListener;
import com.pillowapps.liqear.viewholders.UserViewHolder;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.models.ImageModel;

import java.util.List;

public class UserAdapter extends UltimateViewAdapter<UserViewHolder> {

    private OnRecyclerItemClickListener clickListener;
    private UserViewHolder holder;
    private List<User> items;

    public UserAdapter(List<User> items, OnRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
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
    public UserViewHolder getViewHolder(View view) {
        return holder;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        holder = new UserViewHolder(v, clickListener);
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

    public User getItem(int position) {
        return items.get(position);
    }


    public List<User> getItems() {
        return items;
    }
}
