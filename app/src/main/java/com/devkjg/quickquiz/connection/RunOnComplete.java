package com.devkjg.quickquiz.connection;

public abstract class RunOnComplete implements Runnable {

    protected String result = "";

    protected String getResult() {
        return result;
    }

    void setResult(String result) {
        this.result = result;
    }

}
