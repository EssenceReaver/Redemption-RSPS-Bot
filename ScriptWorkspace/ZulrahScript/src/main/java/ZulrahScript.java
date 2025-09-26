import com.john.RedemptionBotSDK.GameAction;
import com.john.RedemptionBotSDK.GameObject;
import com.john.RedemptionBotSDK.LocalPlayer;
import com.john.RedemptionBotSDK.NPC;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class ZulrahScript implements Script {
    private Integer retries = 0;
    private Integer teleportRetries = 0;
    private Integer afkAttempts = 0;
    private static final int ZULRAH_IDENTIFIER = 111699;
    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        GameObject zulrahIdentifier = GameObject.findClosestGameObjectById(ZULRAH_IDENTIFIER);

        if (zulrahIdentifier == null){
            if (teleportRetries > 3){
                return -1;
            }
            teleportRetries++;
            teleportToZulrah();
            return 3000;
        }

        int npcNearPlayer = tryFindNPC("Zulrah", 45, 250);
        if (npcNearPlayer > 0){
            GameAction.attackNPC(npcNearPlayer);
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

    private void teleportToZulrah(){
        GameAction.sendPlayerCommand("tp");
        sleepSafe(1000);
        GameAction.clickInterface(397541385, 54, 2);
        sleepSafe(400);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 2);
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
