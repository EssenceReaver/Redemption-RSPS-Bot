package com.john.RedemptionBotSDK.util;

public class Region {

    private final int minX, maxX, minY, maxY;

    public Region(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean contains(int x, int y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
