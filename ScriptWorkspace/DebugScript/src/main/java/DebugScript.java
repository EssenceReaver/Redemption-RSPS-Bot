import com.john.RedemptionBotSDK.*;
import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.InterfaceID;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Skill;
import com.john.RedemptionBotSDK.enums.Timers;
import com.john.RedemptionBotSDK.script.Script;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class DebugScript implements Script {

    private static final int staffId = 400722;
    private static final int hammerId = 400148;
    @Override
    public void onLoad() {

    }

    @Override
    public int onLoop() {

        Integer playerPrayer = LocalPlayer.getCurrentSkillLevel(Skill.PRAYER);
        Integer playerMaxPrayer = LocalPlayer.getMaxSkillLevel(Skill.PRAYER);
        System.out.println(playerPrayer + ":" + playerMaxPrayer);
        return 10000;
    }

    @Override
    public void onExit() {

    }

    @Override
    public void sleepSafe(long millis) {
        Script.super.sleepSafe(millis);
    }

    public int getFirstNpcNearPlayer(String wantedName){
        List<Object> npcList = NPC.getNPCList();
        for (Object npc : npcList){
            int npcId = NPC.getNPCId(npc);
            String npcName = NPC.getNPCName(npc);
            if (npcName != null && npcName.toLowerCase().contains(wantedName) && NPC.getNPCHp(npc, 0) > 0){
                return npcId;
            }
        }
        return -1;
    }


    public static String dumpObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        Class<?> clazz = obj.getClass();

        sb.append(clazz.getName()).append(" { ");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                sb.append(clazz.getSimpleName())
                        .append(".")
                        .append(field.getName())
                        .append("=");

                if (value != null && value.getClass().isArray()) {
                    // handle arrays
                    Class<?> compType = value.getClass().getComponentType();
                    if (compType.isPrimitive()) {
                        // primitive arrays need Arrays.* helpers
                        if (compType == int.class) {
                            sb.append(Arrays.toString((int[]) value));
                        } else if (compType == long.class) {
                            sb.append(Arrays.toString((long[]) value));
                        } else if (compType == double.class) {
                            sb.append(Arrays.toString((double[]) value));
                        } else if (compType == float.class) {
                            sb.append(Arrays.toString((float[]) value));
                        } else if (compType == boolean.class) {
                            sb.append(Arrays.toString((boolean[]) value));
                        } else if (compType == char.class) {
                            sb.append(Arrays.toString((char[]) value));
                        } else if (compType == byte.class) {
                            sb.append(Arrays.toString((byte[]) value));
                        } else if (compType == short.class) {
                            sb.append(Arrays.toString((short[]) value));
                        } else {
                            sb.append("<unknown primitive array>");
                        }
                    } else {
                        // object array
                        sb.append(Arrays.deepToString((Object[]) value));
                    }
                } else {
                    // non-array: just use toString
                    sb.append(value);
                }

                sb.append(", ");
            } catch (IllegalAccessException e) {
                sb.append(clazz.getSimpleName())
                        .append(".")
                        .append(field.getName())
                        .append("=<inaccessible>, ");
            }
        }

        // trim trailing comma/space
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }

        sb.append(" }");
        return sb.toString();
    }

}
