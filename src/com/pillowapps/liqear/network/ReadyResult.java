package com.pillowapps.liqear.network;

public class ReadyResult {
    private String method;
    private Long errorCode = null;
    private Object object;
    private boolean append = false;
    private int totalPages;

    public ReadyResult(String method, Object object) {
        this.method = method;
        this.object = object;
    }

    public ReadyResult(String method, Object object, boolean append) {
        this.method = method;
        this.object = object;
        this.append = append;
    }

    public ReadyResult(String method, Long errorCode, ApiMethod methodEnum, Object object) {
        this.method = method;
        this.errorCode = errorCode;
        this.object = object;
    }
    public ReadyResult(String method, Object object, int page) {
        this.method = method;
        this.object = object;
        this.totalPages = page;
    }

    public ReadyResult(String method, Object object, ApiMethod methodEnum, int page) {
        this.method = method;
        this.object = object;
        this.totalPages = page;
    }

    public ReadyResult(String method, Object object, boolean append, int page) {
        this.method = method;
        this.object = object;
        this.append = append;
        this.totalPages = page;
    }

    public ReadyResult(String method, Long errorCode, ApiMethod methodEnum,
                       Object object, int page) {
        this.method = method;
        this.errorCode = errorCode;
        this.object = object;
        this.totalPages = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public String getMethod() {
        return method;
    }

    public Object getObject() {
        return object;
    }

    public boolean isAppend() {
        return append;
    }

    @Override
    public String toString() {
        return "ReadyResult{" +
                "method='" + method + '\'' +
                ", errorCode=" + errorCode +
                ", object=" + object +
                ", append=" + append +
                '}';
    }

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isOk() {
        return false;
    }
}
