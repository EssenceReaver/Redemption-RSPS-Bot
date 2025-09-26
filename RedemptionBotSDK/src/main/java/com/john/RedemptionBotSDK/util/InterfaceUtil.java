package com.john.RedemptionBotSDK.util;

import javax.swing.tree.DefaultMutableTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static com.john.RedemptionBotSDK.util.ReflectionLoader.INTERFACE_RUNELITE_TABLE_HIDDEN_METHOD;
import static com.john.RedemptionBotSDK.util.ReflectionLoader.safeInvoke;

public class InterfaceUtil {

    public static List<Object> getAllOpenInterfaces(){
        DefaultMutableTreeNode openInterfaces = new DefaultMutableTreeNode();
        ReflectionResult topLevelInterfaceNodesResult = safeInvoke(ReflectionLoader.RUNELITE_GETINTERFACENODES_METHOD,null);
        if (!topLevelInterfaceNodesResult.success() || topLevelInterfaceNodesResult.value() == null){
            System.err.println("getAllOpenInterfaces: Could not get openInterfaces");
            return Collections.emptyList();
        }
        Object[] topLevelInterfaceNodes = (Object[]) topLevelInterfaceNodesResult.value();
        for (Object om : topLevelInterfaceNodes){
            DefaultMutableTreeNode openInterface = recurseInterface(om);
            if (openInterface != null){
                openInterfaces.add(openInterface);
            }
        }
        return convertTreeToList(openInterfaces);
    }


    public static DefaultMutableTreeNode recurseInterface(Object omObject){
        if (omObject == null){
            return null;
        }

        ReflectionResult omIsVisibleResult = safeInvoke(INTERFACE_RUNELITE_TABLE_HIDDEN_METHOD, null, omObject);
        if (!omIsVisibleResult.success() || omIsVisibleResult.value() == null){
            System.err.println("recurseInterface: Could not get omIsVisibleResult");
            return null;
        }

        if (((boolean) omIsVisibleResult.value())){
            return null;
        }

        DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(omObject);
        ReflectionResult dChildResult = safeInvoke(ReflectionLoader.INTERFACE_MANAGER_GETD_METHOD, null, omObject);
        if (!dChildResult.success()){
            System.err.println("recurseInterface: Could not get dChildResult");
            return null;
        }
        Object[] dChildArr = (Object[]) dChildResult.value();
        if (dChildArr != null){
            for (Object om : dChildArr){
                DefaultMutableTreeNode defaultMutableTreeNode1 = recurseInterface(om);
                if (defaultMutableTreeNode1 != null){
                    defaultMutableTreeNode.add(defaultMutableTreeNode1);
                }
            }
        }
        ReflectionResult sChildResult = safeInvoke(ReflectionLoader.INTERFACE_MANAGER_GETS_METHOD, null, omObject);
        if (!sChildResult.success()){
            System.err.println("recurseInterface: Could not get sChildResult");
            return null;
        }
        Object[] sChildArr = (Object[]) sChildResult.value();
        if (sChildArr != null){
            for (Object om : sChildArr){
                DefaultMutableTreeNode defaultMutableTreeNode1 = recurseInterface(om);
                if (defaultMutableTreeNode1 != null){
                    defaultMutableTreeNode.add(defaultMutableTreeNode1);
                }
            }
        }

        ReflectionResult nChildResult = safeInvoke(ReflectionLoader.INTERFACE_MANAGER_GETN_METHOD, null, omObject);
        if (!nChildResult.success()){
            System.err.println("recurseInterface: Could not get nChildResult");
            return null;
        }
        Object[] nChildArr = (Object[]) nChildResult.value();
        if (nChildArr != null){
            for (Object om : nChildArr){
                DefaultMutableTreeNode defaultMutableTreeNode1 = recurseInterface(om);
                if (defaultMutableTreeNode1 != null){
                    defaultMutableTreeNode.add(defaultMutableTreeNode1);
                }
            }
        }
        return defaultMutableTreeNode;
    }

    public static List<Object> convertTreeToList(DefaultMutableTreeNode root) {
        List<Object> values = new ArrayList<>();
        Enumeration<?> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            Object element = e.nextElement();
            if (element instanceof DefaultMutableTreeNode node) {
                Object nodeObj = node.getUserObject();
                if (nodeObj != null){
                    values.add(node.getUserObject());
                }
            }
        }
        return values;
    }
}
