package com.jhernandez.frontend.bazaar.core.file;

import android.net.Uri;

import java.io.File;
import java.util.List;

/*
 * Service interface for providing files and URIs.
 */
public interface FileProviderService {

    File getFileFromUri(Uri uri, String prefix);
    List<File> getFilesFromUriList(List<Uri> uris, String prefix);
    List<Uri> getUrisFromUrlList(List<String> urls);
}
