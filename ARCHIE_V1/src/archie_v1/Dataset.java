//License
package archie_v1;

import archie_v1.outputFormats.outputArchieXML;
import java.io.File;
import java.nio.file.Path;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdom2.Document;
import org.jdom2.Element;

public class Dataset {

    public Path mainDirectory;
    public DefaultMutableTreeNode fileTree;
    public Document aXML;

    public Dataset(String name, Path path, Boolean fromArchie) {
        long startTime = System.nanoTime();
        this.mainDirectory = path;
        if (!fromArchie) {
            fileTree = dirToTree(path);
        } else {
            //fileTreeFromDoc
            fileTree = null;
        }
        long currTime = System.nanoTime();
        System.out.println("Filetree created in " + (currTime - startTime)/1000000 + " ms");

        outputArchieXML axc = new outputArchieXML();
        aXML = axc.CreateDocument(fileTree);
        long archieTime = System.nanoTime();
        System.out.println("ArchieXML created in " + (archieTime - currTime)/1000000 + " ms");
    }

    public DefaultMutableTreeNode dirToTree(Path path) {
        DefaultMutableTreeNode dirTree = new DefaultMutableTreeNode(path);
        for (File file : path.toFile().listFiles()) {
            createNodes(file.toPath(), dirTree);
        }
        return dirTree;
    }

    public DefaultMutableTreeNode docToTree(Document doc) {
        DefaultMutableTreeNode resultTree = new DefaultMutableTreeNode(doc.getRootElement());
        for (Element elem : doc.getRootElement().getChildren()) {
            createNodes(elem, resultTree);
        }
        return resultTree;
    }

    private void createNodes(Path file, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file);
        tree.add(fileNode);
        if (file.toFile().isDirectory()) {
            for (File dirFile : file.toFile().listFiles()) {
                createNodes(dirFile.toPath(), fileNode);
            }
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
