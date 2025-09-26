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

public class KarmaBattlefieldScript implements Script {

    public static int afkAttempts = 0;
    public static Long lastUse = null;
    public static int battlefieldRetries = 0;

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;
        useBandageOnInterval(-1, 21);
        if (battlefieldRetries > 80){
            return -1;
        }

        List<Integer> battlefieldSoldiers = getNpcNearPlayer("hell protector");
        if (battlefieldSoldiers.isEmpty()){
            battlefieldRetries++;
            return 250;
        }
        battlefieldRetries = 0;

        int rand = new Random().nextInt(battlefieldSoldiers.size());

        GameAction.attackNPC(battlefieldSoldiers.get(rand));

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

    public List<Integer> getNpcNearPlayer(String npcName){
        int localPlayerTarget = LocalPlayer.getTargetNPC();
        List<Object> npcList = NPC.getNPCList();
        List<Integer> validNPC = new ArrayList<>();
        for (Object npc : npcList){
            int npcId = NPC.getNPCId(npc);
            if (NPC.getNPCName(npc).equalsIgnoreCase(npcName) && npcId != localPlayerTarget && NPC.getNPCHp(npc, 0) > 0){
                validNPC.add(npcId);
            }
        }
        return validNPC;
    }

}
