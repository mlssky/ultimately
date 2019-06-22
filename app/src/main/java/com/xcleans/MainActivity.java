package com.xcleans;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

//import com.xcleans.hotfix.GlobalCfg;
//import com.xcleans.hotfix.manager.PatchException;
//import com.xcleans.hotfix.patch.ResourceManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import static com.xcleans.Reflection.getFieldValue;
import static com.xcleans.Reflection.getStaticFieldValue;
import static com.xcleans.Reflection.invokeMethod;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_actiivty);


        //Landroid/app/ActivityThread$GcIdler

        a("android.app.ActivityThread$GcIdler");

        Class a = a("android.app.ContextImpl");
        Class b = a("android.app.ActivityThread");

        try {

            Object thread = null;
            try {
                Class<?> c = Class.forName("android.app.ActivityThread");
                //public static ActivityThread currentActivityThread()
                Method currentActivityThread = c.getDeclaredMethod("currentActivityThread");
                currentActivityThread.setAccessible(true);
                thread = currentActivityThread.invoke(null);
            } catch (final Throwable t1) {
                try {
                    Class<?> c = Class.forName("android.app.ActivityThread");
                    thread = getStaticFieldValue(c, "sCurrentActivityThread");
                } catch (final Throwable t2) {
                }
            }

            getHandler(thread);

            getDeclaredField(a, "mOuterContext").set(this.getApplication().getBaseContext(), this.getApplication());
            Object var2 = getDeclaredField(a, "mPackageInfo").get(this.getApplication().getBaseContext());
            getDeclaredField(var2.getClass(), "mApplication").set(var2, this.getApplication());
            Object var3 = getDeclaredField(var2.getClass(), "mActivityThread").get(var2);
            getDeclaredField(var3.getClass(), "mInitialApplication").set(var3, this.getApplication());
            List var4 = (List) getDeclaredField(var3.getClass(), "mAllApplications").get(var3);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }


    /**
     * 遍历所有类
     *
     * @param obj
     * @param fieldName
     * @return
     * @throws
     */
    public static Field getDeclaredField(Object obj, String fieldName) throws Exception {
        Class cls = obj.getClass();
        while (cls != null) {
            try {
                Field field = cls.getDeclaredField(fieldName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException var5) {
                cls = cls.getSuperclass();
            }
        }
        throw new Exception("Field " + fieldName + " not found in " + obj.getClass());
    }

    public static Field getDeclaredField(Class clz, String var1) throws NoSuchFieldException {
        Field field = clz.getDeclaredField(var1);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field;
    }

    private static Class a(String var0) {
        try {
            return Class.forName(var0);
        } catch (Exception var2) {
            return null;
        }
    }


    private static Handler getHandler(final Object thread) {
        Handler handler;

        if (null != (handler = getFieldValue(thread, "mH"))) {
            return handler;
        }

        if (null != (handler = invokeMethod(thread, "getHandler"))) {
            return handler;
        }
        try {
            if (null != (handler = getFieldValue(thread, Class.forName("android.app.ActivityThread$H")))) {
                return handler;
            }
        } catch (final Throwable e) {
        }

        return null;
    }
}
