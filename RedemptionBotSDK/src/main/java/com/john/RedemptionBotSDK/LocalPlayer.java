package com.john.RedemptionBotSDK;

import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.util.ReflectionLoader;
import com.john.RedemptionBotSDK.util.ReflectionResult;

import static com.john.RedemptionBotSDK.util.ReflectionLoader.*;

public class LocalPlayer {

    //Returns a boolean whether the player is logged in or not.
    public static boolean isLoggedIn() {
        ReflectionResult enumInstanceResult = safeInvoke(ReflectionLoader.RUNELITE_ISLOGGEDIN_METHOD, null);
        if (!enumInstanceResult.success() || enumInstanceResult.value() == null) {
            System.err.println("isLoggedIn: Could not get enum instance");
            return false;
        }

        ReflectionResult isLoggedInResult = safeGet(ReflectionLoader.RUNELITE_ISLOGGEDIN_ENUM_ID_FIELD, enumInstanceResult.value());
        if (!isLoggedInResult.success() || isLoggedInResult.value() == null) {
            System.err.println("isLoggedIn: Could not get enum ID");
            return false;
        }

        return ((int) isLoggedInResult.value()) == 30;
    }

    public static Integer getMaxSkillLevel(Skill skill) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("getMaxSkillLevel: Could not get client instance");
            return null;
        }

        ReflectionResult maxSkillArray = safeGet(CLIENT_MAX_SKILL_LEVEL_ARRAY_FIELD, client.value());
        if (!maxSkillArray.success() || maxSkillArray.value() == null){
            System.err.println("getMaxSkillLevel: Could not get skill array!");
            return null;
        }
        return ((int[]) maxSkillArray.value())[skill.ordinal()];
    }

    public static Integer getCurrentSkillLevel(Skill skill) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("getMaxSkillLevel: Could not get client instance");
            return null;
        }

        ReflectionResult currentSkillArray = safeGet(CLIENT_CURRENT_SKILL_LEVEL_ARRAY_FIELD, client.value());
        if (!currentSkillArray.success() || currentSkillArray.value() == null){
            System.err.println("getMaxSkillLevel: Could not get skill array!");
            return null;
        }
        return ((int[]) currentSkillArray.value())[skill.ordinal()];
    }


    //Returns the players Scene X coordinate
    public static int getSceneX() {
        ReflectionResult localPlayerResult = safeGet(ReflectionLoader.CLIENT_LOCALPLAYER_FIELD, null);
        if (!localPlayerResult.success() || localPlayerResult.value() == null) {
            System.err.println("getX: Could not get local player");
            return -1;
        }

        ReflectionResult xWaypointResult = safeGet(ReflectionLoader.ENTITY_FIELD_XWAYPOINT_ARRAY, localPlayerResult.value());
        if (!xWaypointResult.success() || xWaypointResult.value() == null) {
            System.err.println("getX: Could not get X waypoint array");
            return -1;
        }

        return ((int[]) xWaypointResult.value())[0];
    }

    public static Object getLocalPlayer(){
        ReflectionResult localPlayerResult = safeGet(ReflectionLoader.CLIENT_LOCALPLAYER_FIELD, null);
        if (!localPlayerResult.success() || localPlayerResult.value() == null) {
            System.err.println("getLocalPlayer: Could not get local player");
            return -1;
        }
        return localPlayerResult.value();
    }

    public static Integer getHp(){
        return getCurrentSkillLevel(Skill.HITPOINTS);
    }

    //Returns the players Scene Y coordinate
    public static int getSceneY() {
        ReflectionResult localPlayerResult = safeGet(ReflectionLoader.CLIENT_LOCALPLAYER_FIELD, null);
        if (!localPlayerResult.success() || localPlayerResult.value() == null) {
            System.err.println("getY: Could not get local player");
            return -1;
        }

        ReflectionResult yWaypointResult = safeGet(ReflectionLoader.ENTITY_FIELD_YWAYPOINT_ARRAY, localPlayerResult.value());
        if (!yWaypointResult.success() || yWaypointResult.value() == null) {
            System.err.println("getY: Could not get Y waypoint array");
            return -1;
        }

        return ((int[]) yWaypointResult.value())[0];
    }

    //Returns the current NPC Id(Unique instanced identifier) the player is targetting
    public static int getTargetNPC() {
        ReflectionResult localPlayerResult = safeGet(ReflectionLoader.CLIENT_LOCALPLAYER_FIELD, null);
        if (!localPlayerResult.success() || localPlayerResult.value() == null) {
            System.err.println("getTargetNPC: Could not get local player");
            return -1;
        }

        ReflectionResult targetNPCResult = safeGet(ReflectionLoader.ENTITY_FIELD_TARGETTED_NPC, localPlayerResult.value());
        if (!targetNPCResult.success() || targetNPCResult.value() == null) {
            System.err.println("getTargetNPC: Could not get targeted NPC");
            return -1;
        }

        return (int) targetNPCResult.value();
    }

    public static int getLocalPlayerAnimationId(){
        ReflectionResult localPlayerResult = safeGet(ReflectionLoader.CLIENT_LOCALPLAYER_FIELD, null);
        if (!localPlayerResult.success() || localPlayerResult.value() == null) {
            System.err.println("getTargetNPC: Could not get local player");
            return -1;
        }

        ReflectionResult npcAnimationIdResult = safeGet(ReflectionLoader.ENTITY_FIELD_ANIMATION_ID, localPlayerResult.value());
        if (!npcAnimationIdResult.success() || npcAnimationIdResult.value() == null){
            return -1;
        }
        return ((int[])npcAnimationIdResult.value())[0];

    }

    //Returns an array of item id's at different indexes.
    public static int[] getInventoryIDs() {
        ReflectionResult invMgmtResult = safeGet(ReflectionLoader.INVENTORY_MGMT_CLASS_INHT_FIELD, null);
        if (!invMgmtResult.success() || invMgmtResult.value() == null) {
            System.err.println("getInventoryIDs: Could not get inventory manager");
            return new int[0];
        }

        ReflectionResult playerInvResult = safeInvoke(ReflectionLoader.INHT_GET_INVENTORY_METHOD, invMgmtResult.value(), 93);
        if (!playerInvResult.success() || playerInvResult.value() == null) {
            System.err.println("getInventoryIDs: Could not get player inventory");
            return new int[0];
        }

        ReflectionResult idArrayResult = safeGet(ReflectionLoader.INVENTORY_IDARR_FIELD, playerInvResult.value());
        if (!idArrayResult.success() || idArrayResult.value() == null) {
            System.err.println("getInventoryIDs: Could not get inventory ID array");
            return new int[0];
        }

        return (int[]) idArrayResult.value();
    }

    //Returns an array of the stack sizes of different inventory indexes.
    public static int[] getInventoryStackSizes() {
        ReflectionResult invMgmtResult = safeGet(ReflectionLoader.INVENTORY_MGMT_CLASS_INHT_FIELD, null);
        if (!invMgmtResult.success() || invMgmtResult.value() == null) {
            System.err.println("getInventoryStackSizes: Could not get inventory manager");
            return new int[0];
        }

        ReflectionResult playerInvResult = safeInvoke(ReflectionLoader.INHT_GET_INVENTORY_METHOD, invMgmtResult.value(), 93);
        if (!playerInvResult.success() || playerInvResult.value() == null) {
            System.err.println("getInventoryStackSizes: Could not get player inventory");
            return new int[0];
        }

        ReflectionResult stackSizeArrayResult = safeGet(ReflectionLoader.INVENTORY_STACKSIZEARR_FIELD, playerInvResult.value());
        if (!stackSizeArrayResult.success() || stackSizeArrayResult.value() == null) {
            System.err.println("getInventoryStackSizes: Could not get inventory stack size array");
            return new int[0];
        }

        return (int[]) stackSizeArrayResult.value();
    }

    //Returns the player's plane coordinate
    public static Integer getPlane(){
        ReflectionResult playerPlaneResult = safeInvoke(RUNELITE_GET_PLAYERPLANE_METHOD, null);
        if (!playerPlaneResult.success() || playerPlaneResult.value() == null) {
            System.err.println("getPlane: Could not get local player plane");
            return null;
        }
        return (Integer) playerPlaneResult.value();
    }

    //Returns thet players Base X Coordinate
    public static Integer getBaseX(){
        ReflectionResult playerBaseXResult = safeInvoke(RUNELITE_GET_PLAYERBASE_X_METHOD, null);
        if (!playerBaseXResult.success() || playerBaseXResult.value() == null) {
            System.err.println("getBaseX: Could not get local player base x coordinate");
            return null;
        }
        return (Integer) playerBaseXResult.value();
    }

    //Returns the players Base Y Coordinate
    public static Integer getBaseY(){
        ReflectionResult playerBaseYResult = safeInvoke(RUNELITE_GET_PLAYERBASE_Y_METHOD, null);
        if (!playerBaseYResult.success() || playerBaseYResult.value() == null) {
            System.err.println("getBaseY: Could not get local player base y coordinate");
            return null;
        }
        return (Integer) playerBaseYResult.value();
    }

    //Returns the player's Tile Y coordinate
    public static Integer getTileX(){
        Integer baseX = getBaseX();
        int sceneX = getSceneX();
        if (baseX == null || sceneX == -1) return null;
        return sceneX + baseX;

    }

    //Returns the player's Tile X coordinate
    public static Integer getTileY(){
        Integer baseY = getBaseY();
        int sceneY = getSceneY();
        if (baseY == null || sceneY == -1) return null;
        return sceneY + baseY;
    }

    //Returns the inventory index of an itemId if it exists. Null otherwise
    public static Integer getItemIndexFromInventory(int itemId){
        int[] inventoryIds = LocalPlayer.getInventoryIDs();
        for (int j = 0; j < inventoryIds.length; j++) {
            if (inventoryIds[j] == itemId) {
                return j;
            }
        }
        return null;
    }
}
