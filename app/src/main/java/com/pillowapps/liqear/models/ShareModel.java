package com.pillowapps.liqear.models;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.VkPassiveCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.models.vk.VkWallModel;

import javax.inject.Inject;

public class ShareModel {

    @Inject
    VkWallModel vkWallModel;

    public void showShareCurrentTrackDialog(final Context context) {

        LBApplication.get(context).applicationComponent().inject(this);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.share_dialog_layout);
        dialog.setTitle(R.string.share_track);
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        ImageButton vkButton = (ImageButton) dialog.findViewById(R.id.vk_button);
        Button otherButton = (Button) dialog.findViewById(R.id.other_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        String template = SharedPreferencesManager.getPreferences().getString(Constants.SHARE_FORMAT,
                context.getString(R.string.listening_now));
        final Album album = Timeline.getInstance().getCurrentAlbum();
        String artist = "";
        String trackTitle = "";
        String albumTitle = "";
        if (currentTrack != null && currentTrack.getArtist() != null) {
            artist = currentTrack.getArtist();
        }
        if (currentTrack != null && currentTrack.getTitle() != null) {
            trackTitle = currentTrack.getTitle();
        }
        if (album != null && album.getTitle() != null) {
            albumTitle = album.getTitle();
        }
        final String shareBody = template.replace("%a%", artist).replace("%t%", trackTitle).replace("%r%", albumTitle);

        vkButton.setOnClickListener(view -> {
            if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                Toast.makeText(context, R.string.vk_not_authorized, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            } else if (!NetworkUtils.isOnline()) {
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            if (currentTrack == null) return;

            String imageUrl = album != null && album.getArtist().equals(currentTrack.getArtist()) ? album.getImageUrl() : null;
            vkWallModel.postMessage(shareBody, imageUrl, currentTrack, new VkPassiveCallback());
            Toast.makeText(context, R.string.shared, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        otherButton.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            context.startActivity(Intent.createChooser(sharingIntent,
                    context.getResources().getString(R.string.share_track)));
            dialog.dismiss();
        });
        cancelButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

}
