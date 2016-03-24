package com.pillowapps.liqear.views;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.materialdrawer.model.AbstractDrawerItem;
import com.mikepenz.materialdrawer.model.utils.ViewHolderFactory;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.models.ImageModel;

public abstract class ProfileHeaderDrawerItem extends AbstractDrawerItem<ProfileHeaderDrawerItem> {
    public static final int VK = 0;
    public static final int LASTFM = 1;
    private ImageModel imageModel;
    private final boolean authorized;
    private final String avatarUrl;
    private final String name;

    protected int authType = VK;

    public ProfileHeaderDrawerItem(ImageModel imageModel, boolean authorized, String avatarUrl, String name) {
        this.imageModel = imageModel;
        this.authorized = authorized;
        this.avatarUrl = avatarUrl;
        this.name = name;
    }

    @Override
    public String getType() {
        return "lb_profile_header";
    }

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.side_menu_header;
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder) {
        ViewHolder viewHolder = (ViewHolder) holder;
        int colorRes = R.color.vk;
        if (authType == LASTFM) {
            colorRes = R.color.lastfm;
        }
        viewHolder.colorView.setBackgroundResource(colorRes);
        viewHolder.nameTextView.setText(name);
        if (authorized) {
            viewHolder.avatarImageView.setScaleType(ImageView.ScaleType.CENTER);
        } else {
            viewHolder.avatarImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        imageModel.loadAvatarImage(avatarUrl, viewHolder.avatarImageView);

        onPostBindView(this, holder.itemView);
    }

    @Override
    public ViewHolderFactory getFactory() {
        return new ItemFactory();
    }

    public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder factory(View v) {
            return new ViewHolder(v);
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final View colorView;
        private final RoundedImageView avatarImageView;
        private final TextView nameTextView;
        private View view;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
            this.colorView = view.findViewById(R.id.color_view);
            this.avatarImageView = (RoundedImageView) view.findViewById(R.id.avatar_image_view);
            this.nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        }
    }
}
