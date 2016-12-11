//License
package archie_v1.UI;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame implements ActionListener {

    UIManager parent;
    public JComponent mainPanel;
    JPanel welcome;
    JSplitPane metadatachanger;
    JMenuItem toIslandora, toDANS, toArchieXML;

    public MainFrame(UIManager parent) {
        // base init
        this.parent = parent;
        this.setTitle("Archie");
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(1000, 800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMenus();
        

        // loading welcome screen
        welcome = new WelcomeScreen();

        mainPanel = welcome;
        this.add(mainPanel, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);
    }

    public void setMenus() {
        JMenuBar menuBar = new JMenuBar();

        //dataset menu
        JMenu dataSet = new JMenu("Dataset");

        JMenu newMenu = new JMenu("New");
        JMenuItem fromDir = new JMenuItem("From directory");
        fromDir.addActionListener(this);
        JMenuItem fromXML = new JMenuItem("From .xml");
        fromXML.addActionListener(this);
        newMenu.add(fromDir);
        newMenu.add(fromXML);
        dataSet.add(newMenu);

        JMenuItem openMenu = new JMenuItem("Open");
        openMenu.addActionListener(this);
        dataSet.add(openMenu);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(this);
        dataSet.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("Save as...");
        saveAsItem.addActionListener(this);
        dataSet.add(saveAsItem);

        JMenu export = new JMenu("Export as...    ");
        toIslandora = new JMenuItem("Islandora .zip");
        toIslandora.addActionListener(this);
        toDANS = new JMenuItem("DANS repo");
        toDANS.addActionListener(this);
        toArchieXML = new JMenuItem("Archie .xml");
        toArchieXML.addActionListener(this);
        export.add(toIslandora);
        export.add(toDANS);
        export.add(toArchieXML);
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
            this.add(nds);
            this.pack();
        } else if (e.getSource() == toIslandora){
            if(mainPanel instanceof MetadataChanger){
                try {
                    MetadataChanger mdc = (MetadataChanger)mainPanel;
                    JFileChooser fc = new JFileChooser(mdc.dataset.mainDirectory.getParent().toString());
                    FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("zip files (*.zip)", "zip");
                    fc.addChoosableFileFilter(zipFilter);
                    fc.setFileFilter(zipFilter);
                    boolean succes = false;
                    int rv = fc.showSaveDialog(this);
                    if(rv == JFileChooser.APPROVE_OPTION){
                        succes = mdc.Save(MetadataChanger.SaveType.Islandora, fc.getSelectedFile().toPath());
                    }
                    if(succes)
                        JOptionPane.showMessageDialog(this, "The file has been succesfully saved.");
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource() == toArchieXML){
            if(mainPanel instanceof MetadataChanger){
                try {
                    MetadataChanger mdc = (MetadataChanger)mainPanel;
                    JFileChooser fc = new JFileChooser(mdc.dataset.mainDirectory.getParent().toString());
                    FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
                    fc.addChoosableFileFilter(zipFilter);
                    fc.setFileFilter(zipFilter);
                    boolean succes = false;
                    int rv = fc.showSaveDialog(this);
                    if(rv == JFileChooser.APPROVE_OPTION)
                        succes = mdc.Save(MetadataChanger.SaveType.ArchieXML, fc.getSelectedFile().toPath());
                    if(succes)
                        JOptionPane.showMessageDialog(this, "The file has been succesfully saved.");
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.out.println(e);
            System.out.println(e.getSource());
        }
    }
    
    //temp
    public JPanel WorkingOnItPanel(){
        JPanel wOI = new JPanel();
        JLabel wOIL = new JLabel("Working on it...");
        Font ft = new Font("Helvetica", 36, 36);
        wOIL.setFont(ft);
        wOIL.setBorder(new EmptyBorder(200, 200, 200, 200));

        wOI.add(wOIL);
        return wOI;
    }
    
    public void goToHome(){
        //todo: home screen implementation?
    }
}
