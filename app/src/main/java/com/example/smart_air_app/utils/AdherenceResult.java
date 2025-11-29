package com.example.smart_air_app.utils;

public class AdherenceResult {
    private double result;
    private boolean done = false;
    private Runnable completionListener;

    //runs the listener when we set done to true
    public void setCompletionListener(Runnable listener) {
        this.completionListener = listener;

        if (done) {
            listener.run();
        }
    }

    //set result and possibly run listener
    public void setResult(double result) {
        this.result = result;
        done = true;
        if (completionListener != null) {
            completionListener.run();
        }
    }

    public double getResult() {
        return result;
    }

    public boolean isDone() {
        return done;
    }
}
