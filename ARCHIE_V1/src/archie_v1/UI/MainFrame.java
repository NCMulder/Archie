//License
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame implements ActionListener {

    ArchieUIManager parent;
    private JComponent mainPanel;
    public JComponent prevPanel;
    public JPanel welcome, working, preferences;
    public JSplitPane metadatachanger;
    JMenuItem toIslandora, toDANS, toArchieXML, editPrefs;
    public JMenu export;
    private JMenuItem tempMenuItem;
    public JMenuItem saveItem;
    private JMenuItem openMenu;
    private JMenuItem about;
    private JMenuItem scan;

    public MainFrame(ArchieUIManager parent) {
        // base init
        this.parent = parent;

        this.setTitle("Archie");
        String imagePath = "/resources/icon2.png";
        try {
            BufferedImage img = ImageIO.read(this.getClass().getResource(imagePath));
            this.setIconImage(img);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(1000, 800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMenus();

        // loading welcome screen
        welcome = new WelcomeScreen();
        prevPanel = welcome;
        working = WorkingOnItPanel(10);
        preferences = new PreferenceChanger(this);

        mainPanel = welcome;
        this.add(mainPanel, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);
    }

    public void setMenus() {
        JMenuBar menuBar = new JMenuBar();

        //dataset menu
        JMenu dataSet = new JMenu("Dataset", true);

        //UIManager.put("Menu.font", new Font(dataSet.getFont().getFontName(), dataSet.getFont().getStyle(), 16));
        dataSet.setPreferredSize(new Dimension(100, 40));
        dataSet.setFont(new Font(dataSet.getFont().getFontName(), dataSet.getFont().getStyle(), 16));
        dataSet.setIconTextGap(8);

        JMenu newMenu = new JMenu("New");
        newMenu.setIcon(UIManager.getIcon("Tree.openIcon"));
        JMenuItem fromDir = new JMenuItem("From directory");
        fromDir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        fromDir.addActionListener(this);
        JMenuItem fromXML = new JMenuItem("From .xml");
        fromXML.addActionListener(this);
        fromXML.setEnabled(false);
        newMenu.add(fromDir);
        newMenu.add(fromXML);
        dataSet.add(newMenu);

        openMenu = new JMenuItem("Open");
        openMenu.addActionListener(this);
        dataSet.add(openMenu);

        saveItem = new JMenuItem("Save");
        saveItem.addActionListener(this);
        saveItem.setEnabled(false);
        dataSet.add(saveItem);

        export = new JMenu("Export as...    ");
        export.setEnabled(false);
        toIslandora = new JMenuItem("Islandora .zip");
        toIslandora.addActionListener(this);
        toDANS = new JMenuItem("DANS repo");
        toDANS.addActionListener(this);
        toDANS.setEnabled(false);
        export.add(toIslandora);
        export.add(toDANS);
        dataSet.add(export);
        
        about = new JMenuItem("About");
        //about.setPreferredSize(new Dimension(100, 40));
        //about.setFont(new Font(dataSet.getFont().getFontName(), dataSet.getFont().getStyle(), 16));
        //about.setIconTextGap(8);
        about.addActionListener(this);
        
        dataSet.add(about);
        
        scan = new JMenuItem("Scan");
        scan.addActionListener(this);
        dataSet.add(scan);

        menuBar.add(dataSet);

        JMenu preferencesMenu = new JMenu("Preferences");
        preferencesMenu.setPreferredSize(new Dimension(100, 40));
        preferencesMenu.setFont(new Font(dataSet.getFont().getFontName(), dataSet.getFont().getStyle(), 16));
        preferencesMenu.setIconTextGap(8);

        editPrefs = new JMenuItem("Edit preferences");
        editPrefs.addActionListener(this);
        preferencesMenu.add(editPrefs);

        menuBar.add(preferencesMenu);

        this.setJMenuBar(menuBar);
        //todo: more menu imps, event listeners
    }

    public void ChangeMainPanel(JComponent newPanel) {
        if (!(newPanel.getClass() == mainPanel.getClass())) {
            prevPanel = mainPanel;
        }
        remove(mainPanel);
        mainPanel = newPanel;
        add(mainPanel, BorderLayout.CENTER);
        pack();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand() == "From directory") {
            NewDataset nds = new NewDataset(this);
            ChangeMainPanel(nds);
        } else if (e.getSource() == tempMenuItem) {
            NewDataset nds = new NewDataset(this, Paths.get("C:\\Users\\niels\\Documents\\Archie\\Archie\\ARCHIE_V1\\saves\\Testset_01.archie").toFile());
            //Temp menu item exists for testing purposes only.
//            SetPart sp = new SetPart(SetPart.Role.Contributor);
//            Object[] buttons = {"Add", "Cancel"};
//            int result = JOptionPane.showOptionDialog(this, sp, "Role addition", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
            //ChangeMainPanel(sp);
        } else if (e.getSource() == editPrefs) {
            PreferenceChanger pfc = new PreferenceChanger(this);
            preferences = pfc;
            ChangeMainPanel(preferences);
        } else if (e.getSource() == toIslandora) {
            if (mainPanel instanceof MetadataChanger) {
                try {
                    MetadataChanger mdc = (MetadataChanger) mainPanel;
                    JFileChooser fc = new JFileChooser(mdc.dataset.mainDirectory.getParent().toString());
                    fc.setSelectedFile(new File(mdc.dataset.name + ".zip"));
                    FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("zip files (*.zip)", "zip");
                    fc.addChoosableFileFilter(zipFilter);
                    fc.setFileFilter(zipFilter);
                    boolean succes = false;
                    int rv = fc.showSaveDialog(this);
                    if (rv == JFileChooser.APPROVE_OPTION) {
                        succes = mdc.Save(MetadataChanger.SaveType.Islandora, fc.getSelectedFile().toPath());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource() == toArchieXML) {
            if (mainPanel instanceof MetadataChanger) {
                try {
                    MetadataChanger mdc = (MetadataChanger) mainPanel;
                    JFileChooser fc = new JFileChooser(mdc.dataset.mainDirectory.getParent().toString());
                    fc.setSelectedFile(new File(mdc.dataset.name + "_AXML.xml"));
                    FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
                    fc.addChoosableFileFilter(zipFilter);
                    fc.setFileFilter(zipFilter);
                    boolean succes = false;
                    int rv = fc.showSaveDialog(this);
                    if (rv == JFileChooser.APPROVE_OPTION) {
                        succes = mdc.Save(MetadataChanger.SaveType.ArchieXML, fc.getSelectedFile().toPath());
                    }
                    if (succes) {
                        JOptionPane.showMessageDialog(this, "The file has been succesfully saved.");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if(e.getSource() == saveItem){
            boolean succes = ((MetadataChanger)mainPanel).dataset.saveDataset();
            if (succes) 
                JOptionPane.showMessageDialog(this, "The dataset has been succesfully saved.");
            else 
                JOptionPane.showMessageDialog(this, "An error occured.\nThe dataset has not been saved.\nPlease try again. If this error persists, please contact us through the help menu.");
        } else if (e.getSource() == openMenu){
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter archieFilter = new FileNameExtensionFilter("archie files(*.archie)", "archie");
            fc.addChoosableFileFilter(archieFilter);
            fc.setFileFilter(archieFilter);
            int rv = fc.showOpenDialog(this);
            if(rv == JFileChooser.APPROVE_OPTION){
                NewDataset nds = new NewDataset(this, fc.getSelectedFile());
                //ChangeMainPanel(nds);
            }
        } else if (e.getSource() == about){
            System.out.println("GOTOABOUT");
            AboutScreen abs = new AboutScreen();
            ChangeMainPanel(abs);
        } else if (e.getSource() == scan){
            ArrayList<String> newFiles = ((MetadataChanger)mainPanel).dataset.Scan();
            if(newFiles.isEmpty()){
                JOptionPane.showMessageDialog(this, "No new files found.");
            } else {
                JPanel filesPanel = new JPanel(new GridLayout(0,1));
                filesPanel.add(new JLabel("New files found:"));
                for(int i = 0; i < newFiles.size(); i++){
                    filesPanel.add(new JLabel(newFiles.get(i)));
                    if(i>5){
                        JLabel andMore = new JLabel("... and " + (newFiles.size() - 5) + " more.");
                        Font andMoreFont = new Font(andMore.getFont().getFontName(), Font.ITALIC, andMore.getFont().getSize());
                        andMore.setFont(andMoreFont);
                        filesPanel.add(andMore);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, filesPanel);
            }
            
            ((MetadataChanger)mainPanel).resetLeftPane();
        } else {
            System.out.println(e);
            System.out.println(e.getSource());
        }
    }

    //temp, rework this todo wip
    public JPanel WorkingOnItPanel(int totalSize) {
        return new ProgressPanel(totalSize);
    }

    public void goToHome() {
        //todo: better home screen implementation? recent files, etc
        welcome = new WelcomeScreen();
        ChangeMainPanel(welcome);
    }
}
