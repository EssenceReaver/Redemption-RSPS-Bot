import com.john.RedemptionBotSDK.GameAction;
import com.john.RedemptionBotSDK.LocalPlayer;
import com.john.RedemptionBotSDK.NPC;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.script.Script;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class CrimsonChinScript implements Script {

    private static final int CRIMSON_CHIN_TELE_ID = 28012;
    private Integer crimsonChinTeleQuantity;
    private Integer retries = 0;
    private Integer afkAttempts = 0;

    @Override
    public void onLoad() {
    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        Integer currentQuantity =  getCrimsonChinTeleQuantity(2, 1000);
        if (currentQuantity == null){
            System.out.println("Could not find CrimsonChin Teleports in inventory");
            return -1;
        }

        if (crimsonChinTeleQuantity == null) {
            crimsonChinTeleQuantity = currentQuantity;
        }
        int currentKills = crimsonChinTeleQuantity - currentQuantity;
        System.out.println("Crimson Chin Count: " + currentKills);

        int npcNearPlayer = tryFindNPC("Crimson Chin", 12, 1000);
        if (npcNearPlayer > 0){
            int localPlayerTarget = LocalPlayer.getTargetNPC();
            if (npcNearPlayer != localPlayerTarget){
                GameAction.attackNPC(npcNearPlayer);
            }
            retries = 0;
            return 1000;
        }
        retries++;
        if (retries > 5) {
            System.out.println("Tried many times to find NPC. ERROR!!!!!!!");
            return -1;
        }
        return teleportToBoss();
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

    private int teleportToBoss(){
        GameAction.sendPlayerCommand("home");
        sleepSafe(2500);
        Integer teleIdx = LocalPlayer.getItemIndexFromInventory(CRIMSON_CHIN_TELE_ID);
        if (teleIdx == null){
            System.out.println("Could not find CrimsonChin Teleports in inventory");
            return -1;
        }
        GameAction.useInventoryObject(teleIdx, InterfaceID.PLAYER_INVENTORY.id);
        return 3000;
    }

    private Integer getCrimsonChinTeleQuantity(int attempts, int waitMs){
        for (int i = 0; i < attempts; i++) {
            Integer teleIndex = LocalPlayer.getItemIndexFromInventory(CRIMSON_CHIN_TELE_ID);
            if (teleIndex != null) {
                int[] inventoryQuantities = LocalPlayer.getInventoryStackSizes();
                return inventoryQuantities[teleIndex];
            }
            sleepSafe(waitMs);
        }
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


}
