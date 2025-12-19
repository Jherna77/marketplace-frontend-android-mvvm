package com.jhernandez.frontend.bazaar.core.file;

import static com.jhernandez.frontend.bazaar.core.constants.Values.BASE_URL;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/*
* Utils class for file operations.
*/
public class FileUtils implements FileProviderService {

    private static final String IMG_EXT = ".jpg";

    private final Context context;

    public FileUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public File getFileFromUri(Uri uri, String prefix) {
        if (uri == null) return null;

        File file = null;
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            File tempFile = File.createTempFile(prefix, IMG_EXT, context.getCacheDir());
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            }
            file = tempFile;
        } catch (IOException e) {
            Log.e("FileUtils", "Error reading file from URI" + e.getMessage());
            return null;
        }
        return file;
    }

    public List<File> getFilesFromUriList(List<Uri> uris, String prefix) {
        List<File> imageFiles = new ArrayList<>();

        if (uris == null || uris.isEmpty()) return imageFiles;

        for (Uri uri : uris) {
            File file = getFileFromUri(uri, prefix);
            if (file != null) imageFiles.add(file);
        }
        return imageFiles;
    }

    public List<Uri> getUrisFromUrlList(List<String> urls) {
        List<Uri> uris = new ArrayList<>();
        if (urls == null || urls.isEmpty()) return uris;
        for (String url : urls) {
            uris.add(Uri.parse(BASE_URL + url));
        }
        return uris;
    }

}


