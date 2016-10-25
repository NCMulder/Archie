//License header
package archie_v1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
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
        dirTree = new DefaultMutableTreeNode(path);
        CreateNodes(path, dirTree);
        CreateDocument(dirTree);

        XMLOutputter outputter = new XMLOutputter();

        try {
            PrintWriter writer = new PrintWriter("basic_xml.xml");
            outputter.output(xml, writer);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "File not found", ex);
        } catch (IOException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "Writer not found", ex);
        }

        //Filetree view
        fileTree = new JTree(dirTree);
        JScrollPane fileTreeView = new JScrollPane(fileTree);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(fileTreeView);
        add(splitPane);
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
        Element root = new Element("root");
        CreateElements(dirTree, root);

        xml = new Document(root);
    }

    private void CreateElements(DefaultMutableTreeNode dir, Element element) {
        Path filePath = (Path) dir.getUserObject();
        Element file = new Element("file");
        Element name = new Element("name");
        name.setText(filePath.getFileName().toString());
        file.addContent(name);

        if (filePath.toFile().isFile()) {
        setFileElements(filePath, file);
        } else {
            file.setName("folder");
            setFolderElements(dir, file);
            for (Object files : Collections.list(dir.children())) {
                CreateElements((DefaultMutableTreeNode) files, file);
            }
        }
        element.addContent(file);
    }
    
    //setFileElements sets several content types for files, such as filetype and modification date.
    private void setFileElements(Path filePath, Element file) {
            BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(filePath, BasicFileAttributes.class);
        } catch (IOException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Filetype setting.
        Element fileType = new Element("filetype");
        String fileTypeString = "unknown";
            try {
                String temp = Files.probeContentType(filePath);
                if (temp != null) {
                    fileTypeString = temp;
                }
            } catch (IOException ex) {
                Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
            }
        fileType.setText(fileTypeString);
        file.addContent(fileType);
        
        Element fileSize = new Element("file_size");
        fileSize.setText(Long.toString(attr.size()));
        file.addContent(fileSize);
    }

    //setFileElements sets several content types for folders, such as filecount.
    private void setFolderElements(DefaultMutableTreeNode folderNode, Element folder) {
            Element fileCount = new Element("filecount");
            fileCount.setText(Integer.toString(folderNode.getChildCount()));
            folder.addContent(fileCount);
    }
}
