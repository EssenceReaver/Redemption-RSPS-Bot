import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class AbyssalSireScript implements Script {
    private static int afkAttempts = 0;
    private static final int ROCKS_ID = 134544;
    private static int sireNpc = -1;
    private static int spawnNPC = -1;
    private static int rockRetries = 0;
    private static int climbRetries = 0;
    private static Long lastUse = null;

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;
        useBandageOnInterval(150, 20);

        GameObject rockObj = GameObject.findClosestGameObjectById(ROCKS_ID);

        if (rockObj == null){
            if (rockRetries > 2){
                teleportToSire();
                rockRetries++;
                return 2000;
            } else if (rockRetries > 3) {
                return -1;
            }
            rockRetries++;
            System.out.println("Couldn't find rocks obj?");
            return 1000;
        }
        rockRetries = 0;
        for (int i=0; i<3; i++){
            sireNpc = tryFindNPC("Abyssal Sire");
            spawnNPC = tryFindNPC("Spawn");
            if (sireNpc != -1 || spawnNPC != -1){
                break;
            }
            sleepSafe(1000);
        }

        if (sireNpc == -1 && spawnNPC == -1){
            if (climbRetries < 3){
                System.out.println("Couldn't find Sire or Spawn. Climbing Rocks!");
                GameAction.interactWithObject(rockObj.objectId, rockObj.xPos, rockObj.yPos);
                climbRetries++;
                sleepSafe(3000);
                rockObj = GameObject.findClosestGameObjectById(ROCKS_ID);
                GameAction.walkTo(rockObj.xPos + 8, rockObj.yPos + 5);
                return 2000;
            }
            System.out.println("Failed climbing 3 times?");
            return -1;
        }else if (spawnNPC != -1){
            climbRetries = 0;
            int localPlayerTarget = LocalPlayer.getTargetNPC();
            if (localPlayerTarget != spawnNPC){
                System.out.println("Attacking Spawn!");
                GameAction.attackNPC(spawnNPC);
                return 1000;
            }
        }else{
            climbRetries = 0;
            int localPlayerTarget = LocalPlayer.getTargetNPC();
            if (localPlayerTarget != sireNpc){
                System.out.println("Attacking Sire!");
                GameAction.attackNPC(sireNpc);
                return 1000;
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

    private int tryFindNPC(String wantedName) {
        String targetNameLower = wantedName.toLowerCase();
        int found = NPC.getFirstNpcIdNearPlayer(targetNameLower);
        if (found != -1) return found;
        return -1;
    }

    private void teleportToSire(){
        GameAction.sendPlayerCommand("tp");
        sleepSafe(1000);
        GameAction.clickInterface(397541385, 1, 2);
        sleepSafe(400);
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
