import com.john.RedemptionBotSDK.GameAction;
import com.john.RedemptionBotSDK.GameObject;
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

public class DreamlandScript implements Script {
    public static int afkAttempts = 0;
    public static final int portalId = 12355;
    public static int objectRetries = 0;
    public static final int snowId = 180032;
    public static final int bowId = 406207;
    public static final int staffId = 27228;
    public static boolean raidenwave = false;



    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        Integer bowIdx = LocalPlayer.getItemIndexFromInventory(bowId);
        if (bowIdx != null && !raidenwave) GameAction.equipInventoryObject(bowIdx, InterfaceID.PLAYER_INVENTORY.id);

        GameObject portalObject = GameObject.findClosestGameObjectById(portalId);
        GameObject snowObject = GameObject.findClosestGameObjectById(snowId);

        if (portalObject != null){
            objectRetries = 0;
            GameAction.interactWithObject(portalObject.objectId, portalObject.xPos, portalObject.yPos);
            return 2000;
        }
        else if (snowObject != null){
            objectRetries = 0;
            List<Object> npcList = NPC.getNPCList();
            List<Object> filteredNpcList = filterNpcList(npcList);
            if (!filteredNpcList.isEmpty()){
                Random rand = new Random();
                int randomInt = rand.nextInt(filteredNpcList.size());
                int givenNPCId = NPC.getNPCId(filteredNpcList.get(randomInt));
                if (NPC.isNpcAttackable(givenNPCId) && NPC.getNPCHp(filteredNpcList.get(randomInt), 0) > 0){
                    if (NPC.getNPCName(filteredNpcList.get(randomInt)).toLowerCase().contains("raiden")){
                        raidenwave = true;
                        Integer staffIdx = LocalPlayer.getItemIndexFromInventory(staffId);
                        if (staffIdx != null) GameAction.equipInventoryObject(staffIdx, InterfaceID.PLAYER_INVENTORY.id);
                        sleepSafe(750);
                        GameAction.attackNPC(NPC.getNPCId(filteredNpcList.get(randomInt)));
                        return 1000;
                    }
                    raidenwave = false;
                    GameAction.attackNPC(NPC.getNPCId(filteredNpcList.get(randomInt)));
                    return 1000;
                }
            }
            return 250;
        }
        else{
            objectRetries++;
            return 1000;
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

    public static List<Object> filterNpcList(List<Object> npcList) {
        List<Object> filtered = new ArrayList<>();

        for (Object obj : npcList) {
            if (NPC.getNPCHp(obj, 0) > 0) {
                filtered.add(obj);
            }
        }
        return filtered;
    }

}
