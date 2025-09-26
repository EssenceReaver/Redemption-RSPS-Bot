import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class ElderArchmageScript implements Script {
    public static int afkAttempts = 0;
    public static Long lastUse = null;
    public static int archmageRetries = 0;

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;
        useBandageOnInterval(183, 20);
        useCorruptedHeartOnInterval();
        useExtremePotOnInterval();
        if (archmageRetries > 80){
            return -1;
        }

        NPC archmageNpc = NPC.getClosestNpc("Elder Archmage");
        if (archmageNpc == null){
            archmageRetries++;
            return 250;
        }
        archmageRetries = 0;
        GameAction.attackNPC(archmageNpc.npcId);

        return 1000;
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

    public void useExtremePotOnInterval(){
        int extremePot4Id = 400896;
        int extremePot3Id = 400898;
        int extremePot2Id = 400900;
        int extremePot1Id = 400902;
        int ticksLeft = GameState.getTimer(Timers.EXTREME_POWER_POTION.getId());
        if (ticksLeft == 0){
            Integer extremePot1Idx = LocalPlayer.getItemIndexFromInventory(extremePot1Id);
            Integer extremePot2Idx = LocalPlayer.getItemIndexFromInventory(extremePot2Id);
            Integer extremePot3Idx = LocalPlayer.getItemIndexFromInventory(extremePot3Id);
            Integer extremePot4Idx = LocalPlayer.getItemIndexFromInventory(extremePot4Id);

            if (extremePot1Idx != null) GameAction.useInventoryObject(extremePot1Idx, InterfaceID.PLAYER_INVENTORY.id);
            else if (extremePot2Idx != null) GameAction.useInventoryObject(extremePot2Idx, InterfaceID.PLAYER_INVENTORY.id);
            else if (extremePot3Idx != null) GameAction.useInventoryObject(extremePot3Idx, InterfaceID.PLAYER_INVENTORY.id);
            else if (extremePot4Idx != null) GameAction.useInventoryObject(extremePot4Idx, InterfaceID.PLAYER_INVENTORY.id);

            sleepSafe(500);
        }
    }


}
