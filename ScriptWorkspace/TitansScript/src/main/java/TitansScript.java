import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;
import com.john.RedemptionBotSDK.util.Region;

import java.awt.*;
import java.util.Locale;
import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class TitansScript implements Script {

    public static int afkAttempts = 0;
    public static Long lastUse = null;


    private static Region inRaid = null;
    private static Point raidCenter = null;
    private static boolean walkingToChest = false;

    private static final int bowId = 25023;
    private static final int hammerId = 400148;
    private static final int staffId = 407003;
    private static final int TITAN_CHEST_OBJ = 180403;
    //from melee
    private static final int TITAN_KEY_1 = 400366;
    //from archer
    private static final int TITAN_KEY_2 = 400367;
    //from mage
    private static final int TITAN_KEY_3 = 400368;
    private static final int TITAN_KEY = 400369;

    private static Long lastWalkClick = null;

    private static Point lastWalkPoint = null;

    private static Long scriptStartTime;

    @Override
    public void onLoad() {
        scriptStartTime = System.currentTimeMillis();
    }

    @Override
    public int onLoop() {

        if (!LocalPlayer.isLoggedIn()) return 500;

        if ((System.currentTimeMillis() - scriptStartTime) / 1000 > 17040) {
            return -1;
        }

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;
        useBandageOnInterval(150, 20);
        useCorruptedHeartOnInterval();

        GameObject titanChest = GameObject.findClosestGameObjectById(TITAN_CHEST_OBJ);

        int localPlayerTileX = LocalPlayer.getTileX();
        int localPlayerTileY = LocalPlayer.getTileY();

        Integer key1Index = LocalPlayer.getItemIndexFromInventory(TITAN_KEY_1);
        Integer key2Index = LocalPlayer.getItemIndexFromInventory(TITAN_KEY_2);
        Integer key3Index = LocalPlayer.getItemIndexFromInventory(TITAN_KEY_3);


        if (inRaid != null) {
            if (!inRaid.contains(localPlayerTileX, localPlayerTileY)){
                return resetRaid();
            }
            if (walkingToChest){
                return walkToChest();
            }
            else if (key2Index == null){
                return eliminateArcher();
            }
            else if (key1Index == null){
                return eliminateMelee();
            }
            else if (key3Index == null){
                return eliminateMage();
            }
            else{
                return useChestAndRestartRaid(key1Index, titanChest);
            }
        } else if (titanChest != null) {
            return setRegion(titanChest);
        } else {
            return teleportToRaid();
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

    private int walkToChest(){
        int tileX = LocalPlayer.getTileX();
        int tileY = LocalPlayer.getTileY();

        if (tileX != (raidCenter.x) || tileY != (raidCenter.y)){
            if (lastWalkClick == null  || System.currentTimeMillis() - lastWalkClick >= 2500) {
                GameAction.walkTo(raidCenter.x, raidCenter.y);
                lastWalkClick = System.currentTimeMillis();
            }
        }
        else{
            walkingToChest = false;
        }
        return 500;
    }

    private int teleportToRaid(){
        GameAction.sendPlayerCommand("titans");
        sleepSafe(1250);
        GameAction.sendPCountDialogue(0);
        return 1000;
    }

    private int setRegion(GameObject titanChest){
        Region raidRegion = new Region(titanChest.xPos - 25, titanChest.yPos - 25, titanChest.xPos + 25, titanChest.yPos + 25);
        Point raidPoint = new Point(titanChest.xPos, titanChest.yPos + 1);
        inRaid = raidRegion;
        raidCenter = raidPoint;
        return 500;
    }

    private int resetRaid(){
        inRaid = null;
        raidCenter = null;
        walkingToChest = false;
        return 500;
    }
    //15 down from chest for archer
    // 16 x 17 for top 2
    private int eliminateArcher(){
        int tileX = LocalPlayer.getTileX();
        int tileY = LocalPlayer.getTileY();
        Integer hammerIdx = LocalPlayer.getItemIndexFromInventory(hammerId);
        if (hammerIdx != null){
            GameAction.equipInventoryObject(hammerIdx, InterfaceID.PLAYER_INVENTORY.id);
        }

        if (tileX != (raidCenter.x) || tileY != (raidCenter.y - 15)){
            boolean sameLast = lastWalkPoint == null || !(lastWalkPoint.x == raidCenter.x && lastWalkPoint.y == raidCenter.y - 15);
            if (lastWalkClick == null  || System.currentTimeMillis() - lastWalkClick >= 2500 || sameLast) {
                GameAction.walkTo(raidCenter.x, raidCenter.y - 15);
                lastWalkPoint = new Point(raidCenter.x, raidCenter.y - 15);
                lastWalkClick = System.currentTimeMillis();
            }
        }
        else{
            NPC archerNPC = NPC.getClosestNpcIgnoreHp("Skyfire Titan");
            if (archerNPC == null || NPC.getNPCHp(archerNPC.npcObject, 0) == 0){
                walkingToChest = true;
            }
            else{
                GameAction.attackNPC(archerNPC.npcId);
            }
        }
        return 500;
    }

    private int eliminateMage(){
        int tileX = LocalPlayer.getTileX();
        int tileY = LocalPlayer.getTileY();
        Integer bowIdx = LocalPlayer.getItemIndexFromInventory(bowId);
        if (bowIdx != null){
            GameAction.equipInventoryObject(bowIdx, InterfaceID.PLAYER_INVENTORY.id);
        }

        if (tileX != (raidCenter.x + 16) || tileY != (raidCenter.y + 17)){
            boolean sameLast = lastWalkPoint == null || !(lastWalkPoint.x == raidCenter.x + 16 && lastWalkPoint.y == raidCenter.y + 17);
            if (lastWalkClick == null  || System.currentTimeMillis() - lastWalkClick >= 2500 || sameLast) {
                GameAction.walkTo(raidCenter.x + 16, raidCenter.y + 17);
                lastWalkPoint = new Point(raidCenter.x + 16, raidCenter.y + 17);
                lastWalkClick = System.currentTimeMillis();
            }
        }
        else{
            NPC mageNPC = NPC.getClosestNpcIgnoreHp("Mystical Titan");
            if (mageNPC == null || NPC.getNPCHp(mageNPC.npcObject, 0) == 0){
                walkingToChest = true;
            }
            else{
                GameAction.attackNPC(mageNPC.npcId);
            }
        }
        return 500;
    }

    private int eliminateMelee(){
        int tileX = LocalPlayer.getTileX();
        int tileY = LocalPlayer.getTileY();
        Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
        if (staffIdx != null){
            GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
        }

        if (tileX != (raidCenter.x - 16) || tileY != (raidCenter.y + 17)){
            boolean sameLast = lastWalkPoint == null || !(lastWalkPoint.x == raidCenter.x - 16 && lastWalkPoint.y == raidCenter.y + 17);
            if (lastWalkClick == null  || System.currentTimeMillis() - lastWalkClick >= 2500 || sameLast) {
                GameAction.walkTo(raidCenter.x - 16, raidCenter.y + 17);
                lastWalkPoint = new Point(raidCenter.x - 16, raidCenter.y + 17);
                lastWalkClick = System.currentTimeMillis();
            }
        }
        else{
            NPC meleeNPC = NPC.getClosestNpcIgnoreHp("Barbaric Titan");
            if (meleeNPC == null || NPC.getNPCHp(meleeNPC.npcObject, 0) == 0){
                walkingToChest = true;
            }
            else{
                GameAction.attackNPC(meleeNPC.npcId);
            }
        }
        return 500;
    }

    public int useChestAndRestartRaid(Integer key1Idx, GameObject titanChest){
        GameAction.useInventoryObject(key1Idx, InterfaceID.PLAYER_INVENTORY.id);
        sleepSafe(1500);
        Integer keyIndex = LocalPlayer.getItemIndexFromInventory(TITAN_KEY);
        if (keyIndex == null){
            System.out.println("Could not find key after trying to create it!");
            return -1;
        }

        GameAction.interactWithObject(titanChest.objectId, titanChest.xPos, titanChest.yPos);
        sleepSafe(1500);
        resetRaid();
        return 500;
    }
}
