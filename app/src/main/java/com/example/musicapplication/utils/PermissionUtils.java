package com.example.musicapplication.utils;

import android.Manifest;
import android.app.Activity;



public class PermissionUtils {

    public static void checkExternalStoragePermission(Activity activity, BasePermission.CallbackPermissionListener listener) {
        BasePermission.checkPermission(
                new BasePermission.PermissionBuilder(
                        activity,
                        listener,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_MEDIA_LOCATION)
                        .setReason("We need external storage permission to read/write your file.")
                        .setRejectedMessage("We can\'t read/write external storage without permission."));
    }
}









