package it.campersamu.audioswitchertest;

import android.content.Context;
import android.media.MediaRoute2Info;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.util.ArrayList;
import java.util.List;

public class AudioRepository {
    private static final String TAG = "AudioRepository";
    private final Context context;
    private final MutableLiveData<List<MediaRoute2Info>> availableRoutes = new MutableLiveData<>(new ArrayList<>());
    private Object mediaRouter2Manager;
    private Class<?> managerClass;

    public AudioRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void initMediaRouter() {
        try {
            boolean injected = ShizukuInjector.inject();
            if (!injected) {
                Log.e(TAG, "Failed to inject Shizuku binder for media_router");
                return;
            }

            HiddenApiBypass.addHiddenApiExemptions("Landroid/media/MediaRouter2Manager;");
            managerClass = Class.forName("android.media.MediaRouter2Manager");
            
            // Provide a wrapper context that returns the shell package name to bypass UID checks
            android.content.ContextWrapper shellContext = new android.content.ContextWrapper(context) {
                @Override
                public String getPackageName() {
                    return "com.android.shell";
                }
                @Override
                public String getOpPackageName() {
                    return "com.android.shell";
                }
                @Override
                public Context getApplicationContext() {
                    return this;
                }
            };
            
            mediaRouter2Manager = HiddenApiBypass.invoke(managerClass, null, "getInstance", shellContext);
            Log.d(TAG, "MediaRouter2Manager initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to init MediaRouter2Manager", e);
        }
    }

    public LiveData<List<MediaRoute2Info>> getAvailableRoutes() {
        return availableRoutes;
    }

    public void refreshRoutes() {
        if (mediaRouter2Manager == null) {
            initMediaRouter();
        }
        if (mediaRouter2Manager == null) return;
        
        try {
            List<MediaRoute2Info> routes = (List<MediaRoute2Info>) HiddenApiBypass.invoke(managerClass, mediaRouter2Manager, "getAllRoutes");
            if (routes != null) {
                Log.d(TAG, "Fetched " + routes.size() + " routes");
                for (MediaRoute2Info r : routes) {
                    Log.d(TAG, "Route: " + r.getName() + " type: " + r.getType());
                }
            } else {
                Log.w(TAG, "getAllRoutes returned null");
            }
            availableRoutes.postValue(routes != null ? routes : new ArrayList<>());
        } catch (Exception e) {
            Log.e(TAG, "Failed to get routes", e);
        }
    }

    public void selectRoute(MediaRoute2Info route) {
        if (mediaRouter2Manager == null) return;
        try {
            HiddenApiBypass.invoke(managerClass, mediaRouter2Manager, "transfer", "com.android.shell", route, android.os.Process.myUserHandle());
            Log.d(TAG, "Selected route: " + route.getName());
            refreshRoutes();
        } catch (Exception e) {
            Log.e(TAG, "Failed to select route", e);
        }
    }
}
