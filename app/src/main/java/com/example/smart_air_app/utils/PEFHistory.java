package com.example.smart_air_app.utils;

public class PEFHistory {
    private int[][] result;
    private boolean done = false;
    //runs when the process (going through logs) is complete
    private Runnable completionListener;

    //runs the listener when we set done to true
    public void setCompletionListener(Runnable listener) {
        this.completionListener = listener;

        if (done) {
            listener.run();
        }
    }

    //set result and possibly run listener (if not null)
    public void setResult(int[][] result) {
        this.result = result;
        done = true;
        if (completionListener != null) {
            completionListener.run();
        }
    }

    public int[][] getResult() {
        return result;
    }

    public boolean isDone() {
        return done;
    }
}
