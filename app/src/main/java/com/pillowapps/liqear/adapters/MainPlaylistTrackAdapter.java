package com.pillowapps.liqear.adapters;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.components.OnRecyclerLongItemClickListener;
import com.pillowapps.liqear.components.OnItemStartDragListener;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.viewholders.TrackViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainPlaylistTrackAdapter extends RecyclerView.Adapter<TrackViewHolder> {

    private View.OnCreateContextMenuListener contextMenuListener;
    private OnRecyclerItemClickListener clickListener;
    private TrackViewHolder holder;
    private List<Track> items;
    private List<Track> original;
    private OnRecyclerLongItemClickListener longClickListener;
    private boolean editMode;
    private PlaylistItemsFilter playlistItemsFilter;
    private int position = 0;
    private final OnItemStartDragListener mDragStartListener;


    public MainPlaylistTrackAdapter(List<Track> items, OnRecyclerItemClickListener clickListener,
                                    View.OnCreateContextMenuListener contextMenuListener,
                                    OnItemStartDragListener dragStartListener) {
        this.clickListener = clickListener;
        this.items = items;
        this.original = new ArrayList<>(items.size());
        this.contextMenuListener = contextMenuListener;
        this.mDragStartListener = dragStartListener;
    }

    @Override
    public void onBindViewHolder(final TrackViewHolder holder, final int position) {
        Track track = items.get(position);
        holder.textView.setText(Html.fromHtml(track.getArtist()));
        holder.secondTextView.setText(Html.fromHtml(track.getTitle()));
        holder.positionTextView.setText(Integer.toString(position + 1));
        holder.mainLayout.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background : R.drawable.list_item_background_tinted);

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                setPosition(holder.getAdapterPosition());
//                return false;
//            }
//        });
        holder.handleView.setVisibility(View.VISIBLE);
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_tab_list_item, parent, false);
        holder = new TrackViewHolder(v, clickListener);
        return holder;
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

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public List<Track> getValues() {
        return items;
    }

    public void setValues(List<Track> tracks) {
        if (tracks == null) return;
        this.items.clear();
        this.original.clear();
        this.items.addAll(tracks);
        this.original.addAll(tracks);
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        if (playlistItemsFilter == null) {
            playlistItemsFilter = new PlaylistItemsFilter();
        }
        return playlistItemsFilter;
    }

    public Track getCurrentItem() {
        return items.get(position);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void removeItem(int position) {
//        remove(getItems(), position);
    }

    private class PlaylistItemsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.length() > 0) {
                List<Track> founded = new ArrayList<>();
                for (Track t : original) {
                    if (t == null) {
                        break;
                    }
                    String artist = t.getArtist();
                    String title = t.getTitle();
                    if (artist != null && title != null) {
                        if (constraint.toString().startsWith("-") && constraint.length() > 1) {
                            String subConstraint =
                                    constraint.subSequence(1, constraint.length()).toString();
                            if (!artist.toLowerCase()
                                    .contains(subConstraint) && !title.toLowerCase().contains(
                                    subConstraint)) {
                                founded.add(t);
                            }
                        } else {
                            if (artist.toLowerCase().contains(constraint)
                                    || title.toLowerCase().contains(constraint)) {
                                founded.add(t);
                            }
                        }
                    }
                }
                result.values = founded;
                result.count = founded.size();
            } else {
                result.values = original;
                result.count = original.size();
            }

            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            items.clear();
            List<Track> values = (List<Track>) filterResults.values;
            items.addAll(values);
            notifyDataSetChanged();
        }
    }

//    @Override
//    public void onItemDismiss(int position) {
//        items.remove(position);
//        notifyItemRemoved(position);
//    }
//
//    @Override
//    public void onItemMove(int fromPosition, int toPosition) {
//        Collections.swap(items, fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
//    }
}
