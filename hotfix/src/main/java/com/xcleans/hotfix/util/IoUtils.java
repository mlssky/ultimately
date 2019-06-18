package com.xcleans.hotfix.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Created by mengliwei on 2019-06-16.
 *
 * @function:
 * @since 1.0.0
 */
public final class IoUtils {


    public static void closeSilent(Closeable closeable) {
        if (closeable != null) {
            boolean isSucc = false;
            try {
                closeable.close();
                isSucc = true;
            } catch (IOException var2) {
            } finally {

            }
        }
    }

    public static void closeSilent(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException var2) {
            }
        }
    }
}
