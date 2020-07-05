package com.example.quartzdemo.job;

public class FakeWorkerServiceThread extends Thread {
    private String message;

    public FakeWorkerServiceThread(String message) {
        this.message = message;
    }

    public void run(){
        System.out.println("FakeWorkerServiceThread running - " + message);
    }
}
