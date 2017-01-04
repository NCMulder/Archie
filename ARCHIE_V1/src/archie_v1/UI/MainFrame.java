//License
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame implements ActionListener {

    ArchieUIManager parent;
    public JComponent mainPanel;
    public JPanel welcome, working;
    JSplitPane metadatachanger;
    JMenuItem toIslandora, toDANS, toArchieXML;
    public JMenu export;

    public MainFrame(ArchieUIManager parent) {
        // base init
        this.parent = parent;
        this.setTitle("Archie");
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(1000, 800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMenus();

        // loading welcome screen
        welcome = new WelcomeScreen();
        working = WorkingOnItPanel(10);

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

        JMenuItem openMenu = new JMenuItem("Open");
        openMenu.addActionListener(this);
        openMenu.setEnabled(false);
        dataSet.add(openMenu);

        JMenuItem saveItem = new JMenuItem("Save");
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

        menuBar.add(dataSet);

        this.setJMenuBar(menuBar);
        //todo: more menu imps, event listeners
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand() == "From directory") {
            NewDataset nds = new NewDataset(this);
            this.remove(mainPanel);
            mainPanel = nds;
            this.add(mainPanel);
            this.pack();
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
                    if (succes) {
                        JOptionPane.showMessageDialog(this, "The file has been succesfully saved.");
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
        this.remove(mainPanel);
        welcome = new WelcomeScreen();
        mainPanel = welcome;
        this.add(mainPanel, BorderLayout.CENTER);
        this.pack();
    }
}
