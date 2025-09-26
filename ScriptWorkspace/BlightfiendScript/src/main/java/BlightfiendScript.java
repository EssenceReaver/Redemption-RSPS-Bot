import com.john.RedemptionBotSDK.GameAction;
import com.john.RedemptionBotSDK.GameState;
import com.john.RedemptionBotSDK.LocalPlayer;
import com.john.RedemptionBotSDK.NPC;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class BlightfiendScript implements Script {
    public static int afkAttempts = 0;
    public static Long lastUse = null;
    public static int blightfiendRetries = 0;
    public static boolean blightfiendRespawn = true;
    public static Point offsetPoint;

    public static boolean lastUp = false;

    private final Map<String, Long> cooldowns = new HashMap<>();

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
        if (blightfiendRetries > 300){
            return -1;
        }

        NPC blightfiendNpc = NPC.getClosestNpc("Blightfiend");
        if (blightfiendNpc == null){
            blightfiendRespawn = true;
            blightfiendRetries++;
            return 100;
        }
        blightfiendRetries = 0;
        int blightfiendAnimation = NPC.getAnimationId(blightfiendNpc.npcObject);

        if (blightfiendAnimation == 123069){
            if (!onCooldown("walkCd", 3000)){
                if (lastUp){
                    offsetPoint = new Point(new Random().nextInt(0, 2), new Random().nextInt(3, 6));
                    GameAction.walkTo(LocalPlayer.getTileX() + offsetPoint.x, LocalPlayer.getTileY() + offsetPoint.y);
                    lastUp = false;
                }
                else {
                    GameAction.walkTo(LocalPlayer.getTileX() - offsetPoint.x, LocalPlayer.getTileY() - offsetPoint.y);
                    lastUp = true;
                }
                return 1500;
            }
        }
        if (!onCooldown("attackCd", 1000)){
            if (blightfiendRespawn){
                sleepSafe(new Random().nextInt(250,4000));
                blightfiendRespawn = false;
            }
            GameAction.attackNPC(blightfiendNpc.npcId);
        }

        return 100;
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

    private boolean onCooldown(String key, long delay) {
        long now = System.currentTimeMillis();
        Long last = cooldowns.get(key);

        if (last == null || now - last >= delay) {
            cooldowns.put(key, now);
            return false;
        }
        return true;
    }


}
