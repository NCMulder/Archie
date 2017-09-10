//License
package archie_v1.UI;

import archie_v1.ARCHIE;
import archie_v1.fileHelpers.MetadataKey;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    public JComponent mainPanel;
    public JComponent prevPanel;
    public JPanel welcome, working, preferences;
    public JSplitPane metadatachanger;
    JMenuItem toIslandora, toDANS, toArchive, editPrefs;
    public JMenu export;
    private JMenuItem tempMenuItem;
    public JMenuItem saveItem, saveAsItem;
    private JMenuItem openMenu;
    private JMenuItem about;
    private JMenuItem scan;

    public NewDataset currentNewDatasetter;

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
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if (!(mainPanel instanceof MetadataChanger) || ((MetadataChanger) mainPanel).dataset.saved) {
                    System.exit(0);
                }
                String ObjButtons[] = {"Yes", "No"};
                int PromptResult = JOptionPane.showOptionDialog(null, "There are unsaved files. Are you sure you want to exit?", "Unsaved files", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == 0) {
                    System.exit(0);
                }
            }
        });

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(1000, 800));
        setMenus();

        // loading welcome screen
        welcome = new WelcomeScreen();
        prevPanel = welcome;
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
        newMenu.add(fromDir);
        dataSet.add(newMenu);

        openMenu = new JMenuItem("Open");
        openMenu.addActionListener(this);
        dataSet.add(openMenu);

        saveItem = new JMenuItem("Save");
        saveItem.addActionListener(this);
        saveItem.setEnabled(false);
        dataSet.add(saveItem);

        saveAsItem = new JMenuItem("Save as");
        saveAsItem.addActionListener(this);
        saveAsItem.setEnabled(false);
        dataSet.add(saveAsItem);

        export = new JMenu("Export as...    ");
        export.setEnabled(false);
        toIslandora = new JMenuItem("Islandora .zip");
        toIslandora.addActionListener(this);
        export.add(toIslandora);
        toDANS = new JMenuItem("DANS .zip");
        toDANS.addActionListener(this);
        export.add(toDANS);
        toArchive = new JMenuItem("Nexus1492-zip");
        toArchive.addActionListener(this);
        export.add(toArchive);
        dataSet.add(export);

        about = new JMenuItem("About");
        about.addActionListener(this);

        dataSet.add(about);

        scan = new JMenuItem("Scan");
        scan.addActionListener(this);
        dataSet.add(scan);

        menuBar.add(dataSet);

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
            if (!checkSave()) {
                return;
            }
            currentNewDatasetter = new NewDataset(this);
            ChangeMainPanel(currentNewDatasetter);
        } else if (e.getSource() == toIslandora) {
            if (mainPanel instanceof MetadataChanger) {
                try {
                    MetadataChanger mdc = (MetadataChanger) mainPanel;
                    String recentlyOpened = ARCHIE.prefs.get(ARCHIE.RECENTLY_SAVED, mdc.dataset.mainDirectory.getParent().toString());
                    JFileChooser fc = new checkingFC(recentlyOpened);
                    fc.setSelectedFile(new File(mdc.dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "_ISLANDORA.zip"));
                    FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("zip files (*.zip)", "zip");
                    fc.addChoosableFileFilter(zipFilter);
                    fc.setFileFilter(zipFilter);
                    boolean succes = false;
                    int rv = fc.showSaveDialog(this);
                    if (rv == JFileChooser.APPROVE_OPTION) {
                        ARCHIE.prefs.put(ARCHIE.RECENTLY_SAVED, fc.getSelectedFile().getPath());
                        succes = mdc.Save(MetadataChanger.SaveType.Islandora, fc.getSelectedFile().toPath());
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "The file could not be saved. Close all programs using files present in dataset and try again.");
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource() == toDANS) {
            if (mainPanel instanceof MetadataChanger) {
                try {
                    MetadataChanger mdc = (MetadataChanger) mainPanel;
                    String recentlyOpened = ARCHIE.prefs.get(ARCHIE.RECENTLY_SAVED, mdc.dataset.mainDirectory.getParent().toString());
                    JFileChooser fc = new checkingFC(recentlyOpened);
                    fc.setSelectedFile(new File(mdc.dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "_DANS.zip"));
                    FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("zip files (*.zip)", "zip");
                    fc.addChoosableFileFilter(zipFilter);
                    fc.setFileFilter(zipFilter);
                    boolean succes = false;
                    int rv = fc.showSaveDialog(this);
                    if (rv == JFileChooser.APPROVE_OPTION) {
                        ARCHIE.prefs.put(ARCHIE.RECENTLY_SAVED, fc.getSelectedFile().getPath());
                        succes = mdc.Save(MetadataChanger.SaveType.Dans, fc.getSelectedFile().toPath());
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "The file could not be saved. Close all programs using files present in dataset and try again.");
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource() == toArchive) {
            if (mainPanel instanceof MetadataChanger) {
                try {
                    MetadataChanger mdc = (MetadataChanger) mainPanel;
                    String recentlyOpened = ARCHIE.prefs.get(ARCHIE.RECENTLY_SAVED, mdc.dataset.mainDirectory.getParent().toString());
                    JFileChooser fc = new checkingFC(recentlyOpened);
                    fc.setSelectedFile(new File(mdc.dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "_Nexus1492.zip"));
                    FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("zip files (*.zip)", "zip");
                    fc.addChoosableFileFilter(zipFilter);
                    fc.setFileFilter(zipFilter);
                    boolean succes = false;
                    int rv = fc.showSaveDialog(this);
                    if (rv == JFileChooser.APPROVE_OPTION) {
                        ARCHIE.prefs.put(ARCHIE.RECENTLY_SAVED, fc.getSelectedFile().getPath());
                        succes = mdc.Save(MetadataChanger.SaveType.Archie, fc.getSelectedFile().toPath());
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "The file could not be saved. Close all programs using files present in dataset and try again.");
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource() == saveAsItem) {
            Save(true);
        } else if (e.getSource() == saveItem) {
            Save(false);
        } else if (e.getSource() == openMenu) {
            if (!checkSave()) {
                return;
            }
            String path = ARCHIE.prefs.get(ARCHIE.RECENTLY_OPENED_ARCHIEFILE, "");
            JFileChooser fc = new JFileChooser(path);
            FileNameExtensionFilter archieFilter = new FileNameExtensionFilter("archie files(*.archie)", "archie");
            fc.addChoosableFileFilter(archieFilter);
            fc.setFileFilter(archieFilter);
            int rv = fc.showOpenDialog(this);
            if (rv == JFileChooser.APPROVE_OPTION) {
                ARCHIE.prefs.put(ARCHIE.RECENTLY_OPENED_ARCHIEFILE, fc.getSelectedFile().getPath());
                currentNewDatasetter = new NewDataset(this, fc.getSelectedFile());
                //((MetadataChanger)mainPanel).dataset.saveLocation = fc.getSelectedFile().toPath();
                //ChangeMainPanel(nds);
            } else {

            }
        } else if (e.getSource() == about) {
            Object[] buttons = {"Close"};
            JOptionPane.showOptionDialog(this, new AboutScreen(), "About", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
            //ChangeMainPanel(abs);
        } else if (e.getSource() == scan) {
            if (mainPanel instanceof MetadataChanger) {
                ArrayList<String> newFiles = ((MetadataChanger) mainPanel).dataset.Scan();
                if (newFiles.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No new files found.");
                } else {
                    JPanel filesPanel = new JPanel(new GridLayout(0, 1));
                    filesPanel.add(new JLabel("New files found:"));
                    for (int i = 0; i < newFiles.size(); i++) {
                        filesPanel.add(new JLabel(newFiles.get(i)));
                        if (i > 5) {
                            JLabel andMore = new JLabel("... and " + (newFiles.size() - 5) + " more.");
                            Font andMoreFont = new Font(andMore.getFont().getFontName(), Font.ITALIC, andMore.getFont().getSize());
                            andMore.setFont(andMoreFont);
                            filesPanel.add(andMore);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this, filesPanel);
                }

                ((MetadataChanger) mainPanel).resetLeftPane();
            }
        } else {
            System.out.println(e);
            System.out.println(e.getSource());
        }
    }

    private class checkingFC extends JFileChooser {

        public checkingFC(String filePath) {
            super(filePath);
        }

        //Source: http://stackoverflow.com/questions/3651494/jfilechooser-with-confirmation-dialog
        @Override
        public void approveSelection() {
            File f = getSelectedFile();
            if (f.exists() && getDialogType() == SAVE_DIALOG) {
                int result = JOptionPane.showConfirmDialog(this, "That file already exists, do you want to overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                switch (result) {
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.NO_OPTION:
                        return;
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        cancelSelection();
                        return;
                }
            }
            super.approveSelection();
        }
    }

    public void goToHome() {
        //todo: better home screen implementation? recent files, etc
        welcome = new WelcomeScreen();
        ChangeMainPanel(welcome);
    }

    public boolean Save(boolean saveAs) {
        if (!(mainPanel instanceof MetadataChanger)) {
            return true;
        }
        MetadataChanger mdc = (MetadataChanger) mainPanel;
        JFileChooser fc;
        File savePath = null;
        if (saveAs || mdc.dataset.saveLocation == null) {
            String recentlyOpened = ARCHIE.prefs.get(ARCHIE.RECENTLY_SAVED, mdc.dataset.mainDirectory.getParent().toString());
            fc = new checkingFC(recentlyOpened);
            fc.setSelectedFile(new File(mdc.dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + ".archie"));
            FileNameExtensionFilter archieFilter = new FileNameExtensionFilter("archie files (*.archie)", "archie");
            fc.addChoosableFileFilter(archieFilter);
            fc.setFileFilter(archieFilter);
            int rv = fc.showSaveDialog(this);
            if (rv == JFileChooser.APPROVE_OPTION) {
                ARCHIE.prefs.put(ARCHIE.RECENTLY_SAVED, fc.getSelectedFile().getPath());
                savePath = fc.getSelectedFile();
            } else if (rv == JFileChooser.CANCEL_OPTION) {
                return false;
            }
        }
        if (savePath == null) {
            savePath = mdc.dataset.saveLocation.toFile();
        }
        boolean succes = mdc.dataset.saveDataset(savePath);
        if (succes) {
            mdc.dataset.saveLocation = savePath.toPath();
            mdc.dataset.saved = true;
            JOptionPane.showMessageDialog(this, "The dataset has been succesfully saved.");
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "An error occured.\nThe dataset has not been saved.\nPlease try again. If this error persists, please contact us through the help menu.");
            return false;
        }
    }

    public boolean checkSave() {
        if (!(mainPanel instanceof MetadataChanger)) {
            return true;
        }
        if (((MetadataChanger) mainPanel).dataset.saved) {
            return true;
        }
        Object[] buttons = {"Save", "Save as", "Ignore", "Cancel"};
        int result = JOptionPane.showOptionDialog(this, "The current dataset is not yet saved. Do you want to save first?", "Opening new dataset", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
        switch (result) {
            case 0:
                return Save(false);
            case 1:
                return Save(true);
            case 2:
                return true;
            default:
                return false;
        }
    }
}
