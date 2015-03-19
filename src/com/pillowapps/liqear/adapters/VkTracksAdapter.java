package com.pillowapps.liqear.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.vk.VkTrack;

import java.util.List;

import butterknife.InjectView;

public class VkTracksAdapter extends ArrayAdapter<VkTrack> {
    private final Context context;
    private final List<VkTrack> values;

    public VkTracksAdapter(Context context, List<VkTrack> values) {
        super(context, R.layout.playlist_tab_list_item);
        this.context = context;
        this.values = values;
    }

    @Override
    public VkTrack getItem(int position) {
        return values.get(position);
    }

    public void addAll(List<VkTrack> values) {
        this.values.addAll(values);
        notifyDataSetChanged();
    }

    public void clear() {
        values.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.playlist_tab_list_item, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        VkTrack track = values.get(position);
        holder.artistTextView.setText(Html.fromHtml(track.getArtist()));
        holder.titleTextView.setText(Html.fromHtml(track.getTitle()));
        holder.positionTextView.setText(Integer.toString(position + 1));

        convertView.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background :
                R.drawable.list_item_background_tinted);

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.artist_list_item)
        TextView artistTextView;
        @InjectView(R.id.title_list_item)
        TextView titleTextView;
        @InjectView(R.id.position_text_view_list_item)
        TextView positionTextView;
    }
}
