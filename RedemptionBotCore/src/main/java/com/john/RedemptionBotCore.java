package com.john;
import com.john.RedemptionBotSDK.util.ReflectionLoader;

import javax.swing.*;
import java.awt.*;
import java.lang.instrument.Instrumentation;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

public class RedemptionBotCore {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            ReflectionLoader.init();
            loadScriptGUI();

        } catch (Throwable t) {
            System.err.println("[BotCore] Fatal error during initialization");
            t.printStackTrace();
        }
    }

    public static void loadScriptGUI() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatOneDarkIJTheme());
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Script GUI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ScriptGUI form = new ScriptGUI();
            frame.setContentPane(form.scriptGUI);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}