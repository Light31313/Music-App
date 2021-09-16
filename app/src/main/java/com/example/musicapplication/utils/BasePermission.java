package com.example.musicapplication.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class BasePermission {

    private static final int REQUEST_CODE_PERMISSION = 1;

    private static PermissionBuilder mBuilder;

    // Permission for fragment
    //https://stackoverflow.com/questions/32714787/android-m-permissions-onrequestpermissionsresult-not-being-called
    public static void checkPermission(PermissionBuilder permissionBuilder) {
        mBuilder = permissionBuilder;
        if (!hasPermissionToRequest()) {
            mBuilder.listener.onResult(true, null);
            return;
        }
        // shouldShowRequestPermissionRationale return về true nếu user đã denied request trước đó
        boolean shouldShowReason = shouldShowRequestPermissionRationale();
        if (shouldShowReason && !TextUtils.isEmpty(mBuilder.reasonMessage)) {
            showReason();
        } else {
            requestPermission();
        }
    }

    private static boolean hasPermissionToRequest() {
        for (String permission : mBuilder.permissions) {
            if (ActivityCompat.checkSelfPermission(mBuilder.activity, permission) == PackageManager.PERMISSION_DENIED)
                return true;
        }
        return false;
    }

    private static boolean shouldShowRequestPermissionRationale() {
        for (String permission : mBuilder.permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mBuilder.activity, permission))
                return true;
        }
        return false;
    }

    private static void showReason() {
        new AlertDialog.Builder(mBuilder.activity)
                .setCancelable(false)
                .setMessage(mBuilder.reasonMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission();
                    }
                })
                .show();
    }

    private static void requestPermission() {
        ActivityCompat.requestPermissions(mBuilder.activity, mBuilder.permissions, REQUEST_CODE_PERMISSION);
    }

    private static void showRejectedMessage(final List<String> deniedPermissions) {
        new AlertDialog.Builder(mBuilder.activity)
                .setCancelable(false)
                .setMessage(mBuilder.rejectedMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBuilder.listener.onResult(false, deniedPermissions);
                    }
                })
                .setNegativeButton("CHANGE SETTING", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppDetailSetting();
                    }
                })
                .show();
    }

    private static void openAppDetailSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", mBuilder.activity.getPackageName(), null));
        mBuilder.activity.startActivityForResult(intent, REQUEST_CODE_PERMISSION);
    }

    public static void onRequestPermissionsResult(int requestCode,
                                                 String[] permissions,
                                                 int[] grantResults) {
        if (REQUEST_CODE_PERMISSION != requestCode) return;

        List<String> deniedPermissions = findDeniedPermission(permissions, grantResults);
        // if not permission denied
        if (deniedPermissions.size() == 0) {
            mBuilder.listener.onResult(true, null);
        } else {
            if (TextUtils.isEmpty(mBuilder.rejectedMessage)) {
                mBuilder.listener.onResult(false, deniedPermissions);
            } else {
                showRejectedMessage(deniedPermissions);
            }
        }
    }

    private static List<String> findDeniedPermission(String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                deniedPermissions.add(permissions[i]);
        }
        return deniedPermissions;
    }

    public interface CallbackPermissionListener {
        void onResult(boolean isSuccess, List<String> deniedPermissions);
    }

    public static class PermissionBuilder {
        private final Activity activity;
        private final String[] permissions;
        private String reasonMessage;
        private String rejectedMessage;
        private final CallbackPermissionListener listener;

        public PermissionBuilder(Activity activity,
                          CallbackPermissionListener listener,
                          String... permissions) {
            this.activity = activity;
            this.listener = listener;
            this.permissions = permissions;
        }

        public PermissionBuilder setReason(String messageReason) {
            this.reasonMessage = messageReason;
            return this;
        }

        public PermissionBuilder setRejectedMessage(String rejectedMessage) {
            this.rejectedMessage = rejectedMessage;
            return this;
        }
    }
}

