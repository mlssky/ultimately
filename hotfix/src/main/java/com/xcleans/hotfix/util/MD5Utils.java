//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public final class MD5Utils {
    private static final char[] a = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取文件的十六进制-Md5值
     * MD5(128位)16b大小
     *
     * @param file
     * @return
     */
    public static String md5(File file) {
        if (file != null && file.exists() && file.canRead() && file.length() != 0L) {
            FileInputStream input = null;
            FileChannel fileChannel = null;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                input = new FileInputStream(file);
                fileChannel = input.getChannel();
                ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);
                int len;
                while ((len = fileChannel.read(buffer)) != -1) {
                    messageDigest.update(buffer.array(), 0, len);
                    buffer.position(0);
                }
                return toHexLowerCase(messageDigest.digest());
            } catch (Exception var11) {
//                d.b("Md5Util", "getFileMD5", var11, new Object[0]);
            } finally {
                IoUtils.closeSilent(input);
                IoUtils.closeSilent(fileChannel);
            }

            return "";
        } else {
            return "";
        }
    }

    /**
     * convert byte array to hex string
     *
     * @param input
     * @return
     */
    private static String toHexLowerCase(byte[] input) {
        char[] out = new char[32];
        int index = 0;
        for (int i = 0; i < 16; ++i) {
            byte current = input[i];
            out[index++] = a[current >>> 4 & 0x0F];
            out[index++] = a[current & 0x0F];
        }
        String var1 = new String(out);
        return var1;
    }
}
