import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.awt.*;
import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.attackNPC;
import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class AvianRaidScript implements Script {

    public static int afkAttempts = 0;
    public static Long lastUse = null;
    private static int ROCK_OBJ_ID = 67529;

    private static final int bowId = 25023;
    private static final int staffId = 407003;
    private static final int meleeId = 400148;

    private static Point phase1Point = null;
    private static Point phase3Point = null;
    private static Point phase4Point = null;

    private static final int TALONHEART_ID = 16853;
    private static final int RAVENWING_ID  = 16854;
    private static final int QUILLSHOT_ID = 16855;
    private static final int GEARBEAK_ID1 = 16856;
    private static final int GEARBEAK_ID2 = 16860;
    private static final int GEARBEAK_ID3 = 16861;

    private static final long BIRD_THRESHOLD_1 = 416666666;
    private static final long BIRD_THRESHOLD_2 = 208333333;
    private static final long GEARBEAK_THRESHOLD_1 = 937500000;
    private static final long GEARBEAK_THRESHOLD_2 = 625000000;
    private static final long GEARBEAK_THRESHOLD_3 = 312500000;

    @Override
    public void onLoad() {
        if (!LocalPlayer.isLoggedIn()) return;
        setTilePoints();
    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;
        useBandageOnInterval(183, 20);
        useCorruptedHeartOnInterval();

        if (phase1Point == null || phase3Point == null || phase4Point == null){
            System.out.println("Issue creating phase points!");
            return -1;
        }

        NPC talonHeartNpc = NPC.getNpcObjByRealId(TALONHEART_ID);
        NPC ravenWingNpc = NPC.getNpcObjByRealId(RAVENWING_ID);
        NPC quillShotNpc = NPC.getNpcObjByRealId(QUILLSHOT_ID);
        NPC gearBeakNpc1 = NPC.getNpcObjByRealId(GEARBEAK_ID1);
        NPC gearBeakNpc2 = NPC.getNpcObjByRealId(GEARBEAK_ID2);
        NPC gearBeakNpc3 = NPC.getNpcObjByRealId(GEARBEAK_ID3);

        GameObject rockObject = GameObject.findClosestGameObjectById(ROCK_OBJ_ID);
        int localPlayerX = LocalPlayer.getTileX();
        int localPlayerY = LocalPlayer.getTileY();

        if (talonHeartNpc != null || ravenWingNpc != null){
            if (localPlayerX != phase1Point.x || localPlayerY != phase1Point.y){
                GameAction.walkTo(phase1Point.x, phase1Point.y);
                return 1500;
            }
            if (talonHeartNpc != null){
                Integer meleeIdx = LocalPlayer.getItemIndexFromInventory(meleeId);
                if (meleeIdx != null) GameAction.equipInventoryObject(meleeIdx, InterfaceID.PLAYER_INVENTORY.id);
                if (NPC.getNPCHp(talonHeartNpc.npcObject, 0) > 0){
                    GameAction.attackNPC(talonHeartNpc.npcId);
                }
            }
            else{
                Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
                if (staffIdx != null) GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
                if (NPC.getNPCHp(ravenWingNpc.npcObject, 0) > 0){
                    GameAction.attackNPC(ravenWingNpc.npcId);
                }
            }
        }
        else if (quillShotNpc != null){
            if (localPlayerX != phase3Point.x || localPlayerY != phase3Point.y){
                GameAction.walkTo(phase3Point.x, phase3Point.y);
                return 1500;
            }
            Integer bowIdx = LocalPlayer.getItemIndexFromInventory(bowId);
            if (bowIdx != null) GameAction.equipInventoryObject(bowIdx, InterfaceID.PLAYER_INVENTORY.id);
            if (NPC.getNPCHp(quillShotNpc.npcObject, 0) > 0){
                GameAction.attackNPC(quillShotNpc.npcId);
            }
        }
        else if (gearBeakNpc1 != null){
            long gearBeakNpc1Hp = NPC.getNPCHp(gearBeakNpc1.npcObject, 0);
            if (gearBeakNpc1Hp != GEARBEAK_THRESHOLD_1) {
                Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
                if (staffIdx != null) GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
                if (NPC.getNPCHp(gearBeakNpc1.npcObject, 0) > 0){
                    GameAction.attackNPC(gearBeakNpc1.npcId);
                }
            }
            else{
                if (localPlayerX != phase4Point.x || localPlayerY != phase4Point.y){
                    GameAction.walkTo(phase4Point.x, phase4Point.y);
                    return 1500;
                }
            }
        }
        else if (gearBeakNpc2 != null){
            long gearBeakNpc2Hp = NPC.getNPCHp(gearBeakNpc2.npcObject, 0);
            if (gearBeakNpc2Hp > GEARBEAK_THRESHOLD_2){
                Integer bowIdx = LocalPlayer.getItemIndexFromInventory(bowId);
                if (bowIdx != null) GameAction.equipInventoryObject(bowIdx, InterfaceID.PLAYER_INVENTORY.id);
                if (NPC.getNPCHp(gearBeakNpc2.npcObject, 0) > 0){
                    GameAction.attackNPC(gearBeakNpc2.npcId);
                }
            }
            else{
                if (localPlayerX != phase4Point.x || localPlayerY != phase4Point.y){
                    GameAction.walkTo(phase4Point.x, phase4Point.y);
                    return 1500;
                }
            }
        }
        else if (gearBeakNpc3 != null){
            long gearBeakNpc3Hp = NPC.getNPCHp(gearBeakNpc3.npcObject, 0);
            if (gearBeakNpc3Hp > GEARBEAK_THRESHOLD_3){
                Integer meleeIdx = LocalPlayer.getItemIndexFromInventory(meleeId);
                if (meleeIdx != null) GameAction.equipInventoryObject(meleeIdx, InterfaceID.PLAYER_INVENTORY.id);
                if (NPC.getNPCHp(gearBeakNpc3.npcObject, 0) > 0){
                    GameAction.attackNPC(gearBeakNpc3.npcId);
                }
            }
            else{
                if (localPlayerX != phase4Point.x || localPlayerY != phase4Point.y){
                    GameAction.walkTo(phase4Point.x, phase4Point.y);
                    return 1500;
                }
            }
        }
        else{
            if (rockObject == null){
                System.out.println("Couldn't find rock obj?");
                return -1;
            }
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

    public void setTilePoints(){

        GameObject rockObject = GameObject.findClosestGameObjectById(ROCK_OBJ_ID);
        //set real point / offsets here
        phase1Point = new Point(rockObject.xPos - 1, rockObject.yPos);
        phase3Point = new Point(rockObject.xPos - 5, rockObject.yPos + 1);
        phase4Point = new Point(rockObject.xPos + 4, rockObject.yPos + 5);
    }
}
