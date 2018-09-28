package com.odoo.experience.core.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {
    public static final int THUMBNAIL_SIZE = 500;

    /**
     * Read bytes.
     *
     * @param uri      the uri
     * @param resolver the resolver
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static byte[] readBytes(Uri uri, ContentResolver resolver, boolean thumbnail)
            throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = resolver.openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        if (!thumbnail) {
            // this is storage overwritten on each iteration with bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            // we need to know how may bytes were read to write them to the
            // byteBuffer
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } else {
            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
            int thumb_width = imageBitmap.getWidth() / 2;
            int thumb_height = imageBitmap.getHeight() / 2;
            if (thumb_width > THUMBNAIL_SIZE) {
                thumb_width = THUMBNAIL_SIZE;
            }
            if (thumb_width == THUMBNAIL_SIZE) {
                thumb_height = ((imageBitmap.getHeight() / 2) * THUMBNAIL_SIZE)
                        / (imageBitmap.getWidth() / 2);
            }
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, thumb_width, thumb_height, false);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteBuffer);
        }
        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public static String uriToBase64(Uri uri, ContentResolver resolver) {
        return uriToBase64(uri, resolver, false);
    }

    public static String uriToBase64(Uri uri, ContentResolver resolver, boolean thumbnail) {
        String encodedBase64 = "";
        try {
            byte[] bytes = readBytes(uri, resolver, thumbnail);
            encodedBase64 = Base64.encodeToString(bytes, 0);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return encodedBase64;
    }

    /**
     * Gets the bitmap image.
     *
     * @param context the context
     * @param base64  the base64
     * @return the bitmap image
     */
    public static Bitmap getBitmapImage(Context context, String base64) {
        byte[] imageAsBytes = Base64.decode(base64.getBytes(), 5);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0,
                imageAsBytes.length);

    }
}