package com.pillowapps.liqear.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;

import java.util.List;

public class ListArrayAdapter<T> extends ArrayAdapter<T> {
    private final Context context;
    private final List<T> values;
    private final Class<T> clazz;
    private final String period;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisc()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(500))
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public ListArrayAdapter(Context context, List<T> values, Class<T> clazz, String period) {
        super(context, R.layout.image_list_item, values);
        this.context = context;
        this.values = values;
        this.clazz = clazz;
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public T get(int position) {
        return values.get(position);
    }

    public List<T> getValues() {
        return values;
    }

    public void addValues(List<T> values) {
        this.values.addAll(values);
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
            if (clazz == Track.class) {
                convertView = inflater.inflate(R.layout.playlist_tab_list_item, null);
                holder.textView = (TextView) convertView.findViewById(R.id.artist_list_item);
                holder.secondTextView = (TextView) convertView.findViewById(R.id.title_list_item);
                holder.positionTextView = (TextView) convertView.findViewById(
                        R.id.position_text_view_list_item);
            } else {
                convertView = inflater.inflate(R.layout.image_list_item, null);
                holder.textView = (TextView) convertView.findViewById(R.id.text_list_item);
                holder.imageView = (ImageView) convertView.findViewById(R.id.image_view_list_item);
                holder.loadImages = PreferencesManager.getPreferences()
                        .getBoolean(Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if (clazz != Track.class) {
                holder.imageView.setImageBitmap(null);
            }
        }

        T currentItem = values.get(position);
        if (clazz == Track.class) {
            Track track = (Track) currentItem;
            holder.textView.setText(Html.fromHtml(track.getArtist()));
            holder.secondTextView.setText(Html.fromHtml(track.getTitle()));
            holder.positionTextView.setText(Integer.toString(position + 1));
        } else if (clazz == Artist.class) {
            Artist artist = (Artist) currentItem;
            holder.textView.setText(Html.fromHtml(artist.getName()));
            if (holder.loadImages) {
                imageLoader.displayImage(artist.getPreviewUrl(), holder.imageView, options);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        } else if (clazz == Album.class) {
            Album album = (Album) currentItem;
            holder.textView.setText(Html.fromHtml(album.getTitle()));
            if (holder.loadImages) {
                imageLoader.displayImage(album.getImageUrl(), holder.imageView, options);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        } else if (clazz == LastfmAlbum.class) {
            LastfmAlbum album = (LastfmAlbum) currentItem;
            holder.textView.setText(Html.fromHtml(album.getTitle()));
            if (holder.loadImages) {
                List<LastfmImage> images = album.getImages();
                if (images != null && images.size() > 0) {
                    imageLoader.displayImage(images.get(images.size() - 1).getUrl(),
                            holder.imageView, options);
                }
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        } else if (clazz == VkAlbum.class) {
            VkAlbum album = (VkAlbum) currentItem;
            holder.textView.setText(Html.fromHtml(album.getTitle()));
            holder.imageView.setVisibility(View.GONE);
        } else if (clazz == Tag.class) {
            holder.textView.setText(Html.fromHtml(((Tag) currentItem).getName()));
            holder.imageView.setVisibility(View.GONE);
        }

        convertView.setBackgroundDrawable(position % 2 == 0 ?
                context.getResources().getDrawable(R.drawable.list_item_background) :
                context.getResources().getDrawable(R.drawable.list_item_background_tinted));

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
        TextView secondTextView;
        TextView positionTextView;
        ImageView imageView;
        boolean loadImages;
    }
}
