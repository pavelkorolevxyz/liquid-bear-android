package com.pillowapps.liqear.models.vk;

import com.google.gson.annotations.SerializedName;

public class VkUser {
    @SerializedName("id")
    private long id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("photo_medium")
    private String photoMedium;

    public VkUser() {
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhotoMedium() {
        return photoMedium;
    }

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    @Override
    public String toString() {
        return "VkUser{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", photoMedium='" + photoMedium + '\'' +
                '}';
    }
}
