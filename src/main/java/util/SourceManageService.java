package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import util.dto.*;
import util.dto.Label;

public class SourceManageService extends JFrame {
    private JPanel mainPanel,contentPanel;
    private JScrollPane scrollPaneLog,scrollPaneList;
    private JButton confirmButton;
    private JTextArea infoLabel;
    private ArrayList<Website> sources;
    private ArrayList<Website> preferredSources;
    private SourceParser sourceParser;
    private Object lock;
    private HashMap<Label,String> labels;

    public SourceManageService(Object lock, HashMap<Label,String> labels) {
        this.labels = labels;
        this.lock = lock;

        sourceParser = new SourceParser();

        sources = sourceParser.getSources();

        initComponents();
        this.toFront();

        if(sourceParser.isFirstTime()) showFirstTimeMessage();
    }

    private void initComponents() {
        this.setSize(800,900);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(0x000000));
        mainPanel.setLayout(new BorderLayout(10,0));
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(sources.size(),1,5,10));
        contentPanel.setPreferredSize(new Dimension(400,400));

        scrollPaneList = new JScrollPane(contentPanel);
        contentPanel.setBackground(mainPanel.getBackground());
        scrollPaneList.setPreferredSize(new Dimension(400,300));

        preferredSources = new ArrayList<Website>();

        infoLabel = new JTextArea();
        infoLabel.setText("~Log~");
        infoLabel.setLineWrap(true);
        infoLabel.setWrapStyleWord(true);
        infoLabel.setEditable(false);
        infoLabel.setBackground(contentPanel.getBackground());
        infoLabel.setForeground(Color.blue);
        infoLabel.setFont(new Font(infoLabel.getFont().getName(),Font.ITALIC,15));
        infoLabel.setPreferredSize(new Dimension(400,100));

        scrollPaneLog = new JScrollPane(infoLabel);

        JPanel panel;
        JLabel label;
        JCheckBox checkBox;
        for(Website w : sources) {
            panel = new JPanel();
            panel.setBackground(new Color(0x000C23));
            panel.setPreferredSize(new Dimension(100,60));
            panel.setLayout(new GridLayout(1,4));

            label = new JLabel(new ImageIcon(w.getImageFile().getScaledInstance(80,60,4)));
            checkBox = new JCheckBox();
            checkBox.setText(labels.get(Label.CHECKBOX_LABEL)+": "+w.getName());
            checkBox.setForeground(Color.LIGHT_GRAY);
            checkBox.setPreferredSize(new Dimension(80,40));
            checkBox.setFont(new Font(checkBox.getFont().getName(),Font.BOLD,15));
            checkBox.addActionListener(e -> {
                checkBoxAction(e,w);
            });

            panel.add(checkBox);
            panel.add(label);

            contentPanel.add(panel);
        }
        confirmButton = new JButton();
        confirmButton.setText(labels.get(Label.CONFIRM));
        confirmButton.setBackground(new Color(0x24A746));
        confirmButton.setPreferredSize(new Dimension(70,50));
        confirmButton.addActionListener(e->{
            confirmAction(e);
        });
        contentPanel.add(confirmButton);

        mainPanel.add(scrollPaneList,BorderLayout.CENTER);
        mainPanel.add(confirmButton,BorderLayout.PAGE_END);
        mainPanel.add(scrollPaneLog,BorderLayout.PAGE_START);

        this.add(mainPanel);
        this.setVisible(true);
    }

    private void confirmAction(ActionEvent e) {
        if(this.preferredSources.isEmpty()) {
            String message = labels.get(Label.EMPTY_PREFERENCES_ERROR) + "  ("+ Label.EMPTY_PREFERENCES_ERROR.getCode() + ")";
            JOptionPane.showMessageDialog(this,message,"Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(sourceParser.isFirstTime()) {
            if(sourceParser.isFirstTime()) {
                String message = labels.get(Label.FIRST_TIME_MESSAGE);
                JOptionPane.showMessageDialog(null,message,"Ready",JOptionPane.INFORMATION_MESSAGE);
            }
        }

        sourceParser.setPreferredSources(this.preferredSources);

        synchronized (this.lock) {
            lock.notifyAll();
        }

        this.dispose();

    }

    private void checkBoxAction(ActionEvent e, Website w) {
        String text = " ";
        if(preferredSources.contains(w)) {
            preferredSources.remove(w);
            String message = labels.get(Label.LOG_SOURCE_ADDED);
            infoLabel.setText("log: "+w.getName()+ " " + message +"\n"+infoLabel.getText());
            infoLabel.setCaretPosition(0);
        } else if(!preferredSources.contains(w)){
            preferredSources.add(w);
            String message = labels.get(Label.LOG_SOURCE_REMOVED);
            infoLabel.setText("log: "+w.getName()+ " " + message +"\n"+infoLabel.getText());
            infoLabel.setCaretPosition(0);
        }

        if(preferredSources.isEmpty()){
            this.setTitle("");
            return;
        }

        for(Website site : preferredSources) {
            text = text + site.getName() + ", ";
        }
        int lastComma = text.lastIndexOf(",");
        text = text.substring(0,lastComma);
        this.setTitle(text);
    }

    public void showFirstTimeMessage() {
        String message = labels.get(Label.WELCOME_MESSAGE);

        JOptionPane.showMessageDialog(this,message,"Welcome !",JOptionPane.INFORMATION_MESSAGE);
        this.toFront();
    }
}
