package com.john.RedemptionBotSDK;

import com.john.RedemptionBotSDK.enums.AfkPatrolStatus;
import com.john.RedemptionBotSDK.enums.VarcacheID;
import com.john.RedemptionBotSDK.util.ReflectionLoader;
import com.john.RedemptionBotSDK.util.ReflectionResult;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.john.RedemptionBotSDK.enums.InterfaceID.AFKPATROL_INTERFACE;
import static com.john.RedemptionBotSDK.enums.InterfaceID.AFKPATROL_INTERFACE_TEXT;
import static com.john.RedemptionBotSDK.util.InterfaceUtil.*;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.*;

public class GameState {

    public static int getSlayerTaskID() {
        return (int) getVarCacheIDX(VarcacheID.SLAYER_TASK_ID);
    }

    public static int getSlayerKillsLeft() {
        return (int) getVarCacheIDX(VarcacheID.SLAYER_REMAINING_KILLS);
    }

    //Returns the var cache result given an index.
    public static Object getVarCacheIDX(VarcacheID id) {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("getSlayerTaskID: Could not get client instance");
            return -1;
        }
        ReflectionResult varcacheClassInstance = safeGet(ReflectionLoader.CLIENT_VARCACHE_FIELD, client.value());
        if (!varcacheClassInstance.success() || varcacheClassInstance.value() == null){
            System.err.println("getSlayerTaskID: Could not get varcacheClassInstance");
            return -1;
        }
        ReflectionResult varcacheArray = safeGet(ReflectionLoader.VARCACHE_ARRAY_FIELD, varcacheClassInstance.value());
        if (!varcacheArray.success() || varcacheArray.value() == null){
            System.err.println("getSlayerTaskID: Could not get varcacheArray");
            return -1;
        }
        return ((Object[]) varcacheArray.value())[id.id];
    }

    //Returns a raw interface object if it is open.
    public static Object getInterfaceIfOpen(int interfaceId, int parentId){
        List<Object> openInterfaces = getAllOpenInterfaces();
        for (Object o : openInterfaces){
            ReflectionResult oIdResult = safeInvoke(INTERFACE_RUNELITE_TABLE_ID_METHOD, null, o);
            if (!oIdResult.success() || oIdResult.value() == null){
                System.err.println("getInterfaceIfOpen: Could not get open interface Id");
                return null;
            }
            int id = (int) oIdResult.value();
            if (id == interfaceId){
                ReflectionResult oParentResult = safeInvoke(INTERFACE_RUNELITE_TABLE_PARENTID_METHOD, null, o);
                if (!oParentResult.success()){
                    System.err.println("getInterfaceIfOpen: Could not get interface parent Id");
                    return null;
                }
                if (oParentResult.value() != null && (int)oParentResult.value() == parentId){
                    return o;
                }
            }
        }
        return null;
    }

    //Returns a boolean whether the specified interface is open (interfaceId, parentId)
    public static boolean isInterfaceOpen(int interfaceId, int parentId) {
        List<Object> openInterfaces = getAllOpenInterfaces();
        for (Object o : openInterfaces){
            ReflectionResult oIdResult = safeInvoke(INTERFACE_RUNELITE_TABLE_ID_METHOD, null, o);
            if (!oIdResult.success() || oIdResult.value() == null){
                System.err.println("getInterfaceIfOpen: Could not get open interface Id");
                return false;
            }
            int id = (int) oIdResult.value();
            if (id == interfaceId){
                ReflectionResult oParentResult = safeInvoke(INTERFACE_RUNELITE_TABLE_PARENTID_METHOD, null, o);
                if (!oParentResult.success()){
                    System.err.println("getInterfaceIfOpen: Could not get interface parent Id");
                    return false;
                }
                if (oParentResult.value() != null && (int)oParentResult.value() == parentId){
                    return true;
                }
            }
        }
        return false;
    }

    //Checks to see if an interface that matches the AFKPatrol interface is open.
    public static AfkPatrolStatus checkForAfkPatrol() {
        Object AFKPatrolInterface = getInterfaceIfOpen(AFKPATROL_INTERFACE_TEXT.id, AFKPATROL_INTERFACE.id);
        if (AFKPatrolInterface == null) return AfkPatrolStatus.CLOSED;

        ReflectionResult interfaceTextResult = safeInvoke(INTERFACE_RUNELITE_TABLE_TEXT_METHOD, null, AFKPatrolInterface);
        if (!interfaceTextResult.success()){
            System.err.println("checkForAfkPatrol: Could not get interface text");
            return AfkPatrolStatus.ERROR;
        }

        String text = (String) interfaceTextResult.value();
        System.out.println("I Found an interface like AFK Patrol with text: " + text);
        return (text != null && text.contains("AFK Patrol")) ? AfkPatrolStatus.OPEN : AfkPatrolStatus.CLOSED;
    }

    public static Object[] getVarCacheArray() {
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("getSlayerTaskID: Could not get client instance");
            return new Object[]{};
        }
        ReflectionResult varcacheClassInstance = safeGet(ReflectionLoader.CLIENT_VARCACHE_FIELD, client.value());
        if (!varcacheClassInstance.success() || varcacheClassInstance.value() == null){
            System.err.println("getSlayerTaskID: Could not get varcacheClassInstance");
            return new Object[]{};
        }
        ReflectionResult varcacheArray = safeGet(ReflectionLoader.VARCACHE_ARRAY_FIELD, varcacheClassInstance.value());
        if (!varcacheArray.success() || varcacheArray.value() == null){
            System.err.println("getSlayerTaskID: Could not get varcacheArray");
            return new Object[]{};
        }
        return ((Object[]) varcacheArray.value());
    }

    public static Object getClientObject(){
        ReflectionResult client = safeInvoke(ReflectionLoader.CLIENT_CLIENT_METHOD, null);
        if (!client.success() || client.value() == null){
            System.err.println("getSlayerTaskID: Could not get client instance");
            return null;
        }
        return client.value();
    }

    public static Integer getTimer(String timerString){
        ReflectionResult timerResult = safeInvoke(TIMERS_GETTIMER_METHOD, null, timerString);
        if (!timerResult.success()){
            System.err.println("getTimer: Could not get timer for string -  " + timerString);
            return null;
        }
        return (Integer) timerResult.value();
    }

}
