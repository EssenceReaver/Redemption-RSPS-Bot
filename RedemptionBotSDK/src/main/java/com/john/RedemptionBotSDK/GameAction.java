package com.john.RedemptionBotSDK;

import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.util.Calculator;
import com.john.RedemptionBotSDK.util.ReflectionLoader;
import com.john.RedemptionBotSDK.util.ReflectionResult;

import static com.john.RedemptionBotSDK.GameState.getInterfaceIfOpen;
import static com.john.RedemptionBotSDK.enums.InterfaceID.AFKPATROL_INTERFACE;
import static com.john.RedemptionBotSDK.enums.InterfaceID.AFKPATROL_INTERFACE_TEXT;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.*;

public class GameAction {

    public static boolean attackNPC(int NPCIndex) {
        if (!NPC.isNpcAttackable(NPCIndex) || LocalPlayer.getTargetNPC() == NPCIndex) {return false;}

        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("attackNPC: Could not get client instance");
            return false;
        }
        ReflectionResult packetResult = safeInvoke(ReflectionLoader.CLIENT_PACKET_METHOD, client.value(), 10, 0, NPCIndex, 0, 0, "", "");
        return packetResult.success();
    }

    //Default InterfaceIndex is 1, some interface uses multiple buttons etc, so multiple index.
    public static boolean clickInterface(int interfaceID, int interfaceIndex, int interfaceOption) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("clickInterface: Could not get client instance");
            return false;
        }
        ReflectionResult clickInterfaceResult = safeInvoke(ReflectionLoader.CLIENT_PACKET_METHOD, client.value(), 57, 0, interfaceOption, interfaceIndex, interfaceID, "", "");
        return clickInterfaceResult.success();
    }

    public static boolean clickDialogue(int dialogueID, int dialogueIndex) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("clickDialogue: Could not get client instance");
            return false;
        }
        ReflectionResult clickDialogueResult = safeInvoke(ReflectionLoader.CLIENT_PACKET_METHOD, client.value(), 30, 0, 0, dialogueIndex, dialogueID, "", "");
        return clickDialogueResult.success();
    }

    //Takes in tile coordinates x, y and converts them to scene coordinates. Walks to them if in range.
    public static boolean walkTo(int xPos, int yPos) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("walkTo: Could not get client instance");
            return false;
        }
        Integer sceneX = convertTileXToSceneX(xPos);
        Integer sceneY = convertTileYToSceneY(yPos);
        if (sceneY == null || sceneX == null){return false;}
        ReflectionResult walkToResult = safeInvoke(ReflectionLoader.CLIENT_PACKET_METHOD, client.value(), 23, 0, -1, sceneX, sceneY, "", "");
        return walkToResult.success();
    }

    public static boolean interactWithObject(int objectID, int objectXPos, int objectYPos) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("interactWithObject: Could not get client instance");
            return false;
        }
        Integer sceneX = convertTileXToSceneX(objectXPos);
        Integer sceneY = convertTileYToSceneY(objectYPos);
        if (sceneY == null || sceneX == null){return false;}
        ReflectionResult interactResult = safeInvoke(ReflectionLoader.CLIENT_PACKET_METHOD, client.value(), 502, 0, objectID, sceneX, sceneY, "", "");
        return interactResult.success();
    }

    public static boolean interactWithObject2(int objectID, int objectXPos, int objectYPos) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("interactWithObject: Could not get client instance");
            return false;
        }
        Integer sceneX = convertTileXToSceneX(objectXPos);
        Integer sceneY = convertTileYToSceneY(objectYPos);
        if (sceneY == null || sceneX == null){return false;}
        ReflectionResult interactResult = safeInvoke(ReflectionLoader.CLIENT_PACKET_METHOD, client.value(), 900, 0, objectID, sceneX, sceneY, "", "");
        return interactResult.success();
    }

    public static boolean useInventoryObject(int inventoryIndex, int interfaceID) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("useInventoryObject: Could not get client instance");
            return false;
        }
        ReflectionResult useInventoryResult = safeInvoke(ReflectionLoader.CLIENT_PACKET_METHOD, client.value(), 57, 0, 2, inventoryIndex, interfaceID, "", "");
        return useInventoryResult.success();
    }

    public static boolean equipInventoryObject(int inventoryIndex, int interfaceID) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("equipInventoryObject: Could not get client instance");
            return false;
        }
        ReflectionResult useInventoryResult = safeInvoke(ReflectionLoader.CLIENT_PACKET_METHOD, client.value(), 57, 0, 3, inventoryIndex, interfaceID, "", "");
        return useInventoryResult.success();
    }

    public static boolean sendPlayerCommand(String command){
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("sendPlayerCommand: Could not get client instance");
            return false;
        }
        ReflectionResult sendCommandResult = safeInvoke(ReflectionLoader.CLIENT_COMMAND_METHOD, client.value(), command, false);
        return sendCommandResult.success();
    }

    public static boolean solveAfkPatrol(){
        Object AFKPatrolInterface = getInterfaceIfOpen(AFKPATROL_INTERFACE_TEXT.id, AFKPATROL_INTERFACE.id);
        if (AFKPatrolInterface == null) return true;

        ReflectionResult interfaceTextResult = safeInvoke(INTERFACE_RUNELITE_TABLE_TEXT_METHOD, null, AFKPatrolInterface);
        if (!interfaceTextResult.success() || interfaceTextResult.value() == null){
            System.err.println("solveAfkPatrol: Could not find text");
            return false;
        }
        String text = ((String) interfaceTextResult.value()).toLowerCase();
        if (text.contains("afk patrol:")){
            int result = Calculator.calculate(text);
            System.out.println("I Solved AFK Patrol, Answer: " + result);
            ReflectionResult sendAnswer = safeInvoke(CLIENT_RESUME_P_NAMEDIALOG_METHOD, null, Integer.toString(result));
            return sendAnswer.success();
        }
        return false;
    }

    public static boolean sendPCountDialogue(int pCountIn){
        ReflectionResult sendAnswer = safeInvoke(CLIENT_RESUME_P_COUNTDIALOG_METHOD, null, pCountIn);
        if (!sendAnswer.success()) System.err.println("sendPCountDialogue: Could not send player count dialogue");
        return sendAnswer.success();
    }

    public static Integer convertTileXToSceneX(int tileCoordIn){
        Integer currentBaseX = LocalPlayer.getBaseX();
        if (currentBaseX == null){ return null;}
        return tileCoordIn - currentBaseX;
    }
    public static Integer convertTileYToSceneY(int tileCoordIn){
        Integer currentBaseY = LocalPlayer.getBaseY();
        if (currentBaseY == null){ return null;}
        return tileCoordIn - currentBaseY;
    }
}
