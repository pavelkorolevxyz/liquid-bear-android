package com.pillowapps.liqear.views;

import com.pillowapps.liqear.models.ImageModel;

public class VkProfileHeaderDrawerItem extends ProfileHeaderDrawerItem {

    public VkProfileHeaderDrawerItem(ImageModel imageModel, boolean authorized, String avatarUrl, String name) {
        super(imageModel, authorized, avatarUrl, name);
        this.authType = VK;
    }

}
