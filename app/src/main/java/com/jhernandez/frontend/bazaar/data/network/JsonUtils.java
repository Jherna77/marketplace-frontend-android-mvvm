package com.jhernandez.frontend.bazaar.data.network;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_IMAGE;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Utility class for JSON operations and request body creation.
 */
public class JsonUtils {

    private static final MediaType JSON = MediaType.parse("application/json");
    private static final MediaType IMAGE = MediaType.parse("image/*");
    public static Gson gson = new Gson();

    public static String parseErrorResponseBody(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();

                try {
                    // Try to parse field errors as a Map<String, String>
                    Type type = new TypeToken<Map<String, String>>() {
                    }.getType();
                    Map<String, String> fieldErrors = gson.fromJson(errorBody, type);

                    if (fieldErrors != null && !fieldErrors.isEmpty()) {
                        StringBuilder errorMessages = new StringBuilder();
                        for (String errorMessage : fieldErrors.values()) {
                            errorMessages.append(errorMessage).append("\n");
                        }
                        return errorMessages.toString();
                    }
                } catch (JsonSyntaxException e) {
                    return errorBody;
                }
            }
        } catch (IOException e) {
            Log.e("ApiErrorParser", "Error parsing errorBody", e);
        }
        return null;
    }

    public static RequestBody createJsonBody(Object object) {
        String json = gson.toJson(object);
        return RequestBody.create(JSON, json);
    }

    public static MultipartBody.Part createImagePart(File imageFile) {
        if (imageFile == null) return null;

        RequestBody requestFile = RequestBody.create(IMAGE, imageFile);
        return MultipartBody.Part.createFormData(
                ARG_IMAGE,
                imageFile.getName(),
                requestFile);
    }

    public static List<MultipartBody.Part> createImagePartList(List<File> imageFiles) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (imageFiles == null) return parts;

        for (File file : imageFiles) {
            MultipartBody.Part part = createImagePart(file);
            if (part != null) parts.add(part);        }
        return parts;
    }
}