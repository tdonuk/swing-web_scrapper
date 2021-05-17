import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class SourceManageService extends JFrame {
    private JPanel mainPanel,contentPanel;
    private JScrollPane scrollPaneLog,scrollPaneList;
    private JButton confirmButton;
    private JTextArea infoLabel;
    private ArrayList<Website> sources,preferredSources;
    private SourceParser sourceParser;
    private GUI parent;

    public SourceManageService(GUI parent) {
        this.parent = parent;

        sourceParser = new SourceParser();

        sources = sourceParser.getSources();

        initComponents();
        this.toFront();

        if(sourceParser.isFirstTime()) showFirstTimeMessage();
    }

    private void initComponents() {
        this.setSize(800,900);
        this.setResizable(false);
        this.getContentPane().setBackground(new Color(0xEFDBDB));
        this.setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10,0));
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(sources.size(),1,5,10));
        contentPanel.setPreferredSize(new Dimension(400,400));

        scrollPaneList = new JScrollPane(contentPanel);
        scrollPaneList.setPreferredSize(new Dimension(400,300));

        preferredSources = new ArrayList<Website>();

        infoLabel = new JTextArea();
        infoLabel.setText("You can find and select current supported source list below.");
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
            panel.setBackground(new Color(0xFFF7F7));
            panel.setPreferredSize(new Dimension(100,60));
            panel.setLayout(new GridLayout(1,4));

            label = new JLabel(new ImageIcon(w.getImageFile().getScaledInstance(80,60,4)));
            checkBox = new JCheckBox();
            checkBox.setText("Click to select "+w.getName());
            checkBox.setPreferredSize(new Dimension(80,40));
            checkBox.setFont(new Font(checkBox.getFont().getName(),Font.BOLD,15));
            checkBox.addActionListener(e -> {
                checkBoxAction(e,w);
            });

            panel.add(checkBox);
            panel.add(label);

            contentPanel.add(panel);
        }
        confirmButton = new JButton("Confirm");
        confirmButton.setBackground(new Color(0x067906));
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
            JOptionPane.showMessageDialog(this,"Please select at least one source","Empty Source List",JOptionPane.ERROR_MESSAGE);
            return;
        }
        parent.toFront();
        parent.setEnabled(true);

        if(sourceParser.isFirstTime()) {
            if(sourceParser.isFirstTime()) {
                String firstTimeText = "Your preferred sources is set now. You are ready to use LastNews." +
                        " Click the menu box\non the top left corner to open drop down source list and then start a connection";
                JOptionPane.showMessageDialog(parent,firstTimeText,"You Are Ready",JOptionPane.INFORMATION_MESSAGE);
            }
        }

        sourceParser.setPreferredSources(this.preferredSources);

        synchronized (parent.lock) {
            parent.lock.notifyAll();
        }

        this.dispose();

    }

    private void checkBoxAction(ActionEvent e, Website w) {
        String text = "Preferred Sources: ";
        if(preferredSources.contains(w)) {
            preferredSources.remove(w);
            infoLabel.setText("log: "+w.getName()+" is removed from preferred sources"+"\n"+infoLabel.getText());
            infoLabel.setCaretPosition(0);
        } else if(!preferredSources.contains(w)){
            preferredSources.add(w);
            infoLabel.setText("log: "+w.getName()+" is added to preferred sources"+"\n"+infoLabel.getText());
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
        String firstTimeText = "Welcome to LastNews! Please select sources you interested in. " +
                "This is a first time setup so you should not have to\ndo this again." +
                " However, you can adjust your preferred sources anytime you want through menu -> source management." +
                "\n\nThank you for using LastNews!";

        JOptionPane.showMessageDialog(this,firstTimeText,"Welcome!",JOptionPane.INFORMATION_MESSAGE);
        this.toFront();
    }
}
