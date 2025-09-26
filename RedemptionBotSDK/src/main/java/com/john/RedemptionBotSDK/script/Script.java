package com.john.RedemptionBotSDK.script;

public interface Script {
    void onLoad();
    int onLoop();
    void onExit();

    default void sleepSafe(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
