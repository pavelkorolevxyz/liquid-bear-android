package com.pillowapps.liqear.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.helpers.CollectionUtils;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LastfmArtistAdapter extends ArrayAdapter<LastfmArtist> {
    private final Context context;
    private final List<LastfmArtist> values;

    public LastfmArtistAdapter(Context context, List<LastfmArtist> values) {
        super(context, R.layout.image_list_item);
        this.context = context;
        this.values = values;
    }

    @Override
    public LastfmArtist getItem(int position) {
        return values.get(position);
    }

    public List<LastfmArtist> getItems() {
        return values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    public void addAll(List<LastfmArtist> values) {
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
            convertView = inflater.inflate(R.layout.image_list_item, parent, false);
            holder = new ViewHolder(convertView);
            holder.loadImages = PreferencesManager.getPreferences()
                    .getBoolean(Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LastfmArtist artist = values.get(position);
        holder.artistTextView.setText(Html.fromHtml(artist.getName()));

        if (holder.loadImages) {
            List<LastfmImage> images = artist.getImages();
            Picasso.with(context)
                    .load(CollectionUtils.last(images).getUrl())
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        convertView.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background :
                R.drawable.list_item_background_tinted);

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.text_list_item)
        TextView artistTextView;
        boolean loadImages;
        @InjectView(R.id.image_view_list_item)
        ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
