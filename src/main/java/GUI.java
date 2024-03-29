import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import util.*;
import util.dto.*;
import util.dto.Label;

public class GUI extends JFrame {
    private JPanel body;
    private JPanel topComponents;
    private JPanel statusPanel;
    private JPanel listPanel;
    private JPanel contentPanel;
    private JPanel downComponents;
    private JScrollPane listPane;
    private JScrollPane contentPane;
    private JList list;
    private JButton exitButton;
    private JButton connectButton;
    private JButton examineButton;
    private JButton shareButton;
    private JToggleButton connectionButton;
    private JComboBox srcBox;
    private JTabbedPane tabs;
    private JLabel currency;
    private JLabel usdLabel;
    private JLabel euroLabel;
    private JLabel logoLabel;
    private JLabel interestLabel;
    private JLabel gbpLabel;
    private JLabel goldLabel;
    private JLabel currentHeaderLabel;
    private JLabel statusLabel;
    private JTextArea content;
    private Website site;
    private ArrayList<Website> preferredSources;
    private ArrayList<Header> headers;
    private String dir = "";
    private Connection con;
    private SourceParser sourceParser;
    private JMenuBar menuBar;
    private JMenu menu,themeMenu;
    private JMenuItem manageSourcesItem,lightModeItem,darkModeItem, languageItem;
    private SourceManageService sms;
    public Object lock = new Object(); //Will be used to stop the thread
    public LanguageHandler lh;
    private HashMap<Label,String> labels;

    private Timer currencyTimer = new Timer(6000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            getCurrency(e);
        }
    });
    private Timer headersTimer = new Timer(20000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            getHeaders(e);
        }
    });


    //These are used to check changes in currency values
    private float oldUsd = 0.0F;
    private float oldEuro = 0.0F;
    private float oldInterest = 0.0F;
    private float oldGbp = 0.0F;
    private float oldGold = 0.0F;
    private float[] cur = new float[5];

    private JButton temp = new JButton();

    public GUI() throws IOException {
        sourceParser = new SourceParser();
        lh = new LanguageHandler();
        con = new Connection();

        if(sourceParser.isFirstTime()) {
            askForLanguage();
            sourceSetup();
        } else {
            //Language initializing
            lh.setLangFile(new File(lh.getPreferredLang()+".lang"));
            lh.setStrings();
            labels = lh.getTexts();

            //Source preferences initializing
            preferredSources = sourceParser.getPreferredSources();
            this.setVisible(true);
        }

        initComponents();
    }

    private void askForLanguage() {
        String langFileName;

        Object[] options = new Object[]{"Türkçe","English"};
        langFileName = (String) JOptionPane.showInputDialog(null,"Please select language","Language",JOptionPane.QUESTION_MESSAGE,null,options,options[0]);

        if(null == langFileName) System.exit(1);

        if(langFileName.equals("Türkçe")) {
            langFileName = "tr.lang";
        }
        else if(langFileName.equals("English")) {
            langFileName = "en.lang";
        }

        lh.setLangFile(new File(langFileName));

        lh.setPreferredLang();
        lh.setStrings();

        labels = lh.getTexts();
    }

    private void srcBoxSetup() {
        if(null != preferredSources && !preferredSources.isEmpty()) {
            srcBox.removeAllItems();
            preferredSources.forEach(w -> srcBox.addItem(w.getName()));
            srcBox.setSelectedIndex(0);
        }
    }

    private void sourceSetup() {
        if(!sourceParser.isFirstTime()) { //then there must be no currently established connection
            connectionButtonState(false);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                GUI.this.setEnabled(false);

                showSourcesFrame();

                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                }

                sourceParser = new SourceParser();

                if(null != preferredSources){
                    preferredSources.clear();
                }
                preferredSources = sourceParser.getPreferredSources();

                if(preferredSources.isEmpty()) return;

                srcBoxSetup();

                GUI.this.setEnabled(true);

                GUI.this.toFront();
                GUI.this.setVisible(true);
            }
        });
        t.start();
    }

    private void langSetup() {
        askForLanguage();
        String message = labels.get(Label.LANGUAGE_CHANGED);
        JOptionPane.showMessageDialog(null,message,"Language Changed",JOptionPane.INFORMATION_MESSAGE);
        System.exit(1);
    }

    private void initComponents() throws IOException {
        //Defining initial components of the user interface
        //this field includes only graphical adjustments
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1500, 900); //Default size
        setMinimumSize(new Dimension(400, 600));
        setTitle("LastNews");
        dir = System.getProperty("user.dir") + "\\resources\\";

        try {
            setIconImage(ImageIO.read(new File(dir + "main_icon.resources")));
        } catch (IOException ioexc) {
            String message = "main_icon.resources "+labels.get(Label.FILE_NOT_FOUND)+"  ("+ Label.FILE_NOT_FOUND.getCode() +")";
            JOptionPane.showMessageDialog(this, message, "File error", 0);
            System.exit(1);
        }

        menu = new JMenu(labels.get(Label.MENU));
        themeMenu = new JMenu(labels.get(Label.THEME));

        languageItem = new JMenuItem(labels.get(Label.LANGUAGE));
        languageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                langSetup();
            }
        });

        manageSourcesItem = new JMenuItem(labels.get(Label.SOURCE_MANAGEMENT));
        manageSourcesItem.addActionListener(e -> {
            sourceSetup();
        });

        lightModeItem = new JMenuItem(labels.get(Label.LIGHT));
        lightModeItem.addActionListener(e -> {
            applyLightMode();
        });

        darkModeItem = new JMenuItem(labels.get(Label.DARK));
        darkModeItem.addActionListener(e->{
            applyDarkMode();
        });

        themeMenu.add(lightModeItem);
        themeMenu.add(darkModeItem);

        menuBar = new JMenuBar();

        Font menuFont = new Font(menuBar.getFont().getName(), Font.BOLD, 14);

        manageSourcesItem.setFont(menuFont);
        lightModeItem.setFont(menuFont);
        darkModeItem.setFont(menuFont);
        themeMenu.setFont(menuFont);
        languageItem.setFont(menuFont);
        menu.setFont(menuFont);

        menu.add(languageItem);
        menu.add(manageSourcesItem);
        menu.add(themeMenu);
        menuBar.setBackground(new Color(0xBFBFD2));
        menuBar.setBorderPainted(true);
        menuBar.add(menu);

        this.setJMenuBar(menuBar);

        //To make frame show up at center of the screen
        this.setLocationRelativeTo(null);

        topComponents = new JPanel(new GridLayout(1, 5, 10, 10));

        currency = new JLabel();
        currency.setForeground(Color.BLUE);
        usdLabel = new JLabel();
        euroLabel = new JLabel();
        interestLabel = new JLabel();
        gbpLabel = new JLabel();
        goldLabel = new JLabel();

        srcBox = new JComboBox();
        Font srcFont = new Font(srcBox.getFont().getName(),Font.BOLD,17);
        srcBox.removeAll();
        srcBox.setFocusable(false);
        srcBoxSetup();
        srcBox.setSelectedIndex(-1);
        srcBox.setFont(srcFont);
        srcBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                srcBoxAction(e);
            }
        });

        currency.setFont(srcFont);

        connectionButton = new JToggleButton();
        connectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getHeaders(e);
                if(srcBox.getSelectedIndex() != -1) {
                    headersTimer.setInitialDelay(10000);
                    headersTimer.start();
                }
            }
        });
        connectionButton.setFont(srcFont);

        statusPanel = new JPanel(new FlowLayout());
        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.RIGHT);

        topComponents.add(srcBox);
        topComponents.add(currency);
        topComponents.add(usdLabel);
        topComponents.add(euroLabel);
        topComponents.add(gbpLabel);
        topComponents.add(goldLabel);
        topComponents.add(interestLabel);
        topComponents.add(logoLabel);
        topComponents.add(connectionButton);

        logoLabel.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                try {
                    logoMouserClicked(e);
                } catch (Exception exc) {
                    String message = labels.get(Label.LOGO_LABEL_CONNECTION_ERROR+" ") + site.getMainUrl() + " ("+Label.LOGO_LABEL_CONNECTION_ERROR.getCode()+")";
                    JOptionPane.showMessageDialog(connectButton,message, "Connection error", 0);
                }

            }

            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                logoMouserEntered(e);
            }

            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                logoMouserExited(e);
            }
        });
        Font font;

        listPane = new JScrollPane();
        contentPane = new JScrollPane();

        tabs = new JTabbedPane();
        tabs.removeAll();
        font = new Font("Arial", 1, 14);
        tabs.setFont(font);

        listPanel = new JPanel(new GridLayout(1, 1, 20, 20));
        list = new JList();
        listPane.setViewportView(listPanel);
        font = new Font("Arial", 1, 22);
        list.setFont(font);
        list.setAutoscrolls(false);

        listPane.setViewportView(list);
        listPanel.add(listPane);

        tabs.addTab(labels.get(Label.HEADERS_LABEL), listPanel);

        contentPanel = new JPanel(new BorderLayout(20, 5));

        content = new JTextArea();
        font = new Font("Arial", 1, 18);
        content.setFont(font);
        content.setText("");
        content.setWrapStyleWord(true);
        content.setLineWrap(true);
        content.setAutoscrolls(false);
        content.setEditable(false);

        currentHeaderLabel = new JLabel("");
        currentHeaderLabel.setLabelFor(content);
        currentHeaderLabel.setFont(new Font("Arial", 1, 16));

        contentPane.setViewportView(content);
        contentPanel.add(currentHeaderLabel, "North");
        contentPanel.add(contentPane, "Center");

        tabs.addTab(labels.get(Label.CONTENTS_LABEL), contentPanel);
        tabs.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        downComponents = new JPanel(new GridBagLayout());
        downComponents.setPreferredSize(new Dimension(getWidth() - 15, topComponents.getHeight()));

        exitButton = new JButton();
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExitButtonAction(e);
            }
        });

        connectButton = new JButton();
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ConnectButtonAction(e);
                } catch (Exception a) {
                    String message = labels.get(Label.CONTENT_LINK_ERROR + "  ("+Label.CONTENT_LINK_ERROR.getCode()+")");
                    JOptionPane.showMessageDialog(null,message,"Connection Error",JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        examineButton = new JButton();
        examineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExamineButtonAction(e);
            }
        });

        shareButton = new JButton();
        shareButton.addActionListener(e -> {
            shareButtonAction(e);
        });

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.05D;
        c.weighty = 1.0D;
        c.fill = 1;
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 0);
        downComponents.add(examineButton, c);

        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        downComponents.add(connectButton, c);

        c.gridx = 2;
        c.gridy = 0;
        downComponents.add(shareButton , c);

        c.anchor = 13;
        c.weightx = 1.0D;
        c.gridx = 3;
        c.gridy = 0;
        c.fill = 3;
        downComponents.add(exitButton, c);

        body = new JPanel();
        body.setLayout(new GridBagLayout());

        statusPanel.setBackground(body.getBackground());
        downComponents.setBackground(body.getBackground());

        GridBagConstraints gbc = new GridBagConstraints();

        Insets insets = new Insets(0, 0, 5, 0);
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.weightx = 1;
        gbc.ipady = 20;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        body.add(topComponents,gbc);

        gbc.insets = new Insets(0,0,0,0);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.CENTER;
        //body.add(statusPanel,gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        body.add(tabs,gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.ipady = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 0, 0);
        body.add(downComponents,gbc);

        add(body);

        currencyTimer.setInitialDelay(0);
        currencyTimer.start();

        applyDarkMode();
        setLabels();
    }

    private void setLabels() {
        srcBox.setToolTipText(labels.get(Label.COMBOBOX_TOOLTIP));

        connectionButton.setText(labels.get(Label.CONNECTION_CONNECT_TEXT));
        connectionButton.setToolTipText(labels.get(Label.CONNECTION_TOOLTIP));

        examineButton.setText(labels.get(Label.EXAMINE_TEXT));
        connectButton.setText(labels.get(Label.READ_MORE_TEXT));
        shareButton.setText(labels.get(Label.SHARE_TEXT));
        exitButton.setText(labels.get(Label.EXIT));

        currency.setText(labels.get(Label.CURRENCY));
    }

    public static void main(String[] args) throws IOException {

        try {
            UIManager.LookAndFeelInfo[] lafList = UIManager.getInstalledLookAndFeels();
            int length = lafList.length;

            for(int i = 0; i < length; ++i) {
                UIManager.LookAndFeelInfo info = lafList[i];
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {

        }

        final GUI main = new GUI();

        SourceParser sp = new SourceParser();

        main.setVisible(true);
    }

    private void getCurrency(ActionEvent e) {
        Color green = new Color(0x1DAA1D);
        Color red = new Color(0xFF0000);

        oldUsd = cur[0];
        oldEuro = cur[1];
        oldInterest = cur[2];
        oldGbp = cur[3];
        oldGold = cur[4];

        try {
            cur = con.getCurrency();
        } catch (IOException var4) {
            String message = labels.get(Label.CURRENCY_CONNECTION_ERROR) + " (" + Label.CURRENCY_CONNECTION_ERROR +")";
            JOptionPane.showMessageDialog(null, message, "Connection error", JOptionPane.ERROR_MESSAGE);
        }

        if (cur[0] >= oldUsd) {
            usdLabel.setForeground(green);
        } else {
            usdLabel.setForeground(red);
        }

        usdLabel.setText(labels.get(Label.USD_LABEL) + " : " + cur[0]);


        if (cur[1] >= oldEuro) {
            euroLabel.setForeground(green);
        } else {
            euroLabel.setForeground(red);
        }

        euroLabel.setText(labels.get(Label.EURO_LABEL) +" : " + cur[1]);


        if (cur[2] >= oldInterest) {
            interestLabel.setForeground(green);
        } else  {
            interestLabel.setForeground(red);
        }

        interestLabel.setText(labels.get(Label.INT_LABEL) +" : " + cur[2]);


        if (cur[3] >= oldGbp) {
            gbpLabel.setForeground(green);
        } else  {
            gbpLabel.setForeground(red);
        }

        gbpLabel.setText(labels.get(Label.POUND_LABEL) +" : " + cur[3]);


        if (cur[4] >= oldGold) {
            goldLabel.setForeground(green);
        } else  {
            goldLabel.setForeground(red);
        }

        goldLabel.setText(labels.get(Label.GOLD_LABEL)+" : " + cur[4]);

    }

    private void getHeaders(ActionEvent e) {
        if(null == site) { //Then user did not select any source
            String message = labels.get(Label.SOURCE_NOT_SELECTED) + "  ("+ Label.SOURCE_NOT_SELECTED.getCode() +")";
            JOptionPane.showMessageDialog(this,message, "Error",JOptionPane.ERROR_MESSAGE);
            connectionButton.setSelected(false);
            return;
        }
        if (connectionButton.isSelected()) {
            connectionButtonState(true);
            con.getNews(site);
            headers = site.getHeaders();

            if(null == headers) {
                JOptionPane.showMessageDialog(this,"Unknown error: "+srcBox.getSelectedItem().toString(), "Error",JOptionPane.ERROR_MESSAGE);
                connectionButtonState(false);
                return;
            }

            int i = 0;
            String listData [] = new String[headers.size()];
            for(Header h : headers) {
                listData[i] = h.getHeader();
                i++;
            }

            list.setListData(listData);

        }else {
            connectionButtonState(false);
        }

    }

    private void connectionButtonState(boolean isSelected) {
        connectionButton.setSelected(isSelected); //this will be used by sourceFrame. when user wants to choose new sources, current established connections must be finished
        if(isSelected) {
            connectionButton.setForeground(new Color(0xDE1C08));
            connectionButton.setText(labels.get(Label.CONNECTION_DISCONNECT_TEXT));
            srcBox.setEnabled(false);
        }
        if(!isSelected) {
            String[] nullData = new String[]{""};
            list.setListData(nullData);
            content.setText("");
            currentHeaderLabel.setText("");
            connectionButton.setForeground(new Color(4886847));
            connectionButton.setText(labels.get(Label.CONNECTION_CONNECT_TEXT));
            srcBox.setEnabled(true);
            tabs.setSelectedIndex(0);
        }
    }

    private void srcBoxAction(ActionEvent e) {
        if(null == srcBox.getSelectedItem()) return;

        dir = System.getProperty("user.dir") + "\\resources\\";
        int index = srcBox.getSelectedIndex();

        /*Here, the data in the srcBox is already taken from the parameter 'preferredSources'
         *so, the item selected from srcBox is obviously exists in 'preferredSources'.
         */
        //Initializing the parameter 'site' by name that user sent from srcBox (JComboBox)
        for(Website w : preferredSources) {
            if(w.getName().equals(srcBox.getSelectedItem().toString())) {
                site = w;
            }
        }

        if(srcBox.getSelectedIndex() != -1) {
            logoLabel.setToolTipText(labels.get(Label.LOGO_LABEL_TOOLTIP)+" "+site.getMainUrl());
        }

        logoLabel.setIcon(new ImageIcon(site.getImageFile().getScaledInstance(40,30,5)));
    }

    private void ExamineButtonAction(ActionEvent e) {
        String selectedTitle = list.getSelectedValue().toString();
        currentHeaderLabel.setText(selectedTitle);

        tabs.setSelectedIndex(1);

        Header selectedHeader = null;
        for(Header h : headers) {
            if(selectedTitle.equals(h.getHeader())) {
                selectedHeader = h;
            }
        }

        con.getContent(selectedHeader,site);

        String titleLabel = labels.get(Label.CONTENTS_TITLE);
        String detailsLabel = labels.get(Label.CONTENTS_DETAILS);
        content.setText("\n"+titleLabel+"\n\n"+selectedHeader.getContentHeader()+"\n\n"+detailsLabel+"\n\n"+selectedHeader.getContentDetails());

        content.setCaretPosition(0);
    }

    private void ExitButtonAction(ActionEvent e) {
        System.exit(1);
    }

    private void shareButtonAction(ActionEvent e) {
        if(srcBox.getSelectedIndex() == -1) return;
        if(null == headers) return;
        if(headers.isEmpty()) return;

        Header header = null;

        if(tabs.getSelectedIndex() == 0) {
            for(Header h : headers) {
                if(h.getHeader().equals(list.getSelectedValue())) {
                    header = h;
                }
            }
        }
        else if(tabs.getSelectedIndex() == 1) {
            for(Header h : headers) {
                if(h.getHeader().equals(currentHeaderLabel.getText())) {
                    header = h;
                }
            }
        }


        if(null == header) {
            String message = labels.get(Label.HEADER_NOT_SELECTED_ERROR) + "  (" + Label.HEADER_NOT_SELECTED_ERROR.getCode() +")";
            JOptionPane.showMessageDialog(this, message,"Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        Clipboard clb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(header.getLink());

        clb.setContents(ss,null);

        String message = labels.get(Label.SHARE_BUTTON_MESSAGE);
        JOptionPane.showMessageDialog(this,message,"Share",JOptionPane.INFORMATION_MESSAGE);
    }

    private void ConnectButtonAction(ActionEvent e) throws IOException, URISyntaxException {
        Header toBrowse = null;
        if(tabs.getSelectedIndex() == 0) {
            for(Header h : headers) {
                if(list.getSelectedValue().toString().equals(h.getHeader())) {
                    toBrowse = h;
                }
            }
        } else {
            for(Header h : headers) {
                if(currentHeaderLabel.getText().equals(h.getHeader())) {
                    toBrowse = h;
                }
            }
        }

      con.browse(toBrowse);
    }

    public void applyLightMode() {
        topComponents.setBackground(new Color(0xF8F3F3));
        topComponents.setBorder(BorderFactory.createLineBorder(Color.black));

        connectionButton.setBackground(new Color(0xCCDAF6));
        connectionButton.setForeground(new Color(0x185C0A));

        currency.setForeground(new Color(0x2020A3));

        srcBox.setBackground(connectionButton.getBackground());
        srcBox.setForeground(Color.black);

        tabs.setBackground(new Color(0xFFE6E6));
        tabs.setForeground(Color.blue);
        tabs.setBorder(BorderFactory.createLineBorder(Color.black));

        list.setForeground(new Color(0x2952BA));
        list.setBackground(new Color(0xFFFFFF));

        content.setBackground(list.getBackground());
        content.setForeground(new Color(0x090909));

        currentHeaderLabel.setForeground(Color.BLUE);
        contentPanel.setBackground(content.getBackground());

        body.setBackground(new Color(0x4A67B1));

        downComponents.setBackground(topComponents.getBackground());

        examineButton.setBackground(new Color(0xC1C1C1));
        examineButton.setForeground(new Color(0x1C1C1C));
        connectButton.setBackground(examineButton.getBackground());
        connectButton.setForeground(examineButton.getForeground());
        exitButton.setBackground(examineButton.getBackground());
        exitButton.setForeground(examineButton.getForeground());
        shareButton.setBackground(examineButton.getBackground());
        shareButton.setForeground(examineButton.getForeground());
    }

    public void applyDarkMode() {
        topComponents.setBackground(new Color(0x062242, false));
        topComponents.setBorder(BorderFactory.createLineBorder(Color.darkGray));

        connectionButton.setBackground(new Color(0x202045));
        connectionButton.setForeground(new Color(0x4CD249));

        currency.setForeground(new Color(0x7979E8));

        srcBox.setBackground(connectionButton.getBackground());
        srcBox.setForeground(new Color(0xC68312));

        tabs.setBackground(new Color(0x19452A, false));
        tabs.setForeground(Color.blue);
        tabs.setBorder(BorderFactory.createLineBorder(Color.darkGray));

        list.setForeground(new Color(0xC68312));
        list.setBackground(new Color(0x232323, false));

        content.setBackground(list.getBackground());
        content.setForeground(new Color(0xA5A5A5));

        currentHeaderLabel.setForeground(Color.BLUE);
        contentPanel.setBackground(content.getBackground());

        body.setBackground(new Color(0x000000));

        downComponents.setBackground(topComponents.getBackground());

        examineButton.setBackground(new Color(0x2F2F2F));
        examineButton.setForeground(new Color(0xE0E0E0));
        connectButton.setBackground(examineButton.getBackground());
        connectButton.setForeground(examineButton.getForeground());
        exitButton.setBackground(examineButton.getBackground());
        exitButton.setForeground(examineButton.getForeground());
        shareButton.setBackground(examineButton.getBackground());
        shareButton.setForeground(examineButton.getForeground());
    }

    public void showSourcesFrame() {
        sms = new SourceManageService(this.lock,this.labels);
        sms.toFront();
    }

    public void logoMouserEntered(MouseEvent e) {
        if(!(null == site)) setCursor(Cursor.HAND_CURSOR);
    }

    public void logoMouserExited(MouseEvent e) {
        if(!(null == site)) setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void logoMouserClicked(MouseEvent e) throws IOException, URISyntaxException {
        if(!(null == site)) Desktop.getDesktop().browse(new URL(site.getMainUrl()).toURI());
    }
}
