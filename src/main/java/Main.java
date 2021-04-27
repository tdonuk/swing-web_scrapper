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
    private JLabel currentHeaderLabel;
    private JTextArea content;
    private String dir = "";
    private String logoName = "";
    private Website site;
    private Connection con;
    private String[] oldData = null;
    private Timer currencyTimer = new Timer(2000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Main.this.getCurrency(e);
        }
    });
    private Timer headersTimer = new Timer(8000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Main.this.getHeaders(e);
        }
    });
    private float oldUsd = 0.0F;
    private float oldEuro = 0.0F;
    private float[] cur = new float[2];

    public Main() {
        this.initComponents();
        this.currencyTimer.setInitialDelay(0);
        this.currencyTimer.start();
    }

    private void initComponents() {
        //Defining initial components of the user interface
        //this field includes only graphical adjustments
        this.setDefaultCloseOperation(3);
        this.setSize(1600, 900); //Default size
        this.setMinimumSize(new Dimension(400, 600));
        this.setTitle("News Scrapper");
        this.dir = System.getProperty("user.dir") + "\\resources\\";

        try {
            this.setIconImage(ImageIO.read(new File(this.dir + "main_logo.resources")));
        } catch (IOException var7) {
            JOptionPane.showMessageDialog(this, "Resource files not  found", "File error", 0);
            System.exit(1);
        }

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - this.getWidth()) / 2, (d.height - this.getHeight()) / 2);
        this.body = new JPanel();
        this.body.setLayout((LayoutManager)null);
        this.body.setBackground(new Color(0xFFFFF8F8, true));
        this.topComponents = new JPanel(new GridLayout(1, 5, 10, 10));
        this.topComponents.setSize(this.getWidth() - 18, 60);
        this.topComponents.setLocation(1, 5);
        this.topComponents.setBackground(new Color(0xFFFDF6F6, true));
        this.topComponents.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Source selection"));
        this.currency = new JLabel("Currency");
        this.currency.setForeground(Color.BLUE);
        this.usdLabel = new JLabel();
        this.euroLabel = new JLabel();
        this.connectionButton = new JToggleButton("Connection");
        this.connectionButton.setEnabled(false);
        this.connectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.this.getHeaders(e);
                Main.this.headersTimer.setInitialDelay(8000);
                Main.this.headersTimer.start();
            }
        });
        this.connectionButton.setForeground(new Color(0x247F22));
        this.connectionButton.setText("Connect");
        this.srcBox = new JComboBox();
        this.srcBox.removeAll();
        String[] boxContents = new String[]{"Haberturk", "CNN", "NTV", "Reuters", "Euronews"};
        String[] var3 = boxContents;
        int var4 = boxContents.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String s = var3[var5];
            this.srcBox.addItem(s);
        }

        this.srcBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.this.srcBoxAction(e);
            }
        });
        this.topComponents.add(this.srcBox);
        this.topComponents.add(this.currency);
        this.topComponents.add(this.usdLabel);
        this.topComponents.add(this.euroLabel);
        this.topComponents.add(this.connectionButton);
        this.logoPanel = new JPanel(new FlowLayout());
        this.logoPanel.setSize(this.getWidth()/2, 140);
        this.logoPanel.setLocation(0, this.topComponents.getY() + this.topComponents.getHeight() + 5);
        this.logoPanel.setBackground(this.body.getBackground());
        this.logoLabel = new JLabel();
        this.logoLabel.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                try {
                    Main.this.logoMouserClicked(e);
                } catch (Exception var3) {
                    JOptionPane.showMessageDialog(Main.this.connectButton, "Not possible to connect  " + Main.this.site.getUrl(), "Connection error", 0);
                }

            }

            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Main.this.logoMouserEntered(e);
            }

            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Main.this.logoMouserExited(e);
            }
        });
        Font font = new Font("Arial", 1, 20);
        this.logoPanel.add(this.logoLabel);
        this.listPane = new JScrollPane();
        this.contentPane = new JScrollPane();
        this.tabs = new JTabbedPane();
        this.tabs.removeAll();
        this.tabs.setSize(this.getWidth() - 10, (this.getHeight() - 100) / 2 + 70);
        this.tabs.setLocation(0, this.logoPanel.getY() + this.logoPanel.getHeight() + 5);
        this.tabs.setBackground(new Color(-341921569, true));
        font = new Font("Arial", 1, 14);
        this.tabs.setFont(font);
        this.listPanel = new JPanel(new GridLayout(1, 1, 20, 20));
        this.list = new JList();
        this.listPane.setViewportView(this.listPanel);
        font = new Font("Arial", 1, 22);
        this.list.setFont(font);
        this.list.setForeground(new Color(-13621857, true));
        this.list.setBackground(new Color(0xFFFFEAEA, true));
        this.list.setAutoscrolls(false);
        this.listPane.setViewportView(this.list);
        this.listPanel.add(this.listPane);
        this.tabs.addTab("Headers", this.listPanel);
        this.contentPanel = new JPanel(new BorderLayout(20, 5));
        this.content = new JTextArea();
        font = new Font("Arial", 1, 18);
        this.content.setFont(font);
        this.content.setText("");
        this.content.setWrapStyleWord(true);
        this.content.setLineWrap(true);
        this.content.setAutoscrolls(false);
        this.content.setBackground(this.list.getBackground());
        this.content.setEditable(false);
        this.currentHeaderLabel = new JLabel("");
        this.currentHeaderLabel.setLabelFor(this.content);
        this.currentHeaderLabel.setForeground(Color.BLUE);
        this.contentPane.setViewportView(this.content);
        this.currentHeaderLabel.setFont(new Font("Arial", 1, 16));
        this.contentPanel.add(this.currentHeaderLabel, "North");
        this.contentPanel.add(this.contentPane, "Center");
        this.tabs.addTab("Contents", this.contentPanel);
        this.tabs.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Content Area"));
        this.downComponents = new JPanel(new GridBagLayout());
        this.downComponents.setBackground(this.topComponents.getBackground());
        this.downComponents.setSize(this.getWidth() - 15, this.topComponents.getHeight());
        this.downComponents.setLocation(0, this.getHeight() - 90);
        this.exitButton = new JButton("Exit");
        this.exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.this.ExitButtonAction(e);
            }
        });
        this.connectButton = new JButton("Read More");
        this.connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Main.this.ConnectButtonAction(e);
                } catch (IOException var3) {
                    JOptionPane.showMessageDialog(Main.this.connectButton, "Not possible to connect  " + Main.this.site.getUrl(), "Connection error", 0);
                } catch (URISyntaxException var4) {
                    JOptionPane.showMessageDialog(Main.this.connectButton, "Not possible to connect  " + Main.this.site.getUrl(), "Connection error", 0);
                }

            }
        });
        this.examineButton = new JButton("Examine");
        this.examineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.this.ExamineButtonAction(e);
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.05D;
        c.weighty = 1.0D;
        c.fill = 1;
        c.anchor = 17;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 5, 15, 0);
        this.downComponents.add(this.examineButton, c);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(15, 5, 15, 5);
        c.anchor = 17;
        this.downComponents.add(this.connectButton, c);
        c.anchor = 13;
        c.weightx = 1.0D;
        c.gridx = 2;
        c.gridy = 0;
        c.fill = 3;
        this.downComponents.add(this.exitButton, c);
        this.body.add(this.topComponents);
        this.body.add(this.logoPanel);
        this.body.add(this.tabs);
        this.body.add(this.downComponents);
        this.add(this.body);
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

    private void timerActionCurrency(ActionEvent e) {
        this.getCurrency(e);
    }

    private void timerActionHeaders(ActionEvent e) {
        this.getHeaders(e);
    }

    private void getCurrency(ActionEvent e) {
        Website site = new Currency();
        this.con = new Connection(site);
        this.oldUsd = this.cur[0];
        this.oldEuro = this.cur[1];

        try {
            this.cur = this.con.getCurrency();
        } catch (IOException var4) {
            JOptionPane.showMessageDialog(this, "Not possible to connect  " + site.getUrl(), "Connection error", 0);
        }

        if (this.cur[0] > this.oldUsd) {
            this.usdLabel.setForeground(new Color(2069513));
            this.usdLabel.setText("$ : " + this.cur[0]);
        } else if (this.cur[0] < this.oldUsd) {
            this.usdLabel.setForeground(new Color(10032399));
            this.usdLabel.setText("$ : " + this.cur[0]);
        }

        if (this.cur[1] > this.oldEuro) {
            this.euroLabel.setForeground(new Color(2069513));
            this.euroLabel.setText("€ : " + this.cur[1]);
        } else if (this.cur[1] < this.oldEuro) {
            this.euroLabel.setForeground(new Color(10032399));
            this.euroLabel.setText("€ : " + this.cur[1]);
        }

    }

    private void getHeaders(ActionEvent e) {
        if (this.connectionButton.isSelected()) {
            Connection con = new Connection(this.site);

            try {
                con.getNews();
                this.oldData = this.site.getHeaders();
            } catch (IOException var9) {
                JOptionPane.showMessageDialog(this, "Not possible to connect  " + this.site.getUrl(), "Connection error", 0);
                return;
            }

            if (this.list.getModel().getSize() <= 1) {
                this.list.setListData(this.site.getHeaders());
            } else {
                int i = 0;
                boolean check = true;
                String[] var5 = this.oldData;
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String s = var5[var7];
                    if (!s.equals(this.list.getModel().getElementAt(i).toString())) {
                        check = false;
                    }

                    ++i;
                }

                if (!check) {
                    this.list.setListData(this.site.getHeaders());
                }
            }

            this.connectionButton.setForeground(new Color(9515047));
            this.connectionButton.setText("Disconnect");
            this.srcBox.setEnabled(false);
        } else {
            String[] nullData = new String[]{""};
            this.list.setListData(nullData);
            this.content.setText("");
            this.currentHeaderLabel.setText("");
            this.connectionButton.setForeground(new Color(4886847));
            this.connectionButton.setText("Connect");
            this.srcBox.setEnabled(true);
            this.tabs.setSelectedIndex(0);
        }

    }

    private void srcBoxAction(ActionEvent e) {
        this.dir = System.getProperty("user.dir") + "\\resources\\";
        int index = this.srcBox.getSelectedIndex();
        String logoName = "";
        switch(index) {
            case 0:
                logoName = "ht_logo.resources";
                this.site = new Haberturk();
                break;
            case 1:
                logoName = "cnn_logo.resources";
                this.site = new Cnn();
                break;
            case 2:
                logoName = "NTV_logo.resources";
                this.site = new Ntv();
                break;
            case 3:
                logoName = "reut_logo.resources";
                this.site = new Reuters();
                break;
            case 4:
                logoName = "eun_logo.resources";
                this.site = new Euronews();
        }

        this.dir = this.dir + logoName;

        try {
            this.img = ImageIO.read(new File(this.dir));
            this.icon = new ImageIcon(this.img.getScaledInstance(400, this.logoPanel.getHeight(), 4));
            this.connectionButton.setEnabled(true);
        } catch (IOException var5) {
            JOptionPane.showMessageDialog(this, logoName + " not found.", "File error", 0);
            System.exit(1);
        }

        this.logoLabel.setIcon(this.icon);
    }

    private void frameResizedAction(ComponentEvent e) {
        this.tabs.setSize(this.getWidth() - 10, (this.getHeight() - 100) / 2 + 70);
        this.tabs.updateUI();
        this.topComponents.setSize(this.getWidth() - 18, this.topComponents.getHeight());
        this.topComponents.updateUI();
        this.logoPanel.setSize(this.getWidth(), this.logoPanel.getHeight());
        this.logoPanel.updateUI();
        this.downComponents.setLocation(0, this.getHeight() - 90);
        this.downComponents.setSize(this.getWidth() - 15, this.downComponents.getHeight());
        this.downComponents.updateUI();
    }

    private void ExamineButtonAction(ActionEvent e) {
        try {
            Connection con = new Connection(this.site);
            con.getContents(this.list.getSelectedIndex());
            String c = this.site.getContent();
            this.content.setText(c);
            this.content.setCaretPosition(0);
            this.currentHeaderLabel.setText(this.list.getSelectedValue().toString());
            this.tabs.setSelectedIndex(1);
        } catch (IOException var4) {
            System.out.println("connection error");
        }

    }

    private void ExitButtonAction(ActionEvent e) {
        System.exit(1);
    }

    private void ConnectButtonAction(ActionEvent e) throws IOException, URISyntaxException {
        String s = this.site.getUrl() + this.site.getContentUrl()[this.list.getSelectedIndex()];
        this.con.connect(s);
    }

    public void logoMouserEntered(MouseEvent e) {
        this.setCursor(12);
    }

    public void logoMouserExited(MouseEvent e) {
        this.setCursor(0);
    }

    public void logoMouserClicked(MouseEvent e) throws IOException, URISyntaxException {
        this.con.connect(this.site.getUrl());
    }
}
