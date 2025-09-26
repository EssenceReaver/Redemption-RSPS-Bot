package com.john;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ScriptGUI implements ScriptListener {
    public JPanel scriptGUI;
    public final JComboBox<String> scriptComboBox;
    public final JButton refreshButton;
    public final JButton playButton;
    public final JButton stopButton;
    public final ScriptManager scriptManager;

    public ScriptGUI() {

        scriptManager = new ScriptManager();
        scriptManager.setListener(this);
        scriptComboBox = new JComboBox<>(new String[]{});
        scriptComboBox.setPreferredSize(new Dimension(300, 25));

        refreshButton = new JButton();
        playButton = new JButton();
        stopButton = new JButton();

        refreshButton.putClientProperty("JButton.buttonType", "toolBarButton");
        refreshButton.setFocusable(false);
        refreshButton.setIcon(new FlatSVGIcon("icons/restart.svg", 16, 16));
        refreshButton.setPreferredSize(new Dimension(30, 30));
        refreshButton.addActionListener(e -> loadScripts());

        playButton.putClientProperty("JButton.buttonType", "toolBarButton");
        playButton.setFocusable(false);
        playButton.setIcon(new FlatSVGIcon("icons/run.svg", 16, 16));
        playButton.setPreferredSize(new Dimension(30, 30));
        playButton.addActionListener(e -> startScript());

        stopButton.putClientProperty("JButton.buttonType", "toolBarButton");
        stopButton.setFocusable(false);
        stopButton.setIcon(new FlatSVGIcon("icons/stop.svg", 16, 16));
        stopButton.setPreferredSize(new Dimension(30, 30));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopScript());

        scriptGUI = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scriptGUI.add(scriptComboBox);
        scriptGUI.add(refreshButton);
        scriptGUI.add(playButton);
        scriptGUI.add(stopButton);

    }

    private void loadScripts() {
        File scriptsDir = new File("scripts"); // relative to your working directory
        String[] scripts = scriptsDir.list((dir, name) -> name.toLowerCase().endsWith(".jar"));

        scriptComboBox.removeAllItems();
        if (scripts != null) {
            for (String script : scripts) {
                scriptComboBox.addItem(script);
            }
        }
    }

    private void startScript() {
        String selectedScript = (String) scriptComboBox.getSelectedItem();
        scriptManager.runScript(selectedScript);
    }

    private void stopScript() {
        scriptManager.stopScript();
    }

    @Override
    public void onScriptStarted() {
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
        scriptComboBox.setEnabled(false);
        refreshButton.setEnabled(false);
    }

    @Override
    public void onScriptStopped() {
        playButton.setEnabled(true);
        stopButton.setEnabled(false);
        scriptComboBox.setEnabled(true);
        refreshButton.setEnabled(true);
        scriptManager.cleanup();
    }
}
