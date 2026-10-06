package it.campersamu.audioswitchertest;
import android.os.IBinder;
import rikka.shizuku.SystemServiceHelper;
import rikka.shizuku.ShizukuBinderWrapper;
import org.lsposed.hiddenapibypass.HiddenApiBypass;
import java.lang.reflect.Field;
import java.util.Map;
import android.util.Log;

public class ShizukuInjector {
    public static boolean inject() {
        try {
            IBinder rawBinder = SystemServiceHelper.getSystemService("media_router");
            if (rawBinder == null) {
                Log.wtf("ShizukuInjector", "Failed to inject Shizuku binder for media_router2: rawBinder is null!");
                return false;
            }
            
            HiddenApiBypass.addHiddenApiExemptions("Landroid/os/ServiceManager;");
            Class<?> smClass = Class.forName("android.os.ServiceManager");
            Field cacheField = smClass.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            Map<String, IBinder> cache = (Map<String, IBinder>) cacheField.get(null);
            
            IBinder binder = new ShizukuBinderWrapper(rawBinder);
            cache.put("media_router", binder);
            Log.d("ShizukuInjector", "Injected media_router2 Shizuku binder into ServiceManager cache");
            return true;
        } catch (Exception e) {
            Log.wtf("ShizukuInjector", "Failed to inject Shizuku binder for media_router2:", e);
            return false;
        }
    }
}
