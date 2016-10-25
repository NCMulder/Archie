//License header
package archie_v1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.jdom2.*;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author Niels Mulder
 */
public class ARCHIE extends JPanel {

    private Path mainDir;
    private DefaultMutableTreeNode dirTree;
    private JTree fileTree;
    private Document xml;

    public ARCHIE(Path path) {
        mainDir = path;
        dirTree = new DefaultMutableTreeNode(path.getFileName());
        CreateNodes(path, dirTree);
        CreateDocument(dirTree);
        
        
        XMLOutputter outputter = new XMLOutputter();
        Path xmlfile = Paths.get("basic_xml.xml");
        try {
            PrintWriter writer = new PrintWriter("basic_xml.xml");
            outputter.output(xml, writer);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "File not found", ex);
        } catch (IOException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "Writer not found", ex);
        }

        fileTree = new JTree(dirTree);

        JScrollPane fileTreeView = new JScrollPane(fileTree);
        //add(fileTreeView);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(fileTreeView);
        add(splitPane);
    }

    private void CreateNodes(Path path, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(path.getFileName());
        //System.out.println("Added dir " + path.getFileName().toString());
        tree.add(fileNode);
        if (path.toFile().isDirectory()) {
            for (File file : path.toFile().listFiles()) {
                CreateNodes(file.toPath(), fileNode);
            }
        }
    }

    public static void main(String[] args) {
        String str = "C:/Users/niels/Desktop";
        ARCHIE arch = new ARCHIE(Paths.get(str));
        
        JFrame frame = new JFrame("DirTree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.add(arch);
        
        frame.pack();
        frame.setVisible(true);
    }

    private void CreateDocument(DefaultMutableTreeNode dirTree) {
        Element root = new Element(dirTree.toString());
        CreateElements(dirTree, root);
        
        xml = new Document(root);
        System.out.println("xml doc: " + xml.toString());
    }
    
    private void CreateElements(DefaultMutableTreeNode dir, Element element){
        if(dir.isLeaf()){
            Element file = new Element("file");
            file.setAttribute("name",dir.toString());
            file.setAttribute("filetype", "unknown");
            element.addContent(file);
        } else {
            Element folder = new Element("folder");
            folder.setAttribute("filecount", Integer.toString(dir.getChildCount()));
            folder.setAttribute("name", dir.toString());
            for (Object file : Collections.list(dir.children())) {
                CreateElements((DefaultMutableTreeNode)file, folder);
            }
            element.addContent(folder);
        }
    }
}
