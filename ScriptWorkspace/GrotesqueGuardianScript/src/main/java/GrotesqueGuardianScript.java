import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.script.Script;

import java.util.List;
import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class GrotesqueGuardianScript implements Script {

    private static int afkAttempts = 0;
    private static final int CLOISTER_ID = 131669;
    private static int teleportAttempts = 0;
    private static int duskNPC = -1;
    private static int dawnNPC = -1;
    private static final int hammerId = 400148;
    private static final int staffId = 27228;
    private static Long scriptStartTime;
    @Override
    public void onLoad() {
        scriptStartTime = System.currentTimeMillis();
    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        if ((System.currentTimeMillis() - scriptStartTime) / 1000 > 18000) {
            return -1;
        }


        int curTask = GameState.getSlayerTaskID();
        if (curTask <= 0) {
            //getNewTask();
            getGuardianTask();
            return 500;
        }

        GameObject cloisterBell = GameObject.findFirstGameObjectById(CLOISTER_ID);
        if (cloisterBell == null){
            System.out.println("Couldn't find cloister. Teleporting!");
            if (teleportAttempts > 3) return -1;
            tryTeleportToGuardians();
            teleportAttempts++;
            return 2000;
        }
        teleportAttempts = 0;

        for (int i=0; i<6; i++){
            duskNPC = tryFindNPC("Dusk");
            dawnNPC = tryFindNPC("Dawn");
            if (duskNPC != -1 || dawnNPC != -1){
                break;
            }
            sleepSafe(1000);
        }

        if (dawnNPC != -1){
            int localPlayerTarget = LocalPlayer.getTargetNPC();
            Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
            if (staffIdx != null) GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
            sleepSafe(750);
            if ((localPlayerTarget != dawnNPC && NPC.isNpcAttackable(dawnNPC))){
                System.out.println("Attacking dawn!");
                GameAction.attackNPC(dawnNPC);
                return 1000;
            }
        }
        else if (duskNPC != -1){
            int localPlayerTarget = LocalPlayer.getTargetNPC();
            Integer hammerIdx = LocalPlayer.getItemIndexFromInventory(hammerId);
            if (hammerIdx != null) GameAction.equipInventoryObject(hammerIdx, InterfaceID.PLAYER_INVENTORY.id);
            sleepSafe(750);
            if (localPlayerTarget != duskNPC && NPC.isNpcAttackable(duskNPC)){
                System.out.println("Attacking dusk!");
                GameAction.attackNPC(duskNPC);
                return 1000;
            }
        }
        else{
            //Couldn't find either after 5 seconds
            //Use cloister bell here
            System.out.println("Couldn't find dawn or dusk. Using Cloister!");
            GameAction.interactWithObject(cloisterBell.objectId, cloisterBell.xPos, cloisterBell.yPos);
            return 1000;
        }
        return 500;
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

    private int tryFindNPC(String wantedName) {
        String targetNameLower = wantedName.toLowerCase();
        int found = NPC.getFirstNpcIdNearPlayer(targetNameLower);
        if (found != -1) return found;
        return -1;
    }
    private void tryTeleportToGuardians(){
        System.out.println("Teleporting to Guardians");
        GameAction.clickInterface(InterfaceID.CHARACTER_SUMMARY_BUTTON.id, -1, 1);
        sleepSafe(200);
        GameAction.clickInterface(InterfaceID.CHARACTER_SUMMARY_PANEL.id, 2, 1);
        sleepSafe(200);
        GameAction.clickInterface(InterfaceID.SLAYER_TELEPORT_BUTTON.id, -1, 1);
        sleepSafe(200);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 1);
        sleepSafe(200);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 2);
        sleepSafe(200);
        GameAction.clickInterface(InterfaceID.BACKPACK_BUTTON.id, -1, 1);
    }

    private void getGuardianTask(){
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
        GameAction.clickInterface(InterfaceID.BACKPACK_BUTTON.id, -1, 1);
        sleepSafe(200);
        GameAction.clickDialogue(12255235, 1);
        sleepSafe(2000);
        GameAction.sendPCountDialogue(25);
        sleepSafe(1500);
    }



}
