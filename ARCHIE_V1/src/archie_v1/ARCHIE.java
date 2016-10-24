//License header
package archie_v1;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author Niels Mulder
 */
public class ARCHIE extends JPanel {

    private Path mainDir;
    private DefaultMutableTreeNode dirTree;
    private JTree fileTree;

    public ARCHIE(Path path) {
        mainDir = path;
        dirTree = new DefaultMutableTreeNode(path.getFileName());
        CreateNodes(path, dirTree);

        fileTree = new JTree(dirTree);

        JScrollPane fileTreeView = new JScrollPane(fileTree);
        //add(fileTreeView);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(fileTreeView);
        add(splitPane);
    }

    private void CreateNodes(Path path, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(path.getFileName());
        System.out.println("Added dir " + path.getFileName().toString());
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
}
