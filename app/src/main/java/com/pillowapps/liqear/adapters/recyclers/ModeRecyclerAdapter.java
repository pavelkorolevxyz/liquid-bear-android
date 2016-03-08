package com.pillowapps.liqear.adapters.recyclers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.Mode;
import com.pillowapps.liqear.listeners.OnModeListener;
import com.pillowapps.liqear.models.ImageModel;

import java.util.List;

public class ModeRecyclerAdapter extends RecyclerView.Adapter<ModeRecyclerAdapter.ModeViewHolder> {

    private OnModeListener clickListener;
    private List<Mode> items;

    public ModeRecyclerAdapter(List<Mode> items, OnModeListener clickListener) {
        this.clickListener = clickListener;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(ModeViewHolder holder, int position) {
        Mode album = items.get(position);
        holder.textView.setText(album.getTitle());
        new ImageModel().loadImageResource(album.getIcon(), holder.imageView, R.color.accent);
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    @Override
    public ModeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mode_list_item, parent, false);
        return new ModeViewHolder(v, clickListener);
    }

    public Mode getItem(int position) {
        return items.get(position);
    }


    public List<Mode> getItems() {
        return items;
    }

    public void addAll(List<Mode> albums) {
        items.addAll(albums);
        notifyDataSetChanged();
    }

    public class ModeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final View mainLayout;
        public final ImageView imageView;
        public final TextView textView;

        public OnModeListener mListener;

        public ModeViewHolder(View itemLayoutView, OnModeListener listener) {
            super(itemLayoutView);
            textView = (TextView) itemLayoutView.findViewById(R.id.text_list_item);
            imageView = (ImageView) itemLayoutView.findViewById(R.id.image_view_list_item);
            mainLayout = itemLayoutView.findViewById(R.id.main_layout);

            mListener = listener;
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(items.get(getAdapterPosition()).getId());
        }
    }
}
