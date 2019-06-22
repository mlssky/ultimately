//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.net;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import com.taobao.sophix.b.b;
import com.taobao.sophix.c.c;
import com.taobao.sophix.e.d;
import com.taobao.sophix.e.f;
import com.taobao.sophix.e.i;
import com.taobao.sophix.e.k;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONObject;

public class NetworkManager implements com.taobao.sophix.c.a {
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;
    private String f;
    private String g;
    private List<String> h;
    private volatile boolean i = true;
    private long j;
    private String k;
    private AtomicBoolean l = new AtomicBoolean(false);


    public synchronized void a(String var1, String var2) {
        if (TextUtils.isEmpty(var2)) {
            throw new RuntimeException("app version is null");
        } else {
            if (!this.l.get()) {
                com.taobao.sophix.e.d.c("NetworkManager", "init", new Object[]{"appVersion", var2});
                this.a = var2;
                File var3 = new File(var1, "download");
                if (!var3.exists()) {
                    var3.mkdirs();
                } else if (com.taobao.sophix.e.f.a(com.taobao.sophix.b.b.b)) {
                    com.taobao.sophix.e.b.a(var3);
                }

                this.g = (new File(var3, "sophix-patch.jar")).getAbsolutePath();

                try {
                    ApplicationInfo var4 = com.taobao.sophix.b.b.b.getPackageManager().getApplicationInfo(com.taobao.sophix.b.b.b.getBaseContext().getPackageName(), 128);
                    if (TextUtils.isEmpty(this.b)) {
                        this.b = String.valueOf(var4.metaData.get("com.taobao.android.hotfix.IDSECRET"));
                    }

                    if (TextUtils.isEmpty(this.c)) {
                        this.c = String.valueOf(var4.metaData.get("com.taobao.android.hotfix.APPSECRET"));
                    }

                    if (TextUtils.isEmpty(this.d)) {
                        this.d = String.valueOf(var4.metaData.get("com.taobao.android.hotfix.RSASECRET"));
                    }
                } catch (Throwable var5) {
                    com.taobao.sophix.e.d.a("NetworkManager", "init", var5, new Object[0]);
                }

                this.b();
                this.l.set(true);
            }

        }
    }

    public void a(String var1, String var2, String var3) {
        this.b = var1;
        this.c = var2;
        this.d = var3;
    }

    public synchronized String a(String var1, c var2, PatchLoadStatusListener var3) {
        if (!this.l.get()) {
            com.taobao.sophix.e.d.d("NetworkManager", "query fail as not init", new Object[0]);
            return null;
        } else if (TextUtils.isEmpty(this.b)) {
            com.taobao.sophix.e.d.d("NetworkManager", "query fail as appId is empty", new Object[0]);
            return null;
        } else if (com.taobao.sophix.d.a.a.c && (TextUtils.isEmpty(this.c) || TextUtils.isEmpty(this.d))) {
            com.taobao.sophix.e.d.d("NetworkManager", "query fail as appSecret/rsaSecret is empty", new Object[0]);
            return null;
        } else if (TextUtils.isEmpty(var1) && var2 == null) {
            com.taobao.sophix.e.d.d("NetworkManager", "query fail as param is empty", new Object[0]);
            return null;
        } else {
            this.a();
            if (TextUtils.isEmpty(this.f)) {
                com.taobao.sophix.e.d.d("NetworkManager", "query fail as can't get device id", new Object[0]);
                return null;
            } else if (this.j != 0L && System.currentTimeMillis() - this.j <= 3000L) {
                com.taobao.sophix.e.d.d("NetworkManager", "query fail as two consecutive query should not short than 3s", new Object[0]);
                return null;
            } else {
                this.j = System.currentTimeMillis();
                this.c();

                try {
                    com.taobao.sophix.e.d.c("NetworkManager", "query start", new Object[0]);
                    long var4 = System.currentTimeMillis();
                    com.taobao.sophix.d.b.a var6 = this.a(var1, var2);
                    com.taobao.sophix.e.d.b("NetworkManager", "query", new Object[]{"get remote patch", var6});
                    if (var2.d != -1 && var6.b <= var2.d) {
                        throw new com.taobao.sophix.a.b(7, "patch version is smaller than current");
                    } else {
                        if (var2.d != -1) {
                            var2.d = var6.b;
                        }

                        String var7 = (new File(this.g)).getParentFile().getAbsolutePath();
                        String var8 = this.a(var6, var7);
                        com.taobao.sophix.e.d.b("NetworkManager", "query", new Object[]{"local server decrypt patch", var8});
                        boolean var9 = com.taobao.sophix.e.a.a(var8, this.g, var6.c, this.d);
                        if (var9 && (new File(this.g)).exists()) {
                            com.taobao.sophix.e.d.c("NetworkManager", "query", new Object[]{"download success"});
                            var3.onLoad(0, 9, "download success", var6.b);
                            com.taobao.sophix.e.i.b(com.taobao.sophix.b.b.b, "hpatch_clear", false);
                            File var10 = new File(this.g);
                            var2.c = "100";
                            var2.g = var10.length();
                            var2.e = System.currentTimeMillis() - var4;
                            this.a(var2);
                            return (new File(this.g)).exists() ? this.g : null;
                        } else {
                            throw new com.taobao.sophix.a.b(11, "server decrypt fail");
                        }
                    }
                } catch (com.taobao.sophix.a.b var11) {
                    var3.onLoad(0, var11.a(), var11.getMessage(), var2.d);
                    if (var11.a() == 6) {
                        com.taobao.sophix.e.d.c("NetworkManager", "query", new Object[]{var11.getMessage()});
                    } else if (var11.a() == 18) {
                        com.taobao.sophix.e.d.d("NetworkManager", "query", new Object[]{var11.getMessage()});
                        com.taobao.sophix.e.i.b(com.taobao.sophix.b.b.b, "hpatch_clear", true);
                    } else {
                        com.taobao.sophix.e.d.b("NetworkManager", "query fail", var11, new Object[]{"code", var11.a(), "msg", var11.getMessage()});
                        var2.c = "101";
                        var2.i = var11.a();
                        this.a(var2);
                    }

                    return null;
                } catch (Throwable var12) {
                    var3.onLoad(0, 31, var12.getMessage(), var2.d);
                    var2.c = "101";
                    var2.i = 31;
                    this.a(var2);
                    return null;
                }
            }
        }
    }

    private void a() {
        String var1;
        try {
            var1 = URLEncoder.encode((String)com.taobao.sophix.b.b.b.getClassLoader().loadClass("com.ta.utdid2.device.UTDevice").getDeclaredMethod("getUtdid", Context.class).invoke((Object)null, com.taobao.sophix.b.b.b.getBaseContext()), "UTF-8");
        } catch (Throwable var3) {
            com.taobao.sophix.e.d.a("NetworkManager", "initDeviceId", var3, new Object[0]);
            throw new RuntimeException("fail to include utdid");
        }

        if (this.f != null) {
            if (!var1.equals(this.f)) {
                com.taobao.sophix.e.d.e("NetworkManager", "device id changed! " + var1 + " " + this.f, new Object[0]);
            }
        } else {
            com.taobao.sophix.e.i.b(com.taobao.sophix.b.b.b, "SP_SOPHIX_DEVICE_ID", var1);
            this.f = var1;
        }

    }

    private void b() {
        StringBuilder var1 = new StringBuilder();
        var1.append("osversion").append("=").append(VERSION.RELEASE).append(",").append("manufacturer").append("=").append(Build.MANUFACTURER).append(",").append("brand").append("=").append(Build.BRAND).append(",").append("model").append("=").append(Build.MODEL).append(",").append("os").append("=").append("android");
        this.e = var1.toString();
        this.f = com.taobao.sophix.e.i.a(com.taobao.sophix.b.b.b, "SP_SOPHIX_DEVICE_ID", (String)null);
    }

    public void a(c var1) {
        if (this.l.get()) {
            com.taobao.sophix.e.d.a("NetworkManager", "commit", new Object[]{var1});
            if (this.i && com.taobao.sophix.e.f.a(com.taobao.sophix.b.b.b)) {
                if (var1 != null && !TextUtils.isEmpty(var1.c) && var1.d != -1) {
                    if (TextUtils.isEmpty(this.f)) {
                        com.taobao.sophix.e.d.d("NetworkManager", "commit", new Object[]{"device id is empty"});
                    } else {
                        final c var2 = new c(var1);
                        long var3;
                        if ("301".equals(var2.c)) {
                            var3 = 0L;
                        } else {
                            var3 = 3000L;
                        }

                        com.taobao.sophix.e.k.a(new Runnable() {
                            public void run() {
                                try {
                                    com.taobao.sophix.d.a.b var1 = new com.taobao.sophix.d.a.b();
                                    a.this.a((com.taobao.sophix.d.a.c)var1, (String)null, (c)var2);
                                    var1.a();
                                    com.taobao.sophix.e.d.a("NetworkManager", "commit success", new Object[]{var1.b()});
                                } catch (Throwable var2x) {
                                    com.taobao.sophix.e.d.a("NetworkManager", "commit fail", var2x, new Object[0]);
                                }

                            }
                        }, var3);
                    }
                }
            }
        }
    }

    private com.taobao.sophix.d.b.a a(String var1, c var2) {
        com.taobao.sophix.d.a.b var3 = null;
        int var4 = 0;

        while(true) {
            try {
                int var5;
                try {
                    var3 = new com.taobao.sophix.d.a.b();
                    this.a((com.taobao.sophix.d.a.c)var3, (String)var1, (c)var2);
                    var3.a();
                    var5 = var3.b();
                } catch (Throwable var30) {
                    throw new com.taobao.sophix.a.b(32, var30);
                }

                if (var5 != 200) {
                    throw new com.taobao.sophix.a.b(var5, "query fail as response code");
                }

                Map var6 = var3.c();
                if (var6 != null && var6.containsKey("x-hotfix-retcode")) {
                    int var7 = Integer.parseInt((String)((List)var6.get("x-hotfix-retcode")).get(0));
                    if (var7 != 0) {
                        byte var37 = 31;
                        String var38 = "query retCode:" + var7;
                        switch(var7) {
                        case -102:
                            var37 = 16;
                            if (var6.containsKey("x-hotfix-cur-time")) {
                                String var39 = (String)((List)var6.get("x-hotfix-cur-time")).get(0);
                                if (!TextUtils.isEmpty(var39) && !var39.equals(this.k)) {
                                    com.taobao.sophix.e.d.d("NetworkManager", "getPatchRemote", new Object[]{"base date", this.k, "correct date", var39});
                                    this.k = var39;
                                }
                            }
                            break;
                        case -101:
                            var37 = 15;
                            break;
                        case -100:
                            var37 = 22;
                            break;
                        case 10:
                            var37 = 6;
                            var38 = "no update";
                            break;
                        case 11:
                            var37 = 17;
                            break;
                        case 12:
                            var37 = 18;
                            var38 = "clear patch";
                            break;
                        case 21:
                            var37 = 6;
                            var38 = "no update";
                            this.i = false;
                            break;
                        case 22:
                            var37 = 6;
                            var38 = "no update";
                            this.i = true;
                        }

                        throw new com.taobao.sophix.a.b(var37, var38);
                    }
                }

                String var36 = null;
                InputStream var8 = null;
                BufferedInputStream var9 = null;
                ByteArrayOutputStream var10 = null;

                try {
                    var8 = var3.d();
                    var9 = new BufferedInputStream(var8);
                    var10 = new ByteArrayOutputStream();
                    byte[] var12 = new byte[8192];

                    int var11;
                    while((var11 = var9.read(var12)) != -1) {
                        var10.write(var12, 0, var11);
                    }

                    var36 = new String(var10.toByteArray());
                } catch (IOException var32) {
                    throw new com.taobao.sophix.a.b(33, var32);
                } finally {
                    com.taobao.sophix.e.b.a(var8);
                    com.taobao.sophix.e.b.a(var9);
                    com.taobao.sophix.e.b.a(var10);
                }

                if (TextUtils.isEmpty(var36)) {
                    throw new com.taobao.sophix.a.b(34, "no content");
                }

                if (com.taobao.sophix.d.a.a.c) {
                    String var40 = com.taobao.sophix.e.c.a(var36, this.c);
                    if (var6 != null && var6.containsKey("x-hotfix-hmac")) {
                        String var42 = (String)((List)var6.get("x-hotfix-hmac")).get(0);
                        if (TextUtils.isEmpty(var40) || !var40.equals(var42)) {
                            throw new com.taobao.sophix.a.b(35, "query broken");
                        }
                    } else {
                        com.taobao.sophix.e.d.d("NetworkManager", "getPatchRemote", new Object[]{"not found remote hmac"});
                    }
                }

                com.taobao.sophix.d.b.a var41;
                try {
                    var41 = new com.taobao.sophix.d.b.a();
                    JSONObject var43 = new JSONObject(var36);
                    var41.c = var43.optString("file_token");
                    var41.a = var43.optString("file_url");
                    var41.b = var43.getInt("patch_version");
                    var41.d = var43.optString("file_hmac");
                } catch (Throwable var29) {
                    throw new com.taobao.sophix.a.b(36, "query parse json fail");
                }

                if (!TextUtils.isEmpty(var41.c) && !TextUtils.isEmpty(var41.a)) {
                    com.taobao.sophix.d.b.a var44 = var41;
                    return var44;
                }

                throw new com.taobao.sophix.a.b(37, "query lack token or url");
            } catch (com.taobao.sophix.a.b var34) {
                ++var4;
                if (var4 > 2) {
                    throw var34;
                }

                if (var34.a() == 6 || var34.a() == 18) {
                    throw var34;
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException var31) {
                }

                com.taobao.sophix.e.d.d("NetworkManager", "getPatchRemote", new Object[]{"retry num", var4});
            } finally {
                if (var3 != null) {
                    var3.e();
                }

            }
        }
    }

    private void a(com.taobao.sophix.d.a.c var1, String var2, c var3) {
        if (!TextUtils.isEmpty(var2)) {
            com.taobao.sophix.e.d.b("NetworkManager", "openConnection", new Object[]{"cdn reqUrl", var2});
            var1.a(var2);
            var1.a("x-hotfix-os", "android");
            var1.a("x-hotfix-sdk-version", "3.2.8");
        } else {
            StringBuilder var4 = new StringBuilder();
            if (TextUtils.isEmpty(var3.c)) {
                var4.append("/u/").append(this.b).append(File.separator).append(this.f).append(File.separator);
            } else {
                var4.append("/r/").append(this.b).append(File.separator).append(this.f).append(File.separator);
                var4.append(var3.c).append(File.separator);
            }

            var4.append(this.a).append(File.separator).append(var3.d).append(File.separator);
            String var5 = (new StringBuilder()).append(com.taobao.sophix.d.a.a.b).append(com.taobao.sophix.d.a.a.a).append(var4).toString();
            com.taobao.sophix.e.d.b("NetworkManager", "openConnection", new Object[]{"auth reqUrl", var5});
            var1.a(var5);
            var1.a("x-hotfix-os", "android");
            var1.a("x-hotfix-sdk-version", "3.2.8");
            var1.a("x-hotfix-info", this.e);
            if (TextUtils.isEmpty(this.k)) {
                SimpleDateFormat var6 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                this.k = var6.format(new Date());
            }

            String var10 = com.taobao.sophix.e.c.a(var4.toString() + this.k, this.c);
            var1.a("x-hotfix-token", var10);
            String var7;
            if (TextUtils.isEmpty(var3.c)) {
                var7 = "cdate=" + this.k;
            } else {
                var7 = var3.b();
                if (var3.i != -9999) {
                    var1.a("x-hotfix-error", String.valueOf(var3.i));
                }
            }

            var1.a("x-hotfix-request-id", var3.a);
            var1.a("x-hotfix-ext", var7);

            try {
                var1.a("x-hotfix-env", (new com.taobao.sophix.d.a.a()).a());
            } catch (Exception var9) {
                com.taobao.sophix.e.d.b("NetworkManager", var9.getMessage(), new Object[]{var9});
            }

        }
    }

    private String a(com.taobao.sophix.d.b.a var1, String var2) {
        com.taobao.sophix.d.a.b var3 = null;

        String var26;
        try {
            int var4;
            try {
                var3 = new com.taobao.sophix.d.a.b();
                var3.a(var1.a);
                var3.a();
                var4 = var3.b();
            } catch (IOException var21) {
                throw new com.taobao.sophix.a.b(51, var21);
            }

            if (var4 != 200) {
                throw new com.taobao.sophix.a.b(var4, "download fail as response code");
            }

            File var5 = new File(var2, this.a(var1.a, var1.b));
            InputStream var6 = null;
            BufferedInputStream var7 = null;
            FileOutputStream var8 = null;

            try {
                var6 = var3.d();
                var7 = new BufferedInputStream(var6);
                if (var5.exists()) {
                    var5.delete();
                }

                var8 = new FileOutputStream(var5);
                byte[] var25 = new byte[8192];

                int var10;
                while((var10 = var7.read(var25)) != -1) {
                    var8.write(var25, 0, var10);
                }
            } catch (IOException var22) {
                IOException var9 = var22;
                throw new com.taobao.sophix.a.b(52, var22);
            } finally {
                com.taobao.sophix.e.b.a(var6);
                com.taobao.sophix.e.b.a(var7);
                com.taobao.sophix.e.b.a(var8);
            }

            if (com.taobao.sophix.d.a.a.c) {
                var26 = com.taobao.sophix.e.c.a(var5, this.c);
                String var27 = var1.d;
                if (!TextUtils.isEmpty(var27) && !var27.equals(var26)) {
                    throw new com.taobao.sophix.a.b(10, "download broken");
                }
            }

            var26 = var5.getAbsolutePath();
        } finally {
            if (var3 != null) {
                var3.e();
            }

        }

        return var26;
    }

    private String a(String var1, int var2) {
        int var3 = var1.lastIndexOf("/");
        String var4 = var1.substring(var3 + 1);
        String[] var5 = var4.split("\\.");
        String var6;
        if (var5 != null && var5.length >= 2) {
            var6 = var5[0] + "_" + var2 + "." + var5[1];
        } else {
            var6 = var4 + "_" + var2 + ".patch";
        }

        return var6;
    }

    private void c() {
        try {
            com.taobao.sophix.b.b.b.getClassLoader().loadClass("com.ta.utdid2.device.SophixInvoker").getDeclaredMethod("invokeAlicloudReport", Application.class).invoke((Object)null, com.taobao.sophix.b.b.b);
        } catch (Exception var2) {
            com.taobao.sophix.e.d.a("NetworkManager", "reportDailyActive fail to invoke ams reporter", var2, new Object[0]);
            throw new RuntimeException("may not keep com.ta.utdid2.device");
        }
    }

    public void a(List<String> var1) {
        this.h = var1;
    }

    private final class a {
        private a() {
        }

        public String a() {
            try {
                JSONObject var1 = new JSONObject();
                JSONArray var2 = new JSONArray();
                Iterator var3 = a.this.h.iterator();

                String var4;
                while(var3.hasNext()) {
                    var4 = (String)var3.next();
                    var2.put(var4);
                }

                var1.put("tags", var2);
                com.taobao.sophix.d.c.a.a().a(com.taobao.sophix.b.b.b);
                var1.put("network", com.taobao.sophix.d.c.a.a().d());
                var1.put("carrier", com.taobao.sophix.d.c.a.a().b());
                var1.put("stable", com.taobao.sophix.b.b.f ? "1" : "0");
                String var6 = var1.toString();
                var4 = new String(Base64.encode(var6.getBytes(), 2));
                com.taobao.sophix.e.d.b("NetworkManager", "[EnvHeaderBuilder] encode: " + var4, new Object[0]);
                return var4;
            } catch (Exception var5) {
                var5.printStackTrace();
                return "";
            }
        }
    }
}
