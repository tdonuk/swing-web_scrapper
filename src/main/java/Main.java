import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends JFrame {
    private Image img = null;
    private Icon icon = null;
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
    private String dir = "";
    private String logoName = "";
    private Website site;
    private Connection con;
    private String[] oldData = null;
    private Timer currencyTimer = new Timer(6000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            getCurrency(e);
        }
    });
    private Timer headersTimer = new Timer(10000, new ActionListener() {
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

    public Main() {
        initComponents();
        currencyTimer.setInitialDelay(0);
        currencyTimer.start();
    }

    private void initComponents() {
        //Defining initial components of the user interface
        //this field includes only graphical adjustments
        setDefaultCloseOperation(3);
        setSize(1500, 900); //Default size
        setMinimumSize(new Dimension(400, 600));
        setTitle("News Scraper");
        dir = System.getProperty("user.dir") + "\\resources\\";

        try {
            setIconImage(ImageIO.read(new File(dir + "main_icon.resources")));
        } catch (IOException ioexc) {
            JOptionPane.showMessageDialog(this, "Resource files not found. Please be sure that resources folder and exe file must be in the same folder", "File error", 0);
            System.exit(1);
        }

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2);

        topComponents = new JPanel(new GridLayout(1, 5, 10, 10));
        topComponents.setBackground(new Color(0xFFFDF6F6, true));
        topComponents.setBorder(BorderFactory.createLineBorder(Color.black));

        currency = new JLabel("Currency");
        currency.setForeground(Color.BLUE);
        usdLabel = new JLabel();
        euroLabel = new JLabel();
        interestLabel = new JLabel();
        gbpLabel = new JLabel();
        goldLabel = new JLabel();

        connectionButton = new JToggleButton("Connection");
        connectionButton.setEnabled(false);
        connectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getHeaders(e);
                headersTimer.setInitialDelay(10000);
                headersTimer.start();
            }
        });
        connectionButton.setForeground(new Color(0x247F22));
        connectionButton.setText("Connect");

        srcBox = new JComboBox();
        srcBox.removeAll();
        String[] boxContents = new String[]{"Haberturk", "CNN", "NTV", "Reuters", "Euronews"};
        String[] var3 = boxContents;

        int boxList = boxContents.length;

        for(int i = 0; i < boxList; ++i) {
            String s = var3[i];
            srcBox.addItem(s);
        }

        srcBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                srcBoxAction(e);
            }
        });

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
                    JOptionPane.showMessageDialog(connectButton, "Not possible to connect  " + site.getUrl(), "Connection error", 0);
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
        tabs.setBackground(new Color(-341921569, false));
        font = new Font("Arial", 1, 14);
        tabs.setFont(font);

        listPanel = new JPanel(new GridLayout(1, 1, 20, 20));
        list = new JList();
        listPane.setViewportView(listPanel);
        font = new Font("Arial", 1, 22);
        list.setFont(font);
        list.setForeground(new Color(-13621857, false));
        list.setBackground(new Color(0x0F8F3ED, false));
        list.setAutoscrolls(false);

        listPane.setViewportView(list);
        listPanel.add(listPane);

        tabs.addTab("Headers", listPanel);

        contentPanel = new JPanel(new BorderLayout(20, 5));

        content = new JTextArea();
        font = new Font("Arial", 1, 18);
        content.setFont(font);
        content.setText("");
        content.setWrapStyleWord(true);
        content.setLineWrap(true);
        content.setAutoscrolls(false);
        content.setBackground(list.getBackground());
        content.setEditable(false);

        currentHeaderLabel = new JLabel("");
        currentHeaderLabel.setLabelFor(content);
        currentHeaderLabel.setForeground(Color.BLUE);
        currentHeaderLabel.setFont(new Font("Arial", 1, 16));

        contentPane.setViewportView(content);
        contentPanel.add(currentHeaderLabel, "North");
        contentPanel.add(contentPane, "Center");

        tabs.addTab("Contents", contentPanel);
        tabs.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Content Area"));

        downComponents = new JPanel(new GridBagLayout());
        downComponents.setSize(getWidth() - 15, topComponents.getHeight());

        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExitButtonAction(e);
            }
        });

        connectButton = new JButton("Read More");
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ConnectButtonAction(e);
                } catch (IOException var3) {
                    JOptionPane.showMessageDialog(connectButton, "Not possible to connect  " + site.getUrl(), "Connection error", 0);
                } catch (URISyntaxException var4) {
                    JOptionPane.showMessageDialog(connectButton, "Not possible to connect  " + site.getUrl(), "Connection error", 0);
                }

            }
        });

        examineButton = new JButton("Examine");
        examineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExamineButtonAction(e);
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.05D;
        c.weighty = 1.0D;
        c.fill = 1;
        c.anchor = 17;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 15, 0);
        downComponents.add(examineButton, c);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 15, 5);
        c.anchor = 17;
        downComponents.add(connectButton, c);
        c.anchor = 13;
        c.weightx = 1.0D;
        c.gridx = 2;
        c.gridy = 0;
        c.fill = 3;
        downComponents.add(exitButton, c);

        body = new JPanel();
        body.setLayout(new GridBagLayout());
        body.setBackground(new Color(0xF0F0FF));

        statusPanel.setBackground(body.getBackground());
        downComponents.setBackground(body.getBackground());

        GridBagConstraints gbc = new GridBagConstraints();

        Insets insets = new Insets(0, 5, 0, 5);
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.weightx = 1;
        gbc.ipady = 20;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        body.add(topComponents,gbc);

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
        gbc.ipady = 15;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        body.add(downComponents,gbc);

        add(body);
    }

    public static void main(String[] args) {
        final Main main = new Main();
        main.setVisible(true);
    }

    private void getCurrency(ActionEvent e) {
        Color green = new Color(0x1DAA1D);
        Color red = new Color(0xFF0000);

        site = new Currency();
        con = new Connection(site);
        oldUsd = cur[0];
        oldEuro = cur[1];
        oldInterest = cur[2];
        oldGbp = cur[3];
        oldGold = cur[4];

        try {
            System.out.println("yenileniyor..");
            cur = con.getCurrency();
        } catch (IOException var4) {
            JOptionPane.showMessageDialog(null, "Not possible to connect Currency service", "Connection error", 0);
        }

        if (cur[0] >= oldUsd) {
            usdLabel.setForeground(green);
        } else {
            usdLabel.setForeground(red);
        }

        usdLabel.setText("$ : " + cur[0]);


        if (cur[1] >= oldEuro) {
            euroLabel.setForeground(green);
        } else {
            euroLabel.setForeground(red);
        }

        euroLabel.setText("€ : " + cur[1]);


        if (cur[2] >= oldInterest) {
            interestLabel.setForeground(green);
        } else  {
            interestLabel.setForeground(red);
        }

        interestLabel.setText("Int % : " + cur[2]);


        if (cur[3] >= oldInterest) {
            gbpLabel.setForeground(green);
        } else  {
            gbpLabel.setForeground(red);
        }

        gbpLabel.setText("Pound : " + cur[3]);


        if (cur[4] >= oldInterest) {
            goldLabel.setForeground(green);
        } else  {
            goldLabel.setForeground(red);
        }

        goldLabel.setText("Gold (Gr) : " + cur[4]);

    }

    private void getHeaders(ActionEvent e) {
        if (connectionButton.isSelected()) {
            Connection con = new Connection(site);

            try {
                con.getNews();
                oldData = site.getHeaders();
            } catch (IOException var9) {
                JOptionPane.showMessageDialog(this, "Not possible to connect  " + site.getUrl(), "Connection error", 0);
                return;
            }

            if (list.getModel().getSize() <= 1) {
                list.setListData(site.getHeaders());
            } else {
                int i = 0;
                boolean check = true;
                String[] var5 = oldData;
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String s = var5[var7];
                    if (!s.equals(list.getModel().getElementAt(i).toString())) {
                        check = false;
                    }

                    ++i;
                }

                if (!check) {
                    list.setListData(site.getHeaders());
                }
            }

            connectionButton.setForeground(new Color(9515047));
            connectionButton.setText("Disconnect");
            srcBox.setEnabled(false);
        } else {
            String[] nullData = new String[]{""};
            list.setListData(nullData);
            content.setText("");
            currentHeaderLabel.setText("");
            connectionButton.setForeground(new Color(4886847));
            connectionButton.setText("Connect");
            srcBox.setEnabled(true);
            tabs.setSelectedIndex(0);
        }

    }

    private void srcBoxAction(ActionEvent e) {
        dir = System.getProperty("user.dir") + "\\resources\\";
        int index = srcBox.getSelectedIndex();
        String logoName = "";
        switch(index) {
            case 0:
                logoName = "ht_logo.resources";
                site = new Haberturk();
                break;
            case 1:
                logoName = "cnn_logo.resources";
                site = new Cnn();
                break;
            case 2:
                logoName = "NTV_logo.resources";
                site = new Ntv();
                break;
            case 3:
                logoName = "reut_logo.resources";
                site = new Reuters();
                break;
            case 4:
                logoName = "eun_logo.resources";
                site = new Euronews();
        }

        dir = dir + logoName;

        try {
            img = ImageIO.read(new File(dir));
            icon = new ImageIcon(img.getScaledInstance(40, 30, 4));
            connectionButton.setEnabled(true);
        } catch (IOException var5) {
            JOptionPane.showMessageDialog(this, logoName + " not found.", "File error", 0);
            System.exit(1);
        }

        logoLabel.setIcon(icon);
    }

    private void ExamineButtonAction(ActionEvent e) {
        try {
            Connection con = new Connection(site);
            con.getContents(list.getSelectedIndex());
            String c = site.getContent();
            content.setText(c);
            content.setCaretPosition(0);
            currentHeaderLabel.setText(list.getSelectedValue().toString());
            tabs.setSelectedIndex(1);
        } catch (IOException var4) {
            System.out.println("connection error");
        }

    }

    private void ExitButtonAction(ActionEvent e) {
        System.exit(1);
    }

    private void ConnectButtonAction(ActionEvent e) throws IOException, URISyntaxException {
        String s = site.getUrl() + site.getContentUrl()[list.getSelectedIndex()];
        con.connect(s);
    }

    public void logoMouserEntered(MouseEvent e) {
        setCursor(Cursor.HAND_CURSOR);
    }

    public void logoMouserExited(MouseEvent e) {
        setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void logoMouserClicked(MouseEvent e) throws IOException, URISyntaxException {
        con.connect(site.getUrl());
    }
}
