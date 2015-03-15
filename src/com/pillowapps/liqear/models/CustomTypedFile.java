package com.pillowapps.liqear.models;

import java.io.File;

import retrofit.mime.TypedFile;

public class CustomTypedFile extends TypedFile {
    private final String filename;

    public CustomTypedFile(String mimeType, File file, String filename) {
        super(mimeType, file);
        this.filename = filename;
    }

    @Override
    public String fileName() {
        return filename;
    }
}
