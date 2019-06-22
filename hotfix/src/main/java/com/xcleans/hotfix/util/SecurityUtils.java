package com.xcleans.hotfix.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.xcleans.hotfix.patch.GlobalProperty;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class SecurityUtils {

    public static boolean a(File file) {
        JarFile jarFile = null;
        boolean var3;
        try {
            jarFile = new JarFile(file);
            Manifest manifest = jarFile.getManifest();
            if (manifest == null || manifest.getEntries() == null) {
                var3 = false;
                return var3;
            }

            Enumeration var14 = jarFile.entries();

            while (var14.hasMoreElements()) {
                JarEntry var4 = (JarEntry) var14.nextElement();
                if (!var4.isDirectory()) {
                    String var5 = var4.getName();
                    if (!var5.startsWith("META-INF") && (!var5.endsWith(".dex") || var5.equals("classes.dex"))) {
                        JarEntry var6 = jarFile.getJarEntry(var5);
                        if (var6 == null) {
//                            d.b("SecurityUtils", "verifyPatchLegal", new Object[]{"jarEntry not exist in patch", var5});
                            boolean var15 = false;
                            return var15;
                        }

                        read(jarFile, var6);
                        Certificate[] var7 = var6.getCertificates();
                        boolean var8;
                        if (var7 == null) {
//                            d.b("SecurityUtils", "verifyPatchLegal", new Object[]{"no certs in META-INF", var5});
                            var8 = false;
                            return var8;
                        }

                        if (!verify(var7)) {
//                            d.b("SecurityUtils", "verifyPatchLegal", new Object[]{"certs no match in META-INF", var5});
                            var8 = false;
                            return var8;
                        }
                    }
                }
            }

            var3 = true;
            return var3;
        } catch (IOException var12) {
//            d.b("SecurityUtils", "verifyPatchLegal", var12, new Object[0]);
            var3 = false;
        } finally {
            IoUtils.closeSilent(jarFile);
        }

        return var3;
    }

    private static void read(JarFile jarFile, JarEntry jarEntry) throws IOException {
        InputStream input = null;
        try {
            input = jarFile.getInputStream(jarEntry);
            byte[] buffer = new byte[8192];
            while (true) {
                if (input.read(buffer) > 0) {
                    continue;
                }
            }
        } finally {
            IoUtils.closeSilent(input);
        }

    }

    private static boolean verify(Certificate[] certificates) {
        PublicKey publicKey = getAPKPublicKey();
        if (certificates.length > 0) {
            int var3 = certificates.length - 1;
            while (var3 >= 0) {
                try {
                    certificates[var3].verify(publicKey);
                    return true;
                } catch (Exception var5) {
                    --var3;
                }
            }
        }

        return false;
    }

    /**
     * @return
     */
    private static PublicKey getAPKPublicKey() {
        try {
            PackageManager pm = GlobalProperty.globalApp.getPackageManager();
            String packageName = GlobalProperty.globalApp.getBaseContext().getPackageName();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(packageInfo.signatures[0].toByteArray());
            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            return x509Certificate.getPublicKey();
        } catch (NameNotFoundException var6) {
            Log.e("SecurityUtils", "getPublicKey", var6);
        } catch (CertificateException var7) {
            Log.e("SecurityUtils", "getPublicKey", var7);
        }

        return null;
    }

    public static boolean b(File var0) {
        String var1 = MD5Utils.md5(var0);
        String var2 = i.a(com.taobao.sophix.b.b.b, var0.getName() + "-md5", (String) null);
        return var1 != null && TextUtils.equals(var1, var2);
    }

    public static void c(File var0) {
        i.b(com.taobao.sophix.b.b.b, var0.getName() + "-md5", MD5Utils.md5(var0));
    }

    public static void a(File var0, String var1) {
        i.b(com.taobao.sophix.b.b.b, var0.getName() + "-md5", var1);
    }
}
