/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dalvik.system;

import java.io.File;

/**
 * 方法中直接引用到的类（第一层级关系，不会进行递归搜索）
 * 和clazz都在同一个dex中的话，那么这个类就会被打上CLASS_ISPREVERIFIED：
 *
 *
 * 在ART模式下，如果类修改了结构，就会出现内存错乱的问题。
 * 为了解决这个问题，就必须把所有相关的调用类、
 * 父类子类等等全部加载到patch.dex中，
 * 这会导致ART下的补丁包异常的大，进一步增加应用启动加载的时候，耗时更加严重
 *
 *
 * QFIX
 * https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653577964&idx=1&sn=bac5c8883b7aaaf7d7d9ea227f200412&chksm=84b3b0ebb3c439fd56a502a27e1adc18f600b875718e537191ef109e2d18dae1c52e5e36f2d9&scene=4#wechat_redirect
 * 补丁安装后，预先以 const-class/instance-of 方式主动引用补丁类，
 * 这次引用会触发加载补丁类并将引用放入 dex 的已解析类缓存里
 * ，后续 app 实际业务逻辑引用到补丁类时，直接从已解析缓存里就能取到
 * ，这样很简单地就绕开了“unexpected DEX”异常，
 * 而且这里只是很简单地执行了一条轻量级的语句，并没有其它额外的影响。
 *
 * 由于”unexpected DEX”异常出现在 dalvik 的实现里，art 模式下不会存在，
 * 以上预先引用补丁类的逻辑只需用在5.0以下的系统。
 *
 *Android热修复:Qfix方案的gradle实践
 * https://www.jianshu.com/p/4ce6ac99afc7
 *
 * A class loader that loads classes from {@code .jar} and {@code .apk} files
 * containing a {@code classes.dex} entry.
 * This can be used to execute code not installed as part of an application.
 *
 * <p>This class loader requires an application-private, writable directory to
 * cache optimized classes. Use {@code Context.getCodeCacheDir()} to create
 * such a directory: <pre>   {@code
 *   File dexOutputDir = context.getCodeCacheDir();
 * }</pre>
 *
 * <p><strong>Do not cache optimized classes on external storage.</strong>
 * External storage does not provide access controls necessary to protect your
 * application from code injection attacks.
 */
public class DexClassLoader extends BaseDexClassLoader {
    /**
     * Creates a {@code DexClassLoader} that finds interpreted and native
     * code.  Interpreted classes are found in a set of DEX files contained
     * in Jar or APK files.
     *
     * <p>The path lists are separated using the character specified by the
     * {@code path.separator} system property, which defaults to {@code :}.
     *
     * @param dexPath the list of jar/apk files containing classes and
     *     resources, delimited by {@code File.pathSeparator}, which
     *     defaults to {@code ":"} on Android
     * @param optimizedDirectory directory where optimized dex files
     *     should be written; must not be {@code null}
     * @param librarySearchPath the list of directories containing native
     *     libraries, delimited by {@code File.pathSeparator}; may be
     *     {@code null}
     * @param parent the parent class loader
     */
    public DexClassLoader(String dexPath, String optimizedDirectory,
            String librarySearchPath, ClassLoader parent) {
        super(dexPath, new File(optimizedDirectory), librarySearchPath, parent);
    }
}