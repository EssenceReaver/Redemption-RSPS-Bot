package com.john.RedemptionBotSDK.enums;

public enum Skill {
    ATTACK("Attack"),
    DEFENCE("Defence"),
    STRENGTH("Strength"),
    HITPOINTS("Hitpoints"),
    RANGED("Ranged"),
    PRAYER("Prayer"),
    MAGIC("Magic"),
    COOKING("Cooking"),
    WOODCUTTING("Woodcutting"),
    FLETCHING("Fletching"),
    FISHING("Fishing"),
    FIREMAKING("Firemaking"),
    CRAFTING("Crafting"),
    SMITHING("Smithing"),
    MINING("Mining"),
    HERBLORE("Herblore"),
    AGILITY("Agility"),
    THIEVING("Thieving"),
    SLAYER("Slayer"),
    FARMING("Farming"),
    RUNECRAFT("Runecraft"),
    SUMMONING("Summoning"),
    HUNTER("Hunter"),
    CONSTRUCTION("Construction"),
    DUNGEONEERING("Dungeoneering"),
    GAMBLING("Gambling"),
    SCAVENGING("Scavenging"),
    KARMA("Karma"),
    SALVAGING("Salvaging"),
    OVERALL("Overall");

    private final String id;

    Skill(String skillString) {
        this.id = skillString;
    }

    public String getId() {
        return this.id;
    }
}
