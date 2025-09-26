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

public class LimestreamScript implements Script {

    private static final int phase1boss = 16348;
    private static final int phase2boss = 16349;
    private static final int phase3boss = 16350;
    public static int afkAttempts = 0;
    public static Long lastUse = null;
    public static int limeRetries = 0;

    private static final int bowId = 25023;
    private static final int hammerId = 400148;
    private static final int staffId = 407003;


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
        if (limeRetries > 80){
            return -1;
        }

        NPC limeNpc1 = NPC.getNpcObjByRealId(phase1boss);
        NPC limeNpc2 = NPC.getNpcObjByRealId(phase2boss);
        NPC limeNpc3 = NPC.getNpcObjByRealId(phase3boss);
        if ((limeNpc1 == null) && (limeNpc2 == null) && (limeNpc3 == null)){
            limeRetries++;
            return 250;
        }
        limeRetries = 0;
        if (limeNpc1 != null){
            Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
            if (staffIdx != null){
                GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
            }
            sleepSafe(500);
            GameAction.attackNPC(limeNpc1.npcId);
        }
        else if (limeNpc2 != null){
            Integer bowIdx = LocalPlayer.getItemIndexFromInventory(bowId);
            if (bowIdx != null){
                GameAction.equipInventoryObject(bowIdx, InterfaceID.PLAYER_INVENTORY.id);
            }
            sleepSafe(500);
            GameAction.attackNPC(limeNpc2.npcId);
        }
        else if (limeNpc3 != null){
            Integer hammerIdx = LocalPlayer.getItemIndexFromInventory(hammerId);
            if (hammerIdx != null){
                GameAction.equipInventoryObject(hammerIdx, InterfaceID.PLAYER_INVENTORY.id);
            }
            sleepSafe(500);
            GameAction.attackNPC(limeNpc3.npcId);
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

}
