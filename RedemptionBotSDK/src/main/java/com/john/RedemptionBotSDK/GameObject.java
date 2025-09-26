package com.john.RedemptionBotSDK;

import com.john.RedemptionBotSDK.util.ReflectionResult;

import java.util.ArrayList;
import java.util.List;

import static com.john.RedemptionBotSDK.util.ReflectionLoader.*;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.MAP_RENDERER_XMAX_FIELD;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.MAP_RENDERER_YMAX_FIELD;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.RUNELITE_TILE_HELPER_COMPARETILETOGAMEOBJ_METHOD;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.TILE_NODE_HELPER_GAMEOBJECT_METHOD;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.safeGet;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.safeInvoke;

public class GameObject {

    public Object gameObject;
    public int objectId;
    public int xPos;
    public int yPos;

    public GameObject(Object gameObject, int objectId, int xPos, int yPos){
        this.gameObject = gameObject;
        this.objectId = objectId;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    //Returns a list of all game objects within the current scene.
    public static List<GameObject> getAllObjects(){
        List<GameObject> gameObjects = new ArrayList<>();
        ReflectionResult mapRendererResult = safeInvoke(RUNELITE_GET_MAPRENDERERCLASS_METHOD,null);
        if (!mapRendererResult.success() || mapRendererResult.value() == null){
            System.err.println("getAllObjects: Could not get mapRendererResult");
            return null;
        }
        ReflectionResult mapTilesResult = safeInvoke(MAP_RENDERER_GETTILES_METHOD, mapRendererResult.value());
        if (!mapTilesResult.success() || mapTilesResult.value() == null){
            System.err.println("getAllObjects: Could not get mapTilesResult");
            return null;
        }
        ReflectionResult mapTileXmaxResult = safeGet(MAP_RENDERER_XMAX_FIELD, mapRendererResult.value());
        ReflectionResult mapTileYmaxResult = safeGet(MAP_RENDERER_YMAX_FIELD, mapRendererResult.value());
        if (!mapTileXmaxResult.success() || !mapTileYmaxResult.success() || mapTileXmaxResult.value() == null || mapTileYmaxResult.value() == null){
            System.err.println("getAllObjects: Could not get X or Y max");
            return null;
        }
        Integer playerPlane = LocalPlayer.getPlane();
        if (playerPlane == null) return null;
        for (int i=0; i < (int) mapTileXmaxResult.value(); i++){
            for (int j=0; j < (int) mapTileYmaxResult.value(); j++){
                Object mapTile = ((Object[][][]) mapTilesResult.value())[playerPlane][i][j];
                if (mapTile == null) continue;
                ReflectionResult gameObjectsOnTileResult = safeInvoke(TILE_NODE_HELPER_GAMEOBJECT_METHOD, null, mapTile);
                if (!gameObjectsOnTileResult.success()){
                    System.err.println("getAllObjects: Could not get gameObjectsOnTileResult");
                    return null;
                }
                Object[] gameObjectsOnTile = (Object[]) gameObjectsOnTileResult.value();
                if (gameObjectsOnTile != null){
                    for (Object o : gameObjectsOnTile){
                        if (o != null){
                            ReflectionResult isObjOnTile = safeInvoke(RUNELITE_TILE_HELPER_COMPARETILETOGAMEOBJ_METHOD, null, mapTile, o);
                            if (!isObjOnTile.success()){
                                System.err.println("getAllObjects: Could not get isObjOnTile");
                                return null;
                            }
                            if ((boolean) isObjOnTile.value()){
                                if (!GAMEOBJECT_INTERFACE_CLASS.isInstance(o)) continue;
                                Integer objectTileX = convertSceneXToTileX(i);
                                Integer objectTileY = convertSceneYToTileY(j);
                                if (objectTileX == null || objectTileY == null){continue;}
                                if (objectDistance(objectTileX, objectTileY) < 25){
                                    ReflectionResult objectId = safeInvoke(GAMEOBJECT_INTERFACE_GETID_METHOD, o);
                                    if (!objectId.success()){
                                        System.err.println("getAllObjects: Could not get objectId");
                                        return null;
                                    }

                                    GameObject newObject = new GameObject(o, (int) objectId.value(), objectTileX, objectTileY);
                                    gameObjects.add(newObject);
                                }
                            }

                        }
                    }
                }

            }
        }
        return gameObjects;
    }

    //Calculates the objects distance from a player.
    public static int objectDistance(int objTileX, int objTileY){
        Integer playerTileX = LocalPlayer.getTileX();
        Integer playerTileY = LocalPlayer.getTileY();
        if (playerTileX == null || playerTileY == null) return Integer.MAX_VALUE;
        int dx = objTileX - playerTileX;
        int dy = objTileY - playerTileY;
        return (int) Math.sqrt((dx*dx) + (dy*dy));
    }

    //Returns the first game object found in the objects array.
    public static GameObject findFirstGameObjectById(int objectId){
        List<GameObject> gameObjects = getAllObjects();
        if (gameObjects == null) return null;
        for (GameObject go : gameObjects){
            if (go.objectId == objectId){
                return go;
            }
        }
        return null;
    }

    //Returns the closest game object by objectId
    public static GameObject findClosestGameObjectById(int objectId){
        List<GameObject> gameObjects = getAllObjects();
        int closest = Integer.MAX_VALUE;
        GameObject closestObj = null;
        if (gameObjects == null) return null;
        for (GameObject go : gameObjects){
            if (go.objectId != objectId) continue;
            int distanceToPlayer = objectDistance(go.xPos, go.yPos);
            if (distanceToPlayer < closest){
                closestObj = go;
                closest = distanceToPlayer;
            }
        }
        return closestObj;
    }

    //Converts a scene x to a tile x coordinate.
    public static Integer convertSceneXToTileX(int sceneXCoordIn){
        Integer currentBaseX = LocalPlayer.getBaseX();
        if (currentBaseX == null){ return null;}
        return currentBaseX + sceneXCoordIn;
    }

    //Converts a scene y to a tile y coordinate.
    public static Integer convertSceneYToTileY(int sceneYCoordIn){
        Integer currentBaseY = LocalPlayer.getBaseY();
        if (currentBaseY == null){ return null;}
        return currentBaseY + sceneYCoordIn;
    }
}
