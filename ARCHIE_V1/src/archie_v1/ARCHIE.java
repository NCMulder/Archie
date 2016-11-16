//License header
package archie_v1;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdom2.*;

public class ARCHIE extends JPanel {

    private Path mainDir;
    private DefaultMutableTreeNode dirTree;
    private JTree fileTree;
    private Document xml;
    private archieXMLcreator aXML;

    public ARCHIE(Path path) {
        mainDir = path;
        
        //Filetree generation
        dirTree = new DefaultMutableTreeNode(path);
        for (File file : path.toFile().listFiles()) {
                CreateNodes(file.toPath(), dirTree);
            }
        
        //Tussenvorm XML creation
        aXML = new archieXMLcreator();
        xml = aXML.CreateDocument(dirTree);
        aXML.saveToXML(xml);

        //Filetree view creation
        fileTree = new JTree(dirTree);
        JScrollPane fileTreeView = new JScrollPane(fileTree);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(fileTreeView);
        add(splitPane);
    }

    public static void main(String[] args) {
        String str = "C:\\Users\\niels\\Documents\\Archie\\Archie\\Documentation\\testset";
        ARCHIE arch = new ARCHIE(Paths.get(str));

        JFrame frame = new JFrame("DirTree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(arch);

        frame.pack();
        frame.setVisible(true);
    }

    private void CreateNodes(Path path, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(path);
        tree.add(fileNode);
        if (path.toFile().isDirectory()) {
            for (File file : path.toFile().listFiles()) {
                CreateNodes(file.toPath(), fileNode);
            }
        }
    }
}
