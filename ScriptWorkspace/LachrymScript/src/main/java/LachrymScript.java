import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class LachrymScript implements Script {
    private int afkAttempts = 0;
    private static Long lastUse = null;
    private static final int BARRIER_OBJ_ID = 136201;
    private static int retries = 0;
    private static final int bowId = 405872;

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        useBandageOnInterval(150, 23);

        GameObject barrierObj = GameObject.findClosestGameObjectById(BARRIER_OBJ_ID);

        if (barrierObj == null){
            System.out.println("Couldn't find barrier obj?");
            return -1;
        }


        int lachrymNpcId = tryFindNPC("Lachrym", 12, 1000);
        int localPlayerTarget = LocalPlayer.getTargetNPC();
        Integer bowIdx = LocalPlayer.getItemIndexFromInventory(bowId);
        if (bowIdx != null) GameAction.equipInventoryObject(bowIdx, InterfaceID.PLAYER_INVENTORY.id);
        if (lachrymNpcId > 0){
            retries = 0;
            if ((localPlayerTarget != lachrymNpcId && NPC.isNpcAttackable(lachrymNpcId))){
                System.out.println("Attacking Lachrym!");
                GameAction.attackNPC(lachrymNpcId);
                return 1000;
            }
        }
        retries++;
        if (retries > 3){
            System.out.println("Tried many times to find NPC. ERROR!!!!!!!");
            return -1;
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
            int found = NPC.getFirstNpcIdNearPlayer(targetNameLower);
            if (found != -1) return found;
            sleepSafe(waitMs);
        }
        return -1;
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
