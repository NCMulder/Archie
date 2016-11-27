//License
package archie_v1.UI;

import archie_v1.UIManager;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame implements ActionListener {

    UIManager parent;
    public JPanel mainPanel;
    JPanel welcome, metadatachanger;

    public MainFrame(UIManager parent) {
        // base init
        this.parent = parent;
        this.setTitle("Archie");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMenus();

        // loading welcome screen
        welcome = new WelcomeScreen();

        mainPanel = welcome;
        this.add(mainPanel);

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
        JMenuItem toIslandora = new JMenuItem("Islandora zip");
        toIslandora.addActionListener(this);
        JMenuItem toDANS = new JMenuItem("DANS repo");
        toDANS.addActionListener(this);
        export.add(toIslandora);
        export.add(toDANS);
        dataSet.add(export);

        menuBar.add(dataSet);

        this.setJMenuBar(menuBar);
        //todo: more menu imps, event listeners and shit
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (e.getActionCommand() == "From directory") {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //set working on it:
                this.remove(mainPanel);
                mainPanel = WorkingOnItPanel();
                this.add(mainPanel);
                this.pack();
                super.paint(super.getGraphics());
                //todo: get name
                metadatachanger = new MetaDataChanger("noname", fc.getSelectedFile().toPath(), false);
                this.remove(mainPanel);
                mainPanel = metadatachanger;
                this.add(mainPanel);
                this.pack();
                System.out.println(mainPanel);
            }
        } else {
            System.out.println(e);
        }
    }
    
    private JPanel WorkingOnItPanel(){
        JPanel wOI = new JPanel();
        JLabel wOIL = new JLabel("Working on it...");
        Font ft = new Font("Helvetica", 36, 36);
        wOIL.setFont(ft);
        wOIL.setBorder(new EmptyBorder(200, 200, 200, 200));

        wOI.add(wOIL);
        return wOI;
    }
}
