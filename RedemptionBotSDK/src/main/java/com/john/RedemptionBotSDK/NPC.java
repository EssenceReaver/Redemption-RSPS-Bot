package com.john.RedemptionBotSDK;

import com.john.RedemptionBotSDK.util.ReflectionLoader;
import com.john.RedemptionBotSDK.util.ReflectionResult;

import java.util.Collections;
import java.util.List;

import static com.john.RedemptionBotSDK.util.ReflectionLoader.*;

public class NPC {

    public Object npcObject;
    public int npcId;
    public long npcRealId;
    public int npcXPos;
    public int npcYPos;

    public NPC(Object npcObject, int objectId, long npcRealId, int xPos, int yPos){
        this.npcObject = npcObject;
        this.npcId = objectId;
        this.npcRealId = npcRealId;
        this.npcXPos = xPos;
        this.npcYPos = yPos;
    }

    //Returns a list of NPC objects
    public static List<Object> getNPCList() {
        ReflectionResult npcListResult = safeInvoke(ReflectionLoader.RUNELITE_NPCLIST_METHOD, null);
        if (!npcListResult.success() || npcListResult.value() == null){
            System.err.println("getNPCList: Could not get npcListResult");
            return Collections.emptyList();
        }
        //noinspection unchecked
        return (List<Object>) npcListResult.value();
    }

    //Returns an array of NPC objects
    public static Object[] getNPCArray() {
        ReflectionResult npcArrayResult = safeInvoke(ReflectionLoader.RUNELITE_NPCARRAY_METHOD, null);
        if (!npcArrayResult.success() || npcArrayResult.value() == null){
            System.err.println("getNPCArray: Could not get npcArrayResult");
            return new Object[]{};
        }
        return (Object[]) npcArrayResult.value();
    }

    //Returns NPC Id(instanced unique Id)
    public static int getNPCId(Object NpcObject) {
        ReflectionResult npcIdResult = safeGet(ReflectionLoader.NPC_ID_FIELD, NpcObject);
        if (!npcIdResult.success() || npcIdResult.value() == null){
            System.err.println("getNPCId: Could not get npcIdResult");
            return -1;
        }
        return (int) npcIdResult.value();
    }

    //Returns NPC realId(Non-instanced static Id)
    public static long getNPCRealId(Object NpcObject){
        ReflectionResult npcInfoClassInstance = safeGet(NPC_INFO_FIELD, NpcObject);
        if (!npcInfoClassInstance.success() || npcInfoClassInstance.value() == null){
            System.err.println("getNPCRealId: Could not get npcInfoClassInstance");
            return -1;
        }
        ReflectionResult npcRealIdResult = safeGet(NPC_INFO_REALID_FIELD, npcInfoClassInstance.value());
        if (!npcRealIdResult.success()){
            System.err.println("getNPCRealId: Could not get npcRealIdResult");
            return -1;
        }
        return (long) npcRealIdResult.value();
    }

    //returns NPC name
    public static String getNPCName(Object NPCObject) {
        ReflectionResult npcNameResult = safeInvoke(ReflectionLoader.NPC_NAME_METHOD, NPCObject);
        if (!npcNameResult.success() || npcNameResult.value() == null){
            System.err.println("getNPCName: Could not get npcNameResult");
            return "";
        }
        return (String) npcNameResult.value();
    }

    //Returns NPC animationId
    public static int getAnimationId(Object NPCObject){
        ReflectionResult npcAnimationIdResult = safeGet(ReflectionLoader.ENTITY_FIELD_ANIMATION_ID, NPCObject);
        if (!npcAnimationIdResult.success() || npcAnimationIdResult.value() == null){
            return -1;
        }
        return ((int[])npcAnimationIdResult.value())[0];
    }

    //Returns NPC hp (index 0 = current HP, index 1 = max HP)
    public static long getNPCHp(Object NPCObject, int index){
        ReflectionResult npcHpResult = safeInvoke(ReflectionLoader.NPC_HP_METHOD, NPCObject, index);
        if (!npcHpResult.success() || npcHpResult.value() == null){
            System.err.println("getNPCHp: Could not get npcHpResult");
            return -1;
        }
        return (long) npcHpResult.value();
    }

    @Deprecated
    public static Object getNpcById(int npcId){
        List<Object> npcList = getNPCList();
        for (Object npc : npcList){
            int curNpcId = getNPCId(npc);
            if (curNpcId == -1) return null;
            if (curNpcId == npcId) return npc;
        }
        return null;
    }

    //Returns an NPC object from a given npcId(unique instanced npc ID)
    public static NPC getNpcObjById(int npcId){
        List<Object> npcList = getNPCList();
        for (Object npc : npcList){
            int curNpcId = getNPCId(npc);
            if (curNpcId == npcId){
                Integer npcTileX = NPC.getNpcTileX(npc);
                Integer npcTileY = NPC.getNpcTileY(npc);
                if (npcTileX == null || npcTileY == null) continue;
                return new NPC(npc, npcId, NPC.getNPCRealId(npc), npcTileX, npcTileY);
            }
        }
        return null;
    }

    //Returns an NPC object from a given RealID(Static non-instanced npc ID)
    public static NPC getNpcObjByRealId(int npcRealId){
        List<Object> npcList = getNPCList();
        for (Object npc : npcList){
            long curNpcRealId = getNPCRealId(npc);
            if (curNpcRealId == npcRealId){
                Integer npcTileX = NPC.getNpcTileX(npc);
                Integer npcTileY = NPC.getNpcTileY(npc);
                if (npcTileX == null || npcTileY == null) continue;
                return new NPC(npc, NPC.getNPCId(npc), NPC.getNPCRealId(npc), npcTileX, npcTileY);
            }
        }
        return null;
    }

    //Returns true if NPC is attackable
    public static boolean isNpcAttackable(int npcId){
        NPC npc = getNpcObjById(npcId);
        if (npc == null) {
            System.err.println("isNpcAttackable: Could not get NPC");
            return false;
        }
        ReflectionResult npcInfoClassInstance = safeGet(NPC_INFO_FIELD, npc.npcObject);
        if (!npcInfoClassInstance.success() || npcInfoClassInstance.value() == null){
            System.err.println("isNpcAttackable: Could not get npcInfoClassInstance");
            return false;
        }
        ReflectionResult npcContextMenu = safeGet(NPC_INFO_CONTEXTMENU_FIELD, npcInfoClassInstance.value());
        if (!npcContextMenu.success()){
            System.err.println("isNpcAttackable: Could not get npcContextMenu");
            return false;
        }

        String[] contextMenu = (String[]) npcContextMenu.value();
        if (contextMenu == null) return false;
        for (String option : contextMenu) {
            if ("attack".equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static int getFirstNpcIdNearPlayer(String wantedName){
        List<Object> npcList = NPC.getNPCList();
        for (Object npc : npcList){
            int npcId = NPC.getNPCId(npc);
            String npcName = NPC.getNPCName(npc);
            if (npcName != null && npcName.toLowerCase().contains(wantedName.toLowerCase()) && NPC.getNPCHp(npc, 0) > 0){
                return npcId;
            }
        }
        return -1;
    }

    //Returns the closest npc by distance which name contains wantedName. Wont return 0 HP npc
    @Deprecated
    public static NPC getClosestNpc(String wantedName){
        List<Object> npcList = NPC.getNPCList();
        int closest = Integer.MAX_VALUE;
        NPC closestNPC = null;
        for (Object npc : npcList){
            int npcId = NPC.getNPCId(npc);
            String npcName = NPC.getNPCName(npc);
            if (npcName != null && npcName.toLowerCase().contains(wantedName.toLowerCase()) && NPC.getNPCHp(npc, 0) > 0){
                Integer npcTileX = NPC.getNpcTileX(npc);
                Integer npcTileY = NPC.getNpcTileY(npc);
                if (npcTileX == null || npcTileY == null) continue;
                int distanceToPlayer = npcDistance(npcTileX, npcTileY);
                if (distanceToPlayer < closest){
                    closestNPC = new NPC(npc, npcId, NPC.getNPCRealId(npc), npcTileX, npcTileY);
                    closest = distanceToPlayer;
                }
            }
        }
        return closestNPC;
    }

    //Returns the closest npc by distance which name contains wantedName Ignores HP
    public static NPC getClosestNpcIgnoreHp(String wantedName){
        List<Object> npcList = NPC.getNPCList();
        int closest = Integer.MAX_VALUE;
        NPC closestNPC = null;
        for (Object npc : npcList){
            int npcId = NPC.getNPCId(npc);
            String npcName = NPC.getNPCName(npc);
            if (npcName != null && npcName.toLowerCase().contains(wantedName.toLowerCase())){
                Integer npcTileX = NPC.getNpcTileX(npc);
                Integer npcTileY = NPC.getNpcTileY(npc);
                if (npcTileX == null || npcTileY == null) continue;
                int distanceToPlayer = npcDistance(npcTileX, npcTileY);
                if (distanceToPlayer < closest){
                    closestNPC = new NPC(npc, npcId, NPC.getNPCRealId(npc), npcTileX, npcTileY);
                    closest = distanceToPlayer;
                }
            }
        }
        return closestNPC;
    }

    //Takes NPC TileX and TileY and calculates the distance to player.
    public static int npcDistance(int npcTileX, int npcTileY){
        Integer playerTileX = LocalPlayer.getTileX();
        Integer playerTileY = LocalPlayer.getTileY();
        if (playerTileX == null || playerTileY == null) return Integer.MAX_VALUE;
        int dx = npcTileX - playerTileX;
        int dy = npcTileY - playerTileY;
        return (int) Math.sqrt((dx*dx) + (dy*dy));
    }

    //Returns the NPC tile X coordinate
    public static Integer getNpcTileX(Object npcObject){
        ReflectionResult xWaypointResult = safeGet(ReflectionLoader.ENTITY_FIELD_XWAYPOINT_ARRAY, npcObject);
        if (!xWaypointResult.success() || xWaypointResult.value() == null) {
            System.err.println("getX: Could not get X waypoint array");
            return null;
        }
        Integer playerBaseX = LocalPlayer.getBaseX();
        if (playerBaseX == null){
            return null;
        }
        return ((int[]) xWaypointResult.value())[0] + playerBaseX;
    }

    //Returns the NPC tile Y coordinate
    public static Integer getNpcTileY(Object npcObject){
        ReflectionResult xWaypointResult = safeGet(ReflectionLoader.ENTITY_FIELD_YWAYPOINT_ARRAY, npcObject);
        if (!xWaypointResult.success() || xWaypointResult.value() == null) {
            System.err.println("getX: Could not get X waypoint array");
            return null;
        }
        Integer playerBaseY = LocalPlayer.getBaseY();
        if (playerBaseY == null){
            return null;
        }

        return ((int[]) xWaypointResult.value())[0] + playerBaseY;
    }
}
