package it.campersamu.audioswitchertest;

import android.content.pm.PackageManager;
import rikka.shizuku.Shizuku;

public class ShizukuManager {

    public interface ShizukuListener {
        void onShizukuAvailable();
        void onShizukuUnavailable();
        void onPermissionGranted();
        void onPermissionDenied();
    }

    private ShizukuListener listener;

    private final Shizuku.OnRequestPermissionResultListener onRequestPermissionResultListener =
            (requestCode, grantResult) -> {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (listener != null) listener.onPermissionGranted();
                } else {
                    if (listener != null) listener.onPermissionDenied();
                }
            };

    private final Shizuku.OnBinderReceivedListener onBinderReceivedListener = () -> {
        if (listener != null) listener.onShizukuAvailable();
    };

    private final Shizuku.OnBinderDeadListener onBinderDeadListener = () -> {
        if (listener != null) listener.onShizukuUnavailable();
    };

    public ShizukuManager(ShizukuListener listener) {
        this.listener = listener;
    }

    public void onCreate() {
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener);
        Shizuku.addBinderReceivedListener(onBinderReceivedListener);
        Shizuku.addBinderDeadListener(onBinderDeadListener);
    }

    public void onDestroy() {
        Shizuku.removeRequestPermissionResultListener(onRequestPermissionResultListener);
        Shizuku.removeBinderReceivedListener(onBinderReceivedListener);
        Shizuku.removeBinderDeadListener(onBinderDeadListener);
    }

    public boolean isShizukuAvailable() {
        return Shizuku.pingBinder();
    }

    public void requestPermission() {
        if (!isShizukuAvailable()) {
            if (listener != null) listener.onShizukuUnavailable();
            return;
        }

        if (Shizuku.isPreV11()) {
            if (listener != null) listener.onPermissionDenied();
            return;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            if (listener != null) listener.onPermissionGranted();
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            if (listener != null) listener.onPermissionDenied();
        } else {
            Shizuku.requestPermission(0);
        }
    }
}
