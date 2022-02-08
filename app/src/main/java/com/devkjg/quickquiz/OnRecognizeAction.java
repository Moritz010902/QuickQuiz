package com.devkjg.quickquiz;

public abstract class OnRecognizeAction implements Runnable {

    String recognitionResult;

    public String getRecognitionResult() {
        return recognitionResult;
    }

    public void setRecognitionResult(String recognitionResult) {
        this.recognitionResult = recognitionResult;
    }

}
