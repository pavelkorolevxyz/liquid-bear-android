package com.pillowapps.liqear.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.entities.Album;

public class RightFragment extends Fragment {
    private MainActivity mainActivity;
    private ImageView albumCoverImageView;
    private TextView albumTitleTextView;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisc()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(300))
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.right_frame_fragment_layout, null);
        mainActivity = (MainActivity) getActivity();
        initUi(v);
        return v;
    }

    private void initUi(View v) {
        albumCoverImageView = (ImageView) v.findViewById(R.id.album_cover_image_view);
        albumTitleTextView = (TextView) v.findViewById(R.id.album_title_text_view);
        View.OnClickListener albumClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, LastfmAlbumViewerActivity.class);
                Album album = AudioTimeline.getAlbum();
                if (album == null) return;
                intent.putExtra(LastfmAlbumViewerActivity.ALBUM, album.getTitle());
                intent.putExtra(LastfmAlbumViewerActivity.ARTIST, album.getArtist());
                mainActivity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
        };
        albumCoverImageView.setOnClickListener(albumClickListener);
        albumTitleTextView.setOnClickListener(albumClickListener);
    }

    public void setAlbum(Album album) {
        if (album == null) return;
        String imageUrl = album.getImageUrl();
        if (imageUrl == null ||
                !PreferencesManager.getPreferences().getBoolean(
                        Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
            albumCoverImageView.setVisibility(View.GONE);
        } else {
            albumCoverImageView.setVisibility(View.VISIBLE);
            imageLoader.displayImage(imageUrl, albumCoverImageView,
                    options);
        }
        String title = album.getTitle();
        if (title == null) {
            albumTitleTextView.setVisibility(View.GONE);
        } else {
            albumTitleTextView.setVisibility(View.VISIBLE);
            albumTitleTextView.setText(title);
        }
    }
}
