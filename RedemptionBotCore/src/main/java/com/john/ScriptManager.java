package com.john;

import com.john.RedemptionBotSDK.script.Script;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScriptManager {
    private ScriptThread scriptThread;
    private Script currentScript;
    private URLClassLoader currentClassLoader;
    private ScriptListener listener;

    public void setListener(ScriptListener listener) {
        this.listener = listener;
    }

    public synchronized void runScript(String jarName) {
        if (scriptThread != null) {
            System.err.println("A script is already running. Stop it first.");
            return;
        }

        if (jarName == null) {
            System.err.println("Jar name was null");
            return;
        }

        File jarFile = new File("scripts", jarName);
        if (!jarFile.exists()) {
            System.err.println("Jar file not found: " + jarName);
            return;
        }

        try {
            currentClassLoader = new URLClassLoader(
                    new URL[]{jarFile.toURI().toURL()},
                    this.getClass().getClassLoader()
            );

            String mainClassName = findScriptClass(jarFile, currentClassLoader);
            if (mainClassName == null) {
                System.err.println("No class implementing Script found in " + jarFile.getName());
                closeClassLoader();
                return;
            }

            Class<?> clazz = currentClassLoader.loadClass(mainClassName);
            Object scriptInstance = clazz.getDeclaredConstructor().newInstance();

            if (!(scriptInstance instanceof Script)) {
                System.err.println("Class " + mainClassName + " does not implement Script.");
                closeClassLoader();
                return;
            }

            currentScript = (Script) scriptInstance;
            scriptThread = new ScriptThread(currentScript, listener);
            scriptThread.start();

            if (listener != null) listener.onScriptStarted();

        } catch (Exception e) {
            e.printStackTrace();
            cleanup();
        }
    }

    public synchronized void stopScript() {
        if (scriptThread != null && scriptThread.isAlive()) {
            scriptThread.stopRunning();
            scriptThread.interrupt();
        }
    }

    public synchronized void cleanup() {
        currentScript = null;
        closeClassLoader();
        scriptThread = null;
    }

    private void closeClassLoader() {
        if (currentClassLoader != null) {
            try {
                currentClassLoader.close();
            } catch (Exception ignored) {
            }
            currentClassLoader = null;
        }
    }

    private String findScriptClass(File jarFile, ClassLoader loader) {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");

                    try {
                        Class<?> clazz = loader.loadClass(className);
                        if (Script.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                            return className;
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
