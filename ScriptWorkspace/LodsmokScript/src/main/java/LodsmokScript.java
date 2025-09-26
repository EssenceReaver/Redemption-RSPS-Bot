import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class LodsmokScript implements Script {
    private static int afkAttempts = 0;
    private static final int BARRIER_OBJ_ID = 126494;
    private static int retries = 0;
    private static final int hammerId = 400148;
    private static final int staffId = 27228;
    private static Long lastUse = null;
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

        if ((System.currentTimeMillis() - scriptStartTime) / 1000 > 22500) {
            return -1;
        }

        GameObject barrierObj = GameObject.findClosestGameObjectById(BARRIER_OBJ_ID);

        if (barrierObj == null){
            System.out.println("Couldn't find barrier obj?");
            return -1;
        }

        useBandageOnInterval(130, 23);

        int lodsmokNpcId = tryFindNPC("Lodsmok", 12, 1000);
        if (lodsmokNpcId > 0){
            retries = 0;
            int localPlayerTarget = LocalPlayer.getTargetNPC();
            Object lodsmokNpc = NPC.getNpcById(lodsmokNpcId);
            long lodsmokHp = NPC.getNPCHp(lodsmokNpc, 0);
            long lodsmokMaxHp = NPC.getNPCHp(lodsmokNpc, 1);
            if (lodsmokHp > 7500000){
                Integer hammerIdx = LocalPlayer.getItemIndexFromInventory(hammerId);
                if (hammerIdx != null) GameAction.equipInventoryObject(hammerIdx, InterfaceID.PLAYER_INVENTORY.id);
                sleepSafe(1000);
                if ((localPlayerTarget != lodsmokNpcId && NPC.isNpcAttackable(lodsmokNpcId))){
                    System.out.println("Attacking Lodsmok with hammer!");
                    GameAction.attackNPC(lodsmokNpcId);
                    return 1000;
                }
            }
            else{
                Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
                if (staffIdx != null) GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
                sleepSafe(1000);
                if ((localPlayerTarget != lodsmokNpcId && NPC.isNpcAttackable(lodsmokNpcId))){
                    System.out.println("Attacking Lodsmok With staff!");
                    GameAction.attackNPC(lodsmokNpcId);
                    return 1000;
                }
            }
            return 1000;
        }
        retries++;
        if (retries > 3){
            System.out.println("Tried many times to find NPC. ERROR!!!!!!!");
            return -1;
        }
        return 3000;
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
