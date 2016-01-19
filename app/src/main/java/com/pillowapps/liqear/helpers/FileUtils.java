package com.pillowapps.liqear.helpers;

import android.webkit.MimeTypeMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final int BUFFER_SIZE = 8 * 1024;

    private FileUtils() {
        // no-op
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        while (true) {
            int count = is.read(bytes, 0, BUFFER_SIZE);
            if (count == -1) {
                break;
            }
            os.write(bytes, 0, count);
        }
    }
}
