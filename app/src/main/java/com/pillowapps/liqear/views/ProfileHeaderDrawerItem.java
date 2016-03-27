package com.pillowapps.liqear.views;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialdrawer.model.AbstractDrawerItem;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.models.ImageModel;

public abstract class ProfileHeaderDrawerItem extends AbstractDrawerItem<ProfileHeaderDrawerItem, ProfileHeaderDrawerItem.ViewHolder> {
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
    public int getType() {
        return R.id.profile_header_drawer_item_type;
    }

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.side_menu_profile_header;
    }

    @Override
    public void bindView(ViewHolder viewHolder) {
        int colorRes = R.color.vk;
        if (authType == LASTFM) {
            colorRes = R.color.lastfm;
        }
        viewHolder.colorView.setBackgroundResource(colorRes);
        viewHolder.nameTextView.setText(name);
        if (!authorized) {
            viewHolder.avatarImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.nameTextView.setVisibility(View.GONE);
            if (authType == VK) {
                viewHolder.descriptionTextView.setText(R.string.not_authorized_in_vk);
            } else {
                viewHolder.descriptionTextView.setText(R.string.not_authorized_in_lastfm);
            }
        } else {
            viewHolder.avatarImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.nameTextView.setVisibility(View.VISIBLE);
            viewHolder.descriptionTextView.setText(R.string.authorized_as);
        }
        imageModel.loadAvatarImage(avatarUrl, viewHolder.avatarImageView);

        onPostBindView(this, viewHolder.itemView);
    }

    @Override
    public ViewHolderFactory getFactory() {
        return new ItemFactory();
    }

    public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder factory(View v) {
            return new ViewHolder(v);
        }

        @Override
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final View colorView;
        private final RoundedImageView avatarImageView;
        private final TextView nameTextView;
        private final TextView descriptionTextView;
        private View view;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
            this.colorView = view.findViewById(R.id.color_view);
            this.avatarImageView = (RoundedImageView) view.findViewById(R.id.avatar_image_view);
            this.nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            this.descriptionTextView = (TextView) view.findViewById(R.id.description_text_view);
        }
    }
}
