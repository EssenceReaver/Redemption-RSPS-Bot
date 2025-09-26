package com.john.RedemptionBotSDK.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionLoader {

    public static Class<?> CLIENT_CLASS;
    public static Method CLIENT_CLIENT_METHOD;
    public static Method CLIENT_PACKET_METHOD;
    public static Method CLIENT_COMMAND_METHOD;
    public static Method CLIENT_RESUME_P_NAMEDIALOG_METHOD;
    public static Method CLIENT_RESUME_P_COUNTDIALOG_METHOD;
    public static Field CLIENT_VARCACHE_FIELD;
    public static Field CLIENT_CURRENT_SKILL_LEVEL_ARRAY_FIELD;
    public static Field CLIENT_MAX_SKILL_LEVEL_ARRAY_FIELD;
    public static Class<?> PLAYER_CLASS;
    public static Field CLIENT_LOCALPLAYER_FIELD;
    public static Class<?> NPC_CLASS;
    public static Field NPC_ID_FIELD;
    public static Method NPC_NAME_METHOD;
    public static Method NPC_HP_METHOD;
    public static Field NPC_INFO_FIELD;
    public static Class<?> NPC_INFO_CLASS;
    public static Field NPC_INFO_CONTEXTMENU_FIELD;
    public static Field NPC_INFO_REALID_FIELD;

    public static Class<?> ENTITY_CLASS;
    public static Field ENTITY_FIELD_TARGETTED_NPC;
    public static Field ENTITY_FIELD_ANIMATION_ID;
    public static Field ENTITY_FIELD_XWAYPOINT_ARRAY;
    public static Field ENTITY_FIELD_YWAYPOINT_ARRAY;
    public static Class<?> VARCACHE_CLASS;
    public static Field VARCACHE_ARRAY_FIELD;
    public static Class<?> INVENTORY_MGMT_CLASS;
    public static Field INVENTORY_MGMT_CLASS_INHT_FIELD;
    public static Class<?> INHT_CLASS;
    public static Method INHT_GET_INVENTORY_METHOD;
    public static Field INVENTORY_IDARR_FIELD;
    public static Field INVENTORY_STACKSIZEARR_FIELD;
    public static Class<?> MAP_RENDER_CLASS;
    public static Method MAP_RENDERER_GETTILES_METHOD;
    public static Field MAP_RENDERER_XMAX_FIELD;
    public static Field MAP_RENDERER_YMAX_FIELD;
    public static Class<?> TILE_CLASS;
    public static Field TILE_CLASS_GAMEOBJECT_NODE_FIELD;
    public static Class<?> TILE_NODE_HELPER_CLASS;
    public static Method TILE_NODE_HELPER_GAMEOBJECT_METHOD;
    public static Class<?> GAMEOBJECT_CLASS;
    public static Class<?> RUNELITE_TILE_HELPER_CLASS;
    public static Method RUNELITE_TILE_HELPER_COMPARETILETOGAMEOBJ_METHOD;
    public static Class<?> RUNELITE_HOOKS_CLASS;
    public static Method RUNELITE_ISLOGGEDIN_METHOD;
    public static Class<?> RUNELITE_ISLOGGEDIN_ENUM_CLASS;
    public static Field RUNELITE_ISLOGGEDIN_ENUM_ID_FIELD;
    public static Method RUNELITE_NPCLIST_METHOD;
    public static Method RUNELITE_NPCARRAY_METHOD;
    public static Method RUNELITE_GETINTERFACENODES_METHOD;
    public static Method RUNELITE_GET_MAPRENDERERCLASS_METHOD;
    public static Method RUNELITE_GET_PLAYERPLANE_METHOD;
    public static Method RUNELITE_GET_PLAYERBASE_X_METHOD;
    public static Method RUNELITE_GET_PLAYERBASE_Y_METHOD;
    public static Class<?> INTERFACE_MANAGER_CLASS;
    public static Class<?> INTERFACE_CLASS;
    public static Class<?> INTERFACE_RUNELITE_TABLE_CLASS;
    public static Method INTERFACE_RUNELITE_TABLE_ID_METHOD;
    public static Method INTERFACE_RUNELITE_TABLE_TEXT_METHOD;
    public static Method INTERFACE_RUNELITE_TABLE_HIDDEN_METHOD;
    public static Method INTERFACE_RUNELITE_TABLE_PARENTID_METHOD;
    public static Method INTERFACE_MANAGER_GETS_METHOD;
    public static Method INTERFACE_MANAGER_GETD_METHOD;
    public static Method INTERFACE_MANAGER_GETN_METHOD;

    public static Class<?> GAMEOBJECT_INTERFACE_CLASS;
    public static Method GAMEOBJECT_INTERFACE_GETID_METHOD;

    public static Class<?> TIMERS_CLASS;
    public static Method TIMERS_GETTIMER_METHOD;

    public static void init() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
        CLIENT_CLASS = loadClass(Hooks.CLIENT_CLASS);
        CLIENT_CLIENT_METHOD = loadMethod(CLIENT_CLASS, Hooks.CLIENT_CLIENT_METHOD);
        CLIENT_PACKET_METHOD = loadMethod(CLIENT_CLASS, Hooks.CLIENT_PACKET_METHOD, int.class, int.class, int.class, int.class, int.class, String.class, String.class);
        CLIENT_COMMAND_METHOD = loadMethod(CLIENT_CLASS, Hooks.CLIENT_COMMAND_METHOD, String.class, boolean.class);
        CLIENT_RESUME_P_NAMEDIALOG_METHOD = loadMethod(CLIENT_CLASS, Hooks.CLIENT_RESUME_P_NAMEDIALOG_METHOD, String.class);
        CLIENT_RESUME_P_COUNTDIALOG_METHOD = loadMethod(CLIENT_CLASS, Hooks.CLIENT_RESUME_P_COUNTDIALOG_METHOD, int.class);
        CLIENT_VARCACHE_FIELD = loadField(CLIENT_CLASS, Hooks.CLIENT_VARCACHE_FIELD);
        CLIENT_CURRENT_SKILL_LEVEL_ARRAY_FIELD = loadField(CLIENT_CLASS, Hooks.CLIENT_CURRENT_SKILL_LEVEL_ARRAY_FIELD);
        CLIENT_MAX_SKILL_LEVEL_ARRAY_FIELD = loadField(CLIENT_CLASS, Hooks.CLIENT_MAX_SKILL_LEVEL_ARRAY_FIELD);
        PLAYER_CLASS = loadClass(Hooks.PLAYER_CLASS);
        CLIENT_LOCALPLAYER_FIELD = loadField(CLIENT_CLASS, Hooks.CLIENT_LOCALPLAYER_FIELD);
        NPC_CLASS = loadClass(Hooks.NPC_CLASS);
        NPC_ID_FIELD = loadField(NPC_CLASS, Hooks.NPC_ID_FIELD);
        NPC_NAME_METHOD = loadMethod(NPC_CLASS, Hooks.NPC_NAME_METHOD);
        NPC_HP_METHOD = loadMethod(NPC_CLASS, Hooks.NPC_HP_METHOD, int.class);
        NPC_INFO_FIELD = loadField(NPC_CLASS, Hooks.NPC_INFO_FIELD);
        NPC_INFO_CLASS = loadClass(Hooks.NPC_INFO_CLASS);
        NPC_INFO_CONTEXTMENU_FIELD = loadField(NPC_INFO_CLASS, Hooks.NPC_INFO_CONTEXTMENU_FIELD);
        NPC_INFO_REALID_FIELD = loadField(NPC_INFO_CLASS, Hooks.NPC_INFO_REALID_FIELD);
        ENTITY_CLASS = loadClass(Hooks.ENTITY_CLASS);
        ENTITY_FIELD_TARGETTED_NPC = loadField(ENTITY_CLASS, Hooks.ENTITY_FIELD_TARGETTED_NPC);
        ENTITY_FIELD_ANIMATION_ID = loadField(ENTITY_CLASS, Hooks.ENTITY_FIELD_ANIMATION_ID);
        ENTITY_FIELD_XWAYPOINT_ARRAY = loadField(ENTITY_CLASS, Hooks.ENTITY_FIELD_XWAYPOINT_ARRAY);
        ENTITY_FIELD_YWAYPOINT_ARRAY = loadField(ENTITY_CLASS, Hooks.ENTITY_FIELD_YWAYPOINT_ARRAY);
        VARCACHE_CLASS = loadClass(Hooks.VARCACHE_CLASS);
        VARCACHE_ARRAY_FIELD = loadField(VARCACHE_CLASS, Hooks.VARCACHE_ARRAY_FIELD);
        INVENTORY_MGMT_CLASS = loadClass(Hooks.INVENTORY_MGMT_CLASS);
        INVENTORY_MGMT_CLASS_INHT_FIELD = loadField(INVENTORY_MGMT_CLASS, Hooks.INVENTORY_MGMT_CLASS_INHT_FIELD);
        INHT_CLASS = loadClass(Hooks.INHT_CLASS);
        INHT_GET_INVENTORY_METHOD = loadMethod(INHT_CLASS, Hooks.INHT_GET_INVENTORY_METHOD, long.class);
        INVENTORY_IDARR_FIELD = loadField(INVENTORY_MGMT_CLASS, Hooks.INVENTORY_IDARR_FIELD);
        INVENTORY_STACKSIZEARR_FIELD = loadField(INVENTORY_MGMT_CLASS, Hooks.INVENTORY_STACKSIZEARR_FIELD);
        MAP_RENDER_CLASS = loadClass(Hooks.MAP_RENDER_CLASS);
        MAP_RENDERER_GETTILES_METHOD = loadMethod(MAP_RENDER_CLASS, Hooks.MAP_RENDERER_GETTILES_METHOD);
        MAP_RENDERER_XMAX_FIELD = loadField(MAP_RENDER_CLASS, Hooks.MAP_RENDERER_XMAX_FIELD);
        MAP_RENDERER_YMAX_FIELD = loadField(MAP_RENDER_CLASS, Hooks.MAP_RENDERER_YMAX_FIELD);
        TILE_CLASS = loadClass(Hooks.TILE_CLASS);
        TILE_CLASS_GAMEOBJECT_NODE_FIELD = loadField(TILE_CLASS, Hooks.TILE_CLASS_GAMEOBJECT_NODE_FIELD);
        TILE_NODE_HELPER_CLASS = loadClass(Hooks.TILE_NODE_HELPER_CLASS);
        TILE_NODE_HELPER_GAMEOBJECT_METHOD = loadMethod(TILE_NODE_HELPER_CLASS, Hooks.TILE_NODE_HELPER_GAMEOBJECT_METHOD, TILE_CLASS);
        GAMEOBJECT_CLASS = loadClass(Hooks.GAMEOBJECT_CLASS);
        RUNELITE_TILE_HELPER_CLASS = loadClass(Hooks.RUNELITE_TILE_HELPER_CLASS);
        RUNELITE_TILE_HELPER_COMPARETILETOGAMEOBJ_METHOD = loadMethod(RUNELITE_TILE_HELPER_CLASS, Hooks.RUNELITE_TILE_HELPER_COMPARETILETOGAMEOBJ_METHOD, TILE_CLASS, GAMEOBJECT_CLASS);
        RUNELITE_HOOKS_CLASS = loadClass(Hooks.RUNELITE_HOOKS_CLASS);
        RUNELITE_ISLOGGEDIN_METHOD = loadMethod(RUNELITE_HOOKS_CLASS, Hooks.RUNELITE_ISLOGGEDIN_METHOD);
        RUNELITE_ISLOGGEDIN_ENUM_CLASS = loadClass(Hooks.RUNELITE_ISLOGGEDIN_ENUM_CLASS);
        RUNELITE_ISLOGGEDIN_ENUM_ID_FIELD = loadField(RUNELITE_ISLOGGEDIN_ENUM_CLASS, Hooks.RUNELITE_ISLOGGEDIN_ENUM_ID_FIELD);
        RUNELITE_NPCLIST_METHOD = loadMethod(RUNELITE_HOOKS_CLASS, Hooks.RUNELITE_NPCLIST_METHOD);
        RUNELITE_NPCARRAY_METHOD = loadMethod(RUNELITE_HOOKS_CLASS, Hooks.RUNELITE_NPCARRAY_METHOD);
        RUNELITE_GETINTERFACENODES_METHOD = loadMethod(RUNELITE_HOOKS_CLASS, Hooks.RUNELITE_GETINTERFACENODES_METHOD);
        RUNELITE_GET_MAPRENDERERCLASS_METHOD = loadMethod(RUNELITE_HOOKS_CLASS, Hooks.RUNELITE_GET_MAPRENDERERCLASS_METHOD);
        RUNELITE_GET_PLAYERPLANE_METHOD = loadMethod(RUNELITE_HOOKS_CLASS, Hooks.RUNELITE_GET_PLAYERPLANE_METHOD);
        RUNELITE_GET_PLAYERBASE_X_METHOD = loadMethod(RUNELITE_HOOKS_CLASS, Hooks.RUNELITE_GET_PLAYERBASE_X_METHOD);
        RUNELITE_GET_PLAYERBASE_Y_METHOD = loadMethod(RUNELITE_HOOKS_CLASS, Hooks.RUNELITE_GET_PLAYERBASE_Y_METHOD);
        INTERFACE_MANAGER_CLASS =  loadClass(Hooks.INTERFACE_MANAGER_CLASS);
        INTERFACE_CLASS = loadClass(Hooks.INTERFACE_CLASS);
        INTERFACE_RUNELITE_TABLE_CLASS = loadClass(Hooks.INTERFACE_RUNELITE_TABLE_CLASS);
        INTERFACE_RUNELITE_TABLE_ID_METHOD = loadMethod(INTERFACE_RUNELITE_TABLE_CLASS, Hooks.INTERFACE_RUNELITE_TABLE_ID_METHOD, INTERFACE_CLASS);
        INTERFACE_RUNELITE_TABLE_TEXT_METHOD = loadMethod(INTERFACE_RUNELITE_TABLE_CLASS, Hooks.INTERFACE_RUNELITE_TABLE_TEXT_METHOD, INTERFACE_CLASS);
        INTERFACE_RUNELITE_TABLE_HIDDEN_METHOD = loadMethod(INTERFACE_RUNELITE_TABLE_CLASS, Hooks.INTERFACE_RUNELITE_TABLE_HIDDEN_METHOD, INTERFACE_CLASS);
        INTERFACE_RUNELITE_TABLE_PARENTID_METHOD = loadMethod(INTERFACE_RUNELITE_TABLE_CLASS, Hooks.INTERFACE_RUNELITE_TABLE_PARENTID_METHOD, INTERFACE_CLASS);
        INTERFACE_MANAGER_GETS_METHOD = loadMethod(INTERFACE_MANAGER_CLASS, Hooks.INTERFACE_MANAGER_GETS_METHOD, INTERFACE_CLASS);
        INTERFACE_MANAGER_GETD_METHOD = loadMethod(INTERFACE_MANAGER_CLASS, Hooks.INTERFACE_MANAGER_GETD_METHOD, INTERFACE_CLASS);
        INTERFACE_MANAGER_GETN_METHOD = loadMethod(INTERFACE_MANAGER_CLASS, Hooks.INTERFACE_MANAGER_GETN_METHOD, INTERFACE_CLASS);
        GAMEOBJECT_INTERFACE_CLASS = loadClass(Hooks.GAMEOBJECT_INTERFACE_CLASS);
        GAMEOBJECT_INTERFACE_GETID_METHOD = loadMethod(GAMEOBJECT_INTERFACE_CLASS, Hooks.GAMEOBJECT_INTERFACE_GETID_METHOD);
        TIMERS_CLASS = loadClass(Hooks.TIMERS_CLASS);
        TIMERS_GETTIMER_METHOD = loadMethod(TIMERS_CLASS, Hooks.TIMERS_GETTIMER_METHOD, String.class);
        CLIENT_CLIENT_METHOD.setAccessible(true);
        CLIENT_PACKET_METHOD.setAccessible(true);
        CLIENT_COMMAND_METHOD.setAccessible(true);
        CLIENT_RESUME_P_NAMEDIALOG_METHOD.setAccessible(true);
        CLIENT_RESUME_P_COUNTDIALOG_METHOD.setAccessible(true);
        CLIENT_VARCACHE_FIELD.setAccessible(true);
        CLIENT_CURRENT_SKILL_LEVEL_ARRAY_FIELD.setAccessible(true);
        CLIENT_MAX_SKILL_LEVEL_ARRAY_FIELD.setAccessible(true);
        CLIENT_LOCALPLAYER_FIELD.setAccessible(true);
        NPC_ID_FIELD.setAccessible(true);
        NPC_NAME_METHOD.setAccessible(true);
        NPC_HP_METHOD.setAccessible(true);
        NPC_INFO_FIELD.setAccessible(true);
        NPC_INFO_CONTEXTMENU_FIELD.setAccessible(true);
        NPC_INFO_REALID_FIELD.setAccessible(true);
        ENTITY_FIELD_TARGETTED_NPC.setAccessible(true);
        ENTITY_FIELD_ANIMATION_ID.setAccessible(true);
        ENTITY_FIELD_XWAYPOINT_ARRAY.setAccessible(true);
        ENTITY_FIELD_YWAYPOINT_ARRAY.setAccessible(true);
        VARCACHE_ARRAY_FIELD.setAccessible(true);
        INVENTORY_MGMT_CLASS_INHT_FIELD.setAccessible(true);
        INHT_GET_INVENTORY_METHOD.setAccessible(true);
        INVENTORY_IDARR_FIELD.setAccessible(true);
        INVENTORY_STACKSIZEARR_FIELD.setAccessible(true);
        RUNELITE_TILE_HELPER_COMPARETILETOGAMEOBJ_METHOD.setAccessible(true);
        TILE_NODE_HELPER_GAMEOBJECT_METHOD.setAccessible(true);
        TILE_CLASS_GAMEOBJECT_NODE_FIELD.setAccessible(true);
        MAP_RENDERER_GETTILES_METHOD.setAccessible(true);
        MAP_RENDERER_XMAX_FIELD.setAccessible(true);
        MAP_RENDERER_YMAX_FIELD.setAccessible(true);
        RUNELITE_ISLOGGEDIN_METHOD.setAccessible(true);
        RUNELITE_ISLOGGEDIN_ENUM_ID_FIELD.setAccessible(true);
        RUNELITE_NPCLIST_METHOD.setAccessible(true);
        RUNELITE_NPCARRAY_METHOD.setAccessible(true);
        RUNELITE_GETINTERFACENODES_METHOD.setAccessible(true);
        RUNELITE_GET_MAPRENDERERCLASS_METHOD.setAccessible(true);
        RUNELITE_GET_PLAYERPLANE_METHOD.setAccessible(true);
        RUNELITE_GET_PLAYERBASE_X_METHOD.setAccessible(true);
        RUNELITE_GET_PLAYERBASE_Y_METHOD.setAccessible(true);
        INTERFACE_RUNELITE_TABLE_ID_METHOD.setAccessible(true);
        INTERFACE_RUNELITE_TABLE_TEXT_METHOD.setAccessible(true);
        INTERFACE_RUNELITE_TABLE_HIDDEN_METHOD.setAccessible(true);
        INTERFACE_RUNELITE_TABLE_PARENTID_METHOD.setAccessible(true);
        INTERFACE_MANAGER_GETS_METHOD.setAccessible(true);
        INTERFACE_MANAGER_GETD_METHOD.setAccessible(true);
        INTERFACE_MANAGER_GETN_METHOD.setAccessible(true);
        GAMEOBJECT_INTERFACE_GETID_METHOD.setAccessible(true);
        TIMERS_GETTIMER_METHOD.setAccessible(true);
    }


    private static Method loadMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws NoSuchMethodException {
        System.out.printf("Loading method %s(%s) from class %s%n",
                methodName,
                paramTypesToString(paramTypes),
                clazz.getName());
        Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method;
    }

    private static String paramTypesToString(Class<?>[] paramTypes) {
        if (paramTypes == null || paramTypes.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (Class<?> c : paramTypes) {
            sb.append(c.getSimpleName()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    private static Class<?> loadClass(String className) throws ClassNotFoundException {
        System.out.println("Loading class: " + className);
        return Class.forName(className);
    }

    private static Field loadField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        System.out.printf("Loading field %s from class %s%n", fieldName, clazz.getName());
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static ReflectionResult safeInvoke(Method method, Object instance, Object... args) {
        try {
            return new ReflectionResult(true, method.invoke(instance, args));
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Reflection invoke failed: " + e.getMessage());
            e.printStackTrace();
            return new ReflectionResult(false, null);
        }
    }

    public static ReflectionResult safeGet(Field field, Object instance) {
        try {
            return new ReflectionResult(true, field.get(instance));
        } catch (IllegalAccessException e) {
            System.err.println("Reflection get failed: " + e.getMessage());
            e.printStackTrace();
            return new ReflectionResult(false, null);
        }
    }


}
