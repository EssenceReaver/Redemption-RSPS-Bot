package com.john;

import com.john.RedemptionBotSDK.script.Script;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScriptThread extends Thread {

    private final Script script;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ScriptListener listener;

    public ScriptThread(Script script, ScriptListener listener){
        this.script = script;
        this.listener = listener;
    }

    public void run(){
        try {
            script.onLoad();

            while (running.get()) {
                int sleepTime = script.onLoop();
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            script.onExit();
            if (listener != null) listener.onScriptStopped();
        }
    }

    public void stopRunning(){
        running.set(false);
    }
}
