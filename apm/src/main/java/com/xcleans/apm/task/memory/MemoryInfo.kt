package com.xcleans.apm.task.memory

/**
 * Created by mengliwei on 2020/3/31.
 */
class MemoryInfo {

    //是否是低内存设备
    var lowMemory: Boolean = false
    //java heap 最大限制定位MB：超过会触发OOM
    var memoryClass: Int = 0

    //java heap
    //当前JVM 内存大小:
    //current JVM 可用内存大小：单位B
    var freeMemory: Long = 0
    //current JVM的总的内存大小：单位B
    var totalMemory: Long = 0
    //If there is no inherent limit then the value {@link
    //     * java.lang.Long#MAX_VALUE} will be returned.
    //measured in bytes
    var maxMemory: Long = 0



//    + "，内存getTotalPss:" + info.getTotalPss()
//    + " nativeSize:" + info.nativePss
//    + " dalvikPss:" + info.dalvikPss
//    + " otherPss:" + info.otherPss

    //system memory
    /**
     * The available memory on the system.  This number should not
     * be considered absolute: due to the nature of the kernel, a significant
     * portion of this memory is actually in use and needed for the overall
     * system to run well.
     */
    var sysAvailMem: Long = 0
    /**
     * The threshold of {@link #availMem} at which we consider memory to be
     * low and start killing background services and other non-extraneous
     * processes.
     */
    var sysThreshold: Long = 0
    /**
     * The total memory accessible by the kernel.  This is basically the
     * RAM size of the device, not including below-kernel fixed allocations
     * like DMA buffers, RAM for the baseband CPU, etc.
     */
    var sysTotalMem: Long = 0


}