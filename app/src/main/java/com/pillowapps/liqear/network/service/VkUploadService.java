package com.pillowapps.liqear.network.service;

import com.pillowapps.liqear.entities.vk.VkPhotoUploadResult;

import java.util.Map;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.QueryMap;
import retrofit.mime.TypedOutput;
import rx.Observable;

public interface VkUploadService {

    @Multipart
    @POST("/upload.php")
    Observable<VkPhotoUploadResult> uploadPhoto(@Part("photo") TypedOutput photo,
                                                @QueryMap Map<String, String> params);

}
