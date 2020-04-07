package com.xcleans.apm.task.memory

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Process
import android.util.Log

/**
 * 1.通过读取/system/build.prop系统文件获取
 *
 * dalvik.vm.heapstartsize：堆分配的初始大小，值越大(应用较大时需要调用gc和堆调整策略，导致应用反应较慢)这个值越大系统ram消耗越快，但是应用更流畅。
 * dalvik.vm.heapgrowthlimit：单个应用可用最大内存主要对应的是这个值,它表示单个进程内存被限定在64m,即程序运行过程中实际只能使用64m内存，超出就会报OOM
 * dalvik.vm.heapsize：This is the size of the application's Dalvik heap if it has specified <code>android:largeHeap="true"</code> in its manifest.
 *
 * Tips:
 * Android O (8.0)开始，对 /system/build.prop 的权限进行了限制，不再对非 root 用户开放
 * <p>
 * 1.优先加载 system/build.prop 文件（可能无访问权限）
 * 2.build.prop加载文件失败后，反射获取 {android.os.SystemProperties}或使用命令获取（兼容Android9.0）
 * </p>
 * Created by mengliwei on 2020/3/31.
 */
class MemoryTask {


    // Return total PSS memory usage in kB mapping a file of one of the following extension:
    //.so, .jar, .apk, .ttf, .dex, .odex, .oat, .art .
    var swappablePss: Int = 0

    var lowMemory: Boolean = false


    fun isLowMemoryDevice(activityManager: ActivityManager): Boolean {
        // Explicitly check with an if statement, on some devices both parts of boolean expressions
        // can be evaluated even if we'd normally expect a short circuit.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activityManager.isLowRamDevice
        } else {
            true
        }
    }


    fun test(context: Context) {

        //        val memoryInfo = Debug.MemoryInfo()
        //        Debug.getMemoryInfo(memoryInfo)

        //        Log.d("TestMM", String.format("nativePss=%dKB", memoryInfo.nativePss))
        //        Log.d("TestMM", String.format("dalvikPss=%dKB", memoryInfo.dalvikPss))
        //        Log.d("TestMM", String.format("getTotalPss=%dKB", memoryInfo.totalPss))

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

        val processMemoryInfo = activityManager?.getProcessMemoryInfo(intArrayOf(Process.myPid()))

        var memoryInfo = processMemoryInfo?.get(0)

        Log.d("TestMM", String.format("getTotalPss=%dKB", memoryInfo?.totalPss))
        Log.d("TestMM", String.format("nativePss=%dKB", memoryInfo?.nativePss))
        Log.d("TestMM", String.format("dalvikPss=%dKB", memoryInfo?.dalvikPss))
        Log.d("TestMM", String.format("otherPss=%dKB", memoryInfo?.otherPss))
        Log.d("TestMM", String.format("nativePrivateDirty=%dKB", memoryInfo?.nativePss))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(
                "TestMM", String.format("dalvikPss=%s", memoryInfo?.memoryStats.toString())
            )
        } else {
            //                        pm.getSummaryJavaHeap();
            //                        stats.put("summary.java-heap", Integer.toString(getSummaryJavaHeap()));
            //                        stats.put("summary.native-heap", Integer.toString(getSummaryNativeHeap()));
            //                        stats.put("summary.code", Integer.toString(getSummaryCode()));
            //                        stats.put("summary.stack", Integer.toString(getSummaryStack()));
            //                        stats.put("summary.graphics", Integer.toString(getSummaryGraphics()));
            //                        stats.put("summary.private-other", Integer.toString(getSummaryPrivateOther()));
            //                        stats.put("summary.system", Integer.toString(getSummarySystem()));
            //                        stats.put("summary.total-pss", Integer.toString(getSummaryTotalPss()));
            //                        stats.put("summary.total-swap", Integer.toString(getSummaryTotalSwap()));
            //                        Log.d("TestMM", String.format("dalvikPss=%s", pm.getMemoryStats("summary.code")));
            //                        {summary.code=688, summary.stack=68, summary.graphics=0, summary.java-heap=5800, summary.native-heap=4004, summary.system=1588, summary.total-pss=13324, summary.private-other=1176, summary.total-swap=8384}
        }


        //内存约束
        //java heap size
        val largeSize = activityManager?.largeMemoryClass
        val mmClz = activityManager?.memoryClass
        val memoryInfo1 = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo1)

        //java heap 限制
        Log.d("TestMM", "lowMemory=" + memoryInfo1.lowMemory)
        Log.d("TestMM", String.format("getLargeMemoryClass=%dMB", largeSize))
        Log.d("TestMM", String.format("getMemoryClass=%dMB", mmClz))

        //系统总的内存
        Log.d("TestMM", "availMem=" + memoryInfo1.availMem)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Log.d("TestMM", "totalMem=" + memoryInfo1.totalMem)
        }
        Log.d("TestMM", "threshold=" + memoryInfo1.threshold)

        Log.d(
            "TestMM",
            String.format("availableProcessors=%d", Runtime.getRuntime().availableProcessors())
        )
        Log.d("TestMM", String.format("freeMemory=%d", Runtime.getRuntime().freeMemory()))
        Log.d("TestMM", String.format("totalMemory=%dB", Runtime.getRuntime().totalMemory()))
        Log.d("TestMM", String.format("maxMemory=%dB", Runtime.getRuntime().maxMemory()))

        Log.d(
            "TestMM",
            String.format("getNativeHeapAllocatedSize=%d", Debug.getNativeHeapAllocatedSize())
        )
        Log.d("TestMM", String.format("getNativeHeapFreeSize=%d", Debug.getNativeHeapFreeSize()))
        Log.d("TestMM", String.format("getNativeHeapSize=%d", Debug.getNativeHeapSize()))
    }
}