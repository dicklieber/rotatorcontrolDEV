package com.wa9nnn.rotator.ui;

import javax.swing.*;
import java.awt.event.*;

public class IdeaConfigDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField host;
    private JTextField name;

    public IdeaConfigDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void setData(RGC data) {
        name.setText(data.getName());
        host.setText(data.getHost());
    }

    public void getData(RGC data) {
        data.setName(name.getText());
        data.setHost(host.getText());
    }

    public boolean isModified(RGC data) {
        if (name.getText() != null ? !name.getText().equals(data.getName()) : data.getName() != null) return true;
        if (host.getText() != null ? !host.getText().equals(data.getHost()) : data.getHost() != null) return true;
        return false;
    }
}
