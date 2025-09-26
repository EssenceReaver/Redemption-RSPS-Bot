import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class DemonicGorillaScript implements Script {

    public static int afkAttempts = 0;
    public static int CAVE_OBJ_ID = 128717;
    public static Long lastUse = null;
    private static final int slayerPotionId = 401694;
    private static NPC currentTarget = null;


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
            //getNewTask();
            getGorillaTask();
            return 500;
        }


        useBandageOnInterval(150, 20);

        boolean inCave = tryTeleport(CAVE_OBJ_ID, 3, 3000);
        if (!inCave){
            System.out.println("Could not teleport to Gorillas!");
            return -1;
        }

        if (currentTarget == null || NPC.getNpcById(currentTarget.npcId) == null || NPC.getNPCHp(NPC.getNpcById(currentTarget.npcId), 0) == 0){
            currentTarget = NPC.getClosestNpc("Demonic Gorilla");
        }
        else{
            currentTarget = NPC.getNpcObjById(currentTarget.npcId);
            if (currentTarget == null){
                return 50;
            }
        }

        if (currentTarget != null){
            walkOnTopNpc(currentTarget);

            GameAction.attackNPC(currentTarget.npcId);
            sleepSafe(1750);
        }

        return 1000;
    }

    @Override
    public void onExit() {

    }

    @Override
    public void sleepSafe(long millis) {
        Script.super.sleepSafe(millis);
    }

    private void walkOnTopNpc(NPC npc){
        GameAction.walkTo(npc.npcXPos, npc.npcYPos);
        sleepSafe(500);
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

    private boolean tryTeleport(int objId, int attempts, int waitMs){
        for (int i = 0; i<attempts; i++){
            GameObject sceneObj = GameObject.findClosestGameObjectById(objId);
            if (sceneObj == null){
                currentTarget = null;
                teleportToGorillas();
                sleepSafe(waitMs);
            }
            else{
                return true;
            }
        }
        return false;
    }

    private void teleportToGorillas(){
        System.out.println("Teleporting to Gorillas");
        GameAction.sendPlayerCommand("slayer");
        sleepSafe(200);
        GameAction.clickInterface(InterfaceID.SLAYER_TELEPORT_BUTTON.id, -1, 1);
        sleepSafe(200);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 1);
        sleepSafe(200);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 2);
        sleepSafe(200);
        GameAction.clickInterface(InterfaceID.BACKPACK_BUTTON.id, -1, 1);
    }

    private void getGorillaTask(){
        System.out.println("Getting new Task!");
        GameAction.sendPlayerCommand("slayer");
        sleepSafe(200);
        GameAction.clickInterface(InterfaceID.SLAYER_ASSIGN_TASK_BUTTON.id, -1, 1);
        sleepSafe(750);
        GameAction.clickDialogue(InterfaceID.SLAYER_ASSIGNTASKDIFFICULTY_INTERMEDIATE_DIALOGUE.id, -1);
        sleepSafe(750);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 4);
        sleepSafe(750);
        GameAction.clickDialogue(InterfaceID.SLAYER_ASSIGNTASKDIFFICULTY_INTERMEDIATE_DIALOGUE.id, -1);
        sleepSafe(750);
        GameAction.clickDialogue(12255235, 3);
        sleepSafe(1250);
        GameAction.sendPCountDialogue(25);
        sleepSafe(500);
    }

    public void useBandageOnInterval(int maxHp, int intervalSeconds){
        int bandageIdR = 404084;
        int bandageIdI = 401662;
        long currentTime = System.currentTimeMillis();
        Integer playerHp = LocalPlayer.getHp();
        Integer playerPrayer = LocalPlayer.getCurrentSkillLevel(Skill.PRAYER);
        Integer playerMaxPrayer = LocalPlayer.getMaxSkillLevel(Skill.PRAYER);
        int prayerDelta = playerMaxPrayer - playerPrayer;
        if (lastUse == null || (currentTime - lastUse) / 1000 > intervalSeconds){
            if (maxHp == -1 || maxHp - playerHp > 60 || prayerDelta > 30){
                lastUse = currentTime;
                Integer bandageIdxR = LocalPlayer.getItemIndexFromInventory(bandageIdR);
                Integer bandageIdxI = LocalPlayer.getItemIndexFromInventory(bandageIdI);
                if (bandageIdxR != null) GameAction.useInventoryObject(bandageIdxR, InterfaceID.PLAYER_INVENTORY.id);
                else if (bandageIdxI != null) GameAction.useInventoryObject(bandageIdxI, InterfaceID.PLAYER_INVENTORY.id);
                sleepSafe(500);
            }
        }
    }
}
