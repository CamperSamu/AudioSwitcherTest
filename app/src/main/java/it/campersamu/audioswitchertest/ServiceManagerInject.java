package it.campersamu.audioswitchertest;
import android.os.IBinder;
import rikka.shizuku.SystemServiceHelper;
import rikka.shizuku.ShizukuBinderWrapper;
import org.lsposed.hiddenapibypass.HiddenApiBypass;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.Field;
import android.util.Log;

public class ServiceManagerInject {
    public static void inject() {
        try {
            HiddenApiBypass.addHiddenApiExemptions("Landroid/os/ServiceManager;");
            Class<?> smClass = Class.forName("android.os.ServiceManager");
            Field cacheField = smClass.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            Map<String, IBinder> cache = (Map<String, IBinder>) cacheField.get(null);
            
            IBinder binder = new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("media_router"));
            cache.put("media_router", binder);
            Log.d("Inject", "Injected media_router2 binder");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
