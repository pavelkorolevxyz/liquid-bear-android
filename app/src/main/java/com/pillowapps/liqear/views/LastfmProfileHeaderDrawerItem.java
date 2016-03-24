package com.pillowapps.liqear.views;

import com.pillowapps.liqear.models.ImageModel;

public class LastfmProfileHeaderDrawerItem extends ProfileHeaderDrawerItem {

    public LastfmProfileHeaderDrawerItem(ImageModel imageModel, boolean authorized, String avatarUrl, String name) {
        super(imageModel, authorized, avatarUrl, name);
        this.authType = LASTFM;
    }

}
