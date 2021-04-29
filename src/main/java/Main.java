import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.Timer;

public class Main extends JFrame {
    private Image img = null;
    private Icon icon = null;
    private JPanel body;
    private JPanel topComponents;
    private JPanel logoPanel;
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
    private JLabel currentHeaderLabel;
    private JTextArea content;
    private String dir = "";
    private String logoName = "";
    private Website site;
    private Connection con;
    private String[] oldData = null;
    private Timer currencyTimer = new Timer(2000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            getCurrency(e);
        }
    });
    private Timer headersTimer = new Timer(8000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            getHeaders(e);
        }
    });
    private float oldUsd = 0.0F;
    private float oldEuro = 0.0F;
    private float oldInterest = 0.0F;
    private float[] cur = new float[3];

    public Main() {
        initComponents();
        currencyTimer.setInitialDelay(0);
        currencyTimer.start();
    }

    private void initComponents() {
        //Defining initial components of the user interface
        //this field includes only graphical adjustments
        setDefaultCloseOperation(3);
        setSize(1600, 900); //Default size
        setMinimumSize(new Dimension(400, 600));
        setTitle("News Scrapper");
        dir = System.getProperty("user.dir") + "\\resources\\";

        try {
            setIconImage(ImageIO.read(new File(dir + "main_logo.resources")));
        } catch (IOException var7) {
            JOptionPane.showMessageDialog(this, "Resource files not  found", "File error", 0);
            System.exit(1);
        }

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2);
        body = new JPanel();
        body.setLayout(null);
        body.setBackground(new Color(0xFFFFF8F8, true));
        topComponents = new JPanel(new GridLayout(1, 5, 10, 10));
        topComponents.setSize(getWidth() - 18, 60);
        topComponents.setLocation(1, 5);
        topComponents.setBackground(new Color(0xFFFDF6F6, true));
        topComponents.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Source selection"));
        currency = new JLabel("Currency");
        currency.setForeground(Color.BLUE);
        usdLabel = new JLabel();
        euroLabel = new JLabel();
        interestLabel = new JLabel();
        connectionButton = new JToggleButton("Connection");
        connectionButton.setEnabled(false);
        connectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getHeaders(e);
                headersTimer.setInitialDelay(8000);
                headersTimer.start();
            }
        });
        connectionButton.setForeground(new Color(0x247F22));
        connectionButton.setText("Connect");
        srcBox = new JComboBox();
        srcBox.removeAll();
        String[] boxContents = new String[]{"Haberturk", "CNN", "NTV", "Reuters", "Euronews"};
        String[] var3 = boxContents;
        int var4 = boxContents.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String s = var3[var5];
            srcBox.addItem(s);
        }

        srcBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                srcBoxAction(e);
            }
        });
        topComponents.add(srcBox);
        topComponents.add(currency);
        topComponents.add(usdLabel);
        topComponents.add(euroLabel);
        topComponents.add(interestLabel);
        topComponents.add(connectionButton);
        logoPanel = new JPanel(new FlowLayout());
        logoPanel.setSize(getWidth()/2, 140);
        logoPanel.setLocation(0, topComponents.getY() + topComponents.getHeight() + 5);
        logoPanel.setBackground(body.getBackground());
        logoLabel = new JLabel();
        logoLabel.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                try {
                    logoMouserClicked(e);
                } catch (Exception var3) {
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
        Font font = new Font("Arial", 1, 20);
        logoPanel.add(logoLabel);
        listPane = new JScrollPane();
        contentPane = new JScrollPane();
        tabs = new JTabbedPane();
        tabs.removeAll();
        tabs.setSize(getWidth() - 10, (getHeight() - 100) / 2 + 100);
        tabs.setLocation(0, logoPanel.getY() + logoPanel.getHeight() + 5);
        tabs.setBackground(new Color(-341921569, true));
        font = new Font("Arial", 1, 14);
        tabs.setFont(font);
        listPanel = new JPanel(new GridLayout(1, 1, 20, 20));
        list = new JList();
        listPane.setViewportView(listPanel);
        font = new Font("Arial", 1, 22);
        list.setFont(font);
        list.setForeground(new Color(-13621857, true));
        list.setBackground(new Color(0xFFFFEAEA, true));
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
        contentPane.setViewportView(content);
        currentHeaderLabel.setFont(new Font("Arial", 1, 16));
        contentPanel.add(currentHeaderLabel, "North");
        contentPanel.add(contentPane, "Center");
        tabs.addTab("Contents", contentPanel);
        tabs.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Content Area"));
        downComponents = new JPanel(new GridBagLayout());
        downComponents.setBackground(topComponents.getBackground());
        downComponents.setSize(getWidth() - 15, topComponents.getHeight());
        downComponents.setLocation(0, getHeight() - 90);
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
        body.add(topComponents);
        body.add(logoPanel);
        body.add(tabs);
        body.add(downComponents);
        add(body);
    }

    public static void main(String[] args) {
        final Main main = new Main();
        main.setVisible(true);
        main.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                main.frameResizedAction(e);
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    private void timerActionHeaders(ActionEvent e) {
        getHeaders(e);
    }

    private void getCurrency(ActionEvent e) {

        Website site = new Currency();
        con = new Connection(site);
        oldUsd = cur[0];
        oldEuro = cur[1];
        oldInterest = cur[2];

        Color green = new Color(2069513);
        Color red = new Color(10032399);

        try {
            cur = con.getCurrency();
        } catch (IOException var4) {
            JOptionPane.showMessageDialog(this, "Not possible to connect Currency service", "Connection error", 0);
        }

        if (cur[0] > oldUsd) {
            usdLabel.setForeground(green);
            usdLabel.setText("$ : " + cur[0]);
        } else {
            usdLabel.setForeground(red);
            usdLabel.setText("$ : " + cur[0]);
        }

        if (cur[1] > oldEuro) {
            euroLabel.setForeground(green);
            euroLabel.setText("€ : " + cur[1]);
        } else  {
            euroLabel.setForeground(red);
            euroLabel.setText("€ : " + cur[1]);
        }

        if (cur[2] > oldInterest) {
            interestLabel.setForeground(green);
            interestLabel.setText("Int %: " + cur[2]);
        } else  {
            interestLabel.setForeground(red);
            interestLabel.setText("Int %: " + cur[2]);
        }
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
            icon = new ImageIcon(img.getScaledInstance(400, logoPanel.getHeight(), 4));
            connectionButton.setEnabled(true);
        } catch (IOException var5) {
            JOptionPane.showMessageDialog(this, logoName + " not found.", "File error", 0);
            System.exit(1);
        }

        logoLabel.setIcon(icon);
    }

    private void frameResizedAction(ComponentEvent e) {
        tabs.setSize(getWidth() - 10, (getHeight() - 100) / 2 + 190);
        tabs.updateUI();
        topComponents.setSize(getWidth() - 18, topComponents.getHeight());
        topComponents.updateUI();
        logoPanel.setSize(getWidth(), logoPanel.getHeight());
        logoPanel.updateUI();
        downComponents.setLocation(0, getHeight() - 90);
        downComponents.setSize(getWidth() - 15, downComponents.getHeight());
        downComponents.updateUI();
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
        setCursor(12);
    }

    public void logoMouserExited(MouseEvent e) {
        setCursor(0);
    }

    public void logoMouserClicked(MouseEvent e) throws IOException, URISyntaxException {
        con.connect(site.getUrl());
    }
}
