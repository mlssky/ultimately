// IMyAidlInterface.aidl
package com.xcleans;
import com.xcleans.MyAidlBean;
// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     * 对于非基本数据类型，也不是String和CharSequence类型的 需要指定in/out
     * 对于非基本类型和CharSequence需要指定in、out，inout 类型
     */
   String basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString,in MyAidlBean bean,out MyAidlBean bean2,inout MyAidlBean bean3);
}
