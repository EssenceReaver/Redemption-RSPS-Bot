import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;
import com.john.RedemptionBotSDK.util.Region;

import java.awt.*;
import java.util.Random;

import static com.john.RedemptionBotSDK.GameAction.solveAfkPatrol;
import static com.john.RedemptionBotSDK.GameState.checkForAfkPatrol;

public class TelosScript implements Script {

    public static int afkAttempts = 0;
    public static Long lastUse = null;
    private static final int ROOTS_OBJ_ID = 503521;
    private static final int BLACK_OBJ = 122895;
    private static final int GREEN_OBJ1 = 122893;
    private static final int GREEN_OBJ2 = 122899;
    private static final int GREEN_OBJ3 = 122902;
    private static final int RED_OBJ1= 122894;
    private static final int RED_OBJ2 = 122900;
    private static final int RED_OBJ3 = 122903;
    private static final int ROOTS_DIALOGUEID_0 = 393936897;
    private static final int ROOTS_DIALOGUEPARENTID_0 = 393936900;
    private static final int ROOTS_CUSTOM_DIALOGUEID = 393936937;
    private static final int ROOTS_CUSTOM_PARENTDIALOGUEID = 393936908;
    private static final int CLAIM_LOOT_PARENTIFID = 393412621;
    private static final int CLAIM_LOOT_IFID = 393412622;
    private static final int CONTINUE_CHALLENGE_PARENTIFID = 393412623;
    private static final int CONTINUE_CHALLENGE_IFID = 393412624;
    private static int missingRetries = 0;
    private static int greenRetries = 0;

    private static Integer baseInstanceX = null;
    private static Integer baseInstanceY = null;
    private static Region platform1 = null;
    private static Region platform2 = null;
    private static Region platform3 = null;
    private static boolean greenFinished = false;
    private static boolean redFinished = false;
    private static Point greenObjPoint = null;
    private static Point redObjPoint = null;
    private static Point blackObjPoint = null;
    private static boolean claimed = false;

    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {
        if (!LocalPlayer.isLoggedIn()) return 500;

        Integer afkCheck = handleAfkChecks();
        if (afkCheck != null) return afkCheck;

        useBandageOnInterval(140, 20);

        int localPlayerTileX = LocalPlayer.getTileX();
        int localPlayerTileY = LocalPlayer.getTileY();
        int localPlayerPlane = LocalPlayer.getPlane();

        GameObject rootsObj = GameObject.findClosestGameObjectById(ROOTS_OBJ_ID);

        if (baseInstanceX == null){
            return handleNullBase(rootsObj);
        }
        else if (platform1.contains(localPlayerTileX, localPlayerTileY)){
            missingRetries = 0;
            return handlePlatform1(localPlayerTileX, localPlayerTileY);
        }
        else if(platform2.contains(localPlayerTileX, localPlayerTileY)){
            missingRetries = 0;
            return handlePlatform2or3(true);
        }
        else if (platform3.contains(localPlayerTileX, localPlayerTileY)){
            missingRetries = 0;
            return handlePlatform2or3(false);
        }
        else if (localPlayerPlane == 1){
            missingRetries = 0;
            if (!greenFinished){
                return handleGreenPhase();
            }
            //check if redObj1
            else if(!redFinished){
                return handleRedPhase();
            }
            else{
                return handleBlackPhase();
            }
        }
        else if (rootsObj != null){
            missingRetries = 0;
            System.out.println("Player not found within any platform!");
            //Successfully exited fountain. Reset instance variables here
            platform1 = null;
            platform2 = null;
            platform3 = null;
            greenFinished = false;
            redFinished = false;
            greenObjPoint = null;
            redObjPoint = null;
            blackObjPoint = null;
            baseInstanceX = null;
            baseInstanceY = null;
            claimed = false;
            return 500;
        }
        else{
            //not in telos fight? Maybe died?
            if (missingRetries > 8){
                System.out.println("Couldn't find anything??? Did I die?");
                return -1;
            }
            else{
                missingRetries++;
                return 2000;
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

    private static void populateRegions(int xCoord, int yCoord){
        int firstPlatformX00 = xCoord - 23;
        int firstPlatformY00 = yCoord - 3;
        int secondPlatformX00 = firstPlatformX00 + 55;
        int secondPlatformY00 = firstPlatformY00 - 2;
        int thirdPlatformX00 = firstPlatformX00 + 9;
        int thirdPlatformY00 = firstPlatformY00 - 66;

        Region firstPlatform = new Region(firstPlatformX00, firstPlatformY00, firstPlatformX00 + 23 , firstPlatformY00 + 23);
        Region secondPlatform = new Region(secondPlatformX00, secondPlatformY00, secondPlatformX00 + 23, secondPlatformY00 + 23);
        Region thirdPlatform = new Region(thirdPlatformX00, thirdPlatformY00, thirdPlatformX00 + 23, thirdPlatformY00 + 23);

        platform1 = firstPlatform;
        platform2 = secondPlatform;
        platform3 = thirdPlatform;
    }

    private static int handleNullBase(GameObject rootsObj){
        NPC dormantTelos = NPC.getClosestNpcIgnoreHp("(dormant)");
        if (dormantTelos != null){
            System.out.println("Setting bases!");
            baseInstanceX = LocalPlayer.getTileX();
            baseInstanceY = LocalPlayer.getTileY();
            populateRegions(baseInstanceX, baseInstanceY);
            return 50;
        }
        else if (rootsObj != null){
            //need some max retry logic in here
            if (GameState.isInterfaceOpen(InterfaceID.AFKPATROL_INTERFACE_TEXT.id, InterfaceID.AFKPATROL_INTERFACE.id)){
                GameAction.sendPCountDialogue(1001);
            }
            else if (GameState.isInterfaceOpen(ROOTS_CUSTOM_DIALOGUEID, ROOTS_CUSTOM_PARENTDIALOGUEID)){
                GameAction.clickDialogue(ROOTS_CUSTOM_DIALOGUEID, -1);
            }
//            if (GameState.isInterfaceOpen(ROOTS_DIALOGUEID_0, ROOTS_DIALOGUEPARENTID_0)){
//                GameAction.clickDialogue(ROOTS_DIALOGUEID_0, -1);
//            }
            else{
                GameAction.interactWithObject(rootsObj.objectId, rootsObj.xPos, rootsObj.yPos);
            }
            return 1000;
        }
        else{
            //Need to teleport to telos here.
            System.out.println("Couldn't find roots while baseInstance is null!");
            return -1;
        }
    }

    private static int handlePlatform1(int localPlayerTileX, int localPlayerTileY){
        NPC dormantTelos = NPC.getClosestNpcIgnoreHp("(dormant)");
        NPC telosNpc = NPC.getClosestNpcIgnoreHp("telos");
        if (dormantTelos != null){
            //walk to wake telos up
            int wakeUpTelosX = baseInstanceX - 7;
            int wakeUpTelosY = baseInstanceY + 11;
            if (localPlayerTileX != wakeUpTelosX || localPlayerTileY != wakeUpTelosY){
                GameAction.walkTo(wakeUpTelosX, wakeUpTelosY);
            }
            return 1000;
        }else if (telosNpc != null){
            long currentHp = NPC.getNPCHp(telosNpc.npcObject, 0);
            long maxHp = NPC.getNPCHp(telosNpc.npcObject, 1);
            int telosTotalHp = (int) ((currentHp * 100.0) / maxHp);
            if (telosTotalHp > 75){
                GameAction.attackNPC(telosNpc.npcId);
            }
            if (telosTotalHp <= 75){
                GameAction.walkTo(LocalPlayer.getTileX() + 8, LocalPlayer.getTileY() + 8);
            }
            return 1000;
        }
        else{
            //Couldn't find telos? Some error maybe
            System.out.println("Couldn't find telos on platform1!");
            return -1;
        }
    }

    private static int handlePlatform2or3(boolean onp2){
        NPC telosNpc = NPC.getClosestNpcIgnoreHp("telos");
        if (telosNpc == null){
            System.out.println("Couldn't find telos on platform2!");
        }
        else{
            int telosAnimation = NPC.getAnimationId(telosNpc.npcObject);
            if (telosAnimation == 128930){
                return 500;
            }
            long currentHp = NPC.getNPCHp(telosNpc.npcObject, 0);
            long maxHp = NPC.getNPCHp(telosNpc.npcObject, 1);
            int telosTotalHp = (int) ((currentHp * 100.0) / maxHp);
            int threshold = onp2 ? 50 : 25;
            if (telosTotalHp > threshold){
                GameAction.attackNPC(telosNpc.npcId);
            }
            if (telosTotalHp <= threshold){
                if (threshold == 50){
                    GameAction.walkTo(LocalPlayer.getTileX() - 8, LocalPlayer.getTileY() - 8);
                }
                else{
                    GameAction.walkTo(LocalPlayer.getTileX() + 6, LocalPlayer.getTileY() - 6);
                }
            }
        }
        return 1000;
    }

    private static int handleGreenPhase(){

        NPC greenObj1 = NPC.getNpcObjByRealId(GREEN_OBJ1);
        NPC greenObj2 = NPC.getNpcObjByRealId(GREEN_OBJ2);
        NPC greenObj3 = NPC.getNpcObjByRealId(GREEN_OBJ3);

        if(greenObj1 != null){
            greenRetries = 0;
            if (greenObjPoint == null){
                greenObjPoint = new Point(greenObj1.npcXPos, greenObj1.npcYPos);
            }
            int localPlayerX = LocalPlayer.getTileX();
            int localPlayerY = LocalPlayer.getTileY();
            NPC telosNpc = NPC.getClosestNpcIgnoreHp("telos");
            if (telosNpc == null) return 250;
            int telosAnimation = NPC.getAnimationId(telosNpc.npcObject);
            if (telosAnimation == 128930){
                return 500;
            }
            if (localPlayerX != greenObjPoint.getX() || localPlayerY != greenObjPoint.getY()){
                GameAction.walkTo((int) greenObjPoint.getX(), (int) greenObjPoint.getY());
                return 2000;
            }
            else{
                if (NPC.npcDistance(telosNpc.npcXPos, telosNpc.npcYPos) <= 8){
                    GameAction.attackNPC(telosNpc.npcId);
                    return 1000;
                }
                return 500;
            }
        }
        //check if greenObj2
        else if(greenObj2 != null){
            greenRetries = 0;
            int currentTarget = LocalPlayer.getTargetNPC();
            NPC telosNpc = NPC.getClosestNpcIgnoreHp("telos");
            if (telosNpc == null) return 250;
            if (telosNpc.npcId == currentTarget){
                GameAction.walkTo((int) greenObjPoint.getX(), (int) greenObjPoint.getY());
                return 500;
            }
            return 500;
        }
        //check if greenObj3
        else if(greenObj3 != null){
            greenRetries = 0;
            GameAction.walkTo((int) greenObjPoint.getX() + 25, (int) greenObjPoint.getY());
            greenFinished = true;
            return 2000;
        }
        else{
            if (greenRetries > 3){
                System.out.println("Encountered some error in green loop!");
                return -1;
            }
            greenRetries++;
            return 2000;
        }
    }

    private static int handleRedPhase(){
        NPC redObj1 = NPC.getNpcObjByRealId(RED_OBJ1);
        NPC redObj2 = NPC.getNpcObjByRealId(RED_OBJ2);
        NPC redObj3 = NPC.getNpcObjByRealId(RED_OBJ3);
        if (redObj1 != null){
            if (redObjPoint == null){
                redObjPoint = new Point(redObj1.npcXPos, redObj1.npcYPos);
            }
            int localPlayerX = LocalPlayer.getTileX();
            int localPlayerY = LocalPlayer.getTileY();
            if (localPlayerX != redObjPoint.getX() || localPlayerY != redObjPoint.getY()){
                GameAction.walkTo((int) redObjPoint.getX(), (int) redObjPoint.getY());
                return 2000;
            }
            else{
                NPC telosNpc = NPC.getClosestNpcIgnoreHp("telos");
                if (telosNpc == null) return 250;
                if (NPC.npcDistance(telosNpc.npcXPos, telosNpc.npcYPos) <= 8){
                    GameAction.attackNPC(telosNpc.npcId);
                    return 1000;
                }
                return 500;
            }
        }
        else if(redObj2 != null){
            //stop attacking(walk on top of self)
            int currentTarget = LocalPlayer.getTargetNPC();
            NPC telosNpc = NPC.getClosestNpcIgnoreHp("telos");
            if (telosNpc == null) return 250;
            if (telosNpc.npcId == currentTarget){
                GameAction.walkTo((int) redObjPoint.getX(), (int) redObjPoint.getY());
                return 500;
            }
            return 250;
        }
        //check if redObj3 or blackobj boolean
        else if(redObj3 != null){
            //start walking to redobj1 and set greenfinished true
            GameAction.walkTo((int) redObjPoint.getX() - 15, (int) redObjPoint.getY() - 20);
            redFinished = true;
            return 2000;
        }
        else{
            //walk to redobj from greenobj coords
            GameAction.walkTo((int) greenObjPoint.getX() + 25, (int) greenObjPoint.getY());
            return 2000;
        }
    }

    private static int handleBlackPhase(){
        NPC blackObj = NPC.getNpcObjByRealId(BLACK_OBJ);
        GameObject endFountain = GameObject.findClosestGameObjectById(503569);
        //walk to black here if on top of black then attack telos
        if (endFountain != null){
            //maybe add max retries here
//            if (GameState.isInterfaceOpen(CONTINUE_CHALLENGE_IFID, CONTINUE_CHALLENGE_PARENTIFID)){
//                GameAction.clickInterface(CONTINUE_CHALLENGE_IFID, -1, 1);
//                claimed = true;
//            }
            if (GameState.isInterfaceOpen(CLAIM_LOOT_IFID, CLAIM_LOOT_PARENTIFID)){
                GameAction.clickInterface(CLAIM_LOOT_IFID, -1, 1);
                claimed = true;
            }
            else if (claimed){
                GameAction.interactWithObject2(endFountain.objectId, endFountain.xPos, endFountain.yPos);
            }
            else{
                GameAction.interactWithObject(endFountain.objectId, endFountain.xPos, endFountain.yPos);
            }
            return 1000;
        }
        else if (blackObjPoint == null){
            if (blackObj != null){
                blackObjPoint = new Point(blackObj.npcXPos, blackObj.npcYPos);
                return 250;
            }
            else{
                GameAction.walkTo((int) redObjPoint.getX() - 15, (int) redObjPoint.getY() - 20);
                return 2000;
            }
        }
        else{
            int localPlayerX = LocalPlayer.getTileX();
            int localPlayerY = LocalPlayer.getTileY();
            if (localPlayerX != blackObjPoint.getX() || localPlayerY != blackObjPoint.getY()){
                GameAction.walkTo((int) blackObjPoint.getX(), (int) blackObjPoint.getY());
                return 2000;
            }
            else{
                NPC telosNpc = NPC.getClosestNpcIgnoreHp("telos");
                if (telosNpc == null) return 50;
                if (NPC.npcDistance(telosNpc.npcXPos, telosNpc.npcYPos) <= 8){
                    GameAction.attackNPC(telosNpc.npcId);
                }
                return 1000;
            }
        }
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
}
