import com.john.RedemptionBotSDK.GameAction;
import com.john.RedemptionBotSDK.GameState;
import com.john.RedemptionBotSDK.LocalPlayer;
import com.john.RedemptionBotSDK.NPC;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class MalignisScript implements Script {

    public static int afkAttempts = 0;
    public static Long lastUse = null;
    public static int malignisRetries = 0;
    private static NPC currentTarget = null;

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;
        useBandageOnInterval(183, 21);
        useCorruptedHeartOnInterval();

        int curTask = GameState.getSlayerTaskID();
        if (curTask <= 0) {
            getMalignisTask();
            return 500;
        }

        if (malignisRetries > 80){
            return -1;
        }

        if (currentTarget == null || NPC.getNpcById(currentTarget.npcId) == null || NPC.getNPCHp(NPC.getNpcById(currentTarget.npcId), 0) == 0){
            currentTarget = NPC.getClosestNpc("Malignis");
        }

        if (currentTarget == null){
            malignisRetries++;
            return 250;
        }
        malignisRetries = 0;
        GameAction.attackNPC(currentTarget.npcId);
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

    public void useCorruptedHeartOnInterval(){
        int corruptedHeartId = 401956;
        int ticksLeft = GameState.getTimer(Timers.CORRUPTED_IMBUED_HEART.getId());
        if (ticksLeft == 0){
            Integer heartIdx = LocalPlayer.getItemIndexFromInventory(corruptedHeartId);
            if (heartIdx != null) GameAction.useInventoryObject(heartIdx, InterfaceID.PLAYER_INVENTORY.id);
            sleepSafe(500);
        }
    }

    private void getMalignisTask(){
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
        GameAction.clickDialogue(12255235, 6);
        sleepSafe(1250);
        GameAction.sendPCountDialogue(25);
        sleepSafe(1250);
    }

}
