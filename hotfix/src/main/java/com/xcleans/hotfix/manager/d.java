//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.manager;

public class d {
    private String a = "3.2.8";
    private boolean b;
    private boolean c;
    private boolean d;
    private boolean e;

    public d() {
    }

    public d(boolean var1, boolean var2, boolean var3, boolean var4) {
        this.b = var1;
        this.c = var2;
        this.d = var3;
        this.e = var4;
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("version").append(":").append(this.a).append("\u0000").append("hot").append(":").append(this.e).append("\u0000").append("dex").append(":").append(this.b).append("\u0000").append("res").append(":").append(this.c).append("\u0000").append("so").append(":").append(this.d);
        return var1.toString();
    }
}
