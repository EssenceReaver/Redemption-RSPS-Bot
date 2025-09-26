import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.script.Script;

import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class QBDScript implements Script {

    public static int afkAttempts = 0;
    public static final int ARTIFACT_1_UNACTIVE_ID = 470776;
    public static final int ARTIFACT_1_ID = 470777;
    public static final int ARTIFACT_2_ID = 470780;
    public static final int ARTIFACT_3_ID = 470783;
    public static final int ARTIFACT_4_ID = 470786;
    public static final int STAIRCASE_ID = 470790;
    private static int stairRetries = 0;
    private static int artifactRetries = 0;
    private static int prevX;
    private static int prevY;
    private static int walkRetries = 0;
    private static boolean setPrev = false;
    private static Long lastWalkClick = null;
    private static Long lastArtifactClick = null;
    private static Long lastAttackClick = null;

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        boolean inLair = checkInLair();
        if (!inLair) return -1;

        if (!setPrev){
            GameObject art1InactiveObj = GameObject.findClosestGameObjectById(ARTIFACT_1_UNACTIVE_ID);
            if (art1InactiveObj == null){
                System.out.println("Couldn't find inactive pilar!");
                return -1;
            }
            prevX = art1InactiveObj.xPos;
            prevY = art1InactiveObj.yPos - 2;
            setPrev = true;
        }


        GameObject art1Obj = GameObject.findClosestGameObjectById(ARTIFACT_1_ID);
        GameObject art2Obj = GameObject.findClosestGameObjectById(ARTIFACT_2_ID);
        GameObject art3Obj = GameObject.findClosestGameObjectById(ARTIFACT_3_ID);
        GameObject art4Obj = GameObject.findClosestGameObjectById(ARTIFACT_4_ID);
        GameObject staircaseObj = GameObject.findClosestGameObjectById(STAIRCASE_ID);

        if (artifactRetries > 20 || walkRetries > 20){
            System.out.println("10 Artifact Retries!");
            return -1;
        }

        if (art1Obj != null){
            stairRetries = 0;
            System.out.println("Activating art1");
            activateArtifact(art1Obj);
        }
        else if (art2Obj != null){
            System.out.println("Activating art2");
            activateArtifact(art2Obj);
        }
        else if (art3Obj != null){
            System.out.println("Activating art3");
            activateArtifact(art3Obj);
        }
        else if (art4Obj != null){
            System.out.println("Activating art4");
            activateArtifact(art4Obj);
        }
        else if (staircaseObj != null){
            //Interact with staircase here
            stairRetries++;
            if (stairRetries < 3){
                System.out.println("Staircase OPEN!");
                restartKill();
            }else{
                return -1;
            }
        }
        else{
            artifactRetries = 0;
            if (walkToPrev(prevX, prevY)){
                walkRetries = 0;
                int target = LocalPlayer.getTargetNPC();
                NPC qbdNpc = NPC.getClosestNpc("Queen Black Dragon");
                if (qbdNpc != null && NPC.isNpcAttackable(qbdNpc.npcId) && qbdNpc.npcId != target){
                    if (lastAttackClick == null  || System.currentTimeMillis() - lastAttackClick >= 3000) {
                        GameAction.attackNPC(qbdNpc.npcId);
                        lastAttackClick = System.currentTimeMillis();
                    }
                }
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

    public boolean checkInLair(){
        for (int j = 0; j < 3; j++){
            for (int i = 470776; i <= 470790; i++){
                GameObject sceneObj = GameObject.findClosestGameObjectById(i);
                if (sceneObj != null) return true;
            }
        }
        return false;
    }

    public void activateArtifact(GameObject art){
        artifactRetries++;
        art = GameObject.findClosestGameObjectById(art.objectId);
        if (art == null) return;
        long now = System.currentTimeMillis();
        if (lastArtifactClick == null  || now - lastArtifactClick >= 3000) {
            System.out.println("Attempting to activate Artifact!");
            GameAction.interactWithObject(art.objectId, art.xPos, art.yPos);
            lastArtifactClick = now;
        }
    }

    public boolean walkToPrev(int prevX, int prevY){
        if (LocalPlayer.getTileX() != prevX || LocalPlayer.getTileY() != prevY){
            walkRetries++;
            long now = System.currentTimeMillis();
            if (lastWalkClick == null  || now - lastWalkClick >= 3000) {
                System.out.println("Walking back!");
                GameAction.walkTo(prevX, prevY);
                lastWalkClick = now;
            }
            return false;
        }
        return true;
    }

    public void restartKill(){
        GameObject stairwellObj = GameObject.findClosestGameObjectById(STAIRCASE_ID);
        if (stairwellObj == null) return;
        GameAction.interactWithObject(stairwellObj.objectId, stairwellObj.xPos, stairwellObj.yPos);
        sleepSafe(3000);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 1);
        sleepSafe(2000);
        GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 2);
        sleepSafe(3000);
        setPrev = false;
    }
}
