import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class OlmScript implements Script {

    public static int afkAttempts = 0;
    public static Long lastUse = null;

    private static final int staffId = 407003;
    private static final int meleeId = 400148;

    private static final int OLM_PORTAL_ID = 129879;
    private static final int OLM_SLIME = 130033;
    private static final int OLM_HEAD_NPCID = 37554;
    private static boolean lastMoveRight = false;

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

        GameObject olmPortal = GameObject.findClosestGameObjectById(OLM_PORTAL_ID);

        if (olmPortal == null){
            System.out.println("Couldn't find olm portal!");
            return -1;
        }

        NPC olmRightHand = NPC.getClosestNpcIgnoreHp("Right Claw");
        NPC olmLeftHand = NPC.getClosestNpcIgnoreHp("Left Claw");
        NPC olmHead = NPC.getNpcObjByRealId(37554);


        if (olmRightHand != null && NPC.isNpcAttackable(olmRightHand.npcId) && NPC.getNPCHp(olmRightHand.npcObject, 0) > 0){
            Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
            if (staffIdx != null) GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
            GameAction.attackNPC(olmRightHand.npcId);
        }
        else if (olmLeftHand != null && NPC.isNpcAttackable(olmLeftHand.npcId) && NPC.getNPCHp(olmLeftHand.npcObject, 0) > 0){
            Integer meleeIdx = LocalPlayer.getItemIndexFromInventory(meleeId);
            if (meleeIdx != null) GameAction.equipInventoryObject(meleeIdx, InterfaceID.PLAYER_INVENTORY.id);
            GameAction.attackNPC(olmLeftHand.npcId);
        }
        else if (olmHead != null && NPC.isNpcAttackable(olmHead.npcId) && NPC.getNPCHp(olmHead.npcObject, 0) > 0){
            Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
            if (staffIdx != null) GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
            GameAction.attackNPC(olmHead.npcId);
        }
        else{
            GameObject slimeObj = GameObject.findClosestGameObjectById(OLM_SLIME);
            int localPlayerX = LocalPlayer.getTileX();
            int localPlayerY = LocalPlayer.getTileY();
            if (slimeObj != null && (localPlayerX == slimeObj.xPos || localPlayerY == slimeObj.yPos)){
                if (lastMoveRight){
                    GameAction.walkTo(localPlayerX - 1, localPlayerY);
                    lastMoveRight = false;
                }
                else{
                    GameAction.walkTo(localPlayerX + 1, localPlayerY);
                    lastMoveRight = true;
                }
            }
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
