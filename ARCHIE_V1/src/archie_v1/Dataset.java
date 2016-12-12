//License
package archie_v1;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdom2.Document;
import org.jdom2.Element;

public class Dataset {

    public String name;
    public Path mainDirectory;
    public DefaultMutableTreeNode fileTree;
    public ArrayList<FileHelper> files = new ArrayList();
    public boolean includeIslandora;

    public Dataset(String name, Path path, boolean fromArchie, boolean includeIslandora) {
        this.name = name;
        this.mainDirectory = path;
        this.includeIslandora = includeIslandora;
        
        if (!fromArchie) {
            fileTree = dirToTree(path);
        } else {
            fileTree = null;
        }
    }

    public DefaultMutableTreeNode dirToTree(Path path) {
        DefaultMutableTreeNode dirTree = new DefaultMutableTreeNode(path);
        FolderHelper folderHelper = new FolderHelper(path, includeIslandora);
        for (File file : path.toFile().listFiles()) {
            createNodes(file.toPath(), dirTree, folderHelper);
        }
        files.add(folderHelper);
        return dirTree;
    }

    //Available for planned xml-to-dataset conversion. WIP.
    public DefaultMutableTreeNode docToTree(Document doc) {
        DefaultMutableTreeNode resultTree = new DefaultMutableTreeNode(doc.getRootElement());
        for (Element elem : doc.getRootElement().getChildren()) {
            createNodes(elem, resultTree);
        }
        return resultTree;
    }

    private void createNodes(Path file, DefaultMutableTreeNode tree, FolderHelper folderH) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file);
        tree.add(fileNode);
        if (file.toFile().isDirectory()) {
            FolderHelper folderHelper = new FolderHelper(file, includeIslandora);
            for (File dirFile : file.toFile().listFiles()) {
                createNodes(dirFile.toPath(), fileNode, folderHelper);
            }
            folderH.children.add(folderHelper);
            files.add(folderHelper);
        } else {
            //possibly do this concurrently, most time lost doing this.
            FileHelper fileHelper = ARCHIE.fileSelector(file, includeIslandora);
            folderH.children.add(fileHelper);
            files.add(fileHelper);
        }
    }

    private void createNodes(Element elem, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(elem);
        tree.add(fileNode);
        File f = new File(elem.getAttributeValue("path"));
        if (f.isDirectory()) {
            for (Element element : elem.getChildren()) {
                createNodes(element, fileNode);
            }
        }
    }
}
