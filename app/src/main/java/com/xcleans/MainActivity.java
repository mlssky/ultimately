package com.xcleans;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import com.xcleans.anno.ModuleProvider;
import com.xcleans.apm.task.memory.MemoryTask;


@ModuleProvider
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_actiivty);

        //65534

        MemoryTask task = new MemoryTask();
        task.test(this);


        Debug.printLoadedClasses(Debug.SHOW_CLASSLOADER);
        Log.d("TestMM", "ss=" + Debug.getLoadedClassCount());

//        findViewById(R.id.testTxt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, TestActiivty.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
////        SecurityManager s= System.getSecurityManager();
//
//        NativeUncaughtExceptionHandler s = new NativeUncaughtExceptionHandler();
//        s.init(this.getApplicationContext());M

//        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
//        Debug.getMemoryInfo(memoryInfo);
//
//        Log.d("TestMM", String.format("nativePss=%dKB", memoryInfo.nativePss));
//        Log.d("TestMM", String.format("dalvikPss=%dKB", memoryInfo.dalvikPss));
//        Log.d("TestMM", String.format("getTotalPss=%dKB", memoryInfo.getTotalPss()));
//
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//
//
//        Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
//        Debug.MemoryInfo pm = processMemoryInfo[0];
//        Log.d("TestMM", String.format("nativePss=%dKB", pm.nativePss));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Log.d("TestMM", String.format("dalvikPss=%s", pm.getMemoryStats().toString()));
//        } else {
////            pm.getSummaryJavaHeap();
////            stats.put("summary.java-heap", Integer.toString(getSummaryJavaHeap()));
////            stats.put("summary.native-heap", Integer.toString(getSummaryNativeHeap()));
////            stats.put("summary.code", Integer.toString(getSummaryCode()));
////            stats.put("summary.stack", Integer.toString(getSummaryStack()));
////            stats.put("summary.graphics", Integer.toString(getSummaryGraphics()));
////            stats.put("summary.private-other", Integer.toString(getSummaryPrivateOther()));
////            stats.put("summary.system", Integer.toString(getSummarySystem()));
////            stats.put("summary.total-pss", Integer.toString(getSummaryTotalPss()));
////            stats.put("summary.total-swap", Integer.toString(getSummaryTotalSwap()));
////            Log.d("TestMM", String.format("dalvikPss=%s", pm.getMemoryStats("summary.code")));
////            {summary.code=688, summary.stack=68, summary.graphics=0, summary.java-heap=5800, summary.native-heap=4004, summary.system=1588, summary.total-pss=13324, summary.private-other=1176, summary.total-swap=8384}
//        }
//        Log.d("TestMM", String.format("getTotalPss=%dKB", pm.getTotalPss()));
//
//        int largeSize = activityManager.getLargeMemoryClass();
//        int mmClz = activityManager.getMemoryClass();
//        ActivityManager.MemoryInfo memoryInfo1 = new ActivityManager.MemoryInfo();
//        activityManager.getMemoryInfo(memoryInfo1);
//        //系统总的内存
//        Log.d("TestMM", "availMem=" + memoryInfo1.availMem);
//        Log.d("TestMM", "totalMem=" + memoryInfo1.totalMem);
//
//
//        Log.d("TestMM", "lowMemory=" + memoryInfo1.lowMemory);
//        Log.d("TestMM", "threshold=" + memoryInfo1.threshold);
//        Log.d("TestMM", String.format("getLargeMemoryClass=%dMB", largeSize));
//        //每个应用分配的内存大小
//        Log.d("TestMM", String.format("getMemoryClass=%dMB", mmClz));
    }

}
