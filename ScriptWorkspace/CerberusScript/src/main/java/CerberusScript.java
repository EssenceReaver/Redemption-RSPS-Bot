import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class CerberusScript implements Script {
    private static int afkAttempts = 0;
    private static int attackRetries = 0;
    private static int teleportRetries = 0;
    private static int winchRetries = 0;
    private static final int WINCH_ID = 123104;
    private static final int GATE_ID_FRONT = 121772;
    private static final int GATE_ID_BACK = 121773;

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {

        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        int curTask = GameState.getSlayerTaskID();
        if (curTask <= 0) {
            getCerberusTask();
            return 500;
        }

        GameObject winchObject = GameObject.findClosestGameObjectById(WINCH_ID);
        GameObject frontGateObect = GameObject.findFirstGameObjectById(GATE_ID_FRONT);
        GameObject backGateObject = GameObject.findFirstGameObjectById(GATE_ID_BACK);

        if (frontGateObect != null || backGateObject != null){
            //in cerberus lair
            winchRetries = 0;
            int playerTileX = LocalPlayer.getTileX();
            int playerTileY = LocalPlayer.getTileY();
            if (playerTileX != 1240 || playerTileY != 1245){
                System.out.println("Trying to walk to lair position");
                GameAction.walkTo(1240, 1245);
                sleepSafe(5000);
            }
            if (attackRetries > 3){
                System.out.println("Could not find cerberus after 3 attempts!");
                return -1;
            }
            int npcNearPlayer = tryFindNPC("Cerberus", 8, 1000);
            if (npcNearPlayer > 0){
                int localPlayerTarget = LocalPlayer.getTargetNPC();
                attackRetries = 0;
                if (npcNearPlayer != localPlayerTarget && NPC.isNpcAttackable(npcNearPlayer)){
                    System.out.println("Attacking Cerberus");
                    GameAction.attackNPC(npcNearPlayer);
                }
                return 500;
            }
            System.out.println("Could not find Cerberus!");
            attackRetries++;
            return 500;
        }
        else if (winchObject != null){
            //in cerberus cave
            teleportRetries = 0;
            if (winchRetries < 3){
                System.out.println("Trying to interact with winch and enter lair!");
                enterCerberusLair(winchObject);
                winchRetries++;
                return 500;
            }
            else {
                System.out.println("Could not enter Cerberus Lair after 3 attempts!");
                return -1;
            }
        }
        else{
            //in neither place, need to TP
            if (teleportRetries < 3){
                teleportToCerberus();
                teleportRetries++;
                return 500;
            }
            else{
                System.out.println("Could not teleport to Cerberus Lair after 3 attempts!");
                return -1;
            }
        }
    }

    @Override
    public void onExit() {

    }

    @Override
    public void sleepSafe(long millis) {
        Script.super.sleepSafe(millis);
    }

    private Integer handleAfkChecks(){
        if (afkAttempts > 5){
            return -1;
        }
        AfkPatrolStatus afkPatrolStatus = checkForAfkPatrol();
        if (afkPatrolStatus == AfkPatrolStatus.ERROR){
            System.out.println("Got AfkPatrolStatus.ERROR");
            return -1;
        }
        else if (afkPatrolStatus == AfkPatrolStatus.OPEN){
            sleepSafe(new Random().nextInt(500, 1000));
            solveAfkPatrol();
            afkAttempts += 1;
            return (3000);
        }
        afkAttempts = 0;
        return null;
    }

    private int tryFindNPC(String wantedName, int attempts, int waitMs) {
        String targetNameLower = wantedName.toLowerCase();
        for (int i = 0; i < attempts; i++) {
            int found = getFirstNpcNearPlayer(targetNameLower);
            if (found != -1) return found;
            sleepSafe(waitMs);
        }
        return -1;
    }

    public int getFirstNpcNearPlayer(String wantedName){
        List<Object> npcList = NPC.getNPCList();
        for (Object npc : npcList){
            int npcId = NPC.getNPCId(npc);
            String npcName = NPC.getNPCName(npc);
            if (npcName != null && npcName.toLowerCase().contains(wantedName) && NPC.getNPCHp(npc, 0) > 0){
                return npcId;
            }
        }
        return -1;
    }

    private void teleportToCerberus(){
        System.out.println("Teleporting to Cerberus");
        GameAction.clickInterface(InterfaceID.CHARACTER_SUMMARY_BUTTON.id, -1, 1);
        sleepSafe(500);
        GameAction.clickInterface(InterfaceID.CHARACTER_SUMMARY_PANEL.id, 2, 1);
        sleepSafe(500);
        GameAction.clickInterface(InterfaceID.SLAYER_TELEPORT_BUTTON.id, -1, 1);
        sleepSafe(500);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 1);
        sleepSafe(3000);
    }

    private void enterCerberusLair(GameObject winchObject){
        GameAction.interactWithObject(winchObject.objectId, winchObject.xPos, winchObject.yPos);
        sleepSafe(5000);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 2);
        sleepSafe(2000);
    }

    private void getCerberusTask(){
        System.out.println("Getting new Task!");
        GameAction.clickInterface(InterfaceID.CHARACTER_SUMMARY_BUTTON.id, -1, 1);
        sleepSafe(1250);
        GameAction.clickInterface(InterfaceID.CHARACTER_SUMMARY_PANEL.id, 2, 1);
        sleepSafe(1250);
        GameAction.clickInterface(InterfaceID.SLAYER_ASSIGN_TASK_BUTTON.id, -1, 1);
        sleepSafe(1250);
        GameAction.clickDialogue(InterfaceID.SLAYER_ASSIGNTASKDIFFICULTY_INTERMEDIATE_DIALOGUE.id, -1);
        sleepSafe(1250);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 4);
        sleepSafe(1250);
        GameAction.clickDialogue(InterfaceID.SLAYER_ASSIGNTASKDIFFICULTY_INTERMEDIATE_DIALOGUE.id, -1);
        sleepSafe(1250);
        GameAction.clickDialogue(12255235, 0);
        sleepSafe(1250);
        GameAction.sendPCountDialogue(25);
        sleepSafe(1250);
    }
}
