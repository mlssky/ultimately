package com.xcleans.hotfix;

public class PatchException extends Exception {
    private int errCode;

    public PatchException(int var1, String var2) {
        super(var2);
        this.errCode = var1;
    }

    public PatchException(int var1, Throwable var2) {
        super(var2);
        this.errCode = var1;
    }

    public int getErrCode() {
        return this.errCode;
    }
}
