package com.example.smart_air_app.utils;

public class AdherenceResult {
    private double result;
    private boolean done = false;

    //responsible for running once the operation (going through the logs) is finished
    private Runnable completionListener;

    //runs the listener when we set done to true
    public void setCompletionListener(Runnable listener) {
        this.completionListener = listener;

        if (done) {
            listener.run();
        }
    }

    //set result and possibly run listener (if not null)
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
