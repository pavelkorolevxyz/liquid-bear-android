package com.pillowapps.liqear.connection;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface VkUploadService {

    @Multipart
    public void uplaodPhoto(@Part("photo") TypedFile photo,
                            Callback<Object> callback);
    
}
