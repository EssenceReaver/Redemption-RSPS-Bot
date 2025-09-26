import com.john.RedemptionBotSDK.GameAction;
import com.john.RedemptionBotSDK.GameState;
import com.john.RedemptionBotSDK.LocalPlayer;
import com.john.RedemptionBotSDK.NPC;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.util.*;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class GroupSlayerScript implements Script {

    private Integer afkAttempts = 0;
    private Integer retries = 0;
    public static Long lastUse = null;
    private static final int slayerPotionId = 401694;

    private static final int SLAYERPANEL_IFID = 398196738;
    private static final int SLAYERPANEL_PARENTIFID = 398196736;
    boolean teleportingToTask = false;
    boolean waitingOnTask = false;

    private final Map<String, Long> cooldowns = new HashMap<>();

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()){ return 500;}

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        useSlayerPotOnInterval();

        int curTask = GameState.getSlayerTaskID();
        if (curTask <= 0 && !teleportingToTask) {
            waitingOnTask = true;
            return 500;
        }
        else if (waitingOnTask){
            waitingOnTask = false;
            teleportingToTask = true;
            return 500;
        }
        else if (teleportingToTask){
            return teleportToTask();
        }
        else{
            String[] taskNames = SlayerMonsterMapsScript.getNames(curTask);
            if (taskNames == null){
                System.out.println("Found task without enum: " + curTask);
                return -1;
            }

            List<Integer> npcNearPlayer = tryFindNPC(taskNames, 8, 1000);
            if (!npcNearPlayer.isEmpty()){
                attackRandomNPC(npcNearPlayer);
                retries = 0;
                return new Random().nextInt(750, 1500);
            }else{
                retries++;
                if (retries > 3){
                    System.out.println("Tried many times to find NPC. ERROR!!!!!!!");
                    return -1;
                }
                teleportingToTask = true;
                return 500;
            }
        }
    }

    @Override
    public void onExit() {

    }

    @Override
    public void sleepSafe(long millis) {
        Script.super.sleepSafe(millis);
    }

    public int teleportToTask() {
        int x = LocalPlayer.getTileX();
        int y = LocalPlayer.getTileY();
        int anim = LocalPlayer.getLocalPlayerAnimationId();
        int curTask = GameState.getSlayerTaskID();
        boolean atTaskTile = (x == 3683 && y == 9889);
        boolean isTeleporting = (anim == 8939 || anim == 8941);

        if (!atTaskTile && !isTeleporting) {
            if (!onCooldown("trippyCd", 3000)){
                System.out.println("Teleporting to trippy!");
                GameAction.sendPlayerCommand("trippy");
            }
        }
        else if (GameState.isInterfaceOpen(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, -1) && !onCooldown("teleportDialogueCd", 3000) && !isTeleporting) {
            System.out.println("Clicking to teleport to instance");
            GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 2);
            teleportingToTask = false;
        }
        else if (GameState.isInterfaceOpen(SLAYERPANEL_IFID, SLAYERPANEL_PARENTIFID) && !onCooldown("teleportButtonCd", 3000) && !isTeleporting) {
            if (curTask <= 0){
                teleportingToTask = false;
                return 1000;
            }
            System.out.println("Clicking Slayer Teleport Button");
            GameAction.clickInterface(InterfaceID.SLAYER_TELEPORT_BUTTON.id, -1, 1);
        }
        else if (!isTeleporting) {
            if (curTask <= 0){
                teleportingToTask = false;
                return 1000;
            }
            if (!onCooldown("slayerIfCd", 3000)){
                System.out.println("Got task: " + Arrays.toString(SlayerMonsterMapsScript.getNames(curTask)) + "x" + GameState.getSlayerKillsLeft());
                System.out.println("Opening Slayer IF (TP)");
                GameAction.sendPlayerCommand("slayer");
            }
        }
        return 500;
    }

    public int getNewTask() {
        if (GameState.isInterfaceOpen(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, -1) && !onCooldown("hardTaskDialogueCd", 3000)){
            System.out.println("Clicking to get hard task");
            GameAction.clickDialogue(InterfaceID.SLAYER_TELEPORT_DIALOGUE.id, 2);
            teleportingToTask = true;
        }
        else if (GameState.isInterfaceOpen(InterfaceID.SLAYER_ASSIGNTASKDIFFICULTY_INTERMEDIATE_DIALOGUE.id, 15138819) && !onCooldown("intermediateCd", 3000)){
            System.out.println("Clicking intermediate dialogue okay");
            GameAction.clickDialogue(InterfaceID.SLAYER_ASSIGNTASKDIFFICULTY_INTERMEDIATE_DIALOGUE.id, -1);
        }
        else if(GameState.isInterfaceOpen(SLAYERPANEL_IFID, SLAYERPANEL_PARENTIFID) && !onCooldown("assignTaskCd", 3000)){
            System.out.println("Clicking assigning task button");
            GameAction.clickInterface(InterfaceID.SLAYER_ASSIGN_TASK_BUTTON.id, -1, 1);
        }
        else{
            if (!onCooldown("slayerIfCd", 3000)){
                System.out.println("Opening Slayer IF");
                GameAction.sendPlayerCommand("slayer");
            }
        }
        return 500;
    }

    public void useSlayerPotOnInterval(){
        int ticksLeft = GameState.getTimer(Timers.TRIPLE_SLAYER_POINTS.getId());
        if (ticksLeft == 0){
            Integer potionIdx = LocalPlayer.getItemIndexFromInventory(slayerPotionId);
            if (potionIdx != null) GameAction.useInventoryObject(potionIdx, InterfaceID.PLAYER_INVENTORY.id);
            sleepSafe(500);
        }
    }

    public void attackRandomNPC(List<Integer> npcIdList){
        int randomNpc = npcIdList.get(new Random().nextInt(npcIdList.size()));
        GameAction.attackNPC(randomNpc);
    }

    private List<Integer> tryFindNPC(String[] taskNames, int attempts, int waitMs) {
        for (int i = 0; i < attempts; i++) {
            List<Integer> found = getNpcNearPlayer(taskNames);
            if (!found.isEmpty()) return found;
            sleepSafe(waitMs);
        }
        return List.of();
    }

    public List<Integer> getNpcNearPlayer(String[] npcIdentifiers){
        int localPlayerTarget = LocalPlayer.getTargetNPC();
        List<Object> npcList = NPC.getNPCList();
        List<Integer> validNPC = new ArrayList<>();
        for (Object npc : npcList){
            int npcId = NPC.getNPCId(npc);
            if (npcIsTask(npc, npcIdentifiers) && NPC.getNPCHp(npc, 0) > 0 && npcId != localPlayerTarget ){
                validNPC.add(npcId);
            }
        }
        return validNPC;
    }

    public boolean npcIsTask(Object npc, String[] npcIdentifiers){
        String npcName = NPC.getNPCName(npc);
        for (String npcIdentifier : npcIdentifiers) {
            if (npcName.toLowerCase().contains(npcIdentifier.toLowerCase())) {
                return true;
            }
        }
        return false;
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
