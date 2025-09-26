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

public class CreatorScript implements Script {

    private static final int CREATOR_KEY_ID = 402449;
    private Integer creatorKeyQuantity;
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
        useCorruptedHeartOnInterval();

        Integer currentQuantity = getCreatorKeyQuantity(2, 1000);
        if (currentQuantity == null){
            System.out.println("Could not find Creator Keys in inventory");
            return -1;
        }

        if (creatorKeyQuantity == null) {
            creatorKeyQuantity = currentQuantity;
        }
        int currentKills = creatorKeyQuantity - currentQuantity;

        int npcNearPlayer = tryFindNPC("Creator", 10, 1000);
        if (npcNearPlayer > 0){
            int localPlayerTarget = LocalPlayer.getTargetNPC();
            if (npcNearPlayer != localPlayerTarget && NPC.isNpcAttackable(npcNearPlayer)){
                GameAction.attackNPC(npcNearPlayer);
            }
            retries = 0;
            return 500;
        }
        retries++;
        if (retries > 3){
            System.out.println("Tried many times to find NPC. ERROR!!!!!!!");
            return -1;
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

    private int teleportToBoss(){
        Integer teleIdx = LocalPlayer.getItemIndexFromInventory(CREATOR_KEY_ID);
        if (teleIdx == null){
            System.out.println("Could not find Creator Keys in inventory");
            return -1;
        }
        GameAction.useInventoryObject(teleIdx, InterfaceID.PLAYER_INVENTORY.id);
        return 3000;
    }

    private Integer getCreatorKeyQuantity(int attempts, int waitMs){
        for (int i = 0; i < attempts; i++) {
            Integer teleIndex = LocalPlayer.getItemIndexFromInventory(CREATOR_KEY_ID);
            if (teleIndex != null) {
                int[] inventoryQuantities = LocalPlayer.getInventoryStackSizes();
                return inventoryQuantities[teleIndex];
            }
            sleepSafe(waitMs);
        }
        return null;
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
