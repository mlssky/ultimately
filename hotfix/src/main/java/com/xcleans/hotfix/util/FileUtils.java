package com.xcleans.hotfix.util;

import com.xcleans.hotfix.manager.PatchException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FileUtils {

    /**
     * 检测目录是否存在
     *
     * @param parent
     * @param child
     * @return
     */
    public static File ensureDirCreated(File parent, String child) {
        if (parent == null || parent.isFile() || child.isEmpty()) {
            return null;
        }
        File var2 = new File(parent, child);
        if (!var2.exists() && !var2.mkdirs()) {
            return null;
        }
        return var2;
    }

    /**
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copy(File fromFile, File toFile) throws IOException {
        FileChannel var2 = null;
        FileChannel var3 = null;
        try {
            if (!toFile.exists()) {
                toFile.createNewFile();
            }
            var2 = (new FileInputStream(fromFile)).getChannel();
            var3 = (new FileOutputStream(toFile)).getChannel();
            var2.transferTo(0L, var2.size(), var3);
        } catch (IOException var8) {
            throw var8;
        } finally {
            IoUtils.closeSilent(var2);
            IoUtils.closeSilent(var3);
        }

    }

    /**
     * @param input
     * @param output
     * @throws IOException
     */
    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] var2 = new byte[4092];
        int var3;
        while ((var3 = input.read(var2)) != -1) {
            output.write(var2, 0, var3);
        }
    }

    public static boolean delete(File fileOrDir) {
        boolean flag = false;
        if (fileOrDir != null) {
            if (fileOrDir.isFile()) {
                if (fileOrDir.canWrite()) {
                    File tmp = new File(fileOrDir + "tmp");
                    if (fileOrDir.renameTo(new File(fileOrDir + "tmp"))) {
                        flag = tmp.delete();
                    } else {
                        flag = fileOrDir.delete();
                    }
                }
            } else if (fileOrDir.isDirectory()) {
                clearDir(fileOrDir);
            }
        }
        return flag;
    }

    /**
     * @param file
     */
    public static void clearDir(File file) {
        clearDir(file, null);
    }

    /**
     * @param dirFile
     * @param keepFiles
     */
    public static void clearDir(File dirFile, File[] keepFiles) {
        if (dirFile != null && dirFile.exists() && dirFile.isDirectory()) {
            File[] listFiles = dirFile.listFiles();
            if (listFiles != null) {
                for (int i = 0, n = listFiles.length; i < n; ++i) {
                    File file = listFiles[i];
                    if (keepFiles == null || !Arrays.asList(keepFiles).contains(file)) {
                        if (file.isDirectory()) {
                            clearDir(file, keepFiles);
                        }
                        delete(file);
                    }
                }
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // zip file
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param zipFile
     * @param zipEntry
     * @param outFilePath
     * @throws PatchException
     */
    public static void unzipToFile(ZipFile zipFile, ZipEntry zipEntry, String outFilePath) throws IOException {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = zipFile.getInputStream(zipEntry);
            out = new FileOutputStream(outFilePath);
            FileUtils.copy(in, out);
        } finally {
            IoUtils.closeSilent(in);
            IoUtils.closeSilent(out);
        }
    }
}
